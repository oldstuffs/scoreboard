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

package io.github.portlek.scoreboard.line;

import io.github.portlek.scoreboard.Board;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hamcrest.core.IsEqual;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;

final class LineTest {

  @Test
  void line() {
    final var observer1 = "observer-1";
    final var observer2 = "observer-2";
    final var observer3 = "observer-3";
    final var board = Board.newBuilder(String.class)
      .setId("test-board")
      .addLines(Line.line(Function.identity()))
      .build();
    final var keys = board.getLines().keySet();
    final var forObserver1 = board.getLines().values().stream()
      .map(line -> line.apply(observer1))
      .collect(Collectors.toList());
    final var forObserver2 = board.getLines().values().stream()
      .map(line -> line.apply(observer2))
      .collect(Collectors.toList());
    final var forObserver3 = board.getLines().values().stream()
      .map(line -> line.apply(observer3))
      .collect(Collectors.toList());
    new Assertion<>(
      "Couldn't create the line correctly.",
      keys,
      new IsEqual<>(Set.of(0))
    ).affirm();
    new Assertion<>(
      "Couldn't create the line correctly.",
      forObserver1,
      new IsEqual<>(List.of("observer-1"))
    ).affirm();
    new Assertion<>(
      "Couldn't create the line correctly.",
      forObserver2,
      new IsEqual<>(List.of("observer-2"))
    ).affirm();
    new Assertion<>(
      "Couldn't create the line correctly.",
      forObserver3,
      new IsEqual<>(List.of("observer-3"))
    ).affirm();
  }
}
