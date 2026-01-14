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

    public double getX() { return x; }
    public double getY() { return y; }

    public void update(double dt) {
        x += vx * dt;
        y += vy * dt;
    }
}
