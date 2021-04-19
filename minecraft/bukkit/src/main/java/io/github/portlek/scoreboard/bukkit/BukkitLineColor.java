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

package io.github.portlek.scoreboard.bukkit;

import io.github.portlek.scoreboard.line.LineColor;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * a class that represents Bukkit's line colors.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class BukkitLineColor implements LineColor {

  /**
   * the color.
   */
  @NotNull
  private final ChatColor color;

  /**
   * creates a new line color instance.
   *
   * @return a newly created line color instance.
   */
  @NotNull
  public static LineColor create() {
    return new BukkitLineColor(ChatColor.RESET);
  }

  @NotNull
  @Override
  public String format(@NotNull final String text) {
    return ChatColor.translateAlternateColorCodes('&', text);
  }

  @Nullable
  @Override
  public LineColor getByChar(final char charAt) {
    final var newColor = ChatColor.getByChar(charAt);
    return newColor == null ? null : new BukkitLineColor(newColor);
  }

  @Override
  public char getColorChar() {
    return ChatColor.COLOR_CHAR;
  }

  @NotNull
  @Override
  public String toString() {
    return this.color.toString();
  }
}
