package CyborgPlatformer.model.world;

import CyborgPlatformer.controller.GameController;
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
    public GameController controller;


    // Level/collision backing store
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
    public Level getLevel() {return level; }

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

}
