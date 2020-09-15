[![idea](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)
[![rultor](https://www.rultor.com/b/yegor256/rultor)](https://www.rultor.com/p/portlek/scoreboard)

[![Build Status](https://travis-ci.com/portlek/scoreboard.svg?branch=master)](https://travis-ci.com/portlek/scoreboard)
![Maven Central](https://img.shields.io/maven-central/v/io.github.portlek/scoreboard-common?label=version)
## How to use
```xml
<!-- For Bukkit projects. -->
<dependency>
    <groupId>io.github.portlek</groupId>
    <artifactId>scoreboard-bukkit</artifactId>
    <version>${version}</version>
</dependency>
<!-- For Nukkit projects. -->
<dependency>
    <groupId>io.github.portlek</groupId>
    <artifactId>scoreboard-nukkit</artifactId>
    <version>${version}</version>
</dependency>
```
```groovy
// For Bukkit projects.
implementation("io.github.portlek:scoreboard-bukkit:${version}")
```
## Example usage
```java
final class TestScoreboard {

    void sendScoreboard(@NotNull Plugin plugin, @NotNull final List<Player> players) {
        BukkitBoard.create(plugin)
            // Adds the players that scoreboard shows up.
            .addObserver(players)
            // Runs before the scoreboard sent for each player. The players list depend on the `sendType`
            // If returns false, player can get the scoreboard for the currency tick.
            .filter(player -> {
                return player.getName().equals("Test");
            })
            // Runs before the scoreboard sent for each player. The players list depend on the `sendType`
            // Removes the player from the list that contains all listed players.
            .removeIf(player -> {
                return player.getName().equals("ShouldRemove");
            })
            .runBefore(player -> {
                player.sendMessage("This message sent before the scoreboard sent!");
            })
            .runAfter(player -> {
                player.sendMessage("This message sent after the scoreboard sent!");
            })
            // Sends the first scoreboard after this value. (20 = 1 second)
            .startDelay(0L)
            // Sends the scoreboards with this period. (20 = 1 second)
            .tick(10L)
            .newLineBuilder()
            .staticLine()
            .back()
            //.sendOnce() Disable the task and send the scoreboard for each player just for once.
            .start();
    }

}
```