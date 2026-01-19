package CyborgPlatformer.assets;

import javafx.scene.image.Image;
import javafx.scene.text.Font;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;

/**
 * Loads and owns all JavaFX assets .
 *
 * Folder layout:
 * - /Background
 * - /Font
 * - /Sprites
 * - /Tiles
 *
 * Notes:
 * - Frame numbering is 1-based in your resources (e.g., Idle_1.png).
 * - Paths are case-sensitive when packaged -> match folder/file casing exactly.
 */
public final class AssetManager {

    // ===================== Player =====================
    private final Image[] playerIdle1;   // 4
    private final Image[] playerIdle;   // 3
    private final Image[] playerRun;    // 6
    private final Image[] playerHurt;   // 2
    private final Image   playerShoot;  // 1

    // ===================== Enemy ======================
    private final Image[] enemyIdle;    // 8
    private final Image[] enemyWalk;    // 8
    private final Image[] enemyRun;     // 7
    private final Image   enemyHurt;    // 1

    // ===================== World ======================
    private final Image[] tiles;        //
    private final Image[] backgrounds;  //
    private final Image   bullet;       //

    // ====================== UI ========================
    private final Image uiHeart;
    private final Image uiBox;
    private final Image uiAmmo;

    private final Font  uiFont;

    public AssetManager() {

        // ---- Player ----
//        playerIdle1  = loadNumbered1Based("/Sprites/Player/idle/Cyborg_idle_", 4);
        playerIdle1 = loadNumbered1Based(
                "/Sprites/Player/idle/Cyborg_idle_",
                4
        );
        playerIdle = Arrays.copyOfRange(
                playerIdle1,
                1, // start at index 1 (frame 2)
                4  // exclusive (up to frame 4)
        );

        playerRun   = loadNumbered1Based("/Sprites/Player/run/Cyborg_run_", 6);
        playerHurt  = loadNumbered1Based("/Sprites/Player/hurt/Cyborg_hurt_", 2);
        playerShoot = loadImage("/Sprites/Player/shoot/shootingSprite.png");

        bullet = loadImage("/Sprites/Player/shoot/bullet.png");

        uiAmmo  = loadImage("/Sprites/ammo.png");
        uiBox   = loadImage("/Sprites/box.png");
        uiHeart = loadImage("/Sprites/heart.png");

        // ---- Enemy ----
        enemyIdle = loadNumbered1Based("/Sprites/Enemy/Idle/Idle_", 8);
        enemyRun  = loadNumbered1Based("/Sprites/Enemy/Running/Run_", 7);
        enemyWalk = loadNumbered1Based("/Sprites/Enemy/Walking/Walk_", 8);
        enemyHurt = loadImage("/Sprites/Enemy/Hurt.png");

        // ---- Tiles ----
        tiles = new Image[] {
                loadImage("/Tiles/1_FrameTopLeftCorner.png"),       // 0
                loadImage("/Tiles/2_FrameTopRightCorner.png"),      // 1
                loadImage("/Tiles/3_FrameBottomLeftCorner.png"),    // 2
                loadImage("/Tiles/4_FrameBottomRightCorner.png"),   // 3
                loadImage("/Tiles/5_FrameTopMid.png"),              // 4
                loadImage("/Tiles/6_FrameLeftMid.png"),             // 5
                loadImage("/Tiles/7_FrameRightMid.png"),            // 6
                loadImage("/Tiles/8_FrameBottomMod.png"),           // 7
                loadImage("/Tiles/9_FrameMid.png"),                 // 8
                loadImage("/Tiles/A_Box.png"),                      // 9
                loadImage("/Tiles/B_HalfSlab.png"),                 // 10
                loadImage("/Tiles/C_IndustrialTabLeft.png"),        // 11
                loadImage("/Tiles/D_IndustrialSlabMid.png"),        // 12
                loadImage("/Tiles/E_IndustrialSlabRight.png"),      // 13
                loadImage("/Tiles/F_LightPole.png"),                // 14
                loadImage("/Tiles/G_LightTop.png"),                 // 15
                loadImage("/Tiles/H_TreadLeft.png"),                // 16
                loadImage("/Tiles/I_TreadMid.png"),                 // 17
                loadImage("/Tiles/J_TreadRight.png")                // 18
        };

        // ---- Background layers  ----
        backgrounds = new Image[] {
                loadImage("/Background/1_Background.png"),
                loadImage("/Background/2_Background.png"),
                loadImage("/Background/3_Background.png"),
                loadImage("/Background/4_Background.png")
        };

        // ---- Font ----
        uiFont = loadFont("/Font/font.ttf", 18);
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
        Objects.requireNonNull(path);

        try (InputStream in = AssetManager.class.getResourceAsStream(path)) {
            if (in == null) {
                throw new IllegalStateException("Missing resource: " + path);
            }
            return new Image(in);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load image: " + path, e);
        }
    }

    /** Loads prefix + (1..count) + ".png" */
    private static Image[] loadNumbered1Based(String prefix, int count) {
        Image[] out = new Image[count];
        for (int i = 1; i <= count; i++) {
            out[i - 1] = loadImage(prefix + i + ".png");
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
