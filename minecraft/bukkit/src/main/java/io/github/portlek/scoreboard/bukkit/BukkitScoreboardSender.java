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

import io.github.portlek.scoreboard.ScoreboardSender;
import io.github.portlek.scoreboard.line.Line;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * a {@link Player} implementation of {@link ScoreboardSender}.
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class BukkitScoreboardSender implements ScoreboardSender<Player> {

  /**
   * the plugin.
   */
  @NotNull
  private final Plugin plugin;

  /**
   * the scoreboards.
   */
  private final Map<UUID, PlayerScoreboard> scoreboards = new ConcurrentHashMap<>();

  @Override
  public void close() {
    this.scoreboards.values()
      .forEach(PlayerScoreboard::close);
    this.scoreboards.clear();
  }

  @Override
  @Synchronized("scoreboards")
  public void send(@NotNull final Collection<Player> observers, @NotNull final Map<Integer, Line<Player>> lines) {
    observers.stream()
      .map(Entity::getUniqueId)
      .map(uniqueId -> this.scoreboards.computeIfAbsent(uniqueId, uuid -> new PlayerScoreboard(this, uuid)))
      .forEach(scoreboard -> scoreboard.updateLines(lines));
  }

  /**
   * runs when the player quits from the game.
   *
   * @param uniqueId the unique id to quit.
   */
  @Synchronized("scoreboards")
  void onQuit(@NotNull final UUID uniqueId) {
    this.scoreboards.remove(uniqueId);
  }
}
