package CyborgPlatformer.app;

import CyborgPlatformer.controller.GameController;
import CyborgPlatformer.model.entities.Enemy;
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
 * - Rendering and input will be attached here (JavaFX).
 */
public final class CyborgPlatformerApp {

    private final World world;
    private final Player player;
    private final GameController controller;

    // Remember spawn so controller can reset cleanly
    private final double spawnX;
    private final double spawnY;

    private boolean debug = true;
    public CyborgPlatformerApp() {
        this.world = new World();

        InputStream mapStream = CyborgPlatformerApp.class.getResourceAsStream("/Maps.txt");

        TileLevel level = TileLevelLoader.load(mapStream);
        world.setLevel(level);

        // Enemy hitbox used for validation
        final double ENEMY_W = 50;
        final double ENEMY_H = 64;

        int idx = 0;
        for (EnemySpawn s : level.getEnemySpawns()) {
            double x = s.x();
            double y = s.y();

            if (idx == 3) {
                y = 224;
            }
            double adjustedY = adjustSpawnY(level, x, y, ENEMY_W, ENEMY_H);

            if (adjustedY != y) {
                System.out.println("Adjusted enemy spawn idx=" + idx
                        + " from y=" + y + " to y=" + adjustedY
                        + " (x=" + x + ", hp=" + s.hp() + ")");
            }

            world.addEntity(new Enemy(x, adjustedY, ENEMY_W, ENEMY_H, s.hp()));
            idx++;
        }

        this.player = new Player();
        player.setSize(20, 20);

        double[] spawn = pickSpawnOnFloor(level, player.getWidth(), player.getHeight());
        this.spawnX = spawn[0];
        this.spawnY = spawn[1];

        if (debug) {spawn[0] = 1000;}

        player.setPosition(spawn[0], spawn[1]);
        System.out.printf("Spawn x=%.1f y=%.1f%n", player.getX(), player.getY());
        this.controller = new GameController(world, player, spawnX, spawnY);
    }

    private static double[] pickSpawnOnFloor(TileLevel level, double playerW, double playerH) {
        var solids = level.getSolids();
        if (solids.isEmpty()) return new double[]{96, 96};

        // Search a left-side window so we start near the beginning of the level.
        double maxX = TileLevelLoader.TILE_SIZE * 60; // adjust if needed (60 tiles = 2880px)

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

            // Also require that immediately below spawn is solid (so it’s actually “floor”)
            boolean hasFloor = level.isSolidRect(spawnX, spawnY + 1, playerW, playerH);
            if (!hasFloor) continue;

            if (!found) {
                bestX = spawnX;
                bestY = spawnY;
                found = true;
                continue;
            }

            // Prefer the LOWEST floor (largest b.y). If tie, prefer smaller x.
            if (b.y() > (bestY + playerH) || (b.y() == (bestY + playerH) && b.x() < (bestX - 10))) {
                bestX = spawnX;
                bestY = spawnY;
            }
        }

        return new double[]{bestX, bestY};
    }

    private static double adjustSpawnY(TileLevel level, double x, double y, double w, double h) {
        // If it fits already, keep legacy value exactly
        if (!level.isSolidRect(x, y, w, h)) return y;

        // Nudge up by one tile at a time (bounded so we don't do anything wild)
        final int MAX_STEPS = 6; // up to 6 tiles upward
        for (int step = 1; step <= MAX_STEPS; step++) {
            double candY = y - step * TileLevelLoader.TILE_SIZE;
            if (!level.isSolidRect(x, candY, w, h)) {
                return candY;
            }
        }

        // If we can't find a free spot quickly, keep original and let logs show it
        System.out.println("WARNING: Could not find valid spawn for x=" + x + " y=" + y);
        return y;
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

    public double getSpawnX() {
        return spawnX;
    }

    public double getSpawnY() {
        return spawnY;
    }
}