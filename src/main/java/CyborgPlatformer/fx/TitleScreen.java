package CyborgPlatformer.fx;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.util.function.Consumer;
import CyborgPlatformer.config.LevelSettings;
import CyborgPlatformer.config.GameMode;

public class TitleScreen {
    private final Stage stage;
    private final Consumer<LevelSettings> onStart;

    public TitleScreen(Stage stage, Consumer<LevelSettings> onStart) {
        this.stage = stage;
        this.onStart = onStart;
    }

    public Scene createScene() {
        StackPane root = new StackPane();

        // ---------- Background ----------
        try {
            var res = getClass().getResourceAsStream("/Background/title_bg.png");
            if (res != null) {
                Image bg = new Image(res, 1280, 720, true, true);
                ImageView bgView = new ImageView(bg);
                bgView.setFitWidth(1280);
                bgView.setFitHeight(720);
                root.getChildren().add(bgView);
            }
        } catch (Exception ignored) {}

        // ---------- PRESS ANY BUTTON ----------
        VBox pressAnyKeyLayer = new VBox();
        pressAnyKeyLayer.setAlignment(Pos.CENTER);

        Text pressAnyKey = new Text("PRESS ANY BUTTON TO START");
        pressAnyKey.getStyleClass().add("glow-label");
        pressAnyKey.setStyle("-fx-font-size:20px; -fx-fill: #83CBEB; -fx-effect: dropshadow(gaussian, white, 2, 0.0, 0, 0);");

        pressAnyKeyLayer.getChildren().add(pressAnyKey);

        // blinking effect
        javafx.animation.FadeTransition blink = new javafx.animation.FadeTransition(
                javafx.util.Duration.seconds(0.6), pressAnyKey
        );
        blink.setFromValue(1.0);
        blink.setToValue(0.3);
        blink.setCycleCount(javafx.animation.Animation.INDEFINITE);
        blink.setAutoReverse(true);
        blink.play();

        // ---------- MAIN MENU ----------
        // --- MENU BAR ---
        final double BOX_W = 760;
        final double BOX_H = 80;

        VBox menuArea = new VBox();
        menuArea.setAlignment(Pos.TOP_CENTER);
        menuArea.setPrefWidth(BOX_W);
        menuArea.setPrefHeight(BOX_H * 4); // taller area for submenus
        menuArea.setMaxWidth(BOX_W);
        menuArea.setTranslateY(340);
        menuArea.setTranslateX(0);
        menuArea.setVisible(false);
        menuArea.setOpacity(0);

        root.getChildren().addAll(pressAnyKeyLayer, menuArea);

        HBox menuBar = new HBox();
        menuBar.setAlignment(Pos.CENTER);
        menuBar.setPrefSize(BOX_W, BOX_H);
        menuBar.setMaxSize(BOX_W, BOX_H);

        // Difficulty selector buttons + custom settings
        final GameMode[] selectedMode = new GameMode[]{GameMode.MEDIUM};
        Button easyBtn = new Button("Easy");
        Button medBtn = new Button("Medium");
        Button hardBtn = new Button("Hard");
        Button customBtn = new Button("");
        HBox difficultyRow = new HBox(8, easyBtn, medBtn, hardBtn, customBtn);
        difficultyRow.setAlignment(Pos.CENTER);
        // preset save/load UI removed from Game Settings; handled in Custom pane

        Button start = new Button("Start");
        Button options = new Button("Options");
        Button quit = new Button("Quit");

        // holder for custom advanced settings
        final LevelSettings[] customSettings = new LevelSettings[1];

        java.util.function.Supplier<LevelSettings> currentSettings = () -> {
            if (customSettings[0] != null) return customSettings[0];
            return LevelSettings.defaultsFor(selectedMode[0]);
        };

        // toggle color on selection
        Runnable updateDifficultyStyles = () -> {
            String activeStyle = "-fx-background-color: #83CBEB; -fx-text-fill: #000000; -fx-border-color: white; -fx-border-width:1;";
            String inactiveStyle = "-fx-background-color: rgba(30,60,120,0.55); -fx-text-fill: white;";
            easyBtn.setStyle(selectedMode[0] == GameMode.EASY ? activeStyle : inactiveStyle);
            medBtn.setStyle(selectedMode[0] == GameMode.MEDIUM ? activeStyle : inactiveStyle);
            hardBtn.setStyle(selectedMode[0] == GameMode.HARD ? activeStyle : inactiveStyle);
            customBtn.setStyle(selectedMode[0] == GameMode.CUSTOM ? "-fx-border-color: white; -fx-border-width:2; -fx-background-color: rgba(30,60,120,0.55); -fx-text-fill: white;" : inactiveStyle);
        };

        final Scene[] sceneRef = new Scene[1];

        for (Button b : new Button[]{start, options, quit}) {
            b.setMaxWidth(Double.MAX_VALUE);
            b.setMaxHeight(Double.MAX_VALUE);
            HBox.setHgrow(b, Priority.ALWAYS);
        }

        // difficulty button sizing and styling
        double DIFF_H = 48; // uniform height for the row
        double DIFF_SPACING = 8;
        difficultyRow.setSpacing(DIFF_SPACING);
        difficultyRow.setPrefWidth(BOX_W);

        // button height == width
        double customSize = DIFF_H; // square

        // remaining width split between three difficulty buttons
        double remaining = BOX_W - customSize - (DIFF_SPACING * 3);
        double otherW = Math.max(48, remaining / 3.0);

        easyBtn.setPrefWidth(otherW);
        medBtn.setPrefWidth(otherW);
        hardBtn.setPrefWidth(otherW);
        customBtn.setPrefWidth(customSize);

        for (Button b : new Button[]{easyBtn, medBtn, hardBtn, customBtn}) {
            b.setMaxWidth(Double.MAX_VALUE);
            b.setPrefHeight(DIFF_H);
            b.setStyle("-fx-background-color: rgba(30,60,120,0.55); -fx-padding:4 10;");
        }
        updateDifficultyStyles.run();

        // label style used across the title screen
        String labelStyle = "-fx-font-size:18px; -fx-text-fill: #83CBEB; -fx-effect: dropshadow(gaussian, white, 2, 0.0, 0, 0);";

        try {
            var cogStream = getClass().getResourceAsStream("/Background/SettingsCog.png");
                if (cogStream != null) {
                Image cog = new Image(cogStream, customSize - 8, customSize - 8, true, true);
                ImageView iv = new ImageView(cog);
                iv.setFitWidth(customSize - 8);
                iv.setFitHeight(customSize - 8);
                customBtn.setGraphic(iv);
            } else {
                customBtn.setText("Custom");
            }
        } catch (Exception ignored) {
            customBtn.setText("Custom");
        }

        start.setOnAction(e -> onStart.accept(currentSettings.get()));
        quit.setOnAction(e -> stage.close());

        start.setDefaultButton(true);
        quit.setCancelButton(true);

        VBox mainMenuBar = new VBox(12);
        mainMenuBar.setAlignment(Pos.CENTER);

        double BTN_W = Math.min(BOX_W, (otherW * 3.0) + (DIFF_SPACING * 2.0));
        for (Button b : new Button[]{start, options, quit}) {
            b.setPrefWidth(BTN_W);
            b.setMaxWidth(BTN_W);
            b.setMinWidth(BTN_W);
        }
        mainMenuBar.getChildren().addAll(start, options, quit);

        // Options submenu (Player Customisation / Game Settings / Back)
        Button playerCustomBtn = new Button("Player Customisation");
        Button gameSettingsBtn = new Button("Game Settings");
        Button optionsBack = new Button("Back");
        VBox optionsMenu = new VBox(8, playerCustomBtn, gameSettingsBtn, optionsBack);
        optionsMenu.setAlignment(Pos.CENTER);
        for (Button b : new Button[]{playerCustomBtn, gameSettingsBtn, optionsBack}) {
            b.setPrefWidth(BTN_W);
            b.setMaxWidth(BTN_W);
            b.setMinWidth(BTN_W);
        }

        // Game Settings pane (difficulty buttons, presets,
        // /delete, back)
        VBox gameSettingsPane = new VBox(12);
        gameSettingsPane.setAlignment(Pos.TOP_CENTER);
        gameSettingsPane.setPrefWidth(BOX_W);
        gameSettingsPane.setMaxWidth(BOX_W);
        gameSettingsPane.setStyle("-fx-padding:12 24 12 24;");

        Button gsToPlayer = new Button("Player Customisation");
        Button gsBack = new Button("Done");


        VBox gsButtons = new VBox(8, gsToPlayer, gsBack);

        gsButtons.setAlignment(Pos.CENTER);

        gsToPlayer.setPrefWidth(BTN_W);
        gsToPlayer.setMaxWidth(BTN_W);
        gsToPlayer.setMinWidth(BTN_W);

        gsBack.setPrefWidth(BTN_W);
        gsBack.setMaxWidth(BTN_W);
        gsBack.setMinWidth(BTN_W);


        HBox rightControlsBox = new HBox(8, difficultyRow);
        rightControlsBox.setAlignment(Pos.CENTER);
        rightControlsBox.setMaxWidth(BOX_W);

        Label difficultyLabel = new Label("Difficulty");
        difficultyLabel.setStyle(labelStyle);

        gameSettingsPane.getChildren().addAll(difficultyLabel, rightControlsBox, gsButtons);

        // Custom settings pane for manual tuning
        VBox customSettingsPane = new VBox(6);
        customSettingsPane.setAlignment(Pos.TOP_CENTER);
        customSettingsPane.setPrefWidth(BOX_W);
        customSettingsPane.setStyle("-fx-padding:8 20 8 20; -fx-background-color: rgba(255,255,255,0.85); -fx-background-radius:6;");

        Label customTitle = new Label("Custom Settings");
        String titleStyle = "-fx-font-size:20px; -fx-text-fill: #83CBEB; -fx-underline:true; -fx-effect: dropshadow(gaussian, white, 2, 0.0, 0, 0);";
        customTitle.setStyle(titleStyle);

        String labelStyleSmall = "-fx-font-size:16px; -fx-text-fill: #83CBEB; -fx-effect: dropshadow(gaussian, white, 2, 0.0, 0, 0);";
        Label speedLbl = new Label("Speed x");
        speedLbl.setStyle(labelStyleSmall);
        Slider speedSlider = new Slider(0.5, 3.0, 1.0);
        speedSlider.setBlockIncrement(0.5);
        speedSlider.setMajorTickUnit(0.5);
        speedSlider.setMinorTickCount(0);
        speedSlider.setSnapToTicks(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.setShowTickLabels(true);

        Label healthLbl = new Label("Health x");
        healthLbl.setStyle(labelStyleSmall);
        Slider healthSlider = new Slider(0.5, 3.0, 1.0);
        healthSlider.setBlockIncrement(0.5);
        healthSlider.setMajorTickUnit(0.5);
        healthSlider.setMinorTickCount(0);
        healthSlider.setSnapToTicks(true);
        healthSlider.setShowTickMarks(true);
        healthSlider.setShowTickLabels(true);

        Label damageLbl = new Label("Damage x");
        damageLbl.setStyle(labelStyleSmall);
        Slider damageSlider = new Slider(0.5, 3.0, 1.0);
        damageSlider.setBlockIncrement(0.5);
        damageSlider.setMajorTickUnit(0.5);
        damageSlider.setMinorTickCount(0);
        damageSlider.setSnapToTicks(true);
        damageSlider.setShowTickMarks(true);
        damageSlider.setShowTickLabels(true);

        Label maxELbl = new Label("Max Enemies");
        maxELbl.setStyle(labelStyleSmall);
        Slider maxESlider = new Slider(1, 10, 5);
        maxESlider.setBlockIncrement(1);
        maxESlider.setMajorTickUnit(1);
        maxESlider.setMinorTickCount(0);
        maxESlider.setSnapToTicks(true);
        maxESlider.setShowTickMarks(true);
        maxESlider.setShowTickLabels(true);

        CheckBox respawnChk = new CheckBox("Enemies respawn on player death");
        respawnChk.setStyle(labelStyleSmall);

        Button saveCustom = new Button("Save");
        Button cancelCustom = new Button("Back");
            VBox customBtns = new VBox(8, saveCustom, cancelCustom);
            customBtns.setAlignment(Pos.CENTER);

        // two columns: left (speed/health/damage/checkbox) & right (max enemies + buttons)
        VBox leftCol = new VBox(6, speedLbl, speedSlider, healthLbl, healthSlider, damageLbl, damageSlider, respawnChk);
        leftCol.setAlignment(Pos.TOP_CENTER);
        leftCol.setFillWidth(true);
        leftCol.setPrefWidth(BOX_W * 0.55);

        // center the labels inside the left column
        speedLbl.setMaxWidth(Double.MAX_VALUE); speedLbl.setAlignment(Pos.CENTER);
        healthLbl.setMaxWidth(Double.MAX_VALUE); healthLbl.setAlignment(Pos.CENTER);
        damageLbl.setMaxWidth(Double.MAX_VALUE); damageLbl.setAlignment(Pos.CENTER);

        VBox rightCol = new VBox(8, maxELbl, maxESlider, customBtns);
        rightCol.setAlignment(Pos.TOP_CENTER);
        rightCol.setPrefWidth(BOX_W * 0.45);

        HBox twoCol = new HBox(12, leftCol, rightCol);
        twoCol.setAlignment(Pos.TOP_CENTER);

        customSettingsPane.getChildren().addAll(customTitle, twoCol);

        // wire difficulty buttons
        easyBtn.setOnAction(e -> { selectedMode[0] = GameMode.EASY; customSettings[0] = null; updateDifficultyStyles.run(); });
        medBtn.setOnAction(e -> { selectedMode[0] = GameMode.MEDIUM; customSettings[0] = null; updateDifficultyStyles.run(); });
        hardBtn.setOnAction(e -> { selectedMode[0] = GameMode.HARD; customSettings[0] = null; updateDifficultyStyles.run(); });
        customBtn.setOnAction(e -> {
            selectedMode[0] = GameMode.CUSTOM;
            updateDifficultyStyles.run();
            // pre-fill sliders from existing customSettings or defaults
            LevelSettings base = (customSettings[0] != null) ? customSettings[0] : LevelSettings.defaultsFor(GameMode.MEDIUM);
            speedSlider.setValue(base.getSpeedMultiplier());
            healthSlider.setValue(base.getHealthMultiplier());
            damageSlider.setValue(base.getDamageMultiplier());
            maxESlider.setValue(base.getMaxEnemies());
            respawnChk.setSelected(base.isRespawnOnPlayerDeath());
            menuArea.getChildren().setAll(customSettingsPane);
        });

        // custom settings buttons
        cancelCustom.setOnAction(ev -> menuArea.getChildren().setAll(gameSettingsPane));
        saveCustom.setOnAction(ev -> {
            try {
                double sp = Math.round(speedSlider.getValue() * 2.0) / 2.0; // ensure 0.5 steps
                double hp = Math.round(healthSlider.getValue() * 2.0) / 2.0;
                double dm = Math.round(damageSlider.getValue() * 2.0) / 2.0;
                int me = (int)Math.round(maxESlider.getValue());
                System.out.println("you selected: " + me + " enemies");
                customSettings[0] = new LevelSettings(me, respawnChk.isSelected(), sp, hp, dm, 1.0, 1.0);
                menuArea.getChildren().setAll(gameSettingsPane);
            } catch (Exception ex) {
                // ignore parse errors for now
            }
        });

        // Start with main menu shown
        mainMenuBar.setPrefSize(BOX_W, BOX_H);
        mainMenuBar.setMaxSize(BOX_W, BOX_H);
        mainMenuBar.setSpacing(8);
        menuArea.getChildren().setAll(mainMenuBar);


        // Navigation handlers
        options.setOnAction(ev -> menuArea.getChildren().setAll(optionsMenu));
        optionsBack.setOnAction(ev -> menuArea.getChildren().setAll(mainMenuBar));

        

        playerCustomBtn.setOnAction(ev -> {
            menuArea.getChildren().setAll(optionsMenu);
            SkinPreviewScene preview = new SkinPreviewScene(stage, sceneRef[0], () -> onStart.accept(currentSettings.get()));
            stage.setScene(preview.createScene());
        });

        gsToPlayer.setOnAction(ev -> {
            menuArea.getChildren().setAll(optionsMenu);
            SkinPreviewScene preview = new SkinPreviewScene(stage, sceneRef[0], () -> onStart.accept(currentSettings.get()));
            stage.setScene(preview.createScene());
        });

        gameSettingsBtn.setOnAction(ev -> menuArea.getChildren().setAll(gameSettingsPane));
        gsBack.setOnAction(ev -> menuArea.getChildren().setAll(optionsMenu));

        Scene scene = new Scene(root, 1280, 720);
        sceneRef[0] = scene;

        // ---------- INPUT HANDLING ----------
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case SHIFT, CONTROL, ALT, META -> {}
                default -> transitionToMenu(pressAnyKeyLayer, menuArea, blink, start);
            }
        });

        // ---------- CSS ----------
        try {
            var css = getClass().getResource("/Styles/arcadeStyle.css");
            if (css != null) scene.getStylesheets().add(css.toExternalForm());
        } catch (Exception ignored) {}

        return scene;
    }

    private void transitionToMenu(
            Region pressLayer,
            Region menuLayer,
            javafx.animation.Animation blink,
            Button startButton
    ) {
        if (!pressLayer.isVisible()) return;

        blink.stop();

        javafx.animation.FadeTransition fadeOut =
                new javafx.animation.FadeTransition(javafx.util.Duration.seconds(0.4), pressLayer);
        fadeOut.setToValue(0);

        javafx.animation.FadeTransition fadeIn =
                new javafx.animation.FadeTransition(javafx.util.Duration.seconds(0.4), menuLayer);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        fadeOut.setOnFinished(e -> {
            pressLayer.setVisible(false);
            menuLayer.setVisible(true);
            fadeIn.play();
            startButton.requestFocus();
        });

        fadeOut.play();
    }


}
