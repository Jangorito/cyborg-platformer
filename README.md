# Cyborg Platformer CyborgPlatformer.legacy.Game

This is a zombie shooter platformer demo game created as a college project in my first year. Built entirely from scratch using **Java Swing** - no external libraries, game engines, or external assistance involved. All physics, mechanics, and features were hand-coded based on intuition when I was starting to code, so please don’t judge too harshly!

In the game, you navigate a level, avoiding obstacles and zombies and tracking your score. It features:

- **Physics-Based Movement**: Simple gravity and collision detection mechanics made by hand.
- **Zombie Shooter**: Engage with enemies while platforming through the levels.
- **HUD**: Displays health, score, attempts, and a timer.
- **Dynamic Level Loading**: Levels are loaded from a text file, where each character represents a different platform or object type.

All sprites and images are from [CraftPix](https://craftpix.net/freebies/). Big thanks to them for providing these awesome assets for free!

To play, ensure all required image files are organized in the `src` directory as per the setup instructions, and launch the game by running `CyborgPlatformer.legacy.CyborgPlatform.java`.

# COMP2013 Context

This repository is used for the COMP2013 Coursework. Work is completed across: 
 - V1 - maintenance 
 - V2 - refactoring
 - V3 - extension

Development is carried out on `dev` and per-task branches.

## How to Run

### IntelliJ IDEA (recommended)
1. Open the repository folder in IntelliJ. 
2. When prompted, import the project as a **Maven project**. 
3. Ensure Project SDK is set to **Java 17**. 
4. Locate the main class: `CyborgPlatformer.legacy.CyborgPlatform` (in src folder).
5. Right-click the class and select **Run 'CyborgPlatformer.legacy.CyborgPlatform'**.

### Command Line (Maven)
From the repository root directory:

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass=CyborgPlatformer.legacy.CyborgPlatform
```

> Note: If the Maven exec plugin is not configured, the project can be run directly
> from IntelliJ by running the `CyborgPlatformer.legacy.CyborgPlatform` main class.

### Controls and objective

## How to Play

**Goal:** Reach the end of the level while avoiding hazards and zombies. The **higher** the score the better!

**Controls (currently):**
- Move Left: A
- Move Right: D
- Jump / Double-Jump: W (press again during initial jump animation)
- Shoot: SPACE
- Restart: ESC
- Pause: N/A

> Note: The in-game instruction overlay does not match these controls yet (tracked as a Task 2 fix).

## Project Structure

- `src/` – Java source files
- `src/<resources>` – sprite/image assets
- `gifs/` – gameplay preview GIFs
- `docs/` (or root mark down files) – coursework documentation

## Documentation

- [Changelog](Changelog.md)
- [Software Design](SoftwareDesign.md)
- [Testing](Testing.md)

## Gameplay Preview

![Gameplay GIF](gifs/StartGif.gif)
![Gameplay GIF2](gifs/EnemyExampleGif.gif)
