/*
Name: Marcos Ibáñez Matles
 */

import javax.swing.*;
import java.awt.*;
/**
 * Game entry point.
 *
 * Responsibilities:
 * - Creates the main Swing window and attaches the {@link canvas} component.
 * - Loads and initialises the shared game resources (via {@link Game} initialisation).
 *
 *
 * Notes (v1 design):
 * - Uses global access (e.g., CyborgPlatform.game, canvas.player).
 * - In v2, could delegate some of this functionality to a GameController class.
 */
public class CyborgPlatform {

    public static Game game;
    public static void main(String[] args) {
        JFrame frame = new JFrame("Platform Game");
        frame.setPreferredSize(new Dimension(1280, 758));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        canvas canvas = new canvas();
        game = new Game();
        game.loadImages();
        game.spawnEntities();
        MapBlocks.getMap();
        frame.add(canvas);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
        game.startTimer(canvas);
    }
}
