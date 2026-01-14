package CyborgPlatformer.legacy;

import java.awt.*;
import java.time.Duration;

/**
 * CyborgPlatformer.legacy.Player-controlled CyborgPlatformer.legacy.entity.
 *
 * Responsibilities:
 * - Reads input state from {@link canvas#keysPressed}.
 * - Applies movement rules and shooting cooldown.
 * - Tracks attempts via {@link #deathCounter} and triggers respawn via {@link Game#spawnEntities()}.
 * - Draws HUD elements (health, ammo, controls text).
 *
 * v1 note:
 * - CyborgPlatformer.legacy.Player both simulates gameplay and draws UI; responsibilities should be separated for v2.
 */
public class Player extends entity {
    protected Point checkPoint = new Point(20, 300);
    protected boolean[] keys = canvas.keysPressed;
    protected boolean justShot;
    protected double lastShot;
    protected static int deathCounter;


    public static Image[] idleSprites;
    public static Image[] runningSprites;
    public static Image[] hurtSprites;
    public static Image shootingSprite;
    public static Image ammoBox;
    public static Image box;
    public static Image heart;

    public Player(int x, int y, int health) {
        super(idleSprites[0], x, y, health, 10, 30);
    }

    /**
     * Per-frame update for player.
     *
     * How:
     * - Apply gravity.
     * - Apply horizontal movement if not blocked by map collision.
     * - Manage jump state (jumpCounter) and acceleration while jump key is held.
     * - Check death conditions [if (out of bounds / health <= 0) {respawn}].
     * - Handle shooting input with cooldown.
     * - Update timers for damage flicker and shooting animation.
     * - Check win condition based on x position.
     * - update animation state and sprite.
     */

    public void update() {
        gravity();
        int maxSpeed = 5;
        // Query whether the next horizontal position causes a collision before doing movement.
        entity entityCopy = copy(x + speed, y);
        if (!entityCopy.intersect()) {
            x += speed;
            if (keys[0]) {
                if (speed > -maxSpeed) speed -= 1;
            }
            if (keys[1]) {
                if (speed < maxSpeed) speed += 1;
            }
            if (((!keys[0] && !keys[1]) || (keys[0] && keys[1])) && !isDamaged) speed = 0;
        } else {
            speed /= 2;
        }
        if (isGrounded) {
            jumpCounter = 0;
        }
        if (jumpCounter == 0 && !isGrounded) jumpCounter = 1;
        if (isGrounded || !keys[2] || velocity > 0) acceleration = 0.5;
        if (keys[2] && acceleration > 0.25) acceleration -= 0.02;
        if (y > 900 || health <= 0) {
            kill();
        }
        if (keys[3]) {
            shoot();
        }


        if ((System.currentTimeMillis() - damagedTime) > 1000 && isDamaged) isDamaged = false;

        if ((System.currentTimeMillis() - lastShot) > 500 && justShot) justShot = false;


        if (x > 8000) CyborgPlatform.game.isWon = true;

        updateState();
        animate();
    }


    public int updateCmr() {
        int camX = x - (1280 - 30) / 2;
        camX = Math.max(0, Math.min(camX, MapBlocks.mapWidth - 1280 - 30));
        return camX;
    }


    public boolean isFacingForwards() {
        if (!keys[0] && keys[1]) return true;
        if (keys[0] && !keys[1]) return false;
        return canvas.isLastDirectionForwards;
    }


    public boolean isMoving() {
        return (keys[0] || keys[1]);
    }


    public boolean isJumping() {
        return (jumpCounter > 0);
    }


    public void updateState() {
        String newState = "idle";
        if (isGrounded && isMoving()) newState = "running";
        if (isGrounded && !isMoving()) newState = "idle";
        if (!isGrounded) newState = "aerial";
        if (isDamaged) newState = "hurt";
        if (justShot) newState = "shooting";
        state = new entitystate(isFacingForwards(), newState);
    }


    public void animate() {
        switch (state.state) {
            case "idle" -> {
                if (lastAnimation >= idleSprites.length) lastAnimation = 0;
                if ((System.currentTimeMillis() - lastTime) > 250) {
                    image = idleSprites[lastAnimation];
                    lastAnimation++;
                    lastTime = System.currentTimeMillis();
                }
            }
            case "running" -> {
                if (lastAnimation >= runningSprites.length) lastAnimation = 0;
                if ((System.currentTimeMillis() - lastTime) > 180) {
                    image = runningSprites[lastAnimation];
                    lastAnimation++;
                    lastTime = System.currentTimeMillis();
                }
            }
            case "aerial" -> {
                if (velocity < 0) image = runningSprites[5];
            }
            case "hurt" -> {
                image = hurtSprites[1];
            }
            case "shooting" -> {
                image = shootingSprite;
            }
        }
    }

    /**
     * Fires a CyborgPlatformer.legacy.bullet if the player is not on cooldown and has ammo.
     *
     * v1 behaviour:
     * - Consumes 1 ammo per shot.
     * - Creates a {@link bullet} instance and stores it in {@link canvas#activeBullets}.
     * - Uses justShot + lastShot to limit fire rate.
     */

    public void shoot() {
        if (!justShot && ammo != 0) {
            ammo--;
            justShot = true;
            lastShot = System.currentTimeMillis();
            if (isFacingForwards()) {
                bullet bullet = new bullet(x + 47, y + 10);
                bullet.speed = 10;
                bullet.startPoint = new Point(bullet.x, bullet.y);
                canvas.activeBullets.add(bullet);
            } else {
                bullet bullet = new bullet(x - 25, y + 10);
                bullet.speed = -10;
                bullet.startPoint = new Point(bullet.x, bullet.y);
                canvas.activeBullets.add(bullet);
            }

        }
    }


    public void damage(Enemy e) {
        if (!isDamaged) {
            health--;
            damagedTime = System.currentTimeMillis();
            isDamaged = true;
            if (x < e.x) {
                speed = -4;
            } else speed = 4;
            velocity = -6;
        }
    }

    /**
     * Draws HUD (health hearts, ammo counter, attempts, controls text) called from rendering.
     * - update and rander are mixed, v2 potentially decouples
     */
    public void drawGUI(Graphics g) {
        Font font = canvas.font;
        font = font.deriveFont(60.0f);
        g.setColor(Color.BLACK);
        g.setFont(font);


        if (health > 0) g.drawImage(heart, 20 + canvas.cameraOffset, 20, null);
        if (health > 1) g.drawImage(heart, 89 + canvas.cameraOffset, 20, null);
        if (health > 2) g.drawImage(heart, 158 + canvas.cameraOffset, 20, null);


        g.drawImage(ammoBox, 1040 + canvas.cameraOffset, 20, null);
        g.drawString(String.valueOf(ammo), 1130 + canvas.cameraOffset, 68);


        long gameTimer = (long) (System.currentTimeMillis() - Game.startTime);
        Duration duration = Duration.ofMillis(gameTimer);
        long minutes = duration.toMinutes();
        duration = duration.minusMinutes(minutes);
        long seconds = duration.getSeconds();
        String timer = String.format("%02d" + ":" + "%02d", minutes, seconds);
        g.drawString(timer, 510 + canvas.cameraOffset, 68);

        font = font.deriveFont(20.0f);
        g.setColor(Color.BLACK);
        g.setFont(font);
        String[] controls = new String[]{
                "Use WASD to Move",
                "You can Double Jump",
                "Use SPACE to Shoot",
                "Press ESCAPE to Restart"
        };
        int printAtY = 250;
        for (String control : controls) {
            g.drawString(control, 470, printAtY);
            printAtY += 30;
        }
        g.drawString("This is The Winner Tunnel", 6800, 310);
        g.drawString("Just Keep Walking!", 6800, 340);
        g.drawString("ATTEMPTS: " + deathCounter, 20 + canvas.cameraOffset, 110);

    }

    /**
     * Handles player death / restart.
     *
     * How:
     * - Increments deathCounter (attempts).
     * - Respawns entities by calling {@link Game#spawnEntities()}.
     */

    public void kill() {
        deathCounter++;
        CyborgPlatform.game.spawnEntities();
    }
}
