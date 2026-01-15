package CyborgPlatformer.view;

import CyborgPlatformer.assets.AssetManager;
import CyborgPlatformer.controller.GameController;
import CyborgPlatformer.model.entities.Bullet;
import CyborgPlatformer.model.entities.Enemy;
import CyborgPlatformer.model.entities.Entity;
import CyborgPlatformer.model.entities.Player;
import CyborgPlatformer.model.world.TileLevel;
import CyborgPlatformer.model.world.World;
import CyborgPlatformer.view.animation.PlayerSpriteAnimator;
import CyborgPlatformer.view.model.PlayerRenderState;
import CyborgPlatformer.view.model.PlayerRenderStateFactory;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.Objects;

public final class Renderer {

    private static final int TILE_SIZE = 48;
    private static final double PLAYER_VISUAL_WIDTH  = 30;
    private static final double PLAYER_VISUAL_HEIGHT = 52;

    private final Camera camera;
    private final AssetManager assets;

    private final PlayerRenderStateFactory playerStateFactory = new PlayerRenderStateFactory();
    private final PlayerSpriteAnimator playerAnimator;

    public Renderer(Camera camera, AssetManager assets) {
        this.camera = Objects.requireNonNull(camera);
        this.assets = Objects.requireNonNull(assets);
        this.playerAnimator = new PlayerSpriteAnimator(assets);
    }

    private int tileIndexFromChar(char c) {
        if (c >= '1' && c <= '9') return c - '1';
        if (c >= 'A' && c <= 'J') return 9 + (c - 'A');
        return -1;
    }

    private void drawParallaxLayer(GraphicsContext gc, Image bg, double parallaxCamX) {
        double bgW = bg.getWidth();
        if (bgW <= 0) return;

        double startX = -(parallaxCamX % bgW);

        for (double x = startX; x < camera.viewportWidth() + bgW; x += bgW) {
            gc.drawImage(bg, x, 0);
        }
    }

    public void render(GraphicsContext g,
                       double w,
                       double h,
                       World world,
                       Player player,
                       GameController controller,
                       boolean showTiles,
                       boolean killFlag) {

        g.clearRect(0, 0, w, h);

        camera.followX(player.getX());

        double camX = camera.camX();

        // Backgrounds (parallax)
        Image[] bgs = assets.backgrounds();
        drawParallaxLayer(g, bgs[1], camX / 16.0); // clouds
        drawParallaxLayer(g, bgs[2], camX / 8.0);  // far industry
        drawParallaxLayer(g, bgs[3], camX / 4.0);  // mid industry

        // Tiles
        if (showTiles && world.getLevel() instanceof TileLevel tl) {
            char[][] grid = tl.getTiles();
            for (int row = 0; row < grid.length; row++) {
                char[] line = grid[row];
                for (int col = 0; col < line.length; col++) {
                    char tile = line[col];
                    if (tile == '0') continue;

                    int idx = tileIndexFromChar(tile);
                    if (idx < 0 || idx >= assets.tiles().length) continue;

                    double worldX = col * TILE_SIZE;
                    double worldY = row * TILE_SIZE;

                    double sx = camera.worldToScreenX(worldX);
                    double sy = camera.worldToScreenY(worldY);

                    if (sx + TILE_SIZE < -100 || sx > w + 100 || sy + TILE_SIZE < -100 || sy > h + 100) continue;

                    g.drawImage(assets.tiles()[idx], sx, sy, TILE_SIZE, TILE_SIZE);
                }
            }
        }

        // =========================
        //       Player sprite
        // =========================
        long nowMs = System.currentTimeMillis();

        PlayerRenderState ps = playerStateFactory.build(player, controller);
        Image pImg = playerAnimator.resolve(ps, nowMs);
        drawPlayerSprite(g, pImg, ps);

        // =========================
        // Other entities (debug rects for now)
        // =========================
        for (Entity e : world.getEntities()) {
            if (e instanceof Player) continue; // don't draw debug rect over player sprite

            double ex = camera.worldToScreenX(e.getX());
            double ey = camera.worldToScreenY(e.getY());

            if (e instanceof Bullet b) {
                g.strokeRect(ex, ey, b.getWidth(), b.getHeight());
            } else {
                g.strokeRect(ex, ey, e.getWidth(), e.getHeight());
            }
        }

        // HUD (screen-space)
        g.setFill(Color.BLACK);
        g.setFont(Font.font(18));

        double hudX = 16;
        double hudY = 24;
        double line = 22;

        g.fillText("Lives: " + controller.getLives(), hudX, hudY); hudY += line;
        g.fillText("HP: " + player.getHealth(), hudX, hudY); hudY += line;
        g.fillText("Ammo: " + player.getAmmo(), hudX, hudY); hudY += line;
        g.fillText("Position: " + (int)player.getX() + ", " + (int)player.getY(), hudX, hudY); hudY += line;
        g.fillText("GameOver: " + controller.isGameOver(), hudX, hudY); hudY += line;

        g.fillText("+_____________________+", hudX, hudY); hudY += line;
        g.fillText("BG0 w/h: " + (int)bgs[0].getWidth() + " / " + (int)bgs[0].getHeight(), hudX, hudY); hudY += line;
        g.fillText("BG1 w/h: " + (int)bgs[1].getWidth() + " / " + (int)bgs[1].getHeight(), hudX, hudY); hudY += line;
        g.fillText("+_____________________+", hudX, hudY); hudY += line;
        g.fillText("Assets OK (tiles): " + assets.tiles().length, hudX, hudY); hudY += line;
        g.fillText("KillEM?: " + killFlag, hudX, hudY); hudY += line;
        g.fillText("Enemies: " + world.getEnemies().size(), hudX, hudY); hudY += line;
        g.fillText("MTF?: " + controller.hasMovedAfterSpawn(), hudX, hudY); hudY += line;
        g.fillText("enemiesAwake: " + controller.isEnemiesAwake(), hudX, hudY); hudY += line;
        g.fillText("Invuln: " + player.isInKnockback(), hudX, hudY); hudY += line;

        Enemy nearest = null;
        double best = Double.POSITIVE_INFINITY;
        for (Enemy e : world.getEnemies()) {
            double dx = e.getX() - player.getX();
            double dy = e.getY() - player.getY();
            double d = dx * dx + dy * dy;
            if (d < best) { best = d; nearest = e; }
        }

        if (nearest != null) {
            g.fillText("Nearest Enemy HP: " + nearest.getHealth(), hudX, hudY); hudY += line;
            g.fillText("Nearest Enemy X: " + (int) nearest.getX(), hudX, hudY); hudY += line;
        }
    }

    private void drawPlayerSprite(GraphicsContext g, Image img, PlayerRenderState ps) {
        double sx = camera.worldToScreenX(ps.x());
        double sy = camera.worldToScreenY(ps.y());

        double drawW = PLAYER_VISUAL_WIDTH;
        double drawH = PLAYER_VISUAL_HEIGHT;

        // Foot-anchored positioning (V1-style)
        double drawX = sx + (ps.w() / 2.0) - (drawW / 2.0);
        double drawY = sy + ps.h() - drawH;

        g.save();

        if (!ps.facingRight()) {
            g.translate(drawX + drawW / 2.0, 0);
            g.scale(-1, 1);
            g.translate(-(drawX + drawW / 2.0), 0);
        }

        g.drawImage(img, drawX, drawY, drawW, drawH);
        g.restore();
    }}
