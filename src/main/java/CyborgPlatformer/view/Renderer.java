package CyborgPlatformer.view;

import CyborgPlatformer.assets.AssetManager;
import CyborgPlatformer.controller.GameController;
import CyborgPlatformer.model.entities.Bullet;
import CyborgPlatformer.model.entities.Enemy;
import CyborgPlatformer.model.entities.Entity;
import CyborgPlatformer.model.entities.Player;
import CyborgPlatformer.model.world.TileLevel;
import CyborgPlatformer.model.world.World;
import CyborgPlatformer.view.animation.EnemySpriteAnimator;
import CyborgPlatformer.view.animation.PlayerSpriteAnimator;
import CyborgPlatformer.view.model.EnemyRenderState;
import CyborgPlatformer.view.model.EnemyRenderStateFactory;
import CyborgPlatformer.view.model.PlayerRenderState;
import CyborgPlatformer.view.model.PlayerRenderStateFactory;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import java.util.Objects;

public final class Renderer {

    private static final int TILE_SIZE = 48;
    private static final double BASE_PLAYER_W = 30;

    private final Camera camera;
    private final AssetManager assets;

    private final PlayerRenderStateFactory playerStateFactory = new PlayerRenderStateFactory();
    private final PlayerSpriteAnimator playerAnimator;

    private final Map<Integer, EnemySpriteAnimator> enemyAnimatorsById = new HashMap<>();


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
        long now = System.nanoTime();

        PlayerRenderState ps = playerStateFactory.build(player, controller);
        Image pImg = playerAnimator.resolve(ps, now);
        drawPlayerSprite(g, pImg, ps);

        /// tuning
//        double muzzleWorldX = player.getX() + (controller.isFacingRight() ? 52 : -2);
//        double muzzleWorldY = player.getY() - 19;

//        double mx = px(camera.worldToScreenX(muzzleWorldX));
//        double my = px(camera.worldToScreenY(muzzleWorldY));
//
//        g.setStroke(Color.RED);
//        g.strokeLine(mx - 4, my, mx + 4, my);
//        g.strokeLine(mx, my - 4, mx, my + 4);


        // =========================
        //           Enemy
        // =========================

        // track alive enemy IDs this frame so we can cleanup animators
        Set<Integer> aliveEnemyIds = new HashSet<>();

        for (Entity e : world.getEntities()) {
            if (e instanceof Player) continue;
            if (e instanceof Bullet) continue;


            if (e instanceof Enemy enemy) {
                int id = enemy.getId();
                aliveEnemyIds.add(id);

                EnemySpriteAnimator anim =
                        enemyAnimatorsById.computeIfAbsent(id, k -> new EnemySpriteAnimator(assets));

                EnemyRenderState rs = EnemyRenderStateFactory.from(enemy);
                Image sprite = anim.resolve(rs, now);

                if (sprite != null) {
                    double sx = camera.worldToScreenX(enemy.getX());
                    double sy = camera.worldToScreenY(enemy.getY());

                    double enemyW = sprite.getWidth();
                    double enemyH = sprite.getHeight();

                    double drawX = sx + (enemy.getWidth() / 2.0) - (enemyW / 2.0);
                    double drawY = sy + enemy.getHeight() - enemyH;

                    drawFlipped(g, sprite, drawX, drawY, enemyW, enemyH, rs.facingRight());
                }

                continue;
            }

            // Fallback debug rects
            double ex = camera.worldToScreenX(e.getX());
            double ey = camera.worldToScreenY(e.getY());

//            if (e instanceof Bullet b) {
//                g.strokeRect(ex, ey, b.getWidth(), b.getHeight());
//            } else {
//                g.strokeRect(ex, ey, e.getWidth(), e.getHeight());
//            }
        }

        // animator clean up
        enemyAnimatorsById.keySet().removeIf(id -> !aliveEnemyIds.contains(id));
        renderBullets(g, world);


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
        g.fillText("Bullet Size: " + assets.bullet().getWidth() + ", " + assets.bullet().getHeight(), hudX, hudY); hudY += line;
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

        double imgW = img.getWidth();
        double imgH = img.getHeight();

        // Foot-anchored Y (stable across frames)
        double drawY = sy + ps.h() - imgH;

        // Stable anchor X based on "normal" player width, not the current frame width
        double baseX = sx + (ps.w() / 2.0) - (BASE_PLAYER_W / 2.0);

        // If this frame is wider (e.g., shoot is 55px), extend forward instead of re-centering
        double drawX = baseX;
        if (!ps.facingRight()) {
            drawX = baseX - (imgW - BASE_PLAYER_W);
        }

        // Pixel snap (reduces shaking from sub-pixel rendering)
        drawX = px(drawX);
        drawY = px(drawY);

        // Use your existing flip helper
        drawFlipped(g, img, drawX, drawY, imgW, imgH, ps.facingRight());
    }



    private void drawFlipped(
            GraphicsContext g,
            Image img,
            double x,
            double y,
            double w,
            double h,
            boolean facingRight
    ) {
        if (facingRight) {
            g.drawImage(img, x, y, w, h);
        } else {
            g.save();

            // Flip around vertical center of the sprite
            g.translate(x + w, y);
            g.scale(-1, 1);

            g.drawImage(img, 0, 0, w, h);

            g.restore();
        }
    }

    private void renderBullets(GraphicsContext g, World world) {
        Image bulletImg = assets.bullet();
        if (bulletImg == null) return;

        double imgW = bulletImg.getWidth();
        double imgH = bulletImg.getHeight();

        for (Entity e : world.getEntities()) {
            if (e instanceof Bullet b) {
                double drawX = px(camera.worldToScreenX(b.getX()));
                double drawY = px(camera.worldToScreenY(b.getY()));
                drawFlipped(g, bulletImg, drawX, drawY, imgW, imgH, b.getVx() >= 0);

                // hitbox overlay (same origin as b.x/b.y)
                g.setStroke(Color.LIMEGREEN);
                g.strokeRect(drawX, drawY, b.getWidth(), b.getHeight());

                // sprite bounds overlay (should match image dims)
                g.setStroke(Color.YELLOW);
                g.strokeRect(drawX, drawY, imgW, imgH);

            }
        }
    }


    private static double px(double v) { return Math.floor(v); }
}

