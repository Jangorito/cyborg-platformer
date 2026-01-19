package CyborgPlatformer.model.world;

import CyborgPlatformer.controller.GameController;
import CyborgPlatformer.config.LevelSettings;
import CyborgPlatformer.game.Updatable;
import CyborgPlatformer.model.entities.Bullet;
import CyborgPlatformer.model.entities.Enemy;
import CyborgPlatformer.model.entities.Entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import CyborgPlatformer.model.world.TileLevelLoader;

/**
 * Headless simulation container for gameplay state.
 *
 * Responsibilities:
 * - Owns entities + their lifecycle
 * - Advances simulation ticks
 * - Provides collision queries needed by entities (e.g., Bullet)
 *
 * Not:
 * - Rendering
 * - Input device handling
 * - Asset loading
 */
public class World implements Updatable {

    private final List<Entity> entities = new ArrayList<>();
    private final List<Enemy> enemies = new ArrayList<>();
    public GameController controller;


    // Level/collision backing store
    private Level level;

    // Current level difficulty/settings
    private LevelSettings levelSettings = LevelSettings.medium();
    // Spawn scheduling
    private List<EnemySpawn> spawnList = new ArrayList<>();
    private int spawnIndex = 0;
    private double spawnTimer = 0.0;
    private static final double BASE_SPAWN_INTERVAL_S = 2.0; // seconds between spawns (multiplied)

    // allows for levels expansion
    public void setLevel(Level level) {
        this.level = level;
        // If the level provides spawn info, initialize spawn list for scheduling
        if (level instanceof TileLevel tl) {
            this.spawnList = new ArrayList<>(tl.getEnemySpawns());
            // default: assume initial entities will be spawned via respawnEnemiesFromLevel()
            this.spawnIndex = 0;
            this.spawnTimer = 0.0;
        } else {
            this.spawnList = new ArrayList<>();
            this.spawnIndex = 0;
            this.spawnTimer = 0.0;
        }
    }

    public List<Entity> getEntities() {
        return entities;
    }
    public List<Enemy> getEnemies() {
        return enemies;
    }
    public Level getLevel() {return level; }

    public LevelSettings getLevelSettings() { return levelSettings; }

    public void setLevelSettings(LevelSettings settings) { this.levelSettings = (settings == null) ? LevelSettings.medium() : settings; }

    public void addEntity(Entity e) {
        // Enforce a spawn cap based on current level settings for enemies.
        if (e instanceof Enemy) {
            if (enemies.size() >= levelSettings.getMaxEnemies()) {
                // Skip adding; spawn cap reached.
                return;
            }
        }

        entities.add(e);
        if (e instanceof Enemy enemy) {
            enemies.add(enemy);
            // newly spawned enemies start active so they immediately behave.
            try {
                if (controller != null && controller.areEnemiesAwake()) {
                    enemy.awaken();
                }
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * World owns bullets
     */
    public void spawnBullet(Bullet bullet) {
        addEntity(bullet);
    }

    @Override
    public void update(double dt) {
        for (Entity e : entities) {
            double prevX = e.getX();
            double prevY = e.getY();

            e.update(dt);

            // Horizontal collision resolution for non-bullets
            // (Bullets already do their own predictive collision checks)
            if (!(e instanceof Bullet) && level != null) {
                if (level.isSolidRect(e.getX(), e.getY(), e.getWidth(), e.getHeight())) {
                    // Revert X; keep Y (PhysicsSystem owns Y)
                    e.setPosition(prevX, prevY);
                    e.setVX(0);
                }
            }
        }

        cleanupDeadEntities();
        rebuildEnemyIndex();

        // Spawn scheduling: attempt to spawn next enemy if under cap
        if (!spawnList.isEmpty() && spawnIndex < spawnList.size()) {
            spawnTimer -= dt;
            double interval = Math.max(0.05, BASE_SPAWN_INTERVAL_S * levelSettings.getSpawnIntervalMultiplier());
            while (spawnIndex < spawnList.size() && spawnTimer <= 0) {
                // respect maxEnemies
                if (enemies.size() >= levelSettings.getMaxEnemies()) break;

                EnemySpawn s = spawnList.get(spawnIndex);
                double x = s.x();
                double y = s.y();
                double adjustedY = adjustSpawnYIfTileLevel(level, x, y, Enemy.DEFAULT_WIDTH, Enemy.DEFAULT_HEIGHT);
                addEntity(new CyborgPlatformer.model.entities.Enemy(x, adjustedY, Enemy.DEFAULT_WIDTH, Enemy.DEFAULT_HEIGHT, s.hp(), levelSettings));
                spawnIndex++;
                spawnTimer += interval;
                // small safeguard to avoid tight loop
                if (spawnTimer > 10.0) break;
            }
        }
    }

    private void cleanupDeadEntities() {
        Iterator<Entity> it = entities.iterator();
        while (it.hasNext()) {
            Entity e = it.next();

            if (e instanceof Bullet b && !b.isAlive()) {
                it.remove();
                continue;
            }

            if (e instanceof CyborgPlatformer.model.entities.Enemy enemy && !enemy.isAlive()) {
                it.remove();
                continue;
            }


            // TODO: if Entity has isAlive()
        }

    }

    /**
     * Update entities list
     */
    private void rebuildEnemyIndex() {
        enemies.clear();
        for (Entity e : entities) {
            if (e instanceof Enemy enemy) enemies.add(enemy);
        }
    }

    /**
     * Solid-collision query used by Bullet.
     * For now, delegates to Level if present; otherwise "no solids".
     */
    public boolean isSolidRect(double x, double y, double w, double h) {
        if (level == null) return false;
        return level.isSolidRect(x, y, w, h);
    } // TODO

    /**
     * enemy overlap query used by Bullet.
     * Returns the first enemy that overlaps the given rect, else null.
     */
    public Enemy findFirstEnemyOverlapping(double x, double y, double w, double h) {
        for (Enemy enemy : enemies) {
            if (rectsOverlap(x, y, w, h, enemy.getX(), enemy.getY(), enemy.getWidth(), enemy.getHeight())) {
                return enemy;
            }
        }
        return null;
    }

    private boolean rectsOverlap(
            double ax, double ay, double aw, double ah,
            double bx, double by, double bw, double bh
    ) {
        return ax < bx + bw &&
                ax + aw > bx &&
                ay < by + bh &&
                ay + ah > by;
    }

    public <T extends Entity> List<T> getEntitiesOfType(Class<T> type) {
        List<T> out = new ArrayList<>();
        for (Entity e : entities) {
            if (type.isInstance(e)) out.add(type.cast(e));
        }
        return out;
    }

    public void onEnemyHitByBullet() {
        this.controller.incrementAmmo();
    }

    public void setController(GameController control){
        this.controller = control;
    }

    /**
     * Remove all existing enemies and recreate them from the current TileLevel's spawn list.
     * Uses the world's `levelSettings` when constructing enemies.
     */
    public void respawnEnemiesFromLevel() {
        if (!(level instanceof TileLevel)) return;
        TileLevel tl = (TileLevel) level;

        // Clear existing enemies from the entity list
        entities.removeIf(e -> e instanceof Enemy);
        enemies.clear();
        // Initialize spawn scheduling so enemies are respawned according to interval
        this.spawnList = new ArrayList<>(tl.getEnemySpawns());
        this.spawnIndex = 0;
        this.spawnTimer = 0.0;
    }

    /**
     * Remove all bullets from the world (used when player dies / respawns).
     */
    public void clearBullets() {
        entities.removeIf(e -> e instanceof Bullet);
    }
    private static double adjustSpawnYIfTileLevel(Level level, double x, double y, double w, double h) {
        if (!(level instanceof TileLevel tl)) return y;
        if (!tl.isSolidRect(x, y, w, h)) return y;
        final int MAX_STEPS = 6;
        for (int step = 1; step <= MAX_STEPS; step++) {
            double candY = y - step * TileLevelLoader.TILE_SIZE;
            if (!tl.isSolidRect(x, candY, w, h)) return candY;
        }
        return y;
    }

}
