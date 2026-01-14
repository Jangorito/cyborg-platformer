package CyborgPlatformer.model.world;

import CyborgPlatformer.game.Updatable;
import CyborgPlatformer.model.entities.Bullet;
import CyborgPlatformer.model.entities.Enemy;
import CyborgPlatformer.model.entities.Entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    // Level/collision backing store (stub for now)
    private Level level;

    // allows for levels expansion
    public void setLevel(Level level) {
        this.level = level;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public void addEntity(Entity e) {
        entities.add(e);
        if (e instanceof Enemy enemy) {
            enemies.add(enemy);
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
            e.update(dt);
        }

        // Remove dead bullets (TODO: other entities with lifecycle)
        cleanupDeadEntities();
        rebuildEnemyIndex();
    }

    private void cleanupDeadEntities() {
        Iterator<Entity> it = entities.iterator();
        while (it.hasNext()) {
            Entity e = it.next();

            if (e instanceof Bullet b && !b.isAlive()) {
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

    // TODO: explain lines
    private boolean rectsOverlap(
            double ax, double ay, double aw, double ah,
            double bx, double by, double bw, double bh
    ) {
        return ax < bx + bw &&
                ax + aw > bx &&
                ay < by + bh &&
                ay + ah > by;
    }

    // test helper TODO
    public <T extends Entity> List<T> getEntitiesOfType(Class<T> type) {
        List<T> out = new ArrayList<>();
        for (Entity e : entities) {
            if (type.isInstance(e)) out.add(type.cast(e));
        }
        return out;
    }

}
