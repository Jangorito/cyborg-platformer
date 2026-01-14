package CyborgPlatformer.model.entities;

import CyborgPlatformer.model.world.World;

/**
 * V2 Player model.
 *
 * Responsibilities:
 * - Stores gameplay state (health, ammo).
 * - Applies shooting rules (ammo + cooldown) and requests bullet spawn via World.
 *
 * Notes:
 * - Does not read keyboard directly.
 * - Controller decides WHEN to call shoot(...) and what facing direction is.
 */
public class Player extends Entity {

    private int health = 100;
    private int ammo = 10;
    private int jumpCounter = 0;

    // Shooting logic
    private boolean justShot = false;
    private long lastShotMs = 0;
    private static final long SHOOT_COOLDOWN_MS = 500;

    // Spawn offsets based on v1
    private static final int SPAWN_Y_OFFSET = 10;
    private static final int SPAWN_X_OFFSET_RIGHT = 47;
    private static final int SPAWN_X_OFFSET_LEFT = -25;

    // Bullet speed based on v1 (10 px per tick (if dt=1.0))
    private static final double BULLET_SPEED_PER_TICK = 10;

    public void moveLeft()  { vx = -100; }
    public void moveRight() { vx = 100; }
    public void stop()      { vx = 0; }

    public int getHealth() { return health; }
    public int getAmmo() { return ammo; }

    /** Call every tick to reset cooldown state. */
    private void updateShootCooldown() {
        if (justShot && (System.currentTimeMillis() - lastShotMs) > SHOOT_COOLDOWN_MS) {
            justShot = false;
        }
    }

    /**
     * Fires bullet if not on cooldown and ammo > 0.
     *
     * @param world owns the bullet
     * @param facingRight true if player is facing right
     * @return true if a bullet was fired
     */
    public boolean shoot(World world, boolean facingRight) {
        updateShootCooldown();

        if (justShot || ammo == 0) return false;

        ammo--;
        justShot = true;
        lastShotMs = System.currentTimeMillis();

        double spawnX = x + (facingRight ? SPAWN_X_OFFSET_RIGHT : SPAWN_X_OFFSET_LEFT);
        double spawnY = y + SPAWN_Y_OFFSET;

        double vx = facingRight ? BULLET_SPEED_PER_TICK : -BULLET_SPEED_PER_TICK;

        // Bullet hitbox: legacy used image bounds; for now keep a small stable box.
        Bullet bullet = new Bullet(world, spawnX, spawnY, vx, 12, 6);

        world.spawnBullet(bullet);
        return true;
    }

    public int getJumpCounter() {
        return jumpCounter;
    }

    public void resetJumpCounter() {
        jumpCounter = 0;
    }

    public void incrementJumpCounter() {
        jumpCounter++;
    }

    @Override
    public void update(double dt) {
        super.update(dt);
        updateShootCooldown();
    }
}
