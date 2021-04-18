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

import java.util.Set;
import java.util.concurrent.Executors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.hamcrest.core.IsEqual;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.HasSize;
import org.llorllale.cactoos.matchers.IsTrue;
import org.llorllale.cactoos.matchers.Throws;

final class BoardTest {

  @Test
  void createBoard() {
    final var observer1 = new CommandSender("observer-1");
    final var observer2 = new CommandSender("observer-2");
    final var observer3 = new CommandSender("observer-3");
    final var originalScheduler = Executors.newScheduledThreadPool(4);
    final var board = Board.builder(CommandSender.class)
      .setId("test-scoreboard")
      .setScoreboardSender(new ScoreboardSenderImpl())
      .setDynamicObservers(Set.of(() -> observer2, () -> observer3))
      .setStaticObservers(Set.of(observer1))
      .addFilters(sender -> true)
      .setFilters(Set.of(sender -> true))
      .addLines(Line.<CommandSender>builder()
        .setLineNumber(0)
        .build())
      .setLines(Set.of(Line.<CommandSender>builder()
        .setLineNumber(0)
        .build()))
      .setTick(50L * 20L)
      .setStartDelay(50L * 20L)
      .addRemoveIf(sender -> false)
      .setRemoveIf(Set.of(sender -> false))
      .addRunBefore(sender -> sender.sendMessages("runs before."))
      .setRunBefore(Set.of(sender -> sender.sendMessages("runs before.")))
      .addRunAfter(sender -> sender.sendMessages("runs after."))
      .setRunAfter(Set.of(sender -> sender.sendMessages("runs after.")))
      .setAsyncScheduler(originalScheduler)
      .build();
    final var id = board.getId();
    final var dynamicObservers = board.getDynamicObservers();
    final var filters = board.getFilters();
    final var staticObservers = board.getStaticObservers();
    final var lines = board.getLines();
    final var tick = board.getTick();
    final var startDelay = board.getStartDelay();
    final var removeIf = board.getRemoveIf();
    final var runBefore = board.getRunBefore();
    final var runAfter = board.getRunAfter();
    final var scheduler = board.getAsyncScheduler();
    final var observerClass = board.getObserverClass();
    new Assertion<>(
      "Couldn't build the board correctly.",
      scheduler,
      new IsEqual<>(originalScheduler)
    ).affirm();
    new Assertion<>(
      "Couldn't build the board correctly.",
      startDelay,
      new IsEqual<>(1000L)
    ).affirm();
    new Assertion<>(
      "Couldn't build the board correctly.",
      tick,
      new IsEqual<>(1000L)
    ).affirm();
    new Assertion<>(
      "Couldn't build the board correctly.",
      runAfter,
      new HasSize(1)
    ).affirm();
    new Assertion<>(
      "Couldn't build the board correctly.",
      runBefore,
      new HasSize(1)
    ).affirm();
    new Assertion<>(
      "Couldn't build the board correctly.",
      removeIf,
      new HasSize(1)
    ).affirm();
    new Assertion<>(
      "Couldn't build the board correctly.",
      lines,
      new HasSize(1)
    ).affirm();
    new Assertion<>(
      "Couldn't build the board correctly.",
      id,
      new IsEqual<>("test-scoreboard")
    ).affirm();
    new Assertion<>(
      "Couldn't build the board correctly.",
      dynamicObservers,
      new HasSize(2)
    ).affirm();
    new Assertion<>(
      "Couldn't build the board correctly.",
      staticObservers,
      new HasSize(1)
    ).affirm();
    new Assertion<>(
      "Couldn't build the board correctly.",
      filters,
      new HasSize(1)
    ).affirm();
    new Assertion<>(
      "Couldn't build the board correctly.",
      observerClass,
      new IsEqual<>(CommandSender.class)
    ).affirm();
    new Assertion<>(
      "Couldn't build the board correctly.",
      Board.getBoardById("test-scoreboard").isPresent(),
      new IsTrue()
    ).affirm();
    board.start();
  }

  @Test
  void creatingBoardTwice() {
    Board.builder(CommandSender.class)
      .setId("test")
      .build();
    new Assertion<>(
      "Board with the same id created.",
      () -> Board.builder(CommandSender.class)
        .setId("test")
        .build(),
      new Throws<>(IllegalArgumentException.class)
    );
  }

  @ToString
  @EqualsAndHashCode
  @RequiredArgsConstructor
  private static final class CommandSender {

    @NotNull
    @Getter
    private final String name;

    void sendMessages(@NotNull final String message) {
      System.out.printf("%s:%s%n", this.name, message);
    }
  }

  private static final class ScoreboardSenderImpl implements ScoreboardSender<CommandSender> {

    @Override
    public void close() {
    }

    @Override
    public void send(@NotNull final Set<CommandSender> observers, @NotNull final Set<Line<CommandSender>> lines) {
    }
  }
}
