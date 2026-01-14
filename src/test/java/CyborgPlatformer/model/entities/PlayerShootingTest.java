package CyborgPlatformer.model.entities;

import CyborgPlatformer.model.world.Level;
import CyborgPlatformer.model.world.World;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerShootingTest {

    private static final Level NO_SOLIDS = (x, y, w, h) -> false;

    @ParameterizedTest(name = "shoot facingRight={0} spawns at xOffset={1}, vxPerTick={2}")
    @CsvSource({
            "true,  47,  10",
            "false, -25, -10"
    })
    void shoot_spawnsBulletWithLegacyOffsetsAndDirection(boolean facingRight, int xOffset, int vxPerTick) {
        World world = new World();
        world.setLevel(NO_SOLIDS);

        Player p = new Player();
        p.x = 100;
        p.y = 200;

        int ammoBefore = p.getAmmo();

        assertTrue(p.shoot(world, facingRight));
        assertEquals(ammoBefore - 1, p.getAmmo());

        Bullet b = world.getEntities().stream()
                .filter(e -> e instanceof Bullet)
                .map(e -> (Bullet) e)
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        "No Bullet spawned into World"));

        assertEquals(100 + xOffset, b.getX(), 0.0001);
        assertEquals(200 + 10, b.getY(), 0.0001); // yOffset fixed at +10 in legacy

        // Movement check: dt=1.0 tick should shift x by +/-10
        double xBefore = b.getX();
        world.update(1.0);
        assertEquals(xBefore + vxPerTick, b.getX(),
                0.0001);
    }

    @Test
    void shoot_onCooldown_doesNotFireSecondBulletImmediately() {
        World world = new World();
        world.setLevel(NO_SOLIDS);

        Player p = new Player();
        p.x = 0;
        p.y = 0;

        assertTrue(p.shoot(world, true));
        assertEquals(1, countBullets(world));

        assertFalse(p.shoot(world, true));
        assertEquals(1, countBullets(world));
    }

    @Test
    void shoot_afterCooldown_allowsAnotherShot() throws InterruptedException {
        World world = new World();
        world.setLevel(NO_SOLIDS);

        Player p = new Player();
        p.x = 0;
        p.y = 0;

        assertTrue(p.shoot(world, true));
        Thread.sleep(520);
        assertTrue(p.shoot(world, true));

        assertEquals(2, countBullets(world));
    }

    @Test
    void shoot_withNoAmmo_doesNotFire() throws InterruptedException {
        World world = new World();
        world.setLevel(NO_SOLIDS);

        Player p = new Player();
        p.x = 0;
        p.y = 0;

        // Drain ammo (10) respecting cooldown
        for (int i = 0; i < 10; i++) {
            assertTrue(p.shoot(world, true));
            Thread.sleep(520);
        }
        assertEquals(0, p.getAmmo());

        int bulletsBefore = countBullets(world);
        assertFalse(p.shoot(world, true));
        assertEquals(bulletsBefore, countBullets(world));
    }

    private int countBullets(World world) {
        return (int) world.getEntities().stream().filter(e ->
                e instanceof Bullet).count();
    }
}
