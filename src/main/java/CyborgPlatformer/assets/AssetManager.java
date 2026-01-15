package CyborgPlatformer.assets;

import javafx.scene.image.Image;
import javafx.scene.text.Font;

import java.io.InputStream;

/**
 * Loads and owns all JavaFX assets (images, fonts).
 *
 * Responsibilities:
 * - Load once from classpath resources.
 * - Provide typed accessors so the rest of the code never hardcodes paths.
 *
 * Notes:
 * - Rendering code should ONLY depend on these getters, not resource paths.
 * - Array ordering matches V1 expectations exactly.
 */
public final class AssetManager {

    // ===================== Player =====================
    private final Image[] playerIdle;   // 4
    private final Image[] playerRun;    // 6
    private final Image[] playerHurt;   // 2
    private final Image   playerShoot;  // 1

    // ===================== Enemy ======================
    private final Image[] enemyIdle;    // 8
    private final Image[] enemyWalk;    // 8
    private final Image[] enemyRun;     // 7
    private final Image   enemyHurt;    // 1 (V1 style)

    // ===================== World ======================
    private final Image[] tiles;        // 19
    private final Image[] backgrounds;  // 4
    private final Image   bullet;

    // ====================== UI ========================
    private final Image uiHeart;
    private final Image uiBox;
    private final Image uiAmmo;
    private final Font  uiFont;

    // ==================================================

    public AssetManager() {

        // ---- Player ----
        playerIdle  = loadNumbered("/assets/sprites/player/idle/player_idle_", 4);
        playerRun   = loadNumbered("/assets/sprites/player/run/player_run_", 6);
        playerHurt  = loadNumbered("/assets/sprites/player/hurt/player_hurt_", 2);
        playerShoot = loadImage("/assets/sprites/player/shoot/player_shoot.png");

        // ---- Enemy ----
        enemyIdle = loadNumbered("/assets/sprites/enemy/idle/enemy_idle_", 8);
        enemyWalk = loadNumbered("/assets/sprites/enemy/walk/enemy_walk_", 8);
        enemyRun  = loadNumbered("/assets/sprites/enemy/run/enemy_run_", 7);
        enemyHurt = loadImage("/assets/sprites/enemy/hurt/enemy_hurt.png");

        // ---- Bullet ----
        bullet = loadImage("/assets/sprites/bullet/bullet.png");

        // ---- UI ----
        uiHeart = loadImage("/assets/ui/heart.png");
        uiBox   = loadImage("/assets/ui/box.png");
        uiAmmo  = loadImage("/assets/ui/ammo.png");

        // ---- Tiles (0–18) ----
        tiles = new Image[19];
        for (int i = 0; i < tiles.length; i++) {
            tiles[i] = loadImage("/assets/tiles/tile_" + i + ".png");
        }

        // ---- Background layers (0–3) ----
        backgrounds = new Image[4];
        for (int i = 0; i < backgrounds.length; i++) {
            backgrounds[i] = loadImage("/assets/background/layer" + i + ".png");
        }

        // ---- Font ----
        uiFont = loadFont("/assets/fonts/ui.ttf", 18);
    }

    // =================== Accessors ====================

    public Image[] playerIdle()  { return playerIdle.clone(); }
    public Image[] playerRun()   { return playerRun.clone(); }
    public Image[] playerHurt()  { return playerHurt.clone(); }
    public Image   playerShoot() { return playerShoot; }

    public Image[] enemyIdle()   { return enemyIdle.clone(); }
    public Image[] enemyWalk()   { return enemyWalk.clone(); }
    public Image[] enemyRun()    { return enemyRun.clone(); }
    public Image   enemyHurt()   { return enemyHurt; }

    public Image   bullet()      { return bullet; }

    public Image   uiHeart()     { return uiHeart; }
    public Image   uiBox()       { return uiBox; }
    public Image   uiAmmo()      { return uiAmmo; }
    public Font    uiFont()      { return uiFont; }

    public Image[] tiles()       { return tiles.clone(); }
    public Image[] backgrounds() { return backgrounds.clone(); }

    // =================== Load helpers =================

    private static Image loadImage(String path) {
        try (InputStream in = AssetManager.class.getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalStateException("Missing resource: " + path);
            }
            return new Image(in);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load image: " + path, e);
        }
    }

    private static Image[] loadNumbered(String prefix, int count) {
        Image[] out = new Image[count];
        for (int i = 0; i < count; i++) {
            out[i] = loadImage(prefix + i + ".png");
        }
        return out;
    }

    private static Font loadFont(String path, double size) {
        try (InputStream in = AssetManager.class.getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalStateException("Missing resource: " + path);
            }
            Font font = Font.loadFont(in, size);
            if (font == null) {
                throw new IllegalStateException("Font load returned null: " + path);
            }
            return font;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load font: " + path, e);
        }
    }
}
