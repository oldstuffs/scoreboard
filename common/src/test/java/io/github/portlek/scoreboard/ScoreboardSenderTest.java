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
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.IsTrue;

final class ScoreboardSenderTest {

  @Test
  void close() {
    final var closed = new AtomicBoolean();
    final var sender = new ScoreboardSender<>() {
      @Override
      public void close() {
        closed.set(true);
      }

      @Override
      public void send(@NotNull final Set<Object> observers, @NotNull final Map<Integer, Line<Object>> lines) {
      }
    };
    sender.close();
    new Assertion<>(
      "Couldn't close the scoreboard sender.",
      closed.get(),
      new IsTrue()
    ).affirm();
  }

  @Test
  void send() {
    final var sent = new AtomicBoolean();
    final var sender = new ScoreboardSender<>() {
      @Override
      public void close() {
      }

      @Override
      public void send(@NotNull final Set<Object> observers, @NotNull final Map<Integer, Line<Object>> lines) {
        sent.set(true);
      }
    };
    sender.send(Collections.emptySet(), Collections.emptyMap());
    new Assertion<>(
      "Couldn't send the scoreboard.",
      sent.get(),
      new IsTrue()
    ).affirm();
  }
}
