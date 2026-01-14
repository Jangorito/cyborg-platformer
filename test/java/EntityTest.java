import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.Image;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

class EntityTest {

    private entity e;

    @BeforeEach
    void setUp() {
        // intersect/gravity so each test depends on a clean CyborgPlatformer.legacy.MapBlocks.map
        MapBlocks.map.clear();

        // Dummy CyborgPlatformer.legacy.entity image w/ real dimensions for intersect calculations
        Image img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);

        e = new entity(img, 0, 0, 100, 10, 32);
    }

    @Test
    @DisplayName("Constructor sets position and basic fields correctly")
    void constructorSetsFields() {
        assertEquals(0, e.x);                   // x      :  should be 0
        assertEquals(0, e.y);                   // y      :  should be 0
        assertEquals(100, e.health);            // health :  should be 100
        assertEquals(10, e.ammo);               // ammo   :  should be 10
        assertEquals(32, e.hitBox);             // hitbox :  should be 32 (img dimensions)

        // state is created in constructor: new entitystate(true, "idle")
        assertNotNull(e.state);                         // state             : should NOT be null
        assertTrue(e.state.isFacingForward);            // isFacingForward   : should be TRUE
        assertEquals("idle", e.state.state);   // state.state       : should be "idle"
    }

    @Test
    @DisplayName("intersect() returns false when map has no blocks")
    void intersectFalseWhenMapEmpty() {
        assertFalse(e.intersect());                     // intersect()  : should be FALSE
    }

    @Test
    @DisplayName("intersect() returns true when CyborgPlatformer.legacy.entity overlaps a block")
    void intersectTrueWhenOverlappingBlock() {
        Image blockImg = new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB);
        MapBlocks.map.add(new MapBlocks(blockImg, 0, 0)); // overlaps CyborgPlatformer.legacy.entity at (0,0)

        assertTrue(e.intersect());                      // intersect()  : should be TRUE
    }

    @Test
    @DisplayName("jump() applies correct velocity and stores jump position (first jump)")
    void jumpFirstJumpSetsVelocityAndStoresPosition() {
        e.jumpCounter = 1; // NOT a double jump
        e.jump();          // call jump method

        assertEquals(-6.0,
                e.velocity, 0.0001);               // velocity : should be -6 TODO: because...
        assertEquals(e.x, e.jumpX);                     // x = jumpX : should be TRUE
        assertEquals(e.y, e.jumpY);                     // y = jumpY : should be TRUE
    }

    @Test
    @DisplayName("jump() applies correct velocity (second jump)")
    void jumpSecondJumpSetsStrongerVelocity() {
        e.jumpCounter = 2;  // double jump
        e.jump();           // call jump method

        assertEquals(-8.0, e.velocity, 0.0001); // velocity : should be -8
    }

    @Test
    @DisplayName("copy(newX,newY) returns a new CyborgPlatformer.legacy.entity with same stats but new position")
    void copyCreatesNewEntityWithNewPosition() {
        entity c = e.copy(100, 200);     // copy CyborgPlatformer.legacy.entity following v1 design pattern

        assertNotSame(e, c);                         // CyborgPlatformer.legacy.entity == copy   : should be FALSE
        assertEquals(100, c.x);             // x                : should be 100
        assertEquals(200, c.y);             // y                : should be 200

        // CyborgPlatformer.legacy.entity's stats should carry over
        assertEquals(e.health, c.health);           // health == health  : should be TRUE
        assertEquals(e.ammo, c.ammo);               // ammo == ammo      : should be TRUE
        assertEquals(e.hitBox, c.hitBox);           // hitBox == hitBox  : should be TRUE
:
        // same image reference is fine in V1
        assertSame(e.image, c.image);               // image == image    : should be TRUE
    }

    @Test
    @DisplayName("gravity() moves CyborgPlatformer.legacy.entity down and increases velocity when no collision")
    void gravityMovesWhenNoCollision() {
        e.velocity = 2.0;   // set CyborgPlatformer.legacy.entity velocity
        e.gravity();        // call gravity method

        assertEquals(2, e.y);                 // y += velocity                   : should be TRUE
        assertEquals(2.5, e.velocity,
                0.0001);                         // velocity += acceleration (0.5)  : should be TRUE
        assertFalse(e.isGrounded);                     // isGrounded                      : should be TRUE
    }

    @Test
    @DisplayName("gravity() sets grounded true and reduces velocity when colliding below")
    void gravityCollidesAndGroundsEntity() {
        // Make the *next* position collide: entityCopy is at y + velocity
        e.velocity = 2.0;

        Image blockImg = new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB);
        MapBlocks.map.add(new MapBlocks(blockImg, 0, 2)); // overlaps entityCopy at y=2

        int oldY = e.y;
        e.gravity();

        // For collisions, y doesn't change
        assertEquals(oldY, e.y);

        // if velocity > 1.5 => velocity /= 1.5
        assertEquals(2.0 / 1.5, e.velocity, 0.0001);

        // if velocity > 0 => grounded = TRUE
        assertTrue(e.isGrounded);
    }
}
