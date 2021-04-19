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

import io.github.portlek.scoreboard.Board;
import io.github.portlek.scoreboard.ScoreboardSender;
import io.github.portlek.scoreboard.line.Line;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Synchronized;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * a {@link Player} implementation of {@link ScoreboardSender}.
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class BukkitScoreboardSender implements ScoreboardSender<Player> {

  /**
   * the scoreboards.
   */
  private final Map<UUID, BukkitPlayerScoreboard> scoreboards = new ConcurrentHashMap<>();

  @Override
  public void close() {
    this.scoreboards.values()
      .forEach(BukkitPlayerScoreboard::close);
    this.scoreboards.clear();
  }

  @Override
  @Synchronized("scoreboards")
  public void send(@NotNull final Board<Player> board, @NotNull final Collection<Player> observers,
                   @NotNull final List<Line<Player>> lines) {
    observers.stream()
      .map(Entity::getUniqueId)
      .map(uniqueId -> this.scoreboards.computeIfAbsent(uniqueId, uuid ->
        new BukkitPlayerScoreboard(board, uuid).setup()))
      .forEach(scoreboard -> scoreboard.update(lines));
  }

  /**
   * obtains the scoreboards.
   *
   * @return scoreboards.
   */
  @NotNull
  @Synchronized("scoreboards")
  public Collection<BukkitPlayerScoreboard> getScoreboards() {
    return this.scoreboards.values();
  }

  /**
   * runs when the player quits from the game.
   *
   * @param player the player to quit.
   */
  @Synchronized("scoreboards")
  void onQuit(@NotNull final Player player) {
    Optional.ofNullable(this.scoreboards.remove(player.getUniqueId()))
      .ifPresent(scoreboard -> BukkitPlayerScoreboard.close(player));
  }
}
