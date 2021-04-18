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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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
public final class Board<O> implements AutoCloseable {

  /**
   * the boards.
   */
  private static final Map<String, Board<?>> BOARDS = new ConcurrentHashMap<>();

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
  private final List<Line<O>> lines;

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
   * the scheduler.
   */
  @NotNull
  private final ScheduledExecutorService scheduler;

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
  }

  /**
   * sends the scoreboard for once.
   */
  public void sendOnce() {
  }

  /**
   * starts the scoreboard sequence.
   */
  public void start() {
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
    private List<Line<O>> lines = new ArrayList<>();

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
     * the scheduler.
     */
    @NotNull
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    /**
     * the start delay.
     */
    private long startDelay = 20L;

    /**
     * the static observers.
     */
    @NotNull
    private Set<O> staticObservers = new HashSet<>();

    /**
     * the tick.
     */
    private long tick = 20L;

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
     * adds the given lines to the {@link #lines}.
     *
     * @param lines the lines to add.
     *
     * @return {@code this} for builder chain.
     */
    @SafeVarargs
    @NotNull
    public final Builder<O> addLines(@NotNull final Line<O>... lines) {
      Collections.addAll(this.lines, lines);
      return this;
    }

    /**
     * adds the given remove if to the {@link #lines}.
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
     * adds the given run after to the {@link #lines}.
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
     * adds the given run before to the {@link #lines}.
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
     * builds a new board instance from the builder's value.
     *
     * @return a newly created instance of {@link Board}.
     */
    @NotNull
    public Board<O> build() {
      final var board = new Board<>(this.dynamicObservers, this.filters, this.id, this.lines, this.observerClass,
        this.removeIf, this.runAfter, this.runBefore, this.scheduler, this.startDelay, this.staticObservers, this.tick);
      if (this.id != null) {
        Board.BOARDS.put(this.id, board);
      }
      return board;
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
    public Builder<O> setLines(@NotNull final List<Line<O>> lines) {
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
     * sets the scheduler.
     *
     * @param scheduler the scheduler to set.
     *
     * @return {@code this} for builder chain.
     */
    @NotNull
    public Builder<O> setScheduler(@NotNull final ScheduledExecutorService scheduler) {
      this.scheduler = scheduler;
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
}
