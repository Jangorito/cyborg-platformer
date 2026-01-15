package CyborgPlatformer.fx;

import CyborgPlatformer.app.CyborgPlatformerApp;
import CyborgPlatformer.controller.GameController;
import CyborgPlatformer.input.InputState;
import CyborgPlatformer.model.entities.Player;
import CyborgPlatformer.model.world.TileLevel;
import CyborgPlatformer.model.world.World;
import CyborgPlatformer.view.Camera;
import CyborgPlatformer.view.Renderer;
import CyborgPlatformer.assets.AssetManager;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


public final class FxLauncher extends Application {

    private GameController controller;
    private AssetManager assets;
    private World world;
    private Player player;

    private Canvas canvas;

    // input flags
    private boolean left, right, jump, shoot, reset, kill, cheat;

    // debug flags
    private boolean showTiles = true;

    // view
    private Camera camera;
    private Renderer renderer;

    @Override
    public void start(Stage stage) {
        CyborgPlatformerApp app = new CyborgPlatformerApp();

        this.controller = app.getController();
        this.world = app.getWorld();
        this.player = app.getPlayer();

        this.canvas = new Canvas(1280, 720);
        GraphicsContext g = canvas.getGraphicsContext2D();

        this.assets = new AssetManager();
        double levelWidthPx = computeLevelWidthPx(world);
        this.camera = new Camera(canvas.getWidth(), canvas.getHeight(), levelWidthPx);
        this.renderer = new Renderer(camera, assets);

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

                InputState input = new InputState(left, right, jump, shoot, reset, kill, cheat);
                controller.step(dt, input);

                renderer.render(
                        g,
                        canvas.getWidth(),
                        canvas.getHeight(),
                        world,
                        player,
                        controller,
                        showTiles,
                        kill
                );
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

                // note: L toggles (like your original)
                case L -> kill = !kill;

                case Q -> cheat = true;
            }
        });

        scene.setOnKeyReleased(e -> {
            switch (e.getCode()) {
                case A, LEFT -> left = false;
                case D, RIGHT -> right = false;
                case W, UP, SPACE -> jump = false;
                case J, K, CONTROL -> shoot = false;

                case R -> reset = false;

                // keep cheat as "hold" instead of sticky
                case Q -> cheat = false;
            }
        });
    }

    private static double computeLevelWidthPx(World world) {
        if (world.getLevel() instanceof TileLevel tl) {
            char[][] grid = tl.getTiles();
            if (grid.length > 0) {
                return grid[0].length * 48.0; // TILE_SIZE
            }
        }
        // Fallback: if level width can't be inferred, keep camera locked
        return 0.0;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
