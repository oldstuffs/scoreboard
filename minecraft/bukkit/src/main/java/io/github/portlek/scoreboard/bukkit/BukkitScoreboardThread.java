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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * a class that represents Bukkit scoreboard thread..
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class BukkitScoreboardThread extends Thread {

  /**
   * the sender.
   */
  @NotNull
  private final BukkitScoreboardSender sender;

  /**
   * the tick.
   */
  private final long tick;

  @Override
  public void run() {
    while (true) {
      try {
        this.tick();
        //noinspection BusyWait
        Thread.sleep(this.tick * 50);
      } catch (final Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * runs every {@link #tick} times 50.
   */
  private void tick() {
    this.sender.getScoreboards().forEach(scoreboard -> {
      try {
        scoreboard.tick();
      } catch (final Exception e) {
        e.printStackTrace();
        throw new IllegalStateException(String.format("There was an error updating %s's scoreboard.",
          scoreboard.getUniqueId()));
      }
    });
  }
}