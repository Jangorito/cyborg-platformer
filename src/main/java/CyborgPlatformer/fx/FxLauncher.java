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

    private GameController controller;
    private World world;
    private Player player;
    private Canvas canvas;

    private boolean left, right, jump, shoot, reset;
    private boolean showTiles = true;


    @Override
    public void start(Stage stage) {
        CyborgPlatformerApp app = new CyborgPlatformerApp();

        this.controller = app.getController();
        this.world = app.getWorld();
        this.player = app.getPlayer();

        this.canvas = new Canvas(960, 540);
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

                render(g, canvas.getWidth(), canvas.getHeight());
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

    private void render(GraphicsContext g, double w, double h)
    {
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

        double hudX = 16;
        double hudY = 24;
        double line = 22;

        g.fillText("HP: " + player.getHealth(), hudX, hudY); hudY += line;
        g.fillText("Ammo: " + player.getAmmo(), hudX, hudY); hudY += line;
        g.fillText("Enemies: " + world.getEnemies().size(), hudX, hudY); hudY += line;
        g.fillText("MTF?: " + controller.hasMovedThisFrame(), hudX, hudY); hudY += line;
        g.fillText("enemiesAwake: "+ controller.isEnemiesAwake(), hudX, hudY); hudY += line;

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

    public static void main(String[] args) {
        launch(args);
    }
}
