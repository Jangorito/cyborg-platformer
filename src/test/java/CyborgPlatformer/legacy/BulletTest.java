package CyborgPlatformer.legacy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class BulletTest {

    @BeforeEach
    void setUp() {
        // Bullet instance requires an image because its width is queried with getWidth
        bullet.bulletImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);

        // each test depends on a clean CyborgPlatformer.legacy.MapBlocks.map
        MapBlocks.map.clear();

        // static globals in CyborgPlatformer.legacy.canvas
        canvas.activeBullets = new ArrayList<>();
        canvas.enemies = new ArrayList<>();

        // We will not create a new CyborgPlatformer.legacy.bullet instance to be shared by each test because different field
        //  mutations could affect other tests
    }


    @Test
    @DisplayName("travelledDistance() returns 0 at the start point")
    void travelledDistanceIsZeroAtStart() {
        bullet b = new bullet(100, 200);                            // create new CyborgPlatformer.legacy.bullet image
        b.startPoint = new Point(100, 200);                         // set CyborgPlatformer.legacy.bullet start point

        assertEquals(0.0, b.travelledDistance(), 0.0001);   // distance travelled    :   should be 0
    }

    @Test
    @DisplayName("travelledDistance() computes Euclidean distance correctly")
    void travelledDistanceComputesCorrectly() {
        bullet b = new bullet(0, 0);                                // create new CyborgPlatformer.legacy.bullet image
        b.startPoint = new Point(0, 0);                             // set CyborgPlatformer.legacy.bullet start point

        b.x = 3;                                                          // set new CyborgPlatformer.legacy.bullet x position
        b.y = 4;                                                          // set new CyborgPlatformer.legacy.bullet y position

        assertEquals(5.0, b.travelledDistance(), 0.0001);   // distance travelled   :   should be 5
    }

    @Test
    @DisplayName("update() moves CyborgPlatformer.legacy.bullet by speed when no collisions and under 600px")
    void updateMovesBulletWhenClear() {
        bullet b = new bullet(0, 0);                                // create new CyborgPlatformer.legacy.bullet image
        b.startPoint = new Point(0, 0);                             // set CyborgPlatformer.legacy.bullet start point
        b.speed = 10;                                                     // set CyborgPlatformer.legacy.bullet speed
        canvas.activeBullets.add(b);                                      // add CyborgPlatformer.legacy.bullet to CyborgPlatformer.legacy.canvas' activeBullets list

        b.update();                                                       // update CyborgPlatformer.legacy.bullet

        assertEquals(10, b.x);                                   // x value                 : should be 10
        assertTrue(canvas.activeBullets.contains(b),
                "Bullet should remain active before 600px");     // list contains CyborgPlatformer.legacy.bullet?   : should be True
    }

    @Test
    @DisplayName("update() removes CyborgPlatformer.legacy.bullet after travelling 600px or more")
    void updateRemovesBulletAfter600px() {
        bullet b = new bullet(600, 0);                              // create new CyborgPlatformer.legacy.bullet image
        b.startPoint = new Point(0, 0);                             // set CyborgPlatformer.legacy.bullet start point
        b.speed = 10;                                                     // set CyborgPlatformer.legacy.bullet speed
        canvas.activeBullets.add(b);                                      // add CyborgPlatformer.legacy.bullet to CyborgPlatformer.legacy.canvas' activeBullets list

        // No collisions (empty map, empty enemies) so it moves then checks distance
        b.update();                                                       // update CyborgPlatformer.legacy.bullet

        // activeBullets should be empty
        assertFalse(canvas.activeBullets.contains(b),
                "Bullet should be removed once travelledDistance >= 600");
    }

    @Test
    @DisplayName("collidesEnemy() returns false when there are no enemies")
    void collidesEnemyFalseWhenNoEnemies() {
        bullet b = new bullet(0, 0);
        b.startPoint = new Point(0, 0);

        assertFalse(b.collidesEnemy());
    }
}
