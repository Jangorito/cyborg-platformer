package CyborgPlatformer.fx;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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
        final double BOX_W = 600;
        final double BOX_H = 80;

        Pane menuArea = new Pane();
        menuArea.setPrefSize(BOX_W, BOX_H);
        menuArea.setMaxSize(BOX_W, BOX_H);

        menuArea.setTranslateY(40);
        menuArea.setTranslateX(0);

        menuArea.setVisible(false);
        menuArea.setOpacity(0);

        root.getChildren().addAll(pressAnyKeyLayer, menuArea);

        HBox menuBar = new HBox();
        menuBar.setAlignment(Pos.CENTER);
        menuBar.setPrefSize(BOX_W, BOX_H);
        menuBar.setMaxSize(BOX_W, BOX_H);

        // Difficulty selector + advanced
        ComboBox<GameMode> modeSelect = new ComboBox<>();
        modeSelect.getItems().addAll(GameMode.values());
        modeSelect.setValue(GameMode.MEDIUM);

        Button advanced = new Button("Advanced");

        Button start = new Button("Start");
        Button options = new Button("Options");
        Button quit = new Button("Quit");

        final Scene[] sceneRef = new Scene[1];

        for (Button b : new Button[]{start, options, quit}) {
            b.setMaxWidth(Double.MAX_VALUE);
            b.setMaxHeight(Double.MAX_VALUE);
            HBox.setHgrow(b, Priority.ALWAYS);
        }

        start.setOnAction(e -> {
            LevelSettings sel = (customSettings[0] != null) ? customSettings[0] : LevelSettings.defaultsFor(modeSelect.getValue());
            onStart.accept(sel);
        });
        options.setOnAction(e -> {
            // open skin preview scene
            SkinPreviewScene preview = new SkinPreviewScene(stage, sceneRef[0], () -> onStart.accept(LevelSettings.defaultsFor(modeSelect.getValue())));
            stage.setScene(preview.createScene());
        });
        quit.setOnAction(e -> stage.close());

        start.setDefaultButton(true);
        quit.setCancelButton(true);

        // Advanced dialog: allow primitive tuning of LevelSettings
        final LevelSettings[] customSettings = new LevelSettings[1];
        advanced.setOnAction(ev -> {
            Stage d = new Stage();
            VBox box = new VBox(8);
            box.setStyle("-fx-padding:16");

            TextField speed = new TextField(Double.toString(LevelSettings.defaultsFor(modeSelect.getValue()).getSpeedMultiplier()));
            TextField health = new TextField(Double.toString(LevelSettings.defaultsFor(modeSelect.getValue()).getHealthMultiplier()));
            TextField damage = new TextField(Double.toString(LevelSettings.defaultsFor(modeSelect.getValue()).getDamageMultiplier()));
            TextField maxE = new TextField(Integer.toString(LevelSettings.defaultsFor(modeSelect.getValue()).getMaxEnemies()));
            CheckBox respawn = new CheckBox("Enemies respawn on player death");
            respawn.setSelected(LevelSettings.defaultsFor(modeSelect.getValue()).isRespawnOnPlayerDeath());

            Button ok = new Button("OK");
            ok.setOnAction(ae -> {
                try {
                    double sp = Double.parseDouble(speed.getText());
                    double hp = Double.parseDouble(health.getText());
                    double dm = Double.parseDouble(damage.getText());
                    int me = Integer.parseInt(maxE.getText());
                    customSettings[0] = new LevelSettings(me, respawn.isSelected(), sp, hp, dm, 1.0, 1.0);
                    d.close();
                } catch (Exception ex) {
                    // ignore parse errors silently for now
                }
            });

            box.getChildren().addAll(new Label("Speed x"), speed, new Label("Health x"), health, new Label("Damage x"), damage, new Label("Max Enemies"), maxE, respawn, ok);
            Scene ds = new Scene(box);
            d.setScene(ds);
            d.initOwner(stage);
            d.showAndWait();
        });

        HBox rightControls = new HBox(8, modeSelect, advanced);
        menuBar.getChildren().addAll(start, options, quit, rightControls);
        menuArea.getChildren().add(menuBar);

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
