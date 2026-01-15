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

    private static final int MAX_HEALTH = 100;
    private int health = MAX_HEALTH;
    private int ammo = 10;
    private int jumpCounter = 0;
    private double invulnTimer = 0.0;
    private static final double INVULN_SECONDS = 0.35;

    private boolean alive = true;

    // Shooting logic
    private boolean justShot = false;
    private long lastShotMs = 0;
    private static final long SHOOT_COOLDOWN_MS = 500;

    // Spawn offsets based on v1
    private static final int SPAWN_Y_OFFSET = 10;
    private static final int SPAWN_X_OFFSET_RIGHT = 47;
    private static final int SPAWN_X_OFFSET_LEFT = -25;

    // Seconds-based speed (px/s)
    private static final double BULLET_SPEED_PX_PER_SEC = 600.0;

    // Movement speed (px/s) — tune later
    private static final double MOVE_SPEED_PX_PER_SEC = 200.0;

    public void moveLeft()  { vx = -MOVE_SPEED_PX_PER_SEC; }
    public void moveRight() { vx =  MOVE_SPEED_PX_PER_SEC; }
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

        double bulletVx = facingRight ? BULLET_SPEED_PX_PER_SEC : -BULLET_SPEED_PX_PER_SEC;

        // Bullet hitbox: legacy used image bounds; for now keep a small stable box.
        Bullet bullet = new Bullet(world, spawnX, spawnY, bulletVx, 12, 6);

        world.spawnBullet(bullet);
        return true;
    }

    public void damage(int amount) {
        if (invulnTimer > 0) return;

        health -= amount;
        if (health <= 0) {
            health = 0;
            alive = false;
        }

        invulnTimer = INVULN_SECONDS;
    }

    public void knockback(double vxImpulse, double vyImpulse) {
        this.vx = vxImpulse;
        this.vy = vyImpulse;
        this.grounded = false;
    }

    public boolean isInKnockback() {
        return invulnTimer > 0;
    }

    public void resetForRespawn() {
        this.alive = true;
        this.health = MAX_HEALTH;   // whatever you use
        this.invulnTimer = INVULN_SECONDS;     // optional
        this.justShot = false;      // if you track shooting cooldown
    }


    public int getJumpCounter() {
        return jumpCounter;
    }

    @Override
    public double getVY() {
        return super.getVY();
    }

    public boolean isAlive() { return alive; }
    public boolean isShootingVisualActive() {
        return justShot;
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

        if (invulnTimer > 0) {
            invulnTimer -= dt;
            if (invulnTimer < 0) invulnTimer = 0;
        }
    }
}
