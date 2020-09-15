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
// For Nukkit projects.
implementation("io.github.portlek:scoreboard-nukkit:${version}")
```
## Example usage
```java
final class TestScoreboard {

    void sendScoreboard(@NotNull Plugin plugin, @NotNull final List<Player> players) {
        Board.create(plugin)
            .addPlayers(players)
            .filter(player -> {
                // Checks before the sent.
                return player.getName().equals("Test");
            })
            .beforeSend(player -> {
                player.sendMessage("This message sent before the scoreboard sent!");
            })
            .afterSend(player -> {
                player.sendMessage("This message sent after the scoreboard sent!");
            })
            .sendType(SendType.forEachPlayer(player -> {
                // You can use PlaceholderAPI plugin here.
                return Arrays.asList("line 1", "line 2", player.getName());
            }))
            .sendType(SendType.forEachOnlinePlayer(online -> {
                // You can use PlaceholderAPI plugin here.
                return Arrays.asList("line 1", "line 2", player.getName());
            }))
            .sendType(SendType.lines(() -> {
                return Arrays.asList("line 1", "line 2", "line 3");
            }))
            .startDelay(0L)
            .tick(10L)
            //.sendOnce() Disable the task and send the scoreboard for each player just for once.
            .start();
    }

}
```