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

import io.github.portlek.scoreboard.Board;
import io.github.portlek.scoreboard.BoardType;
import io.github.portlek.scoreboard.bukkit.BukkitScoreboard;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

// A simple class to show how to create boards.
@RequiredArgsConstructor
public final class TestScoreboard {

  // The Bukkit scoreboard dependency.
  @NotNull
  private final BukkitScoreboard scoreboard;

  // Creates the board and send it to observers.
  public void createScoreboard() {
    // This observer class is like viewer of the scoreboard in Bukkit cases, It should be Player class.
    Board.newBuilder(Player.class)
      // This id is for Board#getBoardById("id") Board#getBoardById(ObserverClass.class "id")
      // getBoardById methods are static methods so, you can use it everywhere.
      .setId("test-scoreboard")
      // This tick is different from the BukkitScoreboard.create(this, TickAsLong)
      // This is basically tick time period to send the board to the player.
      // Animations are calculating in terms of this tick too.
      // It's async so, don't worry about it.
      .setTick(5L)
      // Scoreboard sender is basically how you want to send your scoreboards to observers.
      // It's built in code, but you can also write your own scoreboard sender.
      .setScoreboardSender(this.scoreboard.getSender())
      // It's basically a start delay to wait for the sending scoreboard first time.
      // It's like runTaskTimer(plugin, () -> {}, startDelay, tick) in Bukkit.
      .setStartDelay(100L)
      // The board type is like how you want to calculate your scoreboard's score.
      // It's MODERN, which means high score is on the top, as default.
      .setType(BoardType.MODERN)
      .build();
  }

  // A standard bukkit plugin's main class.
  public static final class BukkitPlugin extends JavaPlugin {

    // This just creates an instance nothing happens here.
    public final BukkitScoreboard scoreboard = BukkitScoreboard.create(
      // The plugin main class's instance.
      this,
      // The tick time settings to calculate lines and set the scoreboard of the player.
      // It's async so, don't worry about it.
      1L);

    @Override
    public void onEnable() {
      // Setups the scoreboard, It does not matter where you setup it, just don't setup twice :D
      this.scoreboard.setup();
      // This is example code to show how you can use scoreboard.
      // It's called Dependency Injection.
      new TestScoreboard(this.scoreboard)
        .createScoreboard();
    }
  }
}
