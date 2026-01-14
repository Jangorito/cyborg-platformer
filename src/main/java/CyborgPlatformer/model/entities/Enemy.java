package CyborgPlatformer.model.entities;

/**
 * Enemy model for Bullet collision + damage.
 * Still need: (AI, health, etc.).
 */
public class Enemy extends Entity implements Damageable{

    private int health = 3;

    private final double width;
    private final double height;

    public Enemy(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public double getWidth() { return width; }
    public double getHeight() { return height; }

    public void damage(int amount) {
        health -= amount;
        if (health < 0) health = 0;
    }

    public int getHealth() {
        return health;
    }
}
