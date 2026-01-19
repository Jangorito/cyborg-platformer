package CyborgPlatformer.app;

import CyborgPlatformer.controller.GameController;
import CyborgPlatformer.model.entities.Enemy;
import CyborgPlatformer.config.LevelSettings;
import CyborgPlatformer.config.GameMode;
import CyborgPlatformer.model.entities.Player;
import CyborgPlatformer.model.world.EnemySpawn;
import CyborgPlatformer.model.world.TileLevel;
import CyborgPlatformer.model.world.TileLevelLoader;
import CyborgPlatformer.model.world.World;

import java.io.InputStream;

/**
 * Application bootstrap for CyborgPlatformer V2.
 *
 * Responsibilities:
 * - Constructs core model objects (World, Player, Enemy).
 * - Loads level collision data from resources.
 * - Wires model + systems into a GameController.
 *
 * Notes:
 * - This replaces legacy static setup in Game / MapBlocks.
 */
public final class CyborgPlatformerApp {

    private final World world;
    private final Player player;
    private final GameController controller;

    // Remember spawn so controller can reset cleanly
    private final double spawnX;
    private final double spawnY;

    private boolean debug = false;

    public CyborgPlatformerApp() {
        this(LevelSettings.defaultsFor(GameMode.MEDIUM));
    }

    public CyborgPlatformerApp(LevelSettings settings) {
        this.world = new World();

        InputStream mapStream = CyborgPlatformerApp.class.getResourceAsStream("/Maps.txt");

        TileLevel level = TileLevelLoader.load(mapStream);
        world.setLevel(level);

        // Game mode / level settings
        LevelSettings localSettings = (settings == null) ? LevelSettings.defaultsFor(GameMode.MEDIUM) : settings;
        // let systems read difficulty parameters
        world.setLevelSettings(localSettings);

        // Initialize spawn
        world.respawnEnemiesFromLevel();

        // creating Player
        this.player = new Player();
        player.setSize(20, 20);

        // ensuring spawn validity
        double[] spawn = pickSpawnOnFloor(level, player.getWidth(), player.getHeight());
        this.spawnX = spawn[0];
        this.spawnY = spawn[1];

        if (debug) {
            spawn[0] = 1000;
        }

        // Player spawn
        player.setPosition(spawn[0], spawn[1]);
        this.controller = new GameController(world, player, spawnX, spawnY);
    }

    // helper method to ensure entity spawn validity
    private static double[] pickSpawnOnFloor(TileLevel level, double playerW, double playerH) {
        var solids = level.getSolids();
        if (solids.isEmpty()) return new double[]{96, 96};

        double maxX = TileLevelLoader.TILE_SIZE * 60;

        double bestX = 96, bestY = 96;
        boolean found = false;

        for (var b : solids) {
            if (b.x() > maxX) continue;

            // Candidate spawn position: player sits ON TOP of this block.
            double spawnX = b.x() + 10;
            double spawnY = b.y() - playerH; // sit exactly on top

            // Check there is enough air space for the player at spawn
            boolean blockedAtSpawn = level.isSolidRect(spawnX, spawnY, playerW, playerH);
            if (blockedAtSpawn) continue;

            // immediately below spawn must be solid
            boolean hasFloor = level.isSolidRect(spawnX, spawnY + 1, playerW, playerH);
            if (!hasFloor) continue;

            if (!found) {
                bestX = spawnX;
                bestY = spawnY;
                found = true;
                continue;
            }

            // Prefer the LOWEST floor (largest b.y).
            if (b.y() > (bestY + playerH) || (b.y() == (bestY + playerH) && b.x() < (bestX - 10))) {
                bestX = spawnX;
                bestY = spawnY;
            }
        }

        return new double[]{bestX, bestY};
    }


    public GameController getController() {
        return controller;
    }


    public World getWorld() {
        world.setController(controller);
        return world;
    }

    public Player getPlayer() {
        return player;
    }
}