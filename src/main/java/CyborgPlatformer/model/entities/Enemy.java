package CyborgPlatformer.model.entities;

import CyborgPlatformer.model.world.World;

/**
 * Minimal functional enemy for V2 demo.
 *
 * Behaviour:
 * - Activates near player and chases (walk/run).
 * - Jumps when player is above (limited).
 * - Takes bullet damage; dies at 0 HP.
 * - Deals contact damage with cooldown.
 *
 * No sprites/animations (renderer decides visuals).
 */
public final class Enemy extends Entity implements Damageable {

    private int health;
    private boolean alive = true;

    // signals for HUD/renderer later
    private boolean running = false;
    private boolean facingRight = true;
    private boolean damaged = false;

    // "sleep until player moves"
    private boolean awakened = false;

    private double damagedTimer = 0.0;
    private double contactCooldown = 0.0;
    private int jumpCounter = 0;
    private double jumpCooldown = 0.0;

    // --- tuning ---
    private static final double ACTIVATION_RADIUS = 400.0;
    private static final double RUN_RADIUS = 200.0;

    private static final double WALK_SPEED = 140.0; // px/s
    private static final double RUN_SPEED  = 260.0; // px/s

    private static final double CONTACT_COOLDOWN_S = 1.5;
    private static final int CONTACT_DAMAGE = 50;

    private static final double JUMP_COOLDOWN_S = 0.45;
    private static final double JUMP_VY = -300.0;

    // knockback (BOTH sides)
    private static final double PLAYER_KB_VX = 320.0;
    private static final double PLAYER_KB_VY = -420.0;

    private static final double ENEMY_KB_VX = 50;
    private static final double ENEMY_KB_VY = -180.0;
    private static final double OUT_OF_BOUNDS_Y = 2000.0;

    public Enemy(double x, double y, double width, double height, int health) {
        setPosition(x, y);
        setSize(width, height);
        this.health = health;
    }

    public boolean isAlive() { return alive; }
    public int getHealth() { return health; }
    public boolean isRunning() { return running; }
    public boolean isFacingRight() { return facingRight; }
    public boolean isDamaged() { return damaged; }

    /** Called by controller once player has moved at least once. */
    public void awaken() { this.awakened = true; }

    @Override
    public void damage(int amount) {
        if (!alive) return;


        if (!damaged) {
            health -= amount;
            if (health <= 0) {
                health = 0;
                alive = false;
                return;
            }
            damaged = true;
            damagedTimer = 0.30;
            vy = -250.0; // small pop up
        }
    }

    /**
     * AI decision step. Call once per tick from controller.
     */
    public void think(World world, Player player, double dt) {
        if (!alive) return;

        // sleep until player moves
        if (!awakened) {
            vx = 0;
            running = false;
            return;
        }


        // timers
        if (damaged) {
            damagedTimer -= dt;
            if (damagedTimer <= 0) damaged = false;
        }
        if (contactCooldown > 0) contactCooldown -= dt;
        if (jumpCooldown > 0) jumpCooldown -= dt;

        if (y > OUT_OF_BOUNDS_Y) {
            alive = false;
            return;
        }

        double dx = player.getX() - x;
        double dy = player.getY() - y;
        double dist = Math.sqrt(dx * dx + dy * dy);

        boolean roughlySameVerticalBand = (y - player.getY()) < 100;
        if (!(dist <= ACTIVATION_RADIUS && roughlySameVerticalBand)) {
            vx = 0;
            running = false;
            return;
        }

        // choose speed
        double speed = (dist <= RUN_RADIUS) ? RUN_SPEED : WALK_SPEED;
        running = dist <= RUN_RADIUS;

        // chase
        if (player.getX() < x) {
            vx = -speed;
            facingRight = false;
        } else if (player.getX() > x) {
            vx = speed;
            facingRight = true;
        } else {
            vx = 0;
        }

        if (Math.abs(x - player.getX()) < 20) vx = 0;
        if (damaged) vx *= 0.5;

        // jump logic (limited)
        if (grounded) jumpCounter = 0;

        boolean playerAbove = player.getY() < y;
        boolean farEnoughHoriz = Math.abs(x - player.getX()) > 40;

        if (playerAbove && jumpCooldown <= 0 && jumpCounter < 1) {
            if (!grounded || (dist <= RUN_RADIUS && farEnoughHoriz)) {
                vy = JUMP_VY;
                grounded = false;
                jumpCounter++;
                jumpCooldown = JUMP_COOLDOWN_S;
            }
        }

        // contact damage + knockback both
        if (contactCooldown <= 0 && overlaps(player)) {
            player.damage(CONTACT_DAMAGE);
            contactCooldown = CONTACT_COOLDOWN_S;

            // Direction: push away from each other
            double dirToPlayer = (player.getX() >= x) ? 1.0 : -1.0;

            // knockback
            player.knockback(dirToPlayer * PLAYER_KB_VX, PLAYER_KB_VY);
            this.vx = -dirToPlayer * ENEMY_KB_VX;
            this.vy = ENEMY_KB_VY;
            this.grounded = false;

            // --- POSITIONAL SEPARATION (CRITICAL) ---
            double separation = 2.0; // tweakable
            player.setPosition(
                    player.getX() + dirToPlayer * separation,
                    player.getY()
            );
            this.setPosition(
                    this.getX() - dirToPlayer * separation,
                    this.getY()
            );
        }
    }

    private boolean overlaps(Entity other) {
        return x < other.getX() + other.getWidth()
                && x + width > other.getX()
                && y < other.getY() + other.getHeight()
                && y + height > other.getY();
    }
}
