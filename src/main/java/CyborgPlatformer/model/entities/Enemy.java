package CyborgPlatformer.model.entities;

import CyborgPlatformer.config.LevelSettings;
import CyborgPlatformer.model.world.World;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * functional enemy.
 *
 * Behaviour:
 * - Activates near player and chases (walk/run).
 * - Jumps when player is above.
 * - Takes bullet damage; dies at 0 HP.
 * - Deals contact damage with cooldown.
 *
 * No sprites/animations (renderer decides visuals).
 */
public final class Enemy extends Entity implements Damageable {

    private static final AtomicInteger NEXT_ID = new AtomicInteger(1);
    private final int id = NEXT_ID.getAndIncrement();

    private int health;
    private boolean alive = true;

    // signals for HUD/renderer
    private boolean running = false;
    private boolean facingRight = true;
    private boolean damaged = false;
    private boolean canDamage = true;

    // "sleep until player moves"
    private boolean awakened = false;

    private double damagedTimer = 0.0;
    private double contactCooldown = 0.0;
    private int jumpCounter = 0;
    private double jumpCooldown = 0.0;

    // --- tuning ---
    private static final double ACTIVATION_RADIUS = 400.0;
    private static final double RUN_RADIUS = 200.0;

    private static final double WALK_SPEED = 110.0;
    private static final double RUN_SPEED  = 160.0;

    private static final double CONTACT_COOLDOWN_S = 1.5;
    private static final int CONTACT_DAMAGE = 1;

    private static final double JUMP_COOLDOWN_S = 0.45;
    private static final double JUMP_VY = -300.0;

    // knockback (BOTH sides)
    private static final double PLAYER_KB_VX = 320.0;
    private static final double PLAYER_KB_VY = -420.0;
    private static final double ENEMY_KB_VX = 50;
    private static final double ENEMY_KB_VY = -180.0;

    private static final double OUT_OF_BOUNDS_Y = 2000.0;

    // Default collision size used by spawn code.
    public static final double DEFAULT_WIDTH = 50.0;
    public static final double DEFAULT_HEIGHT = 64.0;

    // Per-instance difficulty settings
    private final LevelSettings settings;

    /**
     * Backwards-compatible constructor that uses medium presets.
     * Package-private to discourage direct construction outside entity package.
     */
    Enemy(double x, double y, double width, double height, int health) {
        this(x, y, width, height, health, LevelSettings.medium());
    }

    /**
     * Preferred constructor allowing per-level settings to be applied to this enemy.
     * Package-private to discourage direct construction; use `create(...)` factory instead.
     */
    Enemy(double x, double y, double width, double height, int health, LevelSettings settings) {
        setPosition(x, y);
        setSize(width, height);
        this.settings = (settings == null) ? LevelSettings.medium() : settings;
        // Apply health multiplier from settings (ensure at least 1 HP)
        this.health = Math.max(1, (int) Math.round(health * this.settings.getHealthMultiplier()));
    }

    /**
     * Public factory for controlled construction from other packages.
     */
    public static Enemy create(double x, double y, double width, double height, int health, LevelSettings settings) {
        return new Enemy(x, y, width, height, health, settings);
    }

    public boolean isAlive() { return alive; }
    public int getHealth() { return health; }
    public boolean isRunning() { return running; }
    public boolean isFacingRight() { return facingRight; }
    public boolean isDamaged() { return damaged; }


    // Allow this enemy to be damaged by bullets/attacks.
    public void setCanDamage() { this.canDamage = true; }

    /**
     * Immediately kill this enemy — mark as not alive and zero health.
     * Use for debug or force-death situations where we want the enemy
     * to be removed from the world.
     */
    public void kill() {
        this.health = 0;
        this.alive = false;
    }

    /** Called by controller once player has moved at least once. */
    public void awaken() { this.awakened = true; }
    public void sleep() {
        awakened = false;
        vx = 0;
        running = false;
    }

    @Override
    public void damage(int amount) {
        if (!alive) return;
        if (!canDamage) return;


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
        if (!canDamage) return;


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

        // choose speed (apply level speed multiplier)
        double baseSpeed = (dist <= RUN_RADIUS) ? RUN_SPEED : WALK_SPEED;
        double speed = baseSpeed * this.settings.getSpeedMultiplier();
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

        // jump logic
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
            int inflicted = Math.max(1, (int) Math.round(CONTACT_DAMAGE * this.settings.getDamageMultiplier()));
            player.damage(inflicted);
            // reduce cooldown when aggression is higher
            contactCooldown = CONTACT_COOLDOWN_S / Math.max(0.0001, this.settings.getAggressionMultiplier());

            // Direction: push away from each other
            double dirToPlayer = (player.getX() >= x) ? 1.0 : -1.0;

            // knockback
            player.knockback(dirToPlayer * PLAYER_KB_VX, PLAYER_KB_VY);
            this.vx = -dirToPlayer * ENEMY_KB_VX;
            this.vy = ENEMY_KB_VY;
            this.grounded = false;

            // separation
            double separation = 2.0;
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

    public int getId() {
        return id;
    }


}
