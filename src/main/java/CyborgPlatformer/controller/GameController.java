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

    private boolean gameOver = false;
    private boolean win = false;

    private boolean moveIntent = false;


    private static final int MAX_LIVES = 3;
    private int lives = MAX_LIVES;
    private int attempts = 1;
    private double elapsedSeconds = 0;

    private final double spawnX;
    private final double spawnY;

    private boolean facingRight = true;
    private boolean movedAfterReset = false;


    // Edge-trigger state
    private boolean lastJump = false;
    private boolean lastShoot = false;
    private boolean lastReset = false;
    private boolean lastKill = false;
    private boolean lastCheat = false;

    // enemies sleep until player moves
    private boolean enemiesAwake = false;

    // Kill plane (auto reset if you fall below this)
    private static final double FALL_RESET_Y = 1000.0;

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
        elapsedSeconds += dt;
        dt = Math.min(dt, 0.033);


        if (gameOver) {
            System.out.println("Game Won");
            return;
        }


        applyInput(input);
        physics.applyGravity(player, dt);

        for (Enemy e : world.getEnemies()) {
            e.think(world, player, dt);
            physics.applyGravity(e, dt);
        }

        world.update(dt);
        isGameWon();

        // Auto reset if player falls too far
        if (player.getY() > FALL_RESET_Y) {
            handleDeath();
            return;
        }

        if (!player.isAlive()) {
            handleDeath();
        }

    }

    private void applyInput(InputState input) {
        boolean jumpPressed = input.jump() && !lastJump;
        lastJump = input.jump();

        boolean shootPressed = input.shoot() && !lastShoot;
        lastShoot = input.shoot();

        boolean resetPressed = input.reset() && !lastReset;
        lastReset = input.reset();

        boolean killPressed = input.kill() && !lastKill;
        lastKill = input.kill();

        boolean cheatPressed = input.cheat() && !lastCheat;
        lastCheat = input.cheat();

        boolean tryingToMove = (input.left() && !input.right()) || (input.right() && !input.left());
        moveIntent = tryingToMove && !player.isInKnockback();


        // debug god command
        if (cheatPressed) {
            player.setPosition(7300, 600);
            player.setVX(0);
            player.setVY(0);
            player.setGrounded(false);
            player.resetJumpCounter();
        }

        // Movement
        if (!player.isInKnockback()) {
            if (input.left() && !input.right()) {
                player.moveLeft();
                facingRight = false;
                movedAfterReset = true;
            } else if (input.right() && !input.left()) {
                player.moveRight();
                facingRight = true;
                movedAfterReset = true;
            } else {
                player.stop();
            }
        }


        // wake enemies the first time player moves
        if (!enemiesAwake && movedAfterReset) {
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

        if (killPressed) {
            for (Enemy e : world.getEnemies()){
                e.setCanDamage();
            }
        }
    }

    private void handleDeath() {
        attempts++;

        /// TODO: wrap this around level functionality when extending
//        if (lives == 0) {
//            respawnPlayer();
//            gameOver = true;
//            // Freeze the player so the game stops feeling alive
//            player.stop();
//            return;
//        }

        respawnPlayer();
    }

    private void respawnPlayer() {
        resetPlayer();
        enemiesAwake = false;
        movedAfterReset = false;
        lastJump = false;
        lastShoot = false;
        lastReset = false;



        for (Enemy e : world.getEnemies()) {
            e.sleep();
        }
    }

    private void resetPlayer() {
        player.setPosition(spawnX, spawnY);
        player.setVX(0);
        player.setVY(0);
        player.setGrounded(false);
        player.resetJumpCounter();
        player.resetForRespawn();

        // If the level settings request enemies to respawn on player death, rebuild them.
        if (world.getLevelSettings() != null && world.getLevelSettings().isRespawnOnPlayerDeath()) {
            world.respawnEnemiesFromLevel();
        } else {
            for (Enemy e : world.getEnemies()) {
                e.sleep();
            }
        }
        }

        // Otherwise preserve current enemy instances but put them back to sleep.
        for (Enemy e : world.getEnemies()) {
            e.sleep();
        }
            win = true;
            gameOver = true;
        }
        return win;
    }

    public boolean isFacingRight() { return facingRight; }
    public boolean moveIntent() { return moveIntent; }
    public int getLives() { return this.lives; }
    public boolean isGameOver() { return gameOver; }
    public int getAttempts() { return attempts; }
    public int getTimeSeconds() { return (int)Math.floor(elapsedSeconds); }
    public int getPlayerHealth() { return this.player.getHealth(); }
    public void incrementAmmo() {
        this.player.oneMoreBullet();
    }

}