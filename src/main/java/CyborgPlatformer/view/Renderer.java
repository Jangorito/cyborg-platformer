package CyborgPlatformer.view;

import CyborgPlatformer.controller.GameController;
import CyborgPlatformer.model.entities.Bullet;
import CyborgPlatformer.model.entities.Enemy;
import CyborgPlatformer.model.entities.Entity;
import CyborgPlatformer.model.entities.Player;
import CyborgPlatformer.model.world.TileLevel;
import CyborgPlatformer.model.world.World;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.Objects;

/**
 * Debug renderer for V2.
 *
 * Responsibilities:
 * - Draw solids and entity bounds (Phase 1).
 * - Draw debug HUD text (Phase 1).
 *
 * Notes:
 * - No mutation of simulation state.
 * - Later phases replace debug rects with tiles/sprites while keeping call site stable.
 */
public final class Renderer {

    private final Camera camera;

    public Renderer(Camera camera) {
        this.camera = Objects.requireNonNull(camera);
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

        // Phase 1: center camera on player (matches FxLauncher)
        camera.centerOn(player);

        // draw solids
        if (showTiles && world.getLevel() instanceof TileLevel tl) {
            for (var b : tl.getSolids()) {
                double sx = camera.worldToScreenX(b.x());
                double sy = camera.worldToScreenY(b.y());

                // cheap view cull
                if (sx + b.width() < -100 || sx > w + 100 || sy + b.height() < -100 || sy > h + 100) continue;

                g.strokeRect(sx, sy, b.width(), b.height());
            }
        }

        // draw entities
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
