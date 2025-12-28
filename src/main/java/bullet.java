import java.awt.*;

/**
 * Bullet fired by the player.
 *
 * Responsibilities:
 * - Moves in the direction the player is facing (via {@code speed}).
 * - Checks collision against enemies and map blocks.
 * - Removes itself from {@link canvas#activeBullets} when it hits something or leaves the play area.
 */
public class
bullet extends entity {
    public Point startPoint;
    public static Image bulletImage;

    public bullet(int x, int y) {
        super(bulletImage, x, y, 1, 0, bulletImage.getWidth(null));
    }

    /**
     * Advances bullet position and handles its collisions.
     * - Removes itself on collision or after travelling ~600 pixels.
     * - If it overlaps an enemy, applies damage via {@link Enemy#damage()}.
     */
    public void update() {
        bullet bulletCopy = copy(x + speed, y);
        if (!bulletCopy.intersect() && !bulletCopy.collidesEnemy()) {
            x += speed;
            if (travelledDistance() >= 600) canvas.activeBullets.remove(this);
        } else {
            canvas.activeBullets.remove(this);
        }
    }

    /**
     * Creates a bullet copy at a new position for predictive collision checks.
     * Only used for intersection testing.
     */
    public bullet copy(int newX, int newY) {
        bullet copy = new bullet(newX, newY);
        copy.speed = 0;
        copy.velocity = 0;
        copy.image = image;
        copy.state = state;
        return copy;
    }

    /**
     * @return Euclidean distance from the bullet's start point to its current position.
     */
    public double travelledDistance() {
        int dx = startPoint.x - x;
        int dy = startPoint.y - y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Checks collision against all active enemies.
     *
     * How:
     * - If a collision is detected, {@link Enemy#damage()} is called.
     *
     * @return true if the bullet overlaps at least one enemy.
     */
    public boolean collidesEnemy() {
        boolean isInside = false;
        for (Enemy e : canvas.enemies) {
            int x2 = x + image.getWidth(null);
            int y2 = y + image.getHeight(null);
            int eX2 = e.x + e.image.getWidth(null);
            int eY2 = e.y + e.image.getHeight(null);
            boolean widthIsPositive = Math.min(x2, eX2) > Math.max(x, e.x);
            boolean heightIsPositive = Math.min(y2, eY2) > Math.max(y, e.y);
            if (widthIsPositive && heightIsPositive) {
                isInside = true;
                e.damage();
            }
        }
        return isInside;
    }
}
