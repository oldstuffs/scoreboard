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

package io.github.portlek.scoreboard.line.lines;

import io.github.portlek.scoreboard.line.AnimatedLine;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * a class that represents framed lines.
 *
 * @param <O> type of the observers.
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class FramedLine<O> implements AnimatedLine<O> {

  /**
   * the frames.
   */
  @NotNull
  protected final List<String> frames;

  /**
   * the current frame.
   */
  @Getter
  @Setter
  protected int currentFrame = -1;

  /**
   * adds the frame.
   *
   * @param string the string to add.
   */
  public final void addFrame(@NotNull final String string) {
    this.frames.add(string);
  }

  /**
   * gets the frame.
   *
   * @param index the index to get.
   *
   * @return frame.
   */
  @NotNull
  public final String getFrame(final int index) {
    return this.frames.get(index);
  }

  /**
   * obtains the frame size.
   *
   * @return frame size.
   */
  public final int getTotalLength() {
    return this.frames.size();
  }

  /**
   * removes the frame.
   *
   * @param frame the frame to remove.
   */
  public final void removeFrame(@NotNull final String frame) {
    this.frames.remove(frame);
  }

  /**
   * sets the frame.
   *
   * @param index the index to set.
   * @param frame the frame to set.
   */
  public final void setFrame(final int index, @NotNull final String frame) {
    this.frames.set(index, frame);
  }

  @Nullable
  @Override
  public String getCurrent(@NotNull final O observer) {
    if (this.currentFrame <= -1) {
      return null;
    }
    return this.frames.get(this.currentFrame);
  }

  @NotNull
  @Override
  public String getNext(@NotNull final O observer) {
    this.currentFrame++;
    if (this.currentFrame >= this.frames.size()) {
      this.currentFrame = 0;
    }
    return this.frames.get(this.currentFrame);
  }

  @NotNull
  @Override
  public String getPrevious(@NotNull final O observer) {
    this.currentFrame--;
    if (this.currentFrame <= -1) {
      this.currentFrame = this.frames.size() - 1;
    }
    return this.frames.get(this.currentFrame);
  }
}
