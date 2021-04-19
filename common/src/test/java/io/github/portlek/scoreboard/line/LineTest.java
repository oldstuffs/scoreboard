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

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;

final class LineTest {

  @Test
  void close() {
    Line.line(Function.identity())
      .close();
  }

  @Test
  void line() {
    final var printed = new AtomicReference<String>();
    final var line = Line.line(observer -> "observer-1");
    printed.set(line.apply("null"));
    new Assertion<>(
      "Couldn't build the line.",
      printed.get(),
      new IsEqual<>("observer-1")
    ).affirm();
  }

  @Test
  void simple() {
    final var printed = new AtomicReference<String>();
    final var line = Line.simple("observer-1");
    printed.set(line.apply("null"));
    new Assertion<>(
      "Couldn't build the line.",
      printed.get(),
      new IsEqual<>("observer-1")
    ).affirm();
  }
}
