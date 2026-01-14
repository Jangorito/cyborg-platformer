package CyborgPlatformer.game;

import CyborgPlatformer.model.world.World;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class FixedTimestepLoopTest {

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 3, 10})
    void tickWorldNTimes(int ticks) {
        World world = new World();
        FixedTimestepLoop loop =
                new FixedTimestepLoop(world, 1.0 / 60.0);

        for (int i = 0; i < ticks; i++) {
            loop.tickOnce();
        }

        assertEquals(ticks, world.getTickCount());
    }
}
