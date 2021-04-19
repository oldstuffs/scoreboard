/*
 * MIT License
 *
 * Copyright (c) 2021 Hasan Demirtaş
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package io.github.portlek.scoreboard;

import io.github.portlek.scoreboard.line.Line;
import java.io.Closeable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * a class that represents boards.
 *
 * @param <O> type of the observers.
 */
@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Board<O> implements Closeable {

  /**
   * the boards.
   */
  private static final Map<String, Board<?>> BOARDS = new ConcurrentHashMap<>();

  /**
   * the async scheduler.
   */
  @NotNull
  private final ScheduledExecutorService asyncScheduler;

  /**
   * the dynamic observers.
   */
  @NotNull
  private final Set<Supplier<O>> dynamicObservers;

  /**
   * the filters.
   */
  @NotNull
  private final Set<Predicate<O>> filters;

  /**
   * the id.
   */
  @Nullable
  private final String id;

  /**
   * the lines.
   */
  @NotNull
  private final Map<Integer, Line<O>> lines;

  /**
   * the mutable board.
   */
  private final ThreadLocal<MutableBoard<O>> mutableBoard = ThreadLocal.withInitial(() ->
    new MutableBoard<>(this));

  /**
   * the observer class.
   */
  @NotNull
  private final Class<O> observerClass;

  /**
   * the remove if.
   */
  @NotNull
  private final Set<Predicate<O>> removeIf;

  /**
   * the run after.
   */
  @NotNull
  private final Set<Consumer<O>> runAfter;

  /**
   * the run before.
   */
  @NotNull
  private final Set<Consumer<O>> runBefore;

  /**
   * the scoreboard sender.
   */
  @NotNull
  private final ScoreboardSender<O> scoreboardSender;

  /**
   * the start delay.
   */
  private final long startDelay;

  /**
   * the static observers.
   */
  @NotNull
  private final Set<O> staticObservers;

  /**
   * the tick.
   */
  private final long tick;

  /**
   * creates a new instance of {@link Builder}.
   *
   * @param observerClass the observer class to create.
   * @param <O> type of the observers.
   *
   * @return a newly created instance of {@link Builder}.
   */
  @NotNull
  public static <O> Builder<O> builder(@NotNull final Class<O> observerClass) {
    return new Builder<>(observerClass);
  }

  /**
   * obtains the board by id.
   *
   * @param id the id to obtain.
   *
   * @return board.
   */
  @NotNull
  public static Optional<Board<?>> getBoardById(@NotNull final String id) {
    return Optional.ofNullable(Board.BOARDS.get(id));
  }

  /**
   * obtains the by id and observer class.
   *
   * @param observerClass the observer class to obtain.
   * @param id the id to obtain.
   * @param <O> type of the observers.
   *
   * @return board.
   */
  @NotNull
  public static <O> Optional<Board<O>> getBoardById(@NotNull final Class<O> observerClass, @NotNull final String id) {
    //noinspection unchecked
    return Board.getBoardById(id)
      .filter(board -> observerClass.isAssignableFrom(board.getObserverClass()))
      .map(board -> (Board<O>) board);
  }

  @Override
  public void close() {
    this.asyncScheduler.shutdown();
    this.scoreboardSender.close();
  }

  /**
   * resets the {@link #mutableBoard}.
   */
  public void reset() {
    this.mutableBoard.set(new MutableBoard<>(this));
  }

  /**
   * sends the scoreboard for once.
   */
  public void send() {
    this.send(false);
  }

  /**
   * sends the scoreboard for once.
   *
   * @param reset the reset to send.
   */
  public void send(final boolean reset) {
    if (reset) {
      this.reset();
    }
    this.mutableBoard.get().send();
  }

  /**
   * starts the scoreboard sequence.
   */
  public void start() {
    this.reset();
    this.asyncScheduler.scheduleAtFixedRate(this::send, this.startDelay, this.tick, TimeUnit.MILLISECONDS);
  }

  /**
   * a class that represents builders for {@link Board}.
   *
   * @param <O> type of the observers.
   */
  @Getter
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static final class Builder<O> {

    /**
     * the observer class.
     */
    @NotNull
    private final Class<O> observerClass;

    /**
     * the async scheduler.
     */
    @NotNull
    private ScheduledExecutorService asyncScheduler = Executors.newScheduledThreadPool(2);

    /**
     * the dynamic observers.
     */
    @NotNull
    private Set<Supplier<O>> dynamicObservers = new HashSet<>();

    /**
     * the filters.
     */
    @NotNull
    private Set<Predicate<O>> filters = new HashSet<>();

    /**
     * the id.
     */
    @Nullable
    private String id;

    /**
     * the lines.
     */
    @NotNull
    private Map<Integer, Line<O>> lines = new ConcurrentHashMap<>();

    /**
     * the remove if.
     */
    @NotNull
    private Set<Predicate<O>> removeIf = new HashSet<>();

    /**
     * the run after.
     */
    @NotNull
    private Set<Consumer<O>> runAfter = new HashSet<>();

    /**
     * the run before.
     */
    @NotNull
    private Set<Consumer<O>> runBefore = new HashSet<>();

    /**
     * the scoreboard sender.
     */
    @NotNull
    private ScoreboardSender<O> scoreboardSender = new ScoreboardSender.Empty<>();

    /**
     * the start delay.
     */
    private long startDelay = 1000L;

    /**
     * the static observers.
     */
    @NotNull
    private Set<O> staticObservers = new HashSet<>();

    /**
     * the tick.
     */
    private long tick = 1000L;

    /**
     * adds the given dynamic observers to the {@link #dynamicObservers}.
     *
     * @param observers the observers to add.
     *
     * @return {@code this} for builder chain.
     */
    @SafeVarargs
    @NotNull
    public final Builder<O> addDynamicObservers(@NotNull final Supplier<O>... observers) {
      Collections.addAll(this.dynamicObservers, observers);
      return this;
    }

    /**
     * adds the given filters to the {@link #filters}.
     *
     * @param filters the filters to add.
     *
     * @return {@code this} for builder chain.
     */
    @SafeVarargs
    @NotNull
    public final Builder<O> addFilters(@NotNull final Predicate<O>... filters) {
      Collections.addAll(this.filters, filters);
      return this;
    }

    /**
     * adds the given lines to the first empty line of {@link #lines}.
     *
     * @param lines the lines to add.
     *
     * @return {@code this} for builder chain.
     */
    @SafeVarargs
    @NotNull
    public final Builder<O> addLines(@NotNull final Line<O>... lines) {
      final var keys = this.lines.keySet();
      final var lineIterator = Arrays.stream(lines).iterator();
      IntStream.iterate(0, index -> lineIterator.hasNext() && index < keys.size(), index -> index + 1)
        .filter(index -> !keys.contains(index))
        .forEach(index -> this.lines.put(index, lineIterator.next()));
      return this;
    }

    /**
     * adds the given remove if to the {@link #removeIf}.
     *
     * @param removeIf the remove if to add.
     *
     * @return {@code this} for builder chain.
     */
    @SafeVarargs
    @NotNull
    public final Builder<O> addRemoveIf(@NotNull final Predicate<O>... removeIf) {
      Collections.addAll(this.removeIf, removeIf);
      return this;
    }

    /**
     * adds the given run after to the {@link #runAfter}.
     *
     * @param runAfter the run after to add.
     *
     * @return {@code this} for builder chain.
     */
    @SafeVarargs
    @NotNull
    public final Builder<O> addRunAfter(@NotNull final Consumer<O>... runAfter) {
      Collections.addAll(this.runAfter, runAfter);
      return this;
    }

    /**
     * adds the given run before to the {@link #runBefore}.
     *
     * @param runBefore the run before to add.
     *
     * @return {@code this} for builder chain.
     */
    @SafeVarargs
    @NotNull
    public final Builder<O> addRunBefore(@NotNull final Consumer<O>... runBefore) {
      Collections.addAll(this.runBefore, runBefore);
      return this;
    }

    /**
     * adds the given static observers to the {@link #staticObservers}.
     *
     * @param observers the observers to add.
     *
     * @return {@code this} for builder chain.
     */
    @SafeVarargs
    @NotNull
    public final Builder<O> addStaticObservers(@NotNull final O... observers) {
      Collections.addAll(this.staticObservers, observers);
      return this;
    }

    /**
     * adds the given line to the {@link #lines}.
     *
     * @param lineNumber the line number to add.
     * @param line the line to add.
     *
     * @return {@code this} for builder chain.
     */
    @NotNull
    public Builder<O> addLine(final int lineNumber, @NotNull final Line<O> line) {
      this.lines.put(lineNumber, line);
      return this;
    }

    /**
     * adds the given line to the {@link #lines}.
     *
     * @param lineNumber the line number to add.
     * @param line the line to add.
     *
     * @return {@code this} for builder chain.
     */
    @NotNull
    public Builder<O> addLine(final int lineNumber, @NotNull final Function<@NotNull O, @NotNull String> line) {
      return this.addLine(lineNumber, Line.line(line));
    }

    /**
     * builds a new board instance from the builder's value.
     *
     * @return a newly created instance of {@link Board}.
     */
    @NotNull
    public Board<O> build() {
      if (this.id != null && Board.BOARDS.containsKey(this.id)) {
        throw new IllegalArgumentException(String.format("Id called %s is already exist in the boards map.",
          this.id));
      }
      final var board = new Board<>(this.asyncScheduler, this.dynamicObservers, this.filters, this.id, this.lines,
        this.observerClass, this.removeIf, this.runAfter, this.runBefore, this.scoreboardSender, this.startDelay,
        this.staticObservers, this.tick);
      if (this.id != null) {
        Board.BOARDS.put(this.id, board);
      }
      return board;
    }

    /**
     * sets the async scheduler.
     *
     * @param asyncScheduler the async scheduler to set.
     *
     * @return {@code this} for builder chain.
     */
    @NotNull
    public Builder<O> setAsyncScheduler(@NotNull final ScheduledExecutorService asyncScheduler) {
      this.asyncScheduler = asyncScheduler;
      return this;
    }

    /**
     * sets the dynamic observers.
     *
     * @param dynamicObservers the dynamic observers to set.
     *
     * @return {@code this} for build chain.
     */
    @NotNull
    public Builder<O> setDynamicObservers(@NotNull final Set<Supplier<O>> dynamicObservers) {
      this.dynamicObservers = dynamicObservers;
      return this;
    }

    /**
     * sets the filters.
     *
     * @param filters the filters to set.
     *
     * @return {@code this} for build chain.
     */
    @NotNull
    public Builder<O> setFilters(@NotNull final Set<Predicate<O>> filters) {
      this.filters = filters;
      return this;
    }

    /**
     * sets the id.
     *
     * @param id the id to set.
     *
     * @return {@code this} for build chain.
     */
    @NotNull
    public Builder<O> setId(final String id) {
      this.id = id;
      return this;
    }

    /**
     * sets the lines.
     *
     * @param lines the lines to set.
     *
     * @return {@code this} for build chain.
     */
    @NotNull
    public Builder<O> setLines(@NotNull final Map<Integer, Line<O>> lines) {
      this.lines = lines;
      return this;
    }

    /**
     * sets the remove if.
     *
     * @param removeIf the remove if to set.
     *
     * @return {@code this} for builder chain.
     */
    @NotNull
    public Builder<O> setRemoveIf(@NotNull final Set<Predicate<O>> removeIf) {
      this.removeIf = removeIf;
      return this;
    }

    /**
     * sets the run after.
     *
     * @param runAfter the run after to set.
     *
     * @return {@code this} for builder chain.
     */
    @NotNull
    public Builder<O> setRunAfter(@NotNull final Set<Consumer<O>> runAfter) {
      this.runAfter = runAfter;
      return this;
    }

    /**
     * sets the run before.
     *
     * @param runBefore the run before to set.
     *
     * @return {@code this} for builder chain.
     */
    @NotNull
    public Builder<O> setRunBefore(@NotNull final Set<Consumer<O>> runBefore) {
      this.runBefore = runBefore;
      return this;
    }

    /**
     * sets the scoreboard sender.
     *
     * @param scoreboardSender the scoreboard sender to set.
     *
     * @return {@code this} for build chaining.
     */
    @NotNull
    public Builder<O> setScoreboardSender(@NotNull final ScoreboardSender<O> scoreboardSender) {
      this.scoreboardSender = scoreboardSender;
      return this;
    }

    /**
     * sets the start delay.
     *
     * @param startDelay the start delay to set.
     *
     * @return {@code this} for build chain.
     *
     * @throws IllegalArgumentException if the start delay is lower than 0.
     */
    @NotNull
    public Builder<O> setStartDelay(final long startDelay) {
      if (startDelay < 0L) {
        throw new IllegalArgumentException("Start delay shouldn't less than 0.");
      }
      this.startDelay = startDelay;
      return this;
    }

    /**
     * sets the static observers.
     *
     * @param staticObservers the static observers to set.
     *
     * @return {@code this} for build chain.
     */
    @NotNull
    public Builder<O> setStaticObservers(@NotNull final Set<O> staticObservers) {
      this.staticObservers = staticObservers;
      return this;
    }

    /**
     * sets the tick.
     *
     * @param tick the tick to set.
     *
     * @return {@code this} for build chain.
     *
     * @throws IllegalArgumentException if the tick is lower than 0.
     */
    @NotNull
    public Builder<O> setTick(final long tick) {
      if (tick <= 0L) {
        throw new IllegalArgumentException("Tick shouldn't equal or less than 0.");
      }
      this.tick = tick;
      return this;
    }
  }

  /**
   * a class that represents mutable boards.
   *
   * @param <O> type of the observers.
   */
  private static final class MutableBoard<O> {

    /**
     * the board.
     */
    @NotNull
    private final Board<O> board;

    /**
     * the static observers.
     */
    @NotNull
    private final Set<O> staticObservers;

    /**
     * ctor.
     *
     * @param board the board.
     */
    private MutableBoard(@NotNull final Board<O> board) {
      this.board = board;
      this.staticObservers = new HashSet<>(board.getStaticObservers());
    }

    /**
     * sends the {@link #board} to the all observers.
     */
    public void send() {
      this.staticObserversRemoveIf();
      final var observers = this.getObservers();
      this.board.getRunBefore().forEach(observers::forEach);
      this.board.getScoreboardSender().send(observers, this.board.getLines());
      this.board.getRunAfter().forEach(observers::forEach);
    }

    /**
     * obtains the observers.
     *
     * @return observers.
     */
    @NotNull
    @Synchronized("staticObservers")
    private Set<O> getObservers() {
      final var observers = this.staticObservers.stream()
        .filter(observer ->
          this.board.getFilters().stream()
            .anyMatch(predicate -> predicate.test(observer)))
        .collect(Collectors.toSet());
      this.board.getDynamicObservers().stream()
        .map(Supplier::get)
        .filter(observer ->
          this.board.getFilters().stream()
            .anyMatch(predicate -> predicate.test(observer)))
        .forEach(observers::add);
      return observers;
    }

    /**
     * removes each {@link #staticObservers} if the observer passes {@link Board#getRemoveIf()}.
     */
    @Synchronized("staticObservers")
    private void staticObserversRemoveIf() {
      this.staticObservers.removeIf(observer ->
        this.board.getRemoveIf().stream()
          .anyMatch(predicate -> predicate.test(observer)));
    }
  }
}
