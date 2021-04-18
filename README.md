[![idea](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

![master](https://github.com/portlek/scoreboard/workflows/build/badge.svg)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.portlek/scoreboard?label=version)](https://repo1.maven.org/maven2/io/github/portlek/scoreboard/)

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

## Example usage

```java
final class TestScoreboard {

  void sendScoreboard(@NotNull final Plugin plugin, @NotNull final List<Player> players) {
    final BukkitBoard board = BukkitBoard.create(plugin)
      // Adds observers.
      .addObservers(players)
      // Dynamic observer (Bukkit server's Online players).
      .addDynamicObserver(Bukkit::getOnlinePlayers)
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
    board.sendOnce();
  }
}
```