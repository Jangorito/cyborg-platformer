package CyborgPlatformer.fx;

import CyborgPlatformer.app.CyborgPlatformerApp;
import CyborgPlatformer.controller.GameController;
import CyborgPlatformer.input.InputState;
import CyborgPlatformer.model.entities.Bullet;
import CyborgPlatformer.model.entities.Entity;
import CyborgPlatformer.model.entities.Player;
import CyborgPlatformer.model.world.TileLevel;
import CyborgPlatformer.model.world.World;
import CyborgPlatformer.model.entities.Enemy;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public final class FxLauncher extends Application {

    private boolean left, right, jump, shoot;
    private boolean reset;
    private boolean showTiles = true;


    @Override
    public void start(Stage stage) {
        CyborgPlatformerApp app = new CyborgPlatformerApp();
        GameController controller = app.getController();
        World world = app.getWorld();
        Player player = app.getPlayer();

        Canvas canvas = new Canvas(960, 540);
        GraphicsContext g = canvas.getGraphicsContext2D();

        Scene scene = new Scene(new StackPane(canvas));
        hookInput(scene);

        stage.setTitle("CyborgPlatformer V2 - Debug View");
        stage.setScene(scene);
        stage.show();

        new AnimationTimer() {
            private long last = -1;

            @Override
            public void handle(long now) {
                if (last < 0) {
                    last = now;
                    return;
                }

                double dt = (now - last) / 1_000_000_000.0;
                last = now;

                // avoid huge dt when tabbing/window dragging
                dt = Math.min(dt, 1.0 / 30.0);

                InputState input = new InputState(left, right, jump, shoot, reset);

                controller.step(dt, input);

                render(g, world, player, canvas.getWidth(), canvas.getHeight());
            }
        }.start();
    }

    private void hookInput(Scene scene) {
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case A, LEFT -> left = true;
                case D, RIGHT -> right = true;
                case W, UP, SPACE -> jump = true;
                case J, K, CONTROL -> shoot = true;
                case R -> reset = true;
                case T -> showTiles = !showTiles;

            }
        });
        scene.setOnKeyReleased(e -> {
            switch (e.getCode()) {
                case A, LEFT -> left = false;
                case D, RIGHT -> right = false;
                case W, UP, SPACE -> jump = false;
                case J, K, CONTROL -> shoot = false;
                case R -> reset = false;
            }
        });
    }

    private void render(GraphicsContext g, World world, Player player, double w, double h) {
        g.clearRect(0, 0, w, h);

        // basic camera: keep player near center
        double camX = player.getX() - w / 2.0;
        double camY = player.getY() - h / 2.0;

        // draw solids
        if (showTiles && world.getLevel() instanceof TileLevel tl) {
            for (var b : tl.getSolids()) {
                double sx = b.x() - camX;
                double sy = b.y() - camY;

                // cheap view cull
                if (sx + b.width() < -100 || sx > w + 100 || sy + b.height() < -100 || sy > h + 100) continue;

                g.strokeRect(sx, sy, b.width(), b.height());
            }
        }

        // draw entities
        for (Entity e : world.getEntities()) {
            double ex = e.getX() - camX;
            double ey = e.getY() - camY;

            if (e instanceof Bullet b) {
                g.strokeRect(ex, ey, b.getWidth(), b.getHeight());
            } else {
                g.strokeRect(ex, ey, e.getWidth(), e.getHeight());
            }
        }

        // HUD (screen-space, not world-space)
        g.setFill(Color.BLACK);
        g.setFont(Font.font(18));
        g.fillText("HP: " + player.getHealth(), 16, 24);
        g.fillText("Ammo: " + player.getAmmo(), 16, 46);
        g.fillText("Enemies: " + world.getEnemies().size(), 16, 68);

        Enemy nearest = null;
        double best = Double.POSITIVE_INFINITY;
        for (var e : world.getEnemies()) {
            double dx = e.getX() - player.getX();
            double dy = e.getY() - player.getY();
            double d = dx*dx + dy*dy;
            if (d < best) { best = d; nearest = e; }
        }
        if (nearest != null) {
            g.fillText("Nearest Enemy HP: " + nearest.getHealth(), 16, 90);
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}
