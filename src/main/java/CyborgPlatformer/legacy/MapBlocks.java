package CyborgPlatformer.legacy;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Represents a solid block used for level collisions and rendering.
 *
 * current structure:
 * - A static list {@link #map} stores all blocks in the level.
 * - Entities collide against these blocks via {@link entity#intersect()} and {@link entity#gravity()}.
 * - The level layout is loaded from a text file (Maps.txt) where characters map to tile images.
 *
 * v2 note:
 * - Map loading could be made more data-driven and configurable (e.g., external tile legend,
 *   multiple levels, validation, separation of rendering vs collision layers).
 */
public class MapBlocks {

    public static ArrayList<MapBlocks> map = new ArrayList<>();
    protected int x;
    protected int y;
    protected Image image;
    protected static int mapWidth;
    public static Image[] mapImages;


    public MapBlocks(Image image, int x, int y) {
        this.image = image;
        this.x = x;
        this.y = y;
    }

    /**
     * Builds the level layout by adding CyborgPlatformer.legacy.MapBlocks from Maps.txt to the static map list.
     * - Coordinates are a pixel-based 48x47 grid in v1.
     * - '0' = empty space
     * - {@link #mapWidth} is derived from the number of columns.
     */
    public static void getMap() {
        int tileX = 0;
        int tileY = 0;
        File map1 = new File("src/main/resources/Maps.txt");
        try {
            Scanner s = new Scanner(map1);
            Image imageTile = mapImages[9];
            while (s.hasNextLine()) {
                String line = s.nextLine();
                String[] types = line.split("");
                mapWidth = types.length * 48;
                for (String value : types) {
                    switch (value) {
                        case "1" -> imageTile = mapImages[0];
                        case "2" -> imageTile = mapImages[1];
                        case "3" -> imageTile = mapImages[2];
                        case "4" -> imageTile = mapImages[3];
                        case "5" -> imageTile = mapImages[4];
                        case "6" -> imageTile = mapImages[5];
                        case "7" -> imageTile = mapImages[6];
                        case "8" -> imageTile = mapImages[7];
                        case "9" -> imageTile = mapImages[8];
                        case "A" -> imageTile = mapImages[9];
                        case "B" -> imageTile = mapImages[10];
                        case "C" -> imageTile = mapImages[11];
                        case "D" -> imageTile = mapImages[12];
                        case "E" -> imageTile = mapImages[13];
                        case "F" -> imageTile = mapImages[14];
                        case "G" -> imageTile = mapImages[15];
                        case "H" -> imageTile = mapImages[16];
                        case "I" -> imageTile = mapImages[17];
                        case "J" -> imageTile = mapImages[18];
                    }
                    if (value.equals("0")) {
                        tileX += 48;
                        continue;
                    }
                    MapBlocks block = new MapBlocks(imageTile, tileX, tileY);
                    map.add(block);
                    tileX += 48;
                }
                tileY += 48;
                tileX = 0;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Draws all map tiles to the screen at corresponding world coordinates.
     */
    public static void drawMap(Graphics g) {
        for (int i = 0; i < MapBlocks.map.size(); i++) {
            g.drawImage(MapBlocks.map.get(i).image, MapBlocks.map.get(i).x, MapBlocks.map.get(i).y, null);
        }
    }
}

/**
 * Draws layered background images across the full map.
 */
class Background {
    public static Image[] background;

    public static void drawBackground(Graphics g) {
        for (int mapX = 0; mapX < MapBlocks.mapWidth; mapX += 1280) {
            g.drawImage(background[0], mapX + canvas.cameraOffset, 0, null);
            g.drawImage(background[1], mapX + canvas.cameraOffset / 2, 0, null);
            g.drawImage(background[2], mapX + canvas.cameraOffset / 4, 0, null);
            g.drawImage(background[3], mapX + canvas.cameraOffset / 16, 0, null);
        }
    }
}
