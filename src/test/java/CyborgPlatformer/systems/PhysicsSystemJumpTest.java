package CyborgPlatformer.systems;

import CyborgPlatformer.model.entities.Entity;
import CyborgPlatformer.model.world.Level;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class PhysicsSystemJumpTest {

    private static final class TestEntity extends Entity {
        TestEntity() {
            setSize(10, 10);
        }
    }

    @ParameterizedTest(name = "jumpCounter={0} => vy={1}")
    @CsvSource({
            "0, -8.0",
            "1, -6.0",
            "2, -8.0"
    })
    void jump_appliesLegacyImpulse(int jumpCounter, double expectedVy) {
        Level noSolids = (x, y, w, h) -> false;
        PhysicsSystem physics = new PhysicsSystem(noSolids);

        TestEntity e = new TestEntity();
        e.setVY(0);

        physics.jump(e, jumpCounter);

        assertEquals(expectedVy, e.getVY(), 0.0001);
    }
}
