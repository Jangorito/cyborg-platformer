package CyborgPlatformer.view;

import CyborgPlatformer.assets.AssetManager;
import CyborgPlatformer.controller.GameController;
import CyborgPlatformer.model.entities.Bullet;
import CyborgPlatformer.model.entities.Enemy;
import CyborgPlatformer.model.entities.Entity;
import CyborgPlatformer.model.entities.Player;
import CyborgPlatformer.model.world.TileLevel;
import CyborgPlatformer.model.world.World;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.Objects;

/**
 * Debug renderer for V2.
 *
 * Responsibilities:
 * - Draw solids and entity bounds.
 * - Draw tile images using V1 legend mapping.
 * - Draw debug HUD text.
 *
 * Notes:
 * - No mutation of simulation state.
 * - Later phases replace debug rects with sprites/backgrounds/HUD while keeping call site stable.
 */
public final class Renderer {

    private static final int TILE_SIZE = 48;

    private final Camera camera;
    private final AssetManager assets;

    public Renderer(Camera camera, AssetManager assets) {
        this.camera = Objects.requireNonNull(camera);
        this.assets = Objects.requireNonNull(assets);
    }

    /**
     * V1 legend mapping:
     *  '1'..'9' -> 0..8
     *  'A'..'J' -> 9..18
     *  '0' -> empty (skip)
     */
    private int tileIndexFromChar(char c) {
        if (c >= '1' && c <= '9') return c - '1';
        if (c >= 'A' && c <= 'J') return 9 + (c - 'A');
        return -1;
    }

    private void drawParallaxLayer(GraphicsContext gc, Image bg, double parallaxCamX) {
        double bgW = bg.getWidth();
        if (bgW <= 0) return;

        // Screen-space offset: move left as camera moves right, at reduced speed
        double startX = -(parallaxCamX % bgW);

        // Cover the whole viewport (plus one tile for seamless wrap)
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

        double levelWidthPx = w; // fallback
        if (world.getLevel() instanceof TileLevel tl) {
            char[][] grid = tl.getTiles();
            if (grid.length > 0) {
                levelWidthPx = grid[0].length * TILE_SIZE;
            }
        }

        double camX = camera.camX();

//        Image[] bgs = assets.backgrounds();
//        drawParallaxLayer(g, bgs[3], camX / 16.0);
//        drawParallaxLayer(g, bgs[2], camX / 4.0);
//        drawParallaxLayer(g, bgs[1], camX / 2.0);
//        drawParallaxLayer(g, bgs[0], camX);

        Image[] bgs = assets.backgrounds();

        // Use visually meaningful layers
        drawParallaxLayer(g, bgs[1], camX / 16.0); // clouds
        drawParallaxLayer(g, bgs[2], camX / 8.0);  // far industry
        drawParallaxLayer(g, bgs[3], camX / 4.0);  // mid industry
        // optional foreground layer later: bgs[4]




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

                    // cheap view cull
                    if (sx + TILE_SIZE < -100 || sx > w + 100 || sy + TILE_SIZE < -100 || sy > h + 100) continue;

                    g.drawImage(assets.tiles()[idx], sx, sy, TILE_SIZE, TILE_SIZE);
                }
            }
        }


        // Optional: keep drawing solids as overlay/debug (comment out if you want)
        /*
        if (showTiles && world.getLevel() instanceof TileLevel tl) {
            for (var b : tl.getSolids()) {
                double sx = camera.worldToScreenX(b.x());
                double sy = camera.worldToScreenY(b.y());
                if (sx + b.width() < -100 || sx > w + 100 || sy + b.height() < -100 || sy > h + 100) continue;
                g.strokeRect(sx, sy, b.width(), b.height());
            }
        }
        */

        // draw entities (still debug rects for now)
        for (Entity e : world.getEntities()) {
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
}
