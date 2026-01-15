package CyborgPlatformer.controller;

import CyborgPlatformer.input.InputState;
import CyborgPlatformer.model.entities.Enemy;
import CyborgPlatformer.model.entities.Player;
import CyborgPlatformer.model.world.World;
import CyborgPlatformer.systems.PhysicsSystem;

/**
 * Coordinates game flow and translates input -> actions.
 *
 * Responsibilities:
 * - Owns the World + Player lifecycle.
 * - Maps InputState -> player movement + shooting.
 * - Advances simulation by calling World.update(dt).
 *
 * Notes:
 * - Rendering reads World state separately.
 */
public class GameController {

    private final World world;
    private final Player player;
    private final PhysicsSystem physics;

    private final double spawnX;
    private final double spawnY;

    private boolean facingRight = true;
    private boolean movedThisFrame = false;


    // Edge-trigger state
    private boolean lastJump = false;
    private boolean lastShoot = false;
    private boolean lastReset = false;

    // enemies sleep until player moves
    private boolean enemiesAwake = false;

    // Kill plane (auto reset if you fall below this)
    private static final double FALL_RESET_Y = 3000.0;

    public GameController(World world, Player player, double spawnX, double spawnY) {
        this.world = world;
        this.player = player;
        this.physics = new PhysicsSystem(world.getLevel());
        this.spawnX = spawnX;
        this.spawnY = spawnY;

        this.world.addEntity(player);


    }

    public World getWorld() { return world; }
    public Player getPlayer() { return player; }

    /**
     * Advance one simulation tick.
     *
     * @param dt tick
     * @param input current input snapshot
     */
    public void step(double dt, InputState input) {
        applyInput(input);
        physics.applyGravity(player, dt);

        for (Enemy e : world.getEnemies()) {
            e.think(world, player, dt);
            physics.applyGravity(e, dt);
        }

        world.update(dt);

        // Auto reset if player falls too far
        if (player.getY() > FALL_RESET_Y) {
            resetPlayer();
        }
    }

    private void applyInput(InputState input) {
        boolean jumpPressed = input.jump() && !lastJump;
        lastJump = input.jump();

        boolean shootPressed = input.shoot() && !lastShoot;
        lastShoot = input.shoot();

        boolean resetPressed = input.reset() && !lastReset;
        lastReset = input.reset();

        // Movement
        if (!player.isInKnockback()) {
            if (input.left() && !input.right()) {
                player.moveLeft();
                facingRight = false;
                movedThisFrame = true;
            } else if (input.right() && !input.left()) {
                player.moveRight();
                facingRight = true;
                movedThisFrame = true;
            } else {
                player.stop();
            }
        }

        // wake enemies the first time player moves
        if (!enemiesAwake && movedThisFrame) {
            enemiesAwake = true;
            for (Enemy e : world.getEnemies()) e.awaken();
        }

        // Shoot
        if (shootPressed) {
            player.shoot(world, facingRight);
        }

        // Jump (double jump)
        if (jumpPressed) {
            if (player.isGrounded() || player.getJumpCounter() < 2) {
                physics.jump(player, player.getJumpCounter());
                player.incrementJumpCounter();
                player.setGrounded(false);
            }
        }

        // Manual reset
        if (resetPressed) {
            resetPlayer();
        }
    }

    private void resetPlayer() {
        player.setPosition(spawnX, spawnY);
        player.setVX(0);
        player.setVY(0);
        player.setGrounded(false);
        player.resetJumpCounter();

        enemiesAwake = false;

        // TODO: can improve later
    }

    public boolean hasMovedThisFrame() {
        return movedThisFrame;
    }

    public boolean isEnemiesAwake() {
        return enemiesAwake;
    }
}