package CyborgPlatformer.systems;

import CyborgPlatformer.model.entities.Entity;
import CyborgPlatformer.model.entities.Player;
import CyborgPlatformer.model.world.Level;

/**
 * Applies gravity, jumping, and vertical collision resolution.
 *
 * Mirrors legacy entity.gravity() behaviour.
 */
public class PhysicsSystem {

    private final Level level;

    public PhysicsSystem(Level level) {
        this.level = level;
    }

    /**
     * Apply gravity + vertical collision to an entity.
     * dt is treated as "ticks" for now (dt = 1.0 matches legacy).
     */
    public void applyGravity(Entity e, double dt) {

        double nextY = e.getY() + e.getVY() * dt;

        boolean collides = level != null &&
                level.isSolidRect(e.getX(), nextY, e.getWidth(), e.getHeight());

        if (!collides) {
            e.moveY(e.getVY() * dt);
            e.setVY(e.getVY() + e.getAY());
            e.setGrounded(false);
            return;
        }

        double vy = e.getVY();

        if (vy < 0) {
            // Upward collision: damp bounce
            e.setVY(-(vy / 4));
        } else {
            // Any downward collision → grounded
            if (vy > 1.5) {
                e.setVY(vy / 1.5);
            } else {
                e.setVY(0);
            }

            if (!e.isGrounded()) {
                e.setGrounded(true);
                if (e instanceof Player p) {
                    p.resetJumpCounter();
                }
            }
        }
    }

        /**
         * Apply legacy jump impulse.
         */
    public void jump(Entity e, int jumpCounter) {
        if (jumpCounter == 1) e.setVY(-6);
        else e.setVY(-8);
    }
}
