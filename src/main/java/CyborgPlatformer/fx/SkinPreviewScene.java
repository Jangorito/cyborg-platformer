package CyborgPlatformer.fx;

import CyborgPlatformer.assets.AssetManager;
import CyborgPlatformer.view.animation.PlayerSpriteAnimator;
import CyborgPlatformer.view.skin.PlayerSkin;
import CyborgPlatformer.view.skin.PlayerSkinCache;
import CyborgPlatformer.view.skin.PlayerSkinStore;
import CyborgPlatformer.view.skin.PlayerSkins;
import CyborgPlatformer.view.skin.SkinnedPlayerAssets;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import CyborgPlatformer.model.entities.Player;
import CyborgPlatformer.model.world.TileLevel;
import CyborgPlatformer.model.world.TileLevelLoader;
import CyborgPlatformer.model.world.World;
import CyborgPlatformer.controller.GameController;
import CyborgPlatformer.view.Camera;
import CyborgPlatformer.view.Renderer;
import CyborgPlatformer.view.model.PlayerRenderState;

public class SkinPreviewScene {

    private final Stage stage;
    private final Scene previous;

    public SkinPreviewScene(Stage stage, Scene previous) {
        this.stage = stage;
        this.previous = previous;
    }

    public Scene createScene() {
        BorderPane root = new BorderPane();

        final double W = 1280;
        final double H = 720;

            // Declare canvas and graphics context here; create canvas after UI so we can
            // size it relative to the UI width (ui.getPrefWidth()).
            Canvas canvas;
            GraphicsContext g;

        // Right-side UI
        VBox ui = new VBox(8);
        ui.setPadding(new Insets(12));
        ui.setPrefWidth(260);
        ui.setStyle("-fx-background-color: rgba(0,0,0,0.4);");

        Label title = new Label("Skin Preview");
        title.setTextFill(Color.WHITE);

        ToggleGroup skins = new ToggleGroup();
        ToggleButton classic = new ToggleButton("Classic");
        ToggleButton stealth = new ToggleButton("Stealth");
        ToggleButton test = new ToggleButton("Test");

        classic.setToggleGroup(skins);
        stealth.setToggleGroup(skins);
        test.setToggleGroup(skins);

        // select current
        PlayerSkin cur = PlayerSkinStore.get();
        if (cur.equals(PlayerSkins.STEALTH)) stealth.setSelected(true);
        else if (cur.equals(PlayerSkins.TEST)) test.setSelected(true);
        else classic.setSelected(true);

        Button back = new Button("Back");
        back.setOnAction(e -> stage.setScene(previous));

        ui.getChildren().addAll(title, classic, stealth, test, back);
        ui.setAlignment(Pos.TOP_CENTER);

            root.setRight(ui);

            // Now create the canvas sized to leave room for the right-side UI and add it.
            double canvasW = W - ui.getPrefWidth();
            canvas = new Canvas(canvasW, H);
            g = canvas.getGraphicsContext2D();
            root.setCenter(canvas);
        root.setRight(ui);

        Scene scene = new Scene(root, W, H);

        // Assets and animator
        AssetManager assets = new AssetManager();
        PlayerSkinCache skinCache = new PlayerSkinCache(assets);

        final PlayerSkin[] currentSkin = new PlayerSkin[]{PlayerSkinStore.get()};
        final PlayerSpriteAnimator[] animator = new PlayerSpriteAnimator[1];
        animator[0] = new PlayerSpriteAnimator(skinCache.get(currentSkin[0]));

        // Load level to compute spawn and tiles
        TileLevel level = TileLevelLoader.load(getClass().getResourceAsStream("/Maps.txt"));
        char[][] tileGrid = level.getTiles();

        // Camera control holders (mutable via UI sliders)
        // Seed sliders with your preferred values (zoom ~2, camY ~360)
        double[] cameraZoom = new double[]{2.0};
        double[] cameraWorldX = new double[]{0.0};
        double[] cameraWorldY = new double[]{360.0};

        // Build minimal model objects required by Renderer
        World world = new World();
        world.setLevel(level);

        Player player = new Player();
        player.setSize(30, 48);

        // compute spawn and position player at spawn by default
        double[] spawn = pickSpawnOnFloor(level, 30, 48);
        double spawnX = spawn[0];
        double spawnY = spawn[1];

        player.setPosition(spawnX, spawnY);
        player.setGrounded(true);

        // controller (won't be stepped) - used for render state
        GameController controller = new GameController(world, player, spawnX, spawnY);
        world.setController(controller);

        // --- UI sliders for camera tuning ---
        int cols = tileGrid.length > 0 ? tileGrid[0].length : 0;
        int rows = tileGrid.length;
        final int TILE_PX = TileLevelLoader.TILE_SIZE;
        double levelWidthPx = cols * TILE_PX;

        Slider zoomSlider = new Slider(0.5, 3.0, cameraZoom[0]);
        zoomSlider.setMajorTickUnit(0.5);
        zoomSlider.setShowTickMarks(true);
        zoomSlider.setShowTickLabels(true);
        zoomSlider.setPrefWidth(140);

        Label zoomValue = new Label(String.format("%.2f", cameraZoom[0]));
        zoomValue.setTextFill(Color.WHITE);
        zoomSlider.valueProperty().addListener((obs, o, n) -> {
            cameraZoom[0] = n.doubleValue();
            zoomValue.setText(String.format("%.2f", n.doubleValue()));
        });

        Slider xSlider = new Slider(0, Math.max(0, levelWidthPx), cameraWorldX[0]);
        xSlider.setMajorTickUnit(Math.max(1, levelWidthPx / 4.0));
        xSlider.setShowTickMarks(false);
        xSlider.setPrefWidth(140);

        Label xValue = new Label(String.valueOf((int)cameraWorldX[0]));
        xValue.setTextFill(Color.WHITE);
        xSlider.valueProperty().addListener((obs, o, n) -> {
            cameraWorldX[0] = n.doubleValue();
            xValue.setText(String.valueOf((int)Math.round(n.doubleValue())));
        });

        Slider ySlider = new Slider(0, Math.max(0, rows * TILE_PX), cameraWorldY[0]);
        ySlider.setMajorTickUnit(Math.max(1, rows * TILE_PX / 4.0));
        ySlider.setShowTickMarks(false);
        ySlider.setPrefWidth(140);

        Label yValue = new Label(String.valueOf((int)cameraWorldY[0]));
        yValue.setTextFill(Color.WHITE);
        ySlider.valueProperty().addListener((obs, o, n) -> {
            cameraWorldY[0] = n.doubleValue();
            yValue.setText(String.valueOf((int)Math.round(n.doubleValue())));
        });

        // Layout sliders in UI (place before Back button for visibility)
        Label zoomLabel = new Label("Zoom"); zoomLabel.setTextFill(Color.WHITE);
        Label xLabel = new Label("Cam X"); xLabel.setTextFill(Color.WHITE);
        Label yLabel = new Label("Cam Y"); yLabel.setTextFill(Color.WHITE);

        HBox zoomRow = new HBox(8, zoomLabel, zoomSlider, zoomValue);
        HBox xRow = new HBox(8, xLabel, xSlider, xValue);
        HBox yRow = new HBox(8, yLabel, ySlider, yValue);
        zoomRow.setAlignment(Pos.CENTER_LEFT);
        xRow.setAlignment(Pos.CENTER_LEFT);
        yRow.setAlignment(Pos.CENTER_LEFT);

        // Insert the sliders before the Back button so they are visible above it.
        ui.getChildren().remove(back);
        ui.getChildren().addAll(zoomRow, xRow, yRow, back);

        // Animation loop
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                // clear
                g.setFill(Color.BLACK);
                g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

                Image[] bgs = assets.backgroundsCropped();

                int TILE_PX = TileLevelLoader.TILE_SIZE;

                // Compute viewport in unscaled world pixels so we can apply GC.scale(zoom)
                double viewW_unscaled = W / cameraZoom[0];
                double viewH_unscaled = H / cameraZoom[0];

                int cols = tileGrid.length > 0 ? tileGrid[0].length : 0;
                int rows = tileGrid.length;

                // default tile-aligned start column/row near spawn
                int spawnCol = (int) Math.floor(spawnX / TILE_PX);
                int spawnRow = (int) Math.floor(spawnY / TILE_PX);
                final int VIEW_TILES = 7;
                int defaultStartCol = Math.max(0, Math.min(cols - VIEW_TILES, Math.max(0, spawnCol - 1)));
                int defaultStartRow = Math.max(0, Math.min(rows - VIEW_TILES, Math.max(0, spawnRow - 1)));

                double desiredCamX = cameraWorldX[0] != 0.0 ? cameraWorldX[0] : defaultStartCol * TILE_PX;
                // Always use the slider value for vertical camera offset. Default is 0 (no vertical scroll)
                double desiredCamY = cameraWorldY[0];

                double levelWidthPx = cols * TILE_PX;

                // Create a Camera sized for the unscaled viewport and level width
                Camera cam = new Camera(viewW_unscaled, viewH_unscaled, levelWidthPx);

                // Position camera directly (keep player at spawn so tiles line up)
                cam.setCamX(desiredCamX);
                cam.setLockX(true);
                // set vertical camera offset from slider (default 0 to match game)
                cam.setCamY(desiredCamY);

                // Draw the cropped preview backgrounds (no scaling/cropping math)
                if (bgs != null) {
                    for (int bi = 1; bi <= 3; bi++) {
                        if (bi >= bgs.length) break;
                        Image bg = bgs[bi];
                        if (bg == null) continue;

                        // Use the pre-cropped image for the preview; stretch to canvas
                        g.drawImage(bg, 0, 0, canvas.getWidth(), canvas.getHeight());
                    }
                } else {
                    // fallback to original backgrounds if cropped variants missing
                    Image[] full = assets.backgrounds();
                    if (full != null) {
                        for (int bi = 1; bi <= 3; bi++) {
                            if (bi >= full.length) break;
                            Image bg = full[bi];
                            if (bg == null) continue;
                            g.drawImage(bg, 0, 0, canvas.getWidth(), canvas.getHeight());
                        }
                    }
                }

                // Build renderer with fresh camera (cheap for preview)
                Renderer renderer = new Renderer(cam, assets, skinCache, () -> PlayerSkinStore.get());

                // Apply zoom by scaling the GraphicsContext; renderer expects unscaled w/h
                g.save();
                g.scale(cameraZoom[0], cameraZoom[0]);
                // don't clear canvas here (we drew backgrounds already)
                renderer.render(g, viewW_unscaled, viewH_unscaled, world, player, controller, false, false, true, false, false);
                g.restore();
            }
        }.start();

        // Skin selector wiring (live)
        classic.setOnAction(e -> {
            PlayerSkinStore.set(PlayerSkins.CLASSIC);
            currentSkin[0] = PlayerSkins.CLASSIC;
        });
        stealth.setOnAction(e -> {
            PlayerSkinStore.set(PlayerSkins.STEALTH);
            currentSkin[0] = PlayerSkins.STEALTH;
        });
        test.setOnAction(e -> {
            PlayerSkinStore.set(PlayerSkins.TEST);
            currentSkin[0] = PlayerSkins.TEST;
        });

        return scene;
    }

    // Copied spawn selection logic from CyborgPlatformerApp.pickSpawnOnFloor
    private static double[] pickSpawnOnFloor(TileLevel level, double playerW, double playerH) {
        var solids = level.getSolids();
        if (solids.isEmpty()) return new double[]{96, 96};

        // Search a left-side window so we start near the beginning of the level.
        double maxX = TileLevelLoader.TILE_SIZE * 60;

        double bestX = 96, bestY = 96;
        boolean found = false;

        for (var b : solids) {
            if (b.x() > maxX) continue;

            double spawnX = b.x() + 10;
            double spawnY = b.y() - playerH; // sit exactly on top

            boolean blockedAtSpawn = level.isSolidRect(spawnX, spawnY, playerW, playerH);
            if (blockedAtSpawn) continue;

            boolean hasFloor = level.isSolidRect(spawnX, spawnY + 1, playerW, playerH);
            if (!hasFloor) continue;

            if (!found) {
                bestX = spawnX;
                bestY = spawnY;
                found = true;
                continue;
            }

            if (b.y() > (bestY + playerH) || (b.y() == (bestY + playerH) && b.x() < (bestX - 10))) {
                bestX = spawnX;
                bestY = spawnY;
            }
        }

        return new double[]{bestX, bestY};
    }
}
