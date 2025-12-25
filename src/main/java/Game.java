import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static java.lang.System.nanoTime;

public class Game {
    public boolean isWon = false;
    public static double startTime;
    protected boolean isRunning = true;

    public void loadImages(){

        try {
            Player.idleSprites = new Image[]{
                    ImageIO.read(new File("src/main/resources/Sprites/Player/idle/Cyborg_idle_1.png")),
                    ImageIO.read(new File("src/main/resources/Sprites/Player/idle/Cyborg_idle_2.png")),
                    ImageIO.read(new File("src/main/resources/Sprites/Player/idle/Cyborg_idle_3.png")),
                    ImageIO.read(new File("src/main/resources/Sprites/Player/idle/Cyborg_idle_4.png"))
            };

            Player.runningSprites = new Image[]{
                    ImageIO.read(new File("src/main/resources/Sprites/Player/run/Cyborg_run_1.png")),
                    ImageIO.read(new File("src/main/resources/Sprites/Player/run/Cyborg_run_2.png")),
                    ImageIO.read(new File("src/main/resources/Sprites/Player/run/Cyborg_run_3.png")),
                    ImageIO.read(new File("src/main/resources/Sprites/Player/run/Cyborg_run_4.png")),
                    ImageIO.read(new File("src/main/resources/Sprites/Player/run/Cyborg_run_5.png")),
                    ImageIO.read(new File("src/main/resources/Sprites/Player/run/Cyborg_run_6.png"))
            };

            Player.hurtSprites = new Image[]{
                    ImageIO.read(new File("src/main/resources/Sprites/Player/hurt/Cyborg_hurt_1.png")),
                    ImageIO.read(new File("src/main/resources/Sprites/Player/hurt/Cyborg_hurt_2.png"))
            };

            Player.heart = ImageIO.read(new File("src/main/resources/Sprites/heart.png"));
            Player.box = ImageIO.read(new File("src/main/resources/Sprites/box.png"));
            Player.ammoBox = ImageIO.read(new File("src/main/resources/Sprites/ammo.png"));
            Player.shootingSprite = ImageIO.read(new File("src/main/resources/Sprites/Player/shoot/shootingSprite.png"));
            bullet.bulletImage = ImageIO.read(new File("src/main/resources/Sprites/Player/shoot/bullet.png"));
            canvas.cloud = ImageIO.read(new File("src/main/resources/Sprites/cloud.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            MapBlocks.mapImages = new Image[]{
                    ImageIO.read(new File("src/main/resources/Tiles/1_FrameTopLeftCorner.png")),
                    ImageIO.read(new File("src/main/resources/Tiles/2_FrameTopRightCorner.png")),
                    ImageIO.read(new File("src/main/resources/Tiles/3_FrameBottomLeftCorner.png")),
                    ImageIO.read(new File("src/main/resources/Tiles/4_FrameBottomRightCorner.png")),
                    ImageIO.read(new File("src/main/resources/Tiles/5_FrameTopMid.png")),
                    ImageIO.read(new File("src/main/resources/Tiles/6_FrameLeftMid.png")),
                    ImageIO.read(new File("src/main/resources/Tiles/7_FrameRightMid.png")),
                    ImageIO.read(new File("src/main/resources/Tiles/8_FrameBottomMod.png")),
                    ImageIO.read(new File("src/main/resources/Tiles/9_FrameMid.png")),
                    ImageIO.read(new File("src/main/resources/Tiles/A_Box.png")),
                    ImageIO.read(new File("src/main/resources/Tiles/B_HalfSlab.png")),
                    ImageIO.read(new File("src/main/resources/Tiles/C_IndustrialTabLeft.png")),
                    ImageIO.read(new File("src/main/resources/Tiles/D_IndustrialSlabMid.png")),
                    ImageIO.read(new File("src/main/resources/Tiles/E_IndustrialSlabRight.png")),
                    ImageIO.read(new File("src/main/resources/Tiles/F_LightPole.png")),
                    ImageIO.read(new File("src/main/resources/Tiles/G_LightTop.png")),
                    ImageIO.read(new File("src/main/resources/Tiles/H_TreadLeft.png")),
                    ImageIO.read(new File("src/main/resources/Tiles/I_TreadMid.png")),
                    ImageIO.read(new File("src/main/resources/Tiles/J_TreadRight.png"))
            };

            canvas.font = Font.createFont(Font.TRUETYPE_FONT, new File("src/main/resources/Font/font.TTF"));
        } catch (IOException | FontFormatException e) {
            throw new RuntimeException(e);
        }

        try {
            Background.background = new Image[]{
                    ImageIO.read(new File("src/main/resources/Background/1_Background.png")),
                    ImageIO.read(new File("src/main/resources/Background/2_Background.png")),
                    ImageIO.read(new File("src/main/resources/Background/3_Background.png")),
                    ImageIO.read(new File("src/main/resources/Background/4_Background.png"))
            };
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {

            Enemy.idleSprites = new Image[]{
                    ImageIO.read(new File("src/main/resources/Sprites/Enemy/Idle/Idle_1.png")),
                    ImageIO.read(new File("src/main/resources/Sprites/Enemy/Idle/Idle_2.png")),
                    ImageIO.read(new File("src/main/resources/Sprites/Enemy/Idle/Idle_3.png")),
                    ImageIO.read(new File("src/main/resources/Sprites/Enemy/Idle/Idle_4.png")),
                    ImageIO.read(new File("src/main/resources/Sprites/Enemy/Idle/Idle_5.png")),
                    ImageIO.read(new File("src/main/resources/Sprites/Enemy/Idle/Idle_6.png")),
                    ImageIO.read(new File("src/main/resources/Sprites/Enemy/Idle/Idle_7.png")),
                    ImageIO.read(new File("src/main/resources/Sprites/Enemy/Idle/Idle_8.png"))
            };

            Enemy.walkingSprites = new Image[]{
                    ImageIO.read(new File("src/main/resources/Sprites/Enemy/Walking/Walk_1.png")),
                    ImageIO.read(new File("src/main/resources/Sprites/Enemy/Walking/Walk_2.png")),
                    ImageIO.read(new File("src/main/resources/Sprites/Enemy/Walking/Walk_3.png")),
                    ImageIO.read(new File("src/main/resources/Sprites/Enemy/Walking/Walk_4.png")),
                    ImageIO.read(new File("src/main/resources/Sprites/Enemy/Walking/Walk_5.png")),
                    ImageIO.read(new File("src/main/resources/Sprites/Enemy/Walking/Walk_6.png")),
                    ImageIO.read(new File("src/main/resources/Sprites/Enemy/Walking/Walk_7.png")),
                    ImageIO.read(new File("src/main/resources/Sprites/Enemy/Walking/Walk_8.png"))
            };

            Enemy.runningSprites = new Image[]{
                    ImageIO.read(new File("src/main/resources/Sprites/Enemy/Running/Run_1.png")),
                    ImageIO.read(new File("src/main/resources/Sprites/Enemy/Running/Run_2.png")),
                    ImageIO.read(new File("src/main/resources/Sprites/Enemy/Running/Run_3.png")),
                    ImageIO.read(new File("src/main/resources/Sprites/Enemy/Running/Run_4.png")),
                    ImageIO.read(new File("src/main/resources/Sprites/Enemy/Running/Run_5.png")),
                    ImageIO.read(new File("src/main/resources/Sprites/Enemy/Running/Run_6.png")),
                    ImageIO.read(new File("src/main/resources/Sprites/Enemy/Running/Run_7.png"))
            };

            Enemy.hurtSprite = ImageIO.read(new File("src/main/resources/Sprites/Enemy/Hurt.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void spawnEntities() {
        if (CyborgPlatform.game.isWon) Player.deathCounter = 0;
        CyborgPlatform.game.isWon = false;
        startTime = System.currentTimeMillis();
        canvas.player = new Player(20, 300, 3);
        canvas.enemies = new ArrayList<>();
        canvas.enemies.add(new Enemy(1475, 230,2));
        canvas.enemies.add(new Enemy(2570, 196,2));
        canvas.enemies.add(new Enemy(2750, 320,2));
        canvas.enemies.add(new Enemy(3060, 470,2));
        canvas.enemies.add(new Enemy(4219, 100,2));
        canvas.enemies.add(new Enemy(4900, 530,2));
        canvas.enemies.add(new Enemy(4970, 530,2));
        canvas.enemies.add(new Enemy(5040, 539,2));
        canvas.enemies.add(new Enemy(6397, 196,2));
        canvas.enemies.add(new Enemy(6540, 520,2));
        canvas.enemies.add(new Enemy(6600, 520,2));
        canvas.enemies.add(new Enemy(6660, 520,2));
        canvas.enemies.add(new Enemy(6720, 520,2));
        canvas.activeBullets = new ArrayList<>();
    }


    public void startTimer(canvas canvas) {
        double t = nanoTime();
        while (isRunning) {
            if ((nanoTime() - t) > 1000000000. / 60) {
                t = nanoTime();
                canvas.repaint();
            }
        }
    }
}
