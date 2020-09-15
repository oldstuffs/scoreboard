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
        final BukkitBoard board = BukkitBoard.create(this.plugin)
            .addObserver(this.players)
            .filter(observer -> "Test".equals(observer.get().getName()))
            .removeIf(observer -> "ShouldRemove".equals(observer.get().getName()))
            .runBefore(observer ->
                observer.get().sendMessage("This message sent before the scoreboard sent!"))
            .runAfter(observer ->
                observer.get().sendMessage("This message sent after the scoreboard sent!"))
            .startDelay(0L)
            .tick(10L);
        board.newLineBuilder()
            .staticLine(0, "Static Title")
            .staticLine(1, "Static Line!")
            .dynamicLine(2, () -> "Dynamic Line!")
            .observerLine(3, observer -> observer.get().getName() + " Line!");
        board.start();
        //board.sendOnce();
    }

}
```