package CyborgPlatformer.systems;

import CyborgPlatformer.model.entities.Entity;
import CyborgPlatformer.model.entities.Player;
import CyborgPlatformer.model.world.Level;

/**
 * Seconds-based vertical physics with collision.
 *
 * Key features:
 * - Gravity in px/s^2
 * - Jump impulse in px/s
 * - Vertical sub-stepping prevents tunneling through tiles at high vy
 */
public class PhysicsSystem {

    private final Level level;

    private static final double GRAVITY = 2000.0;   // px/s^2
    private static final double JUMP_V1 = -880.0;   // px/s (tune)
    private static final double JUMP_V2 = -800.0;   // px/s (tune)

    // Max vertical movement per substep (px). Prevents tunneling.
    private static final double MAX_STEP = 4.0;

    // Ground probe epsilon (px)
    private static final double GROUND_EPS = 0.5;

    public PhysicsSystem(Level level) {
        this.level = level;
    }

    public void applyGravity(Entity e, double dt) {
        if (level == null) {
            // No geometry: free fall
            e.moveY(e.getVY() * dt);
            e.setVY(e.getVY() + GRAVITY * dt);
            e.setGrounded(false);
            return;
        }

        // Ground probe (stops flicker)
        boolean onGround = level.isSolidRect(e.getX(), e.getY() + GROUND_EPS, e.getWidth(), e.getHeight());
        if (onGround && e.getVY() >= 0) {
            e.setVY(0);
            e.setGrounded(true);
            if (e instanceof Player p) p.resetJumpCounter();
            return;
        }

        // Desired vertical movement this frame
        double dy = e.getVY() * dt;

        // Sub-step vertical movement to avoid tunneling
        double remaining = dy;
        boolean collided = false;

        while (Math.abs(remaining) > 0) {
            double step = clamp(remaining, -MAX_STEP, MAX_STEP);

            double nextY = e.getY() + step;
            boolean collides = level.isSolidRect(e.getX(), nextY, e.getWidth(), e.getHeight());

            if (collides) {
                collided = true;

                // Stop vertical motion on collision
                e.setVY(0);

                if (step > 0) {
                    // Falling onto ground
                    e.setGrounded(true);
                    if (e instanceof Player p) p.resetJumpCounter();
                }
                break;
            }

            e.moveY(step);
            remaining -= step;
        }

        if (!collided) {
            e.setGrounded(false);
        }

        // Integrate gravity AFTER moving (semi-implicit)
        e.setVY(e.getVY() + GRAVITY * dt);
    }

    public void jump(Entity e, int jumpCounter) {
        e.setVY(jumpCounter == 0 ? JUMP_V1 : JUMP_V2);
    }

    private static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }
}
