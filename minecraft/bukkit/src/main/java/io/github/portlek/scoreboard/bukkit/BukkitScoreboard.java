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
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * a class that represents initializer for Bukkit's scoreboard system.
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class BukkitScoreboard implements Listener, AutoCloseable {

  /**
   * the plugin.
   */
  @NotNull
  @Getter
  private final Plugin plugin;

  /**
   * the thread.
   */
  @NotNull
  private final BukkitScoreboardThread thread;

  /**
   * initiate the scoreboard system.
   *
   * @param plugin the plugin to initiate.
   * @param tick the tick to initiate.
   *
   * @return a bukkit scoreboard sender instance to use.
   */
  @NotNull
  public static BukkitScoreboard create(@NotNull final Plugin plugin, final long tick) {
    return new BukkitScoreboard(plugin, new BukkitScoreboardThread(new BukkitScoreboardSender(), tick));
  }

  @Override
  public void close() {
    this.thread.interrupt();
    this.thread.getSender().close();
    HandlerList.unregisterAll(this);
  }

  /**
   * obtains the sender.
   *
   * @return sender.
   */
  @NotNull
  public BukkitScoreboardSender getSender() {
    return this.thread.getSender();
  }

  /**
   * runs when a player quits.
   *
   * @param event the event to handle.
   */
  @EventHandler
  public void handle(final PlayerQuitEvent event) {
    this.getSender().onQuit(event.getPlayer());
  }

  /**
   * registers the listener and starts the thread.
   */
  public void setup() {
    this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    this.thread.start();
  }
}
