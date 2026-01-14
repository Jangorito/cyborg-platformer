package CyborgPlatformer.controller;

import CyborgPlatformer.input.InputState;
import CyborgPlatformer.model.entities.Player;
import CyborgPlatformer.model.world.World;

/**
 * Coordinates game flow and translates input -> actions.
 *
 * Responsibilities:
 * - Owns the World + Player lifecycle.
 * - Maps InputState -> player movement + shooting.
 * - Advances simulation by calling World.update(dt).
 *
 * Notes:
 * - Currently no TODO: JavaFX.
 * - Rendering reads World state separately.
 */
public class GameController {

    private final World world;
    private final Player player;

    private boolean facingRight = true;

    public GameController(World world, Player player) {
        this.world = world;
        this.player = player;

        // Adds player to world list so it updates with everything else.
        this.world.addEntity(player);
    }

    public World getWorld() { return world; }
    public Player getPlayer() { return player; }
    public boolean isFacingRight() { return facingRight; }

    /**
     * Advance one simulation tick.
     *
     * @param dt tick
     * @param input current input snapshot
     */
    public void step(double dt, InputState input) {
        applyInput(input);
        world.update(dt);
    }

    private void applyInput(InputState input) {
        // User input -> player velocity
        if (input.left() && !input.right()) {
            player.moveLeft();
            facingRight = false;
        } else if (input.right() && !input.left()) {
            player.moveRight();
            facingRight = true;
        } else {
            player.stop();
        }

        // Shooting input -> spawn bullet through Player + World
        if (input.shoot()) {
            player.shoot(world, facingRight);
        }

        // TODO: Jump
    }
}
