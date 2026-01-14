package CyborgPlatformer.legacy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Main Swing component: handles rendering, input, and the update loop.
 *
 * Responsibilities:
 * - Renders the world, player and zombies and HUD each frame.
 * - Advances game through update methods called during rendering
 * - Stores states (player, enemies, bullets, cameraOffset).
 * - Handles keyboard input and maps them to KeysPressed array.
 *
 * v1 design notes:
 * - multiple static/global fields for player enemy and keysPressed objects, coupling can be separated to create classes such as InputHandler, Renderer, etc.
 */

public class canvas extends JComponent implements KeyListener, ActionListener, MouseListener {
    protected double last = System.nanoTime() / 1000000000.;
    protected static Player player;
    protected static ArrayList<Enemy> enemies;
    protected static ArrayList<bullet> activeBullets = new ArrayList<>();
    // keysPressed is an input state array read by CyborgPlatformer.legacy.Player.update()
    protected static boolean[] keysPressed = new boolean[4];
    protected static boolean isLastDirectionForwards = true;
    protected static int cameraOffset;
    protected static Font font;
    protected static Image cloud;

    public canvas() {
        addKeyListener(this);
        addMouseListener(this);
        setFocusable(true);
    }

    /**
     * Main render method.
     * - uses {@code cameraOffset} to implement a simple camera.
     * - Draws world, player&enemies and UI.
     * - Also advances simulation by calling update methods which affect players, enemies and bullets.
     *
     * Note:
     * - update + render mixed, separate for v2
     */
    public void paint(Graphics g) {
        if (!CyborgPlatform.game.isWon) {
            ArrayList<bullet> bulletsCopy = new ArrayList<bullet>(activeBullets);
            ArrayList<Enemy> enemiesCopy = new ArrayList<Enemy>(enemies);

            cameraOffset = player.updateCmr();
            g.translate(-cameraOffset, 0);


            Background.drawBackground(g);
            MapBlocks.drawMap(g);


            player.update();


            displayFPS();


            player.drawGUI(g);


            if (player.isFacingForwards()) g.drawImage(player.image, player.x, player.y, null);
            else
                g.drawImage(player.image, player.x + 30, player.y, -player.image.getWidth(null), player.image.getHeight(null), null);
            if (player.jumpCounter > 2 && player.velocity < 0)
                g.drawImage(cloud, player.jumpX, player.jumpY + 42, null);


            for (Enemy enemy : enemiesCopy) {
                enemy.doBehavior();
            }

            for (Enemy enemy : enemiesCopy) {
                if (enemy.isFacingForwards) g.drawImage(enemy.image, enemy.x, enemy.y, null);
                else
                    g.drawImage(enemy.image, enemy.x + 30, enemy.y, -enemy.image.getWidth(null), enemy.image.getHeight(null), null);
            }


            for (bullet bullet : bulletsCopy) {
                if (!activeBullets.isEmpty()) bullet.update();
            }
            if (!activeBullets.isEmpty()) {

                for (bullet bullet : bulletsCopy) {
                    if (bullet.speed > 0) g.drawImage(bullet.image, bullet.x, bullet.y, null);
                    else
                        g.drawImage(bullet.image, bullet.x + bullet.image.getWidth(null), bullet.y, -bullet.image.getWidth(null), bullet.image.getHeight(null), null);
                }
            }
        } else end(g);
    }


    public void displayFPS() {
        System.out.println((int) (1 / (System.nanoTime() / 1000000000. - last)));
        last = System.nanoTime() / 1000000000.;
    }

    /**
     * End screen graphic displayed when {@code CyborgPlatformer.legacy.CyborgPlatform.game.isWon} is true.
     */
    public void end(Graphics g) {

        g.drawImage(Background.background[0], 0, 0, null);
        g.drawImage(Background.background[1], 0, 0, null);

        Font font = canvas.font;
        font = font.deriveFont(60.0f);
        g.setColor(Color.BLACK);
        g.setFont(font);
        String[] ends = new String[]{
                "You Won!",
                "It Took You",
                Player.deathCounter + " Attempts",
                "Press ESC to Restart,",
                "Or ENTER to exit",
        };
        g.drawString(ends[0], 460, 220);
        g.drawString(ends[1], 375, 300);
        g.drawString(ends[2], 405, 380);
        g.drawString(ends[3], 150, 460);
        g.drawString(ends[4], 270, 540);

    }


    @Override
    public void keyTyped(KeyEvent e) {

    }
     /**
     * Keyboard input handler.
     * Translates  key events into the keysPressed[] state used by {@link Player#update()}.
     *
     * keysPressed mapping (v1):
     * - [0] = A (move left), [1] = D (move right), [2] = W (jump), [3] = SPACE (shoot)
     * - ESC triggers restart by respawning entities and increments deathCounter.
     * - ENTER exits application.
     */

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A -> {
                keysPressed[0] = true;
                isLastDirectionForwards = false;
            }
            case KeyEvent.VK_D -> {
                keysPressed[1] = true;
                isLastDirectionForwards = true;
            }
            case KeyEvent.VK_W -> {
                keysPressed[2] = true;
                if (player.jumpCounter <= 1) {
                    player.jump();
                    player.jumpCounter += 2;
                }
            }
            case KeyEvent.VK_SPACE -> {
                keysPressed[3] = true;
            }
            case KeyEvent.VK_ESCAPE -> {
                Player.deathCounter++;
                CyborgPlatform.game.spawnEntities();
            }
            case KeyEvent.VK_ENTER -> {
                System.exit(0);
            }
        }
    }


    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A -> {
                keysPressed[0] = false;
            }
            case KeyEvent.VK_D -> {
                keysPressed[1] = false;
            }
            case KeyEvent.VK_W -> {
                if (player.jumpCounter < 3) player.jumpCounter--;
                keysPressed[2] = false;
            }
            case KeyEvent.VK_SPACE -> {
                keysPressed[3] = false;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        System.out.println(e.getX() + cameraOffset + "," + e.getY());
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

}
