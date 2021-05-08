[![idea](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

![master](https://github.com/portlek/scoreboard/workflows/build/badge.svg)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.portlek/scoreboard-common?label=version)](https://repo1.maven.org/maven2/io/github/portlek/scoreboard-common/)

## How to use

```xml
<dependency>
  <groupId>io.github.portlek</groupId>
  <artifactId>scoreboard-bukkit</artifactId>
  <version>${version}</version>
</dependency>
```

```groovy
implementation("io.github.portlek:scoreboard-bukkit:${version}")
```

## Example of Usage

```java
import io.github.portlek.scoreboard.Board;
import io.github.portlek.scoreboard.BoardType;
import io.github.portlek.scoreboard.bukkit.BukkitScoreboard;
import io.github.portlek.scoreboard.line.AnimatedLine;
import io.github.portlek.scoreboard.line.Line;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

// A simple class to show how to create boards.
@RequiredArgsConstructor
public final class TestScoreboard {

  private final Player player = null;

  // The Bukkit scoreboard dependency.
  @NotNull
  private final BukkitScoreboard scoreboard;

  // Creates the board and send it to observers.
  public void createScoreboard() {
    // This observer class is like viewer of the scoreboard in Bukkit cases, It should be Player class.
    final var board = Board.newBuilder(Player.class)
      // This id is for Board#getBoardById("id") Board#getBoardById(ObserverClass.class "id")
      // getBoardById methods are static methods so, you can use it everywhere.
      .setId("test-scoreboard")
      // Sets the title line of the scoreboard.
      // You can write what you want as a Line instance.
      // You can also put animated lines in it.
      .setTitleLine(
        Line.dynamic(observer -> "Observer based title line"))
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
      // Sets the async scheduler which calculates and sends the scoreboard lines to the player.
      .setAsyncScheduler(Executors.newScheduledThreadPool(4))
      // Usage example for adding and setting the dynamic observer list.
      .setDynamicObserverList(() -> Bukkit.getOnlinePlayers())
      .setDynamicObserverList(Bukkit::getOnlinePlayers)
      .setDynamicObserverList(Set.of(() -> Bukkit.getOnlinePlayers(), () -> Bukkit.getOnlinePlayers()))
      .setDynamicObserverList(Set.of(Bukkit::getOnlinePlayers, Bukkit::getOnlinePlayers))
      .addDynamicObserverList(() -> Bukkit.getOnlinePlayers(), Bukkit::getOnlinePlayers)
      // Usage example for adding and setting the dynamic observers.
      // Dynamic observers are calculating every tick asynchronously.
      .setDynamicObservers(() -> this.player, () -> this.player)
      .setDynamicObservers(Set.of(() -> this.player, () -> this.player))
      .addDynamicObservers(() -> this.player, () -> this.player)
      // Usage example for adding and settings static observers.
      .addStaticObservers(this.player, this.player)
      .setStaticObservers(this.player, this.player)
      .setStaticObservers(Set.of(this.player, this.player))
      // Usage example for adding and setting filters.
      // Filters are basically if the predicate of each observer can't pass the filter,
      // don't send the scoreboard for him.
      .addFilters(
        observer -> observer.getName().startsWith("filt"),
        observer -> observer.getName().equals("filtered"))
      .setFilters(Set.of(
        observer -> observer.getName().startsWith("filt"),
        observer -> observer.getName().equals("filtered")))
      // Usage example for adding and setting lines.
      // A line represents each score's value on the scoreboard.
      .addLines(
        Line.immutable("Immutable line which won't calculate twice, because it's immutable."),
        Line.dynamic(observer -> "Observer based lines which will calculate every tick of scoreboard."),
        Line.merged(
          Line.immutable("Merged lines are basically contains lines and merges them into 1 line"),
          Line.dynamic(observer -> "Observer based line."),
          // You can also merge animated lines with other lines.
          AnimatedLine.framed("test", "tes", "te", "t", "t", "te", "tes", "test")),
        Line.merged(List.of(
          Line.immutable("Merged lines are basically contains lines and merges them into 1 line"),
          Line.dynamic(observer -> "Observer based line."),
          AnimatedLine.framed("test", "tes", "te", "t", "t", "te", "tes", "test"))))
      .setLines(List.of(
        Line.immutable("Immutable line which won't calculate twice, because it's immutable.")))
      // The remove if method is basically remove the observer from the mutable board so,
      // the scoreboard won't send to the observer anymore.
      // But the dynamic observers are calculating every time so the observer will be added
      // in the observer list again. This feature is for static observers which you want
      // to remove after a certain time or how you want.
      // In this case, remove the observer which has `willremove` name.
      .addRemoveIf(
        observer -> observer.getName().equals("willremove"))
      .setRemoveIf(Set.of(
        observer -> observer.getName().equals("willremove")))
      // Usage example of the run after.
      // This methods will run after scoreboard send.
      .addRunAfter(
        observer -> observer.sendMessage("Scoreboard sent."))
      .setRunAfter(Set.of(
        observer -> observer.sendMessage("Scoreboard sent.")))
      // Usage example of the run before.
      // This methods will run before scoreboard send.
      .addRunBefore(
        observer -> observer.sendMessage("Scoreboard is sending."))
      .setRunBefore(Set.of(
        observer -> observer.sendMessage("Scoreboard is sending.")))
      .build();
    // Send the board for once.
    board.send();
    // Start the scoreboard scheduler which waits for the start delay then runs send method every tick.
    board.start();
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
```
