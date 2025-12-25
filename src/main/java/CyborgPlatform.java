/*
Name: Marcos Ibáñez Matles
 */

import javax.swing.*;
import java.awt.*;

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
