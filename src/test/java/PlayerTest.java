import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @BeforeEach
    void setUp() {
        // each test depends on a clean MapBlocks.map
        MapBlocks.map.clear();


        canvas.keysPressed = new boolean[4];            // set keyPressed list for keyboard entry testing
        canvas.activeBullets = new ArrayList<>();       // create list for activeBullets
        canvas.enemies = new ArrayList<>();             // create list for enemies

        // Emulate Game.loadImages() functionality with Dummy images for Player animation arrays
        Image dummy = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Player.idleSprites = new Image[]{dummy};
        Player.runningSprites = new Image[]{dummy, dummy, dummy, dummy, dummy, dummy}; // runningSprites[5] used in aerial
        Player.hurtSprites = new Image[]{dummy, dummy};
        Player.shootingSprite = dummy;

        // Bullet instance requires an image because its width is queried with getWidth
        bullet.bulletImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
    }

    @Test
    @DisplayName("Player constructor sets position, health, ammo and hitbox")
    void constructorSetsCoreFields() {
        // create Player instance
        Player p = new Player(10, 20, 3);

        assertEquals(10, p.x);          // x value          : should be 10
        assertEquals(20, p.y);          // y value          : should be 20
        assertEquals(3, p.health);      // health           : should be 3

        // Since player passes ammo=10, hitBox=30 into entity constructor:
        assertEquals(10, p.ammo);       // ammo             : should be 10
        assertEquals(30, p.hitBox);     // hitBox           : should be 30

        assertNotNull(p.state);                 //  player state     : shouldn't be Null
        assertNotNull(p.image);                 //  player img       : shouldn't be Null
    }

    @Test
    @DisplayName("isMoving() true when left or right key pressed")
    void isMovingReflectsKeys() {
        Player p = new Player(0, 0, 3);

        canvas.keysPressed[0] = false;          // indicates 'left'
        canvas.keysPressed[1] = false;          // indicates 'right'
        assertFalse(p.isMoving());              // isMoving         :   should be False

        canvas.keysPressed[0] = true;
        assertTrue(p.isMoving());               // isMoving         :   should be True

        canvas.keysPressed[0] = false;
        canvas.keysPressed[1] = true;
        assertTrue(p.isMoving());               // isMoving         :   should be True
    }

    @Test
    @DisplayName("isJumping() true when jumpCounter > 0")
    void isJumpingReflectsJumpCounter() {
        Player p = new Player(0, 0, 3);

        p.jumpCounter = 0;              // set jumpCounter to 0
        assertFalse(p.isJumping());     // isJumping    :   should be False
        p.jumpCounter = 1;              // set jumpCounter to 1
        assertTrue(p.isJumping());      // isJumping    :   should be True
    }

    @Test
    @DisplayName("updateState() sets running when grounded and moving")
    void updateStateRunning() {
        Player p = new Player(0, 0, 3);

        p.isGrounded = true;            // set isGrounded to True
        canvas.keysPressed[1] = true;

        p.isDamaged = false;            // set isDamaged to False
        p.justShot = false;             // set justShot to False

        p.updateState();                // call updateState() to enable state query
        assertEquals("running",
                p.state.state);         // state.state      :   should be "running"
    }

    @Test
    @DisplayName("updateState() sets aerial when not grounded")
    void updateStateAerial() {
        Player p = new Player(0, 0, 3);

        p.isGrounded = false;
        canvas.keysPressed[0] = false;
        canvas.keysPressed[1] = false;

        p.isDamaged = false;
        p.justShot = false;

        p.updateState();
        assertEquals("aerial",
                p.state.state);         // state.state      :   should be "aerial"
    }

    @Test
    @DisplayName("updateState() sets hurt when damaged")
    void updateStateHurt() {
        Player p = new Player(0, 0, 3);

        p.isGrounded = true;
        canvas.keysPressed[0] = false;
        canvas.keysPressed[1] = false;

        p.isDamaged = true;
        p.justShot = false;

        p.updateState();
        assertEquals("hurt",
                p.state.state);     // state.state      :   should be "hurt"
    }

    @Test
    @DisplayName("updateState() shooting overrides hurt when justShot is true")
    void updateStateShootingOverridesHurt() {
        Player p = new Player(0, 0, 3);

        p.isGrounded = true;
        p.isDamaged = true;
        p.justShot = true;

        p.updateState();
        assertEquals("shooting",
                p.state.state);     // state.state      :   should be "shooting"
    }

    @Test
    @DisplayName("shoot() consumes ammo and adds a bullet when not on cooldown (facing right)")
    void shootAddsBulletFacingRight() {
        Player p = new Player(100, 200, 3);


        p.keys[0] = false;                                // player keys set to false
        p.keys[1] = true;                                 // player keys set to true
        p.ammo = 2;                                       // set ammo to 2
        p.justShot = false;                               // set justShot to False
        p.lastShot = 0;                                   // set lastShot to 0

        int beforeAmmo = p.ammo;                          // local integer variable to track ammo before shot
        int beforeCount = canvas.activeBullets.size();    // local integer variable to check size of activeBullets list

        p.shoot();                                        // SHOOT!!

        assertEquals(beforeCount + 1,
                canvas.activeBullets.size());             // activeBullet   :   should have size of : beforeCount + 1
        assertEquals(beforeAmmo - 1, p.ammo);    // ammo           :   should == beforeAmmo
        assertTrue(p.justShot);                           // justShot       :   should be True

        bullet b = canvas.activeBullets.
                get(canvas.activeBullets.size() - 1);     // get bullet to query
        assertEquals(10, b.speed);               // speed          :   should be facing right => +10
        assertNotNull(b.startPoint);                      // startPoint     :   shouldn't be null


        assertEquals(100 + 47, b.x);             // x              :    should consider x's offset of 47
                                                          // Regression Note:    V1's fixed spawn offset is +47 pixels
        assertTrue(b.x > p.x, "Bullet should spawn in front of player when facing right");
        assertEquals(200 + 10, b.y);             // y              :    should be 210

    }

    @Test
    @DisplayName("shoot() consumes ammo and adds a bullet when not on cooldown (facing left)")
    void shootAddsBulletFacingLeft() {
        Player p = new Player(100, 200, 3);

        p.keys[0] = true;
        p.keys[1] = false;

        p.ammo = 2;
        p.justShot = false;
        p.lastShot = 0;

        int beforeAmmo = p.ammo;
        int beforeCount = canvas.activeBullets.size();

        p.shoot();

        assertEquals(beforeAmmo - 1, p.ammo);
        assertTrue(p.justShot);
        assertEquals(beforeCount + 1, canvas.
                activeBullets.size());

        bullet b = canvas.activeBullets.get(canvas.
                activeBullets.size() - 1);
        assertEquals(-10, b.speed);             // speed    :   should be -10
        assertNotNull(b.startPoint);
        assertEquals(100 - 25, b.x);            // x        :   should be -25
        assertEquals(200 + 10, b.y);            // y        :   should be 210
    }

    @Test
    @DisplayName("shoot() does nothing when ammo is zero")
    void shootDoesNothingWhenNoAmmo() {
        Player p = new Player(0, 0, 3);
        p.state = new entitystate(true, "idle");

        p.ammo = 0;
        p.justShot = false;
        p.lastShot = 0;

        int beforeCount = canvas.activeBullets.size();

        p.shoot();

        assertEquals(beforeCount,
                canvas.activeBullets.size());       // activeBullets' size  :   should == beforeCount
        assertEquals(0, p.ammo);           // ammo                 :    should be 0
        assertFalse(p.justShot);                    // justShot             :   should be False
    }
}
