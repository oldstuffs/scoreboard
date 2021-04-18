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
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.HasSize;

final class BoardTest {

  @Test
  void createBoard() {
    final var observer = new CommandSender() {
    };
    final var board = Board.builder(CommandSender.class)
      .setId("test-scoreboard")
      .addDynamicObservers(() -> observer)
      .addStaticObservers(observer)
      .setDynamicObservers(Set.of(() -> observer))
      .setStaticObservers(Set.of(observer))
      .build();
    final var id = board.getId();
    final var dynamicObservers = board.getDynamicObservers();
    final var staticObservers = board.getStaticObservers();
    final var observerClass = board.getObserverClass();
    new Assertion<>(
      "Couldn't build the board correctly.",
      id,
      new IsEqual<>("test-scoreboard")
    ).affirm();
    new Assertion<>(
      "Couldn't build the board correctly.",
      dynamicObservers,
      new HasSize(1)
    ).affirm();
    new Assertion<>(
      "Couldn't build the board correctly.",
      staticObservers,
      new HasSize(1)
    ).affirm();
    new Assertion<>(
      "Couldn't build the board correctly.",
      observerClass,
      new IsEqual<>(CommandSender.class)
    ).affirm();
  }

  private interface CommandSender {

  }
}
