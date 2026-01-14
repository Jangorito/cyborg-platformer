package CyborgPlatformer.legacy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EnemyTest {

    @BeforeEach
    void setUp() {
        // Reset shared/global state used by bullets/enemies
        MapBlocks.map.clear();
        canvas.enemies = new ArrayList<>();
        canvas.activeBullets = new ArrayList<>();

        // Dummy images required for CyborgPlatformer.legacy.Enemy & hitbox maths
        Image enemyDummy = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Enemy.idleSprites = new Image[]{ enemyDummy };   // constructor uses idleSprites[0]
        Enemy.walkingSprites = new Image[]{ enemyDummy };
        Enemy.runningSprites = new Image[]{ enemyDummy };
        Enemy.hurtSprite = enemyDummy;

        // Bullet constructor depends on bulletImage existing
        bullet.bulletImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);

        // kill() via doBehavior() needs a player (e.g kill() awards ammo)
        Image playerDummy = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Player.idleSprites = new Image[]{ playerDummy };
        Player.runningSprites = new Image[]{ playerDummy, playerDummy, playerDummy, playerDummy, playerDummy, playerDummy };
        Player.hurtSprites = new Image[]{ playerDummy, playerDummy };
        Player.shootingSprite = playerDummy;

        canvas.keysPressed = new boolean[4];
        canvas.player = new Player(0, 0, 3);
    }

    @Test
    @DisplayName("CyborgPlatformer.legacy.Enemy constructor sets position and uses V1 default health (2) regardless of parameter")
    void constructorSetsFields() {
        Enemy z = new Enemy(100, 200, 999);     // create enemy instance

        assertEquals(100, z.x);         // x        :   should be 100
        assertEquals(200, z.y);         // y        :   should be 200

        // health hardcoded to 2 therefore:
        assertEquals(2, z.health);      // health   :   should be 2 (3)

        assertNotNull(z.image);                  // image    :   shouldn't be Null
    }

    @Test
    @DisplayName("damage() reduces health by 1, marks damaged, and applies knockback velocity")
    void damageReducesHealthAndMarksDamaged() {
        Enemy z = new Enemy(0, 0, 2);

        int before = z.health;              // create integer var for testing health before mutation
        z.isDamaged = false;                // set isDamaged to false

        z.damage();                         // call damage!

        assertEquals(before - 1,
                z.health);                  // health       :   should == before - 1
        assertTrue(z.isDamaged);            // isDamaged    :   should be True
        assertEquals(-6.0,
                z.velocity, 0.0001);  // velocity     :   should == -6
    }

    @Test
    @DisplayName("damage() does not apply repeatedly while already damaged")
    void damageIsSingleHitWhileDamaged() {
        Enemy z = new Enemy(0, 0, 2);

        z.isDamaged = true;      // simulate still in damaged window
        int before = z.health;

        z.damage();

        assertEquals(before, z.health, "Health should not change while isDamaged is true");
    }

    @Test
    @DisplayName("Bullet collides with enemy and triggers damage()")
    void bulletCollisionTriggersDamage() {
        Enemy z = new Enemy(0, 0, 2);
        z.isDamaged = false;
        int before = z.health;

        canvas.enemies.add(z);

        bullet b = new bullet(0, 0);        // create CyborgPlatformer.legacy.bullet object at enemy's position
        b.startPoint = new Point(b.x, b.y);       // set x & y

        boolean collided = b.collidesEnemy();     // create collided boolean to test before mutation

        assertTrue(collided, "Bullet should overlap enemy at same position");
        assertEquals(before - 1, z.health, "CyborgPlatformer.legacy.Enemy health should decrease by 1 on hit");
        assertTrue(z.isDamaged, "CyborgPlatformer.legacy.Enemy should be marked as damaged after CyborgPlatformer.legacy.bullet hit");
    }

    @Test
    @DisplayName("Bullet update removes itself from activeBullets when it hits an enemy")
    void bulletUpdateRemovesOnEnemyHit() {
        Enemy z = new Enemy(0, 0, 2);
        z.isDamaged = false;
        canvas.enemies.add(z);

        bullet b = new bullet(0, 0);
        b.startPoint = new Point(b.x, b.y);
        b.speed = 10;

        canvas.activeBullets.add(b);    // add CyborgPlatformer.legacy.bullet to activeBullets list

        b.update();                     // call update

        assertFalse(canvas.activeBullets.contains(b),
                "Bullet should remove itself from CyborgPlatformer.legacy.canvas.activeBullets after hitting enemy");
    }

    @Test
    @DisplayName("doBehavior() kills enemy when health <= 0: removes from list and awards ammo")
    void doBehaviorKillsEnemyAtZeroHealth() {
        Enemy z = new Enemy(0, 0, 2);
        canvas.enemies.add(z);

        int ammoBefore = canvas.player.ammo;    // create int var to check ammo before testing

        z.health = 0;                           // triggers kill() inside doBehavior()
        z.doBehavior();                         // call doBehaviour()

        assertFalse(canvas.enemies.contains(z), "CyborgPlatformer.legacy.Enemy should be removed from CyborgPlatformer.legacy.canvas.enemies on kill()");
        assertEquals(ammoBefore + 2, canvas.player.ammo, "kill() should award +2 ammo to player");
    }
}
