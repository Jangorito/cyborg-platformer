package CyborgPlatformer.model.entities;

import CyborgPlatformer.model.world.World;

/**
 * Bullet fired by the player.
 *
 * Functionality:
 * - Moves horizontally each tick (direction via vx sign).
 * - Performs predictive collision check.
 * - If it overlaps an enemy, applies damage and despawns.
 * - If it hits solid level collision, despawns.
 * - Despawns after travelling ~600 pixels.
 *
 * Notes:
 * - No rendering, no asset references, no global collections.
 * - World owns lifecycle; Bullet marks itself dead and World removes it.
 */
public final class Bullet extends Entity {

    private final World world;

    // bullets remove after travellingDistance() >= 600
    private static final double MAX_RANGE_PX = 600.0;

    // "damage()" effect
    private static final int DAMAGE = 1;

    private final double startX;
    private final double startY;

    private final double width;
    private final double height;

    // alive flag
    private boolean alive = true;

    /**
     * @param world owning world (used for collision queries + enemy list)
     * @param x spawn x
     * @param y spawn y
     * @param vx horizontal velocity (sign = direction)
     * @param width bullet hitbox width
     * @param height bullet hitbox height
     */
    public Bullet(World world, double x, double y, double vx, double width, double height) {
        this.world = world;

        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = 0;

        this.startX = x;
        this.startY = y;

        this.width = width;
        this.height = height;
    }

    public boolean isAlive() {
        return alive;
    }

    public double getWidth() { return width; }
    public double getHeight() { return height; }

    /**
     * start point -> current.
     */
    public double travelledDistance() {
        double dx = x - startX;
        double dy = y - startY;
        return Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public void update(double dt) {
        if (!alive) return;

        // Predictive step without copy
        double nextX = x + vx * dt;
        double nextY = y + vy * dt;

        // Check collision with solid tiles
        if (world.isSolidRect(nextX, nextY, width, height)) {
            alive = false;
            return;
        }

        // Check collision with enemies and apply damage
        Enemy hit = world.findFirstEnemyOverlapping(nextX, nextY, width, height);
        if (hit != null) {
            hit.damage(DAMAGE);
            alive = false;
            world.onEnemyHitByBullet();
            return;
        }

        // update movement
        x = nextX;
        y = nextY;

        // Bullet range expiry
        if (travelledDistance() >= MAX_RANGE_PX) {
            alive = false;
        }
    }

    public double getVx() { return vx; }

}
