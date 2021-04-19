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
import io.github.portlek.scoreboard.line.Line;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.Synchronized;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * a class that represents player scoreboards.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class BukkitPlayerScoreboard implements Closeable {

  /**
   * the board.
   */
  @NotNull
  private final Board<Player> board;

  /**
   * the entries.
   */
  @NotNull
  private final List<Entry> entries = new ArrayList<>();

  /**
   * the identifiers.
   */
  @NotNull
  @Getter
  private final List<String> identifiers = new ArrayList<>();

  /**
   * the lines.
   */
  @NotNull
  private final List<Line<Player>> lines;

  /**
   * the plugin.
   */
  @NotNull
  private final Plugin plugin;

  /**
   * the setup.
   */
  private final AtomicBoolean setup = new AtomicBoolean();

  /**
   * the unique id.
   */
  @NotNull
  @Getter
  private final UUID uniqueId;

  /**
   * closes the scoreboard.
   *
   * @param player the player to close.
   */
  static void close(@NotNull final Player player) {
    final var scoreboardManager = Bukkit.getScoreboardManager();
    if (scoreboardManager != null) {
      player.setScoreboard(scoreboardManager.getNewScoreboard());
    }
  }

  /**
   * creates and initiates a player scoreboard instance.
   *
   * @param board the board to create.
   * @param lines the lines to create.
   * @param plugin the plugin to create.
   * @param uniqueId the unique id to create.
   *
   * @return a newly created and initialized player scoreboard instance.
   */
  @NotNull
  static BukkitPlayerScoreboard create(@NotNull final Board<Player> board, @NotNull final List<Line<Player>> lines,
                                       @NotNull final Plugin plugin, @NotNull final UUID uniqueId) {
    final var scoreboard = new BukkitPlayerScoreboard(board, lines, plugin, uniqueId);
    scoreboard.setup();
    return scoreboard;
  }

  /**
   * gets a random chat color.
   *
   * @param position the position to get.
   *
   * @return random color.
   */
  @NotNull
  private static String getRandomChatColor(final int position) {
    return ChatColor.values()[position].toString();
  }

  @Override
  public void close() {
    final var player = Bukkit.getPlayer(this.uniqueId);
    if (player == null) {
      return;
    }
    BukkitPlayerScoreboard.close(player);
  }

  /**
   * ticks.
   */
  @Synchronized("lines")
  void tick() {
    if (!this.setup.get()) {
      return;
    }
    final var player = Bukkit.getPlayer(this.uniqueId);
    if (player == null) {
      return;
    }
    final var optionalScoreboard = this.getScoreboard();
    if (optionalScoreboard.isEmpty()) {
      return;
    }
    final var optionalObjective = this.getObjective();
    if (optionalObjective.isEmpty()) {
      return;
    }
    final var objective = optionalObjective.get();
    final var title = ChatColor.translateAlternateColorCodes('&', this.board.getTitleLine().apply(player));
    final var scoreboard = optionalScoreboard.get();
    if (!objective.getDisplayName().equals(title)) {
      objective.setDisplayName(title);
    }
    if (this.lines.isEmpty()) {
      this.entries.forEach(Entry::remove);
      this.entries.clear();
      if (player.getScoreboard() != scoreboard) {
        player.setScoreboard(scoreboard);
      }
      return;
    }
    final List<Line<Player>> newLines;
    if (this.lines.size() > 15) {
      newLines = this.lines.subList(0, 15);
    } else {
      newLines = new ArrayList<>(this.lines);
    }
    final var boardType = this.board.getType();
    if (!boardType.isDescending()) {
      Collections.reverse(newLines);
    }
    if (this.entries.size() > newLines.size()) {
      IntStream.range(newLines.size(), this.entries.size())
        .mapToObj(this::getEntry)
        .filter(Objects::nonNull)
        .forEach(Entry::remove);
    }
    var cache = boardType.getStartNumber();
    for (var index = 0; index < newLines.size(); index++) {
      var entry = this.getEntry(index);
      final var line = ChatColor.translateAlternateColorCodes('&', newLines.get(index).apply(player));
      if (entry == null) {
        entry = new Entry(this.generateIdentifier(index), this, line);
      } else {
        entry.setLine(line);
      }
      entry.setup();
      entry.send(boardType.isDescending() ? cache-- : cache++);
    }
    if (player.getScoreboard() != scoreboard) {
      player.setScoreboard(scoreboard);
    }
  }

  /**
   * updates {@link #lines}.
   *
   * @param lines the lines to update.
   */
  @Synchronized("lines")
  void update(@NotNull final List<Line<Player>> lines) {
    IntStream.range(0, lines.size())
      .forEach(index -> {
        final var line = lines.get(index);
        if (line.isUpdate()) {
          this.lines.set(index, line);
        }
      });
  }

  /**
   * generates an identifier.
   *
   * @param position the position to generate.
   *
   * @return a newly generated identifier.
   */
  @NotNull
  private String generateIdentifier(final int position) {
    var identifier = BukkitPlayerScoreboard.getRandomChatColor(position) + ChatColor.WHITE;
    while (this.identifiers.contains(identifier)) {
      identifier = identifier + BukkitPlayerScoreboard.getRandomChatColor(position) + ChatColor.WHITE;
    }
    if (identifier.length() > 16) {
      return this.generateIdentifier(position);
    }
    this.identifiers.add(identifier);
    return identifier;
  }

  /**
   * gets the entry at the position.
   *
   * @param position the position to get.
   *
   * @return entry at the position.
   */
  @Nullable
  private Entry getEntry(final int position) {
    return position >= this.entries.size() ? null : this.entries.get(position);
  }

  /**
   * obtains the objective.
   *
   * @return objective.
   */
  @NotNull
  private Optional<Objective> getObjective() {
    final var player = Bukkit.getPlayer(this.uniqueId);
    if (player == null) {
      return Optional.empty();
    }
    final var optionalScoreboard = this.getScoreboard();
    if (optionalScoreboard.isEmpty()) {
      return Optional.empty();
    }
    final var scoreboard = optionalScoreboard.get();
    if (scoreboard.getObjective("SBoard") == null) {
      final var objective = scoreboard.registerNewObjective("SBoard", "dummy",
        this.board.getTitleLine().apply(player));
      objective.setDisplaySlot(DisplaySlot.SIDEBAR);
      return Optional.of(objective);
    }
    return Optional.ofNullable(scoreboard.getObjective("SBoard"));
  }

  /**
   * obtains the scoreboard.
   *
   * @return scoreboard.
   */
  @NotNull
  private Optional<Scoreboard> getScoreboard() {
    final var player = Bukkit.getPlayer(this.uniqueId);
    if (player == null) {
      return Optional.empty();
    }
    final var scoreboardManager = Bukkit.getScoreboardManager();
    if (scoreboardManager == null) {
      return Optional.empty();
    }
    if (player.getScoreboard() != scoreboardManager.getMainScoreboard()) {
      return Optional.of(player.getScoreboard());
    }
    return Optional.of(scoreboardManager.getNewScoreboard());
  }

  /**
   * setups the player.
   *
   * @return {@code this} for builder chain.
   */
  @NotNull
  private BukkitPlayerScoreboard setup() {
    Bukkit.getScheduler().runTask(this.plugin, () -> {
      final var player = Bukkit.getPlayer(this.uniqueId);
      if (player != null) {
        this.getScoreboard().ifPresent(player::setScoreboard);
        this.getObjective();
      }
      this.setup.set(true);
    });
    return this;
  }

  /**
   * a class that represents entries.
   */
  @RequiredArgsConstructor
  private static final class Entry {

    /**
     * the identifier.
     */
    @NotNull
    private final String identifier;

    /**
     * the scoreboard.
     */
    @NotNull
    private final BukkitPlayerScoreboard scoreboard;

    /**
     * the line.
     */
    @NotNull
    @Setter
    private String line;

    /**
     * the team.
     */
    @Nullable
    private Team team;

    /**
     * removes the entry.
     */
    private void remove() {
      this.scoreboard.getIdentifiers().remove(this.identifier);
      this.scoreboard.getScoreboard().ifPresent(score ->
        score.resetScores(this.identifier));
    }

    /**
     * sends the entry.
     *
     * @param position the position to send.
     */
    @Synchronized("team")
    private void send(final int position) {
      if (this.team == null) {
        return;
      }
      if (this.line.length() > 16) {
        var prefix = this.line.substring(0, 16);
        String suffix;
        if (prefix.charAt(15) == ChatColor.COLOR_CHAR) {
          prefix = prefix.substring(0, 15);
          suffix = this.line.substring(15);
        } else if (prefix.charAt(14) == ChatColor.COLOR_CHAR) {
          prefix = prefix.substring(0, 14);
          suffix = this.line.substring(14);
        } else if (ChatColor.getLastColors(prefix).equalsIgnoreCase(ChatColor.getLastColors(this.identifier))) {
          suffix = this.line.substring(16);
        } else {
          suffix = ChatColor.getLastColors(prefix) + this.line.substring(16);
        }
        if (suffix.length() > 16) {
          suffix = suffix.substring(0, 16);
        }
        this.team.setPrefix(prefix);
        this.team.setSuffix(suffix);
      } else {
        this.team.setPrefix(this.line);
        this.team.setSuffix("");
      }
      this.scoreboard.getObjective()
        .map(objective -> objective.getScore(this.identifier))
        .ifPresent(score -> score.setScore(position));
    }

    /**
     * setups the entry.
     */
    private void setup() {
      this.scoreboard.getScoreboard().ifPresent(score -> {
        final var teamName = this.identifier.length() > 16
          ? this.identifier.substring(0, 16)
          : this.identifier;
        var team = score.getTeam(teamName);
        if (team == null) {
          team = score.registerNewTeam(teamName);
        }
        if (team.getEntries().isEmpty() || !team.getEntries().contains(this.identifier)) {
          team.addEntry(this.identifier);
        }
        if (!this.scoreboard.entries.contains(this)) {
          this.scoreboard.entries.add(this);
        }
        this.team = team;
      });
    }
  }
}
