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

import java.util.function.Function;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.jetbrains.annotations.NotNull;

/**
 * an interface to determine a line of scoreboards.
 *
 * @param <O> type of the observer.
 */
@FunctionalInterface
public interface Line<O> extends Function<@NotNull O, @NotNull String> {

  /**
   * creates a simple line instance.
   *
   * @param line the line to create.
   *
   * @return a newly created line instance.
   */
  @NotNull
  static <O> Line<O> line(@NotNull final Function<@NotNull O, @NotNull String> line) {
    return new Impl<>(line);
  }

  /**
   * a simple implementation of {@link Line}.
   *
   * @param <O> type of the observer.
   */
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  final class Impl<O> implements Line<O> {

    /**
     * the function.
     */
    @NotNull
    @Delegate
    private final Function<@NotNull O, @NotNull String> function;
  }
}
