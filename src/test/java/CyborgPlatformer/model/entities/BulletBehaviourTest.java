package CyborgPlatformer.model.entities;

import CyborgPlatformer.model.world.Level;
import CyborgPlatformer.model.world.World;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class BulletBehaviourTest {

    @ParameterizedTest(name = "bullet despawns after range when vx={0}")
    @ValueSource(doubles = {10.0, -10.0})
    void bullet_despawnsAfterTravelling600pxOrMore(double vx) {
        World world = new World();
        world.setLevel((x, y, w, h) -> false);

        Bullet b = new Bullet(world, 0, 0, vx, 12, 6);
        world.spawnBullet(b);

        // 60 ticks at 10px/tick => 600px
        for (int i = 0; i < 59; i++) {
            world.update(1.0);
            assertTrue(b.isAlive());
        }

        world.update(1.0);
        assertFalse(b.isAlive());
    }

    @Test
    void bullet_despawnsOnSolidCollision_predictiveStep() {
        Level solidWallAt50 = (x, y, w, h) -> x >= 50;

        World world = new World();
        world.setLevel(solidWallAt50);

        Bullet b = new Bullet(world, 40, 0, 10, 12, 6);
        world.spawnBullet(b);

        world.update(1.0);

        assertFalse(b.isAlive());
        assertEquals(40, b.getX(), 0.0001, "Should not commit movement after predictive collision");
    }

    @Test
    void bullet_damagesEnemyAndDespawns() {
        World world = new World();
        world.setLevel((x, y, w, h) -> false);

        Enemy enemy = new Enemy(50, 0, 20, 20);
        world.addEntity(enemy);

        int healthBefore = enemy.getHealth();

        Bullet b = new Bullet(world, 40, 0, 10, 12, 6);
        world.spawnBullet(b);

        world.update(1.0);

        assertFalse(b.isAlive());
        assertEquals(healthBefore - 1, enemy.getHealth());
    }
}
