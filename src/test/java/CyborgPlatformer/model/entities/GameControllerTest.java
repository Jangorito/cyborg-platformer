package CyborgPlatformer.model.entities;

import CyborgPlatformer.controller.GameController;
import CyborgPlatformer.input.InputState;
import CyborgPlatformer.model.world.World;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameControllerTest {

    @ParameterizedTest(name = "shoot with facingRight={0} spawns at xOffset={1}, movesPerTick={2}")
    @CsvSource({
            "true,  47,  10",
            "false, -25, -10"
    })
    void controller_shootSpawnsBulletUsingFacingDirection(boolean facingRight, int xOffset, int movePerTick) {
        World world = new World();
        Player player = new Player();
        player.x = 100;
        player.y = 200;

        GameController controller = new GameController(world, player);

        // Set facing direction by simulating a movement input first
        InputState setFacing = facingRight
                ? new InputState(false, true, false, false)   // right
                : new InputState(true, false, false, false);  // left
        controller.step(1.0, setFacing);

        // Shoot this tick
        controller.step(1.0, new InputState(false, false, false, true));

        Bullet b = world.getEntities().stream()
                .filter(e -> e instanceof Bullet)
                .map(e -> (Bullet) e)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No Bullet spawned"));

        assertEquals(100 + xOffset, b.getX(), 0.0001);
        assertEquals(200 + 10, b.getY(), 0.0001);

        double before = b.getX();
        controller.step(1.0, InputState.none());
        assertEquals(before + movePerTick, b.getX(), 0.0001);
    }

    @Test
    void controller_leftRightConflict_stopsPlayer() {
        World world = new World();
        Player player = new Player();
        GameController controller = new GameController(world, player);

        controller.step(1.0, new InputState(true, true, false, false)); // conflict
        double xBefore = player.getX();

        controller.step(1.0, InputState.none());
        assertEquals(xBefore, player.getX(), 0.0001, "Player should not move when left & right are both held");
    }
}
