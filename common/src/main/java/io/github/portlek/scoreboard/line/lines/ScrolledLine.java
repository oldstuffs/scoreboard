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

import io.github.portlek.scoreboard.line.LineColor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.jetbrains.annotations.NotNull;

/**
 * a class that represents scrolled lines.
 *
 * @param <O> type of the observers.
 */
public abstract class ScrolledLine<O> extends FramedLine<O> {

  /**
   * the list.
   */
  @NotNull
  private final List<String> list = new ArrayList<>();

  /**
   * the message.
   */
  @NotNull
  private final String message;

  /**
   * the position.
   */
  private final AtomicInteger position = new AtomicInteger();

  /**
   * the space between.
   */
  private final int spaceBetween;

  /**
   * the width.
   */
  private final int width;

  /**
   * the line color.
   */
  @NotNull
  private LineColor color;

  /**
   * ctor.
   *
   * @param message the message.
   * @param spaceBetween the space between.
   * @param width the width.
   * @param color the color.
   */
  protected ScrolledLine(@NotNull final String message, final int spaceBetween, final int width,
                         @NotNull final LineColor color) {
    super(new ArrayList<>());
    this.message = message;
    this.spaceBetween = spaceBetween;
    this.width = width;
    this.color = color;
  }

  @NotNull
  @Override
  public final String getNext(@NotNull final O observer) {
    var sb = this.generateNextBuilder();
    if (sb.charAt(sb.length() - 1) == this.color.getColorChar()) {
      sb.setCharAt(sb.length() - 1, ' ');
    }
    if (sb.charAt(0) == this.color.getColorChar()) {
      final var c = this.color.getByChar(sb.charAt(1));
      if (c != null) {
        this.color = c;
        sb = this.generateNextBuilder();
        if (sb.charAt(0) != ' ') {
          sb.setCharAt(0, ' ');
        }
      }
    }
    return this.color.toString() + sb;
  }

  /**
   * generates the frame list.
   */
  protected final void generate() {
    var tempWidth = this.width;
    var tempSpaceBetween = this.spaceBetween;
    var tempMessage = this.message;
    if (tempMessage.length() < tempWidth) {
      final var sb = new StringBuilder(tempMessage);
      while (sb.length() < tempWidth) {
        sb.append(" ");
      }
      tempMessage = sb.toString();
    }
    tempWidth -= 2;
    if (tempWidth < 1) {
      tempWidth = 1;
    }
    if (tempSpaceBetween < 0) {
      tempSpaceBetween = 0;
    }
    for (var i = 0; i < tempMessage.length() - tempWidth; i++) {
      this.list.add(tempMessage.substring(i, i + tempWidth));
    }
    final var space = new StringBuilder();
    for (var i = 0; i < tempSpaceBetween; ++i) {
      this.list.add(tempMessage.substring(tempMessage.length() - tempWidth + Math.min(i, tempWidth)) + space);
      if (space.length() < tempWidth) {
        space.append(" ");
      }
    }
    for (var i = 0; i < tempWidth - tempSpaceBetween; ++i) {
      this.list.add(tempMessage.substring(tempMessage.length() - tempWidth + tempSpaceBetween + i) + space + tempMessage.substring(0, i));
    }
    for (var i = 0; i < tempSpaceBetween; i++) {
      if (i > space.length()) {
        break;
      }
      this.list.add(space.substring(0, space.length() - i) + tempMessage.substring(0, tempWidth - Math.min(tempSpaceBetween, tempWidth) + i));
    }
  }

  /**
   * generated the next string builder.
   *
   * @return next string builder.
   */
  @NotNull
  private StringBuilder generateNextBuilder() {
    return new StringBuilder(this.list.get(this.position.getAndIncrement() % this.list.size()));
  }
}
