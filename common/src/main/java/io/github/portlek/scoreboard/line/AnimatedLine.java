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
import io.github.portlek.scoreboard.line.lines.HighlightedLine;
import io.github.portlek.scoreboard.line.lines.ScrolledLine;
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
   * creates a simple highlight animation with frames.
   *
   * @param context the context to create.
   * @param highlightFormat the highlight format to create.
   * @param normalFormat the normal format to create.
   * @param <O> type of the observers.
   *
   * @return a newly created a line which has highlight animation.
   */
  @NotNull
  static <O> AnimatedLine<O> highlighted(@NotNull final String context, @NotNull final String highlightFormat,
                                         @NotNull final String normalFormat) {
    return AnimatedLine.highlighted(context, highlightFormat, normalFormat, "", "");
  }

  /**
   * creates a simple highlight animation with frames.
   *
   * @param context the context to create.
   * @param highlightFormat the highlight format to create.
   * @param normalFormat the normal format to create.
   * @param update the update to create.
   * @param <O> type of the observers.
   *
   * @return a newly created a line which has highlight animation.
   */
  @NotNull
  static <O> AnimatedLine<O> highlighted(@NotNull final String context, @NotNull final String highlightFormat,
                                         @NotNull final String normalFormat, final boolean update) {
    return AnimatedLine.highlighted(context, highlightFormat, normalFormat, "", "", update);
  }

  /**
   * creates a simple highlight animation with frames.
   *
   * @param context the context to create.
   * @param highlightFormat the highlight format to create.
   * @param normalFormat the normal format to create.
   * @param prefix the prefix to create.
   * @param suffix the suffix to create.
   * @param <O> type of the observers.
   *
   * @return a newly created a line which has highlight animation.
   */
  @NotNull
  static <O> AnimatedLine<O> highlighted(@NotNull final String context, @NotNull final String highlightFormat,
                                         @NotNull final String normalFormat, @NotNull final String prefix,
                                         @NotNull final String suffix) {
    return AnimatedLine.highlighted(context, highlightFormat, normalFormat, prefix, suffix, false);
  }

  /**
   * creates a simple highlight animation with frames.
   *
   * @param context the context to create.
   * @param highlightFormat the highlight format to create.
   * @param normalFormat the normal format to create.
   * @param prefix the prefix to create.
   * @param suffix the suffix to create.
   * @param update the update to create.
   * @param <O> type of the observers.
   *
   * @return a newly created a line which has highlight animation.
   */
  @NotNull
  static <O> AnimatedLine<O> highlighted(@NotNull final String context, @NotNull final String highlightFormat,
                                         @NotNull final String normalFormat, @NotNull final String prefix,
                                         @NotNull final String suffix, final boolean update) {
    return new Highlighted<>(context, highlightFormat, normalFormat, prefix, suffix, update);
  }

  /**
   * creates a simple scroll animation.
   *
   * @param message the message.
   * @param spaceBetween the space between.
   * @param width the width.
   * @param color the color.
   * @param <O> type of the observers.
   *
   * @return a newly created a line which has scroll animation.
   */
  @NotNull
  static <O> AnimatedLine<O> scrolled(@NotNull final String message, final int spaceBetween, final int width,
                                      @NotNull final LineColor color) {
    return AnimatedLine.scrolled(message, spaceBetween, width, color, false);
  }

  /**
   * creates a simple scroll animation.
   *
   * @param message the message.
   * @param spaceBetween the space between.
   * @param width the width.
   * @param color the color.
   * @param update the update.
   * @param <O> type of the observers.
   *
   * @return a newly created a line which has scroll animation.
   */
  @NotNull
  static <O> AnimatedLine<O> scrolled(@NotNull final String message, final int spaceBetween, final int width,
                                      @NotNull final LineColor color, final boolean update) {
    return new Scrolled<>(message, spaceBetween, width, color, update);
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

  /**
   * a class that represents simple highlight animation lines.
   *
   * @param <O> type of the observers.
   */
  final class Highlighted<O> extends HighlightedLine<O> {

    /**
     * the update.
     */
    @Getter
    private final boolean update;

    /**
     * ctor.
     *
     * @param context the context.
     * @param highlightFormat the highlight format.
     * @param normalFormat the normal format.
     * @param prefix the prefix.
     * @param suffix the suffix.
     * @param update the update.
     */
    private Highlighted(@NotNull final String context, @NotNull final String highlightFormat,
                        @NotNull final String normalFormat, @NotNull final String prefix, @NotNull final String suffix,
                        final boolean update) {
      super(context, highlightFormat, normalFormat, prefix, suffix);
      this.update = update;
      this.generate();
    }
  }

  /**
   * a class that represents simple scrolled animation lines.
   *
   * @param <O> type of the observers.
   */
  final class Scrolled<O> extends ScrolledLine<O> {

    /**
     * the update.
     */
    @Getter
    private final boolean update;

    /**
     * ctor.
     *
     * @param message the message.
     * @param spaceBetween the space between.
     * @param width the width.
     * @param color the color.
     * @param update the update.
     */
    private Scrolled(@NotNull final String message, final int spaceBetween, final int width,
                     @NotNull final LineColor color, final boolean update) {
      super(message, spaceBetween, width, color);
      this.update = update;
      this.generate();
    }
  }
}
