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
    private final Runnable onStart;

    public SkinPreviewScene(Stage stage, Scene previous, Runnable onStart) {
        this.stage = stage;
        this.previous = previous;
        this.onStart = onStart;
    }

    public Scene createScene() {
        BorderPane root = new BorderPane();

        final double W = 1280;
        final double H = 720;

            Canvas canvas;
            GraphicsContext g;

        // Right-side UI
        VBox ui = new VBox(8);
        ui.setPadding(new Insets(12));
        ui.setPrefWidth(260);
        ui.setStyle("-fx-background-color: rgba(255,255,255,0.4);");

        Label title = new Label("Skin Preview");
        title.setTextFill(Color.WHITE);

        // Presets dropdown (first-screen) + advanced/go/back
        javafx.scene.control.ComboBox<String> presets = new javafx.scene.control.ComboBox<>();
        Button advanced = new Button("Advanced Customisation");
        Button go = new Button("Go");
        Button back = new Button("Done");

        // select current skin state
        PlayerSkin cur = PlayerSkinStore.get();

        // Populate presets list
        presets.getItems().addAll("Classic", "Stealth", "Elite Neon Unit", "Tactical Recon");

        // initial selection: match current skin if known
        PlayerSkin curSkin = PlayerSkinStore.get();
        if (curSkin.equals(PlayerSkins.CLASSIC)) presets.setValue("Classic");
        else if (curSkin.equals(PlayerSkins.STEALTH)) presets.setValue("Stealth");
        else if (curSkin.equals(PlayerSkins.ELITE_NEON_UNIT)) presets.setValue("Elite Neon Unit");
        else if (curSkin.equals(PlayerSkins.TACTICAL_RECON)) presets.setValue("Tactical Recon");

        presets.setOnAction(e -> {
            String v = presets.getValue();
            if (v == null) return;
            switch (v) {
                case "Classic" -> PlayerSkinStore.set(PlayerSkins.CLASSIC);
                case "Stealth" -> PlayerSkinStore.set(PlayerSkins.STEALTH);
                case "Elite Neon Unit" -> PlayerSkinStore.set(PlayerSkins.ELITE_NEON_UNIT);
                case "Tactical Recon" -> PlayerSkinStore.set(PlayerSkins.TACTICAL_RECON);
            }
        });

        back.setOnAction(e -> stage.setScene(previous));

        ui.getChildren().addAll(title, presets, advanced, go, back);
        ui.setAlignment(Pos.TOP_CENTER);

            root.setRight(ui);

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

        // Fixed preview zoom
        final double previewZoom = 2.0;

        // Build objects necessary for Renderer
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

        // controller for render state
        GameController controller = new GameController(world, player, spawnX, spawnY);
        world.setController(controller);


        // Animation loop
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                // clear
                g.setFill(Color.BLACK);
                g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

                Image[] bgs = assets.backgroundsCropped();

                int TILE_PX = TileLevelLoader.TILE_SIZE;

                // Compute viewport in unscaled world pixels using fixed preview zoom
                double viewW_unscaled = W / previewZoom;
                double viewH_unscaled = H / previewZoom;

                int cols = tileGrid.length > 0 ? tileGrid[0].length : 0;
                int rows = tileGrid.length;

                // default tile-aligned start column/row near spawn
                int spawnCol = (int) Math.floor(spawnX / TILE_PX);
                int spawnRow = (int) Math.floor(spawnY / TILE_PX);
                final int VIEW_TILES = 7;
                int defaultStartCol = Math.max(0, Math.min(cols - VIEW_TILES, Math.max(0, spawnCol - 1)));

                double desiredCamX = defaultStartCol * TILE_PX;
                // Fixed vertical offset for preview
                double desiredCamY = 360.0;

                double levelWidthPx = cols * TILE_PX;

                // Create a Camera sized for the viewport and level width
                Camera cam = new Camera(viewW_unscaled, viewH_unscaled, levelWidthPx);

                // Position camera directly (keep player at spawn so tiles line up)
                cam.setCamX(desiredCamX);
                cam.setLockX(true);
                // set vertical camera offset
                cam.setCamY(desiredCamY);

                // Draw the cropped preview backgrounds
                if (bgs != null) {
                    for (int bi = 1; bi <= 3; bi++) {
                        if (bi >= bgs.length) break;
                        Image bg = bgs[bi];
                        if (bg == null) continue;

                        // Use the pre-cropped image for the preview; stretch to canvas
                        g.drawImage(bg, 0, 0, canvas.getWidth(), canvas.getHeight());
                    }
                } else {
                    // OG backgrounds if cropped is missing
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

                // Build renderer with fresh camera ##
                Renderer renderer = new Renderer(cam, assets, skinCache, () -> PlayerSkinStore.get());

                // Apply fixed preview zoom by scaling the GraphicsContext
                g.save();
                g.scale(previewZoom, previewZoom);
                renderer.render(g, viewW_unscaled, viewH_unscaled, world, player, controller, false, false, true, false, false);
                g.restore();
            }
        }.start();

        // Skin selector wiring is handled by the presets ComboBox

        // --- Advanced customisation data and UI builder ---
        // Options arrays
        // HAIR options (use the middle/base color from each option group)
        int[] hairOptions = new int[] {
            // include hair hexes found across presets
            PlayerSkin.hex("158968"), // Muted Tech Teal (CLASSIC)
            PlayerSkin.hex("2A2A2A"), // Dark Stealth
            PlayerSkin.hex("1BD4C4"), // Neon Cyber Accent
            PlayerSkin.hex("3C8F76")  // Military Green
        };

        // SKIN options
        int[] skinOptions = new int[] {
            PlayerSkin.hex("FFDBA5"), // Natural Light
            PlayerSkin.hex("E7B87F"), // Warm Tan
            PlayerSkin.hex("8A5A3B"),  // Warm Brown
            PlayerSkin.hex("6A4027"),  // Deep Cocoa
            PlayerSkin.hex("4F3F39"),  // Cool Dark
            PlayerSkin.hex("3E2617"),  // Rich Ebony
            PlayerSkin.hex("C8A77B"),  // Stealth Tan
            PlayerSkin.hex("F0D4B8")   // Synth-Human
        };

        // VISOR options
        int[] visorOptions = new int[] {
            PlayerSkin.hex("663B93"), // Royal Tech Purple
            PlayerSkin.hex("A23BBF"), // Neon Magenta
            PlayerSkin.hex("3F1F5E")  // Dark Tactical
        };

        // BELT options
        int[] beltOptions = new int[] {
            PlayerSkin.hex("38002C"), // Industrial Red
            PlayerSkin.hex("1B1B24"), // Carbon Black
            PlayerSkin.hex("7A5228")  // Brass Tech
        };

        // SUIT_BODY palettes (user-provided groups)
        int[][] suitBodyOptions = new int[][]{
                { PlayerSkin.hex("222A5C"), PlayerSkin.hex("566A89"), PlayerSkin.hex("8BABBF") },
                { PlayerSkin.hex("111827"), PlayerSkin.hex("374151"), PlayerSkin.hex("6B7280") },
                { PlayerSkin.hex("4A3B00"), PlayerSkin.hex("8F7A1A"), PlayerSkin.hex("E3C84A") },
                { PlayerSkin.hex("2A0F3D"), PlayerSkin.hex("6B2FA3"), PlayerSkin.hex("B46CFF") },
                { PlayerSkin.hex("6B7280"), PlayerSkin.hex("CBD5E1"), PlayerSkin.hex("F8FAFC") },
                { PlayerSkin.hex("3A0D0D"), PlayerSkin.hex("8F2D2D"), PlayerSkin.hex("D16C6C") }
        };

        int[] ledOptions = new int[] {
            PlayerSkin.hex("5BECF1"), // Classic Cyan
            PlayerSkin.hex("74D8FF"), // Ice Blue
            PlayerSkin.hex("5FFFFF")  // High-Energy Neon
        };

        // Current indices (mutable holders)
        int[] hairIdx = new int[]{0};
        int[] skinIdx = new int[]{0};
        int[] visorIdx = new int[]{0};
        int[] beltIdx = new int[]{0};
        int[] suitIdx = new int[]{0};
        int[] ledIdx = new int[]{0};

        // Helper to apply current selections to the PlayerSkinStore
        Runnable applySelection = () -> {
            PlayerSkin s = new PlayerSkin(
                    hairOptions[hairIdx[0]],
                    skinOptions[skinIdx[0]],
                    visorOptions[visorIdx[0]],
                    beltOptions[beltIdx[0]],
                    suitBodyOptions[suitIdx[0]][0],
                    suitBodyOptions[suitIdx[0]][1],
                    suitBodyOptions[suitIdx[0]][2],
                    ledOptions[ledIdx[0]]
            );
            PlayerSkinStore.set(s);
        };

        // Build advanced UI when requested
        Runnable showAdvanced = () -> {
            ui.getChildren().clear();
            ui.getChildren().add(title);


            // HAIR row (color swatch)
            Label hairLabel = new Label(); hairLabel.setTextFill(Color.WHITE);
            hairLabel.setMinSize(36, 18);
            hairLabel.setPrefSize(36, 18);
            Button hairLeft = new Button("<");
            Button hairRight = new Button(">");
            Label hairText = new Label("Hair"); hairText.setTextFill(Color.WHITE);
            HBox hairRow = new HBox(8, hairLeft, hairLabel, hairText, hairRight);
            hairRow.setAlignment(Pos.CENTER_LEFT);
            hairLeft.setOnAction(e -> {
                hairIdx[0] = (hairIdx[0] - 1 + hairOptions.length) % hairOptions.length;
                updateSwatch(hairLabel, hairOptions[hairIdx[0]]);
                applySelection.run();
            });
            hairRight.setOnAction(e -> {
                hairIdx[0] = (hairIdx[0] + 1) % hairOptions.length;
                updateSwatch(hairLabel, hairOptions[hairIdx[0]]);
                applySelection.run();
            });
            // initialize swatch
            updateSwatch(hairLabel, hairOptions[hairIdx[0]]);

            // SKIN row (color swatch)
            Label skinLabel = new Label(); skinLabel.setMinSize(36,18); skinLabel.setPrefSize(36,18); skinLabel.setTextFill(Color.WHITE);
            Button skinLeft = new Button("<");
            Button skinRight = new Button(">");
            Label skinText = new Label("Skin"); skinText.setTextFill(Color.WHITE);
            HBox skinRow = new HBox(8, skinLeft, skinLabel, skinText, skinRight);
            skinRow.setAlignment(Pos.CENTER_LEFT);
            skinLeft.setOnAction(e -> { skinIdx[0] = (skinIdx[0]-1 + skinOptions.length) % skinOptions.length; updateSwatch(skinLabel, skinOptions[skinIdx[0]]); applySelection.run(); });
            skinRight.setOnAction(e -> { skinIdx[0] = (skinIdx[0]+1) % skinOptions.length; updateSwatch(skinLabel, skinOptions[skinIdx[0]]); applySelection.run(); });
            updateSwatch(skinLabel, skinOptions[skinIdx[0]]);

            // VISOR row (color swatch)
            Label visorLabel = new Label(); visorLabel.setMinSize(36,18); visorLabel.setPrefSize(36,18); visorLabel.setTextFill(Color.WHITE);
            Button visorLeft = new Button("<");
            Button visorRight = new Button(">");
            Label visorText = new Label("Visor"); visorText.setTextFill(Color.WHITE);
            HBox visorRow = new HBox(8, visorLeft, visorLabel, visorText, visorRight);
            visorRow.setAlignment(Pos.CENTER_LEFT);
            visorLeft.setOnAction(e -> { visorIdx[0] = (visorIdx[0]-1 + visorOptions.length) % visorOptions.length; updateSwatch(visorLabel, visorOptions[visorIdx[0]]); applySelection.run(); });
            visorRight.setOnAction(e -> { visorIdx[0] = (visorIdx[0]+1) % visorOptions.length; updateSwatch(visorLabel, visorOptions[visorIdx[0]]); applySelection.run(); });
            updateSwatch(visorLabel, visorOptions[visorIdx[0]]);

            // BELT row (color swatch)
            Label beltLabel = new Label(); beltLabel.setMinSize(36,18); beltLabel.setPrefSize(36,18); beltLabel.setTextFill(Color.WHITE);
            Button beltLeft = new Button("<");
            Button beltRight = new Button(">");
            Label beltText = new Label("Belt"); beltText.setTextFill(Color.WHITE);
            HBox beltRow = new HBox(8, beltLeft, beltLabel, beltText, beltRight);
            beltRow.setAlignment(Pos.CENTER_LEFT);
            beltLeft.setOnAction(e -> { beltIdx[0] = (beltIdx[0]-1 + beltOptions.length) % beltOptions.length; updateSwatch(beltLabel, beltOptions[beltIdx[0]]); applySelection.run(); });
            beltRight.setOnAction(e -> { beltIdx[0] = (beltIdx[0]+1) % beltOptions.length; updateSwatch(beltLabel, beltOptions[beltIdx[0]]); applySelection.run(); });
            updateSwatch(beltLabel, beltOptions[beltIdx[0]]);

            // SUIT_BODY row (show middle swatch)
            Label suitLabel = new Label(); suitLabel.setMinSize(48,18); suitLabel.setPrefSize(48,18); suitLabel.setTextFill(Color.WHITE);
            Button suitLeft = new Button("<");
            Button suitRight = new Button(">");
            Label suitText = new Label("Suit Body"); suitText.setTextFill(Color.WHITE);
            HBox suitRow = new HBox(8, suitLeft, suitLabel, suitText, suitRight);
            suitRow.setAlignment(Pos.CENTER_LEFT);
            suitLeft.setOnAction(e -> { suitIdx[0] = (suitIdx[0]-1 + suitBodyOptions.length) % suitBodyOptions.length; updateSwatch(suitLabel, suitBodyOptions[suitIdx[0]][1]); applySelection.run(); });
            suitRight.setOnAction(e -> { suitIdx[0] = (suitIdx[0]+1) % suitBodyOptions.length; updateSwatch(suitLabel, suitBodyOptions[suitIdx[0]][1]); applySelection.run(); });
            updateSwatch(suitLabel, suitBodyOptions[suitIdx[0]][1]);

            // SUIT_LED row (color swatch)
            Label ledLabel = new Label(); ledLabel.setMinSize(36,18); ledLabel.setPrefSize(36,18); ledLabel.setTextFill(Color.WHITE);
            Button ledLeft = new Button("<");
            Button ledRight = new Button(">");
            Label ledText = new Label("LED"); ledText.setTextFill(Color.WHITE);
            HBox ledRow = new HBox(8, ledLeft, ledLabel, ledText, ledRight);
            ledRow.setAlignment(Pos.CENTER_LEFT);
            ledLeft.setOnAction(e -> { ledIdx[0] = (ledIdx[0]-1 + ledOptions.length) % ledOptions.length; updateSwatch(ledLabel, ledOptions[ledIdx[0]]); applySelection.run(); });
            ledRight.setOnAction(e -> { ledIdx[0] = (ledIdx[0]+1) % ledOptions.length; updateSwatch(ledLabel, ledOptions[ledIdx[0]]); applySelection.run(); });
            updateSwatch(ledLabel, ledOptions[ledIdx[0]]);

            // Add all rows to UI
            ui.getChildren().addAll(hairRow, skinRow, visorRow, beltRow, suitRow, ledRow);

            // Add control row with Presets and Go
            Button showPresets = new Button("Presets");
            showPresets.setOnAction(ev -> {
                ui.getChildren().clear();
                ui.getChildren().addAll(title, presets, advanced, go, back);
            });
            go.setOnAction(ev -> {
                applySelection.run();
                if (onStart != null) onStart.run();
            });
            HBox ctrl = new HBox(8, showPresets, go);
            ctrl.setAlignment(Pos.CENTER);
            ui.getChildren().add(ctrl);
        };

        // Advanced button wiring
        advanced.setOnAction(e -> showAdvanced.run());
        go.setOnAction(e -> { applySelection.run(); if (onStart != null) onStart.run(); });

        return scene;
    }

    private static String toCssColor(int argb) {
        int rgb = argb & 0xFFFFFF;
        return String.format("#%06X", rgb);
    }

    private static void updateSwatch(Label lbl, int argb) {
        String css = "-fx-background-color: " + toCssColor(argb) + "; -fx-border-color: white; -fx-border-width: 1; -fx-border-radius: 2; -fx-background-radius: 2;";
        lbl.setStyle(css);
    }

    private static double[] pickSpawnOnFloor(TileLevel level, double playerW, double playerH) {
        var solids = level.getSolids();

        if (solids.isEmpty()) {
            return new double[]{96, 96};
        }

        // Search a left-side window, so we start near the beginning of the level.
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
