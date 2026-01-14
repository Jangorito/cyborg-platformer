package CyborgPlatformer.legacy;

import java.awt.*;

/**
 * Base class for v1 game entities ({@link Player} and {@link Enemy}).
 *
 * Each CyborgPlatformer.legacy.entity contains:
 * - Position (x, y), velocity/acceleration, grounded state.
 * - Basic gravity integration.
 * - Collision checks against blocks stored in {@link MapBlocks}.
 * - Shared field animation & gameplay used by subclasses (health, ammo, animation state).
 *
 * v1 note:
 * - responsible for physics, collision, and game stats; v2 could split these into components/systems.
 */
public class entity {
    protected boolean isDamaged;
    protected double damagedTime;
    protected boolean isGrounded = false;
    protected int speed = 0;
    protected Image image;
    protected int x;
    protected int y;
    protected double velocity;
    protected double acceleration = 0.5;
    protected int health;
    protected int ammo;
    protected entitystate state;
    public int jumpCounter = 0;
    public int jumpX;
    public int jumpY;
    public double lastTime = System.currentTimeMillis();
    public int lastAnimation = 0;
    public int hitBox;

    public entity(Image image, int x, int y, int health, int ammo, int hitBox) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.health = health;
        this.ammo = ammo;
        this.hitBox = hitBox;
        this.state = new entitystate(true, "idle");
    }

    /**
     * Checks whether the current CyborgPlatformer.legacy.entity's bounding region intersects any solid block in {@link MapBlocks#map}.
     * (is the CyborgPlatformer.legacy.entity colliding with anything using {@code hitBox})
     * @return true if the CyborgPlatformer.legacy.entity overlaps any block; false otherwise.
     */
    public boolean intersect() {
        int x2 = x + hitBox;
        int y2 = y + image.getHeight(null);
        boolean isInside = false;
        for (int i = 0; i < MapBlocks.map.size(); i++) {
            MapBlocks block = MapBlocks.map.get(i);
            int blockX = block.x;
            int blockY = block.y;
            int blockX2 = block.x + block.image.getWidth(null);
            int blockY2 = block.y + block.image.getHeight(null);
            boolean widthIsPositive = Math.min(x2, blockX2) > Math.max(x, blockX);
            boolean heightIsPositive = Math.min(y2, blockY2) > Math.max(y, blockY);
            if (widthIsPositive && heightIsPositive) {
                isInside = true;
                break;
            }
        }
        return isInside;
    }

    /**
     * Applies a jump to the CyborgPlatformer.legacy.entity.
     *
     * How:
     * - Smaller value for first jump and a stronger value for second.
     * - Stores the jump position (jumpX/jumpY) for visual effects (e.g., cloud sprite).
     */

    public void jump() {
        if (jumpCounter == 1) velocity = -6;
        else velocity = -8;
        jumpX = x;
        jumpY = y;
    }

    /**
     * Creates a lightweight copy of this CyborgPlatformer.legacy.entity at a new position to test potential next position without moving the real CyborgPlatformer.legacy.entity.
     */
    public entity copy(int newX, int newY) {
        entity copy = new entity(image, newX, newY, health, ammo, hitBox);
        copy.image = image;
        return copy;
    }

    /**
     * Handles gravity and resolves vertical collisions against map blocks.
     *
     * How:
     * - If the next vertical position does not collide, move down/up by {@code velocity} and increase velocity by {@code acceleration}.
     * - If colliding with map blocks, adjusts y/velocity and sets isGrounded accordingly.
     */
    public void gravity() {
        entity entityCopy = copy(x, (int) (y + velocity));
        if (!entityCopy.intersect()) {
            y += velocity;
            velocity += acceleration;
            isGrounded = false;
        } else {
            if (velocity > 1.5) velocity /= 1.5;
            if (velocity < 0) velocity = -(velocity / 4);
            else isGrounded = true;
        }
    }
}

/**
 * Simple state container used for animation decisions (direction + named state string).
 * How:
 * - uses string-based states such as "idle", "running", "hurt", etc.
 */
class entitystate {
    protected boolean isFacingForward;
    protected String state;

    public entitystate(boolean isFacingForward, String state) {
        this.isFacingForward = isFacingForward;
        this.state = state;
    }
}