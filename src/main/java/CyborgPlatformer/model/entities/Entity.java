package CyborgPlatformer.model.entities;

/**
 * Base model for V2 simulated world entities.
 *
 * Responsibilities:
 * - Stores core physical states: position and velocity.
 * - {@link #update(double)} updates x/y using vx/vy.
 *
 * Notes:
 * - Does not contain images, sprites or UI references.
 * - Does not perform collision detection.
 * - Does not handle player input.
 */

public abstract class Entity {

    protected double x;
    protected double y;
    protected double vx;
    protected double vy;
    protected double width = 0;
    protected double height = 0;
    protected boolean grounded = false;
    protected double ay = 0.5; // legacy acceleration default

    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public double getX() { return x; }
    public double getY() { return y; }
    public boolean isGrounded() { return grounded; }
    public double getVY() { return vy; }
    public double getAY() { return ay; }   // or getAccelerationY()
    public void setVY(double vy) { this.vy = vy; }
    public void setGrounded(boolean grounded) { this.grounded = grounded; }

    public void update(double dt) {
        x += vx * dt;
        y += vy * dt;
    }


    public void moveY(double dy) {
        this.y += dy;
    }

}
