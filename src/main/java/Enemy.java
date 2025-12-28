import java.awt.*;

/**
 * Basic Zombie entity.
 *
 * Responsibilities:
 * - chase: zombie moves towards player when within range; increases speed when close.
 * - Attempts jumps depending on closing distance or if a player is above.
 * - Damages player on collision.
 * - Updates & plays state-based animations (idle/walk/run/hurt).
 *
 * v1 note:
 * - Behaviour is embedded inside the entity. v2 could move AI decisions to a dedicated system.
 */
public class Enemy extends entity {
    public Enemy(int x, int y, int health) {
        super(idleSprites[0], x, y, 2, 0, 30);
    }

    public boolean isRunning;
    public boolean isFacingForwards;
    public static Image[] idleSprites;
    public static Image[] walkingSprites;
    public static Image[] runningSprites;
    public static Image hurtSprite;

    /**
     * AI behaviour (per-frame update for enemy):
     *
     * - If player is within an activation radius and on roughly similar vertical level,
     *   move towards player (walk/run).
     * - If grounded and conditions met, jump to navigate terrain / reach player.
     * - Damages player on collision
     * - Handles death
     */
    public void doBehavior() {
        Player p = canvas.player;
        double distanceFromPlayer = distanceFromPlayer(p);
        gravity();


        double maxSpeed = 1;
        entity entityCopy = copy(x + speed, y);
        // if player is close and not far above
        if (distanceFromPlayer <= 400 && y - p.y < 100) {
            if (!entityCopy.intersect()) {
                x += speed;
                if (distanceFromPlayer <= 200) {
                    maxSpeed = 3;
                    isRunning = true;
                }
                if (p.x < x) {
                    if (speed > -maxSpeed) speed -= 1;
                    isFacingForwards = false;
                }
                if (p.x > x) {
                    if (speed < maxSpeed) speed += 1;
                    isFacingForwards = true;
                } else if (Math.abs(x - p.x) < 20) speed = 0;
            }

            // Jump Behavior
            if (isGrounded) jumpCounter = 0;
            if (((!isGrounded) || (distanceFromPlayer <= 200 && Math.abs(x - p.x) > 40)) && jumpCounter < 1 && p.y < y) {
                jump();
                jumpCounter++;
            }


            if (collidesPlayer(p)) {
                p.damage(this);
            }
        } else {
            speed = 0;
            isRunning = false;
        }

        if (health <= 0 || y > 900) {
            kill();
        }

        if (isDamaged) speed /= 2;


        if ((System.currentTimeMillis() - damagedTime) > 300 && isDamaged) isDamaged = false;

        updateState();
        animate();
    }


    public double distanceFromPlayer(Player p) {
        int dx = p.x - x;
        int dy = p.y - y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Used to apply contact damage to player if zombie collides with player.
     */
    public boolean collidesPlayer(Player p) {
        int x2 = x + image.getWidth(null);
        int y2 = y + image.getHeight(null);
        boolean isInside = false;
        int playerX2 = p.x + p.image.getWidth(null);
        int playerY2 = p.y + p.image.getHeight(null);
        boolean widthIsPositive = Math.min(x2, playerX2) > Math.max(x, p.x);
        boolean heightIsPositive = Math.min(y2, playerY2) > Math.max(y, p.y);
        if (widthIsPositive && heightIsPositive) {
            isInside = true;
        }
        return isInside;
    }

    public boolean isMoving() {
        return speed != 0;
    }


    public void updateState() {
        String newState = "idle";
        if (isGrounded && isMoving()) newState = "walking";
        if (isGrounded && isMoving() && isRunning) newState = "running";
        if (isGrounded && !isMoving()) newState = "idle";
        if (!isGrounded) newState = "aerial";
        if (isDamaged) newState = "hurt";
        state = new entitystate(isFacingForwards, newState);
    }


    public void animate() {
        switch (state.state) {
            case "idle" -> {
                if (lastAnimation >= idleSprites.length) lastAnimation = 0;
                if ((System.currentTimeMillis() - lastTime) > 250) {
                    image = idleSprites[lastAnimation];
                    lastAnimation++;
                    lastTime = System.currentTimeMillis();
                }
            }
            case "walking" -> {
                if (lastAnimation >= walkingSprites.length) lastAnimation = 0;
                if ((System.currentTimeMillis() - lastTime) > 180) {
                    image = walkingSprites[lastAnimation];
                    lastAnimation++;
                    lastTime = System.currentTimeMillis();
                }
            }
            case "running" -> {
                if (lastAnimation >= runningSprites.length) lastAnimation = 0;
                if ((System.currentTimeMillis() - lastTime) > 180) {
                    image = runningSprites[lastAnimation];
                    lastAnimation++;
                    lastTime = System.currentTimeMillis();
                }
            }
            case "aerial" -> {
                if (velocity < 0) image = runningSprites[4];
            }
            case "hurt" -> {
                image = hurtSprite;
            }
        }
    }


    public void damage() {
        if (!isDamaged) {
            health--;
            damagedTime = System.currentTimeMillis();
            isDamaged = true;
            velocity = -6;
        }
    }

    /**
     * Removes the enemy from the active list and awards ammo
     * Called when health <= 0 or when the enemy falls out of bounds
     *
     * in v2 conditions should be changed such that ammo gets incremented only when zombie is shot to death
     */
    public void kill() {
        canvas.player.ammo += 2;
        canvas.enemies.remove(this);
    }
}
