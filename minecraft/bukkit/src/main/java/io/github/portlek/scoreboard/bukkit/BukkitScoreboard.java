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
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * a class that represents initializer for Bukkit's scoreboard system.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class BukkitScoreboard implements Listener {

  /**
   * the sender.
   */
  @NotNull
  private final BukkitScoreboardSender sender;

  /**
   * initiate the scoreboard system.
   *
   * @param plugin the plugin to initiate.
   *
   * @return a bukkit scoreboard sender instance to use.
   */
  @NotNull
  public static BukkitScoreboardSender init(@NotNull final Plugin plugin) {
    final var sender = new BukkitScoreboardSender(plugin);
    plugin.getServer().getPluginManager().registerEvents(new BukkitScoreboard(sender), plugin);
    return sender;
  }

  /**
   * runs when a player quits.
   *
   * @param event the event to handle.
   */
  @EventHandler
  public void handle(final PlayerQuitEvent event) {
    this.sender.onQuit(event.getPlayer().getUniqueId());
  }
}
