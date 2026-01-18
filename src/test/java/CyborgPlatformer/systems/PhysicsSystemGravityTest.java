//package CyborgPlatformer.systems;
//
//import CyborgPlatformer.model.entities.Entity;
//import CyborgPlatformer.model.entities.Player;
//import CyborgPlatformer.model.world.Level;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.CsvSource;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class PhysicsSystemGravityTest {
//
//    /**
//     * Simple concrete entity for physics tests.
//     */
//    private static final class TestEntity extends Entity {
//        TestEntity(double x, double y, double width, double height) {
//            this.x = x;
//            this.y = y;
//            setSize(width, height);
//        }
//    }
//
//    @ParameterizedTest(name = "no collision: vy={0}, dt={1} => y += vy*dt and vy += ay; grounded=false")
//    @CsvSource({
//            "0.0, 1.0",
//            "2.0, 1.0",
//            "-3.0, 1.0",
//            "4.0, 0.5"
//    })
//    void applyGravity_noCollision_movesAndAccelerates(double initialVy, double dt) {
//        Level noSolids = (x, y, w, h) -> false;
//        PhysicsSystem physics = new PhysicsSystem(noSolids);
//
//        TestEntity e = new TestEntity(10, 20, 12, 20);
//        e.setVY(initialVy);
//        e.setGrounded(true); // should be forced false when free-moving
//
//        double yBefore = e.getY();
//
//        physics.applyGravity(e, dt);
//
//        assertEquals(yBefore + initialVy * dt, e.getY(), 0.0001);
//        assertEquals(initialVy + e.getAY(), e.getVY(), 0.0001);
//        assertFalse(e.isGrounded(), "Entity should not be grounded if no collision occurs");
//    }
//
//    @Test
//    void applyGravity_collisionDownwardSlow_setsGroundedTrue_andZeroesVy() {
//        Level solidEverywhere = (x, y, w, h) -> true;
//        PhysicsSystem physics = new PhysicsSystem(solidEverywhere);
//
//        TestEntity e = new TestEntity(0, 100, 10, 10);
//        e.setVY(1.0);            // slow downward
//        e.setGrounded(false);
//
//        physics.applyGravity(e, 1.0);
//
//        assertTrue(e.isGrounded(), "Any downward collision should ground the entity");
//        assertEquals(0.0, e.getVY(), 0.0001, "Slow downward collision should zero vertical velocity");
//        assertEquals(100, e.getY(), 0.0001, "Should not commit y movement when collision happens");
//    }
//
//    @Test
//    void applyGravity_collisionDownwardFast_setsGroundedTrue_andDampensVy() {
//        Level solidEverywhere = (x, y, w, h) -> true;
//        PhysicsSystem physics = new PhysicsSystem(solidEverywhere);
//
//        TestEntity e = new TestEntity(0, 100, 10, 10);
//        e.setVY(6.0);           // fast downward => dampen
//        e.setGrounded(false);
//
//        physics.applyGravity(e, 1.0);
//
//        assertTrue(e.isGrounded(), "Any downward collision should ground the entity");
//        assertEquals(6.0 / 1.5, e.getVY(), 0.0001, "Fast downward collision should dampen vy");
//        assertEquals(100, e.getY(), 0.0001);
//    }
//
//    @Test
//    void applyGravity_collisionUpward_bouncesAndDoesNotGround() {
//        Level solidEverywhere = (x, y, w, h) -> true;
//        PhysicsSystem physics = new PhysicsSystem(solidEverywhere);
//
//        TestEntity e = new TestEntity(0, 100, 10, 10);
//        e.setVY(-8.0);          // upward => bounce to +2.0
//        e.setGrounded(false);
//
//        physics.applyGravity(e, 1.0);
//
//        assertFalse(e.isGrounded(), "Upward collision should not ground the entity");
//        assertEquals(2.0, e.getVY(), 0.0001, "Upward collision should bounce: -(vy/4)");
//        assertEquals(100, e.getY(), 0.0001);
//    }
//
//    @Test
//    void applyGravity_landingResetsPlayerJumpCounter_onlyOnTransitionToGrounded() {
//        Level solidEverywhere = (x, y, w, h) -> true;
//        PhysicsSystem physics = new PhysicsSystem(solidEverywhere);
//
//        Player p = new Player();
//        p.setSize(12, 20);
//
//        // simulate the player has jumped already
//        p.incrementJumpCounter();
//        assertEquals(1, p.getJumpCounter());
//
//        // landing transition
//        p.setGrounded(false);
//        p.setVY(1.0);
//
//        physics.applyGravity(p, 1.0);
//
//        assertTrue(p.isGrounded());
//        assertEquals(0, p.getJumpCounter(), "Jump counter should reset when player becomes grounded");
//
//        // if already grounded, your PhysicsSystem checks !isGrounded() before resetting
//        p.incrementJumpCounter(); // back to 1
//        p.setGrounded(true);
//        p.setVY(1.0);
//
//        physics.applyGravity(p, 1.0);
//
//        assertEquals(1, p.getJumpCounter(), "Should not reset jump counter again if already grounded");
//    }
//}
