package CyborgPlatformer.model.entities;

/**
 * Base model simulated world entities.
 *
 * Notes:
 * - Rendering-free.
 * - Movement integration is split:
 *   - PhysicsSystem moves Y (and can also resolve X collisions via World).
 *   - Default update moves X only (used for simple entities).
 */

public abstract class Entity {

    protected double x;
    protected double y;
    protected double vx;
    protected double vy;
    protected double width = 0;
    protected double height = 0;
    protected boolean grounded = false;

    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public double getX() { return x; }
    public double getY() { return y; }
    public boolean isGrounded() { return grounded; }
    public double getVY() { return vy; }
    public double getVX() { return vx; }

    public void setVY(double vy) { this.vy = vy; }
    public void setVX(double vx) { this.vx = vx; }
    public void setGrounded(boolean grounded) { this.grounded = grounded; }

    /** Default update: horizontal integration only (Y handled by PhysicsSystem). */
    public void update(double dt) {
        x += vx * dt;
    }

    public void setSize(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void moveX(double dx) {
        this.x += dx;
    }

    public void moveY(double dy) {
        this.y += dy;
    }
}
