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

import io.github.portlek.scoreboard.line.lines.FramedLine;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * an interface to determine animated lines.
 *
 * @param <O> type of the observers.
 */
public interface AnimatedLine<O> extends Line<O> {

  /**
   * creates a simple animated line with frames.
   *
   * @param frames the frames to create.
   * @param <O> type of the observers.
   *
   * @return a newly created a line which has frame animation.
   */
  @NotNull
  static <O> AnimatedLine<O> framed(@NotNull final String... frames) {
    return AnimatedLine.framed(false, frames);
  }

  /**
   * creates a simple animated line with frames.
   *
   * @param frames the frames to create.
   * @param <O> type of the observers.
   *
   * @return a newly created a line which has frame animation.
   */
  @NotNull
  static <O> AnimatedLine<O> framed(@NotNull final List<String> frames) {
    return AnimatedLine.framed(frames, false);
  }

  /**
   * creates a simple animated line with frames.
   *
   * @param update the update to create.
   * @param frames the frames to create.
   * @param <O> type of the observers.
   *
   * @return a newly created a line which has frame animation.
   */
  @NotNull
  static <O> AnimatedLine<O> framed(final boolean update, @NotNull final String... frames) {
    return AnimatedLine.framed(List.of(frames), update);
  }

  /**
   * creates a simple animated line with frames.
   *
   * @param frames the frames to create.
   * @param update the update to create.
   * @param <O> type of the observers.
   *
   * @return a newly created a line which has frame animation.
   */
  @NotNull
  static <O> AnimatedLine<O> framed(@NotNull final List<String> frames, final boolean update) {
    return new Framed<>(frames, update);
  }

  /**
   * checks if the line should continue to the animation.
   *
   * @return {@code true} if the line should continue to the animation.
   *
   * @see #getCurrent(Object)
   */
  default boolean animate() {
    return true;
  }

  @Override
  @NotNull
  default String apply(@NotNull final O o) {
    if (!this.animate()) {
      return Objects.requireNonNullElse(this.getCurrent(o), "");
    }
    if (this.forward()) {
      return this.getNext(o);
    }
    return this.getPrevious(o);
  }

  /**
   * checks if the animation should show the next frame or previous frame.
   *
   * @return {@code true} if the animation should show the next frame.
   *
   * @see #getNext(Object)
   * @see #getPrevious(Object)
   */
  default boolean forward() {
    return true;
  }

  /**
   * obtains the current text.
   *
   * @param observer the observer to obtain.
   *
   * @return current text.
   */
  @Nullable
  String getCurrent(@NotNull O observer);

  /**
   * obtains the next text.
   *
   * @param observer the observer to obtain.
   *
   * @return next text.
   */
  @NotNull
  String getNext(@NotNull O observer);

  /**
   * obtains the previous text.
   *
   * @param observer the observer to obtain.
   *
   * @return previous text.
   */
  @NotNull
  String getPrevious(@NotNull O observer);

  /**
   * a class that represents simple framed animation lines.
   *
   * @param <O> type of the observers.
   */
  final class Framed<O> extends FramedLine<O> {

    /**
     * the update.
     */
    @Getter
    private final boolean update;

    /**
     * ctor.
     *
     * @param frames the frames.
     * @param update the update.
     */
    private Framed(@NotNull final List<String> frames, final boolean update) {
      super(new ArrayList<>(frames));
      this.update = update;
    }
  }
}
