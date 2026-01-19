package CyborgPlatformer.model.world;

import CyborgPlatformer.systems.PhysicsSystem;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Loads tile-based level collision data from a text map resource.
 *
 * Responsibilities:
 * - Reads a tile map (e.g., Maps.txt) and builds a {@link TileLevel} containing solid collision blocks.
 * - Preserves the raw tile grid (Phase 3) for rendering.
 * - '0' = empty & other character = solid tile (collision rule matches V1).
 *
 * Notes:
 * - Does not load images or decide which sprite corresponds to which tile character.
 * - Does not render tiles or background.
 * - Does not manage World/game state directly (caller sets {@code world.setLevel(...)}).
 *
 * V2 note:
 * - Replaces legacy {@code MapBlocks.getMap()} which mixed file parsing, collision storage, and rendering.
 * - Produces data-only level geometry compatible with {@link PhysicsSystem} via {@link Level#isSolidRect}.
 */
public final class TileLevelLoader {

    /**
     * Legacy tile grid step. In V1, tileX/tileY advanced by 48 each cell.
     */
    public static final int TILE_SIZE = 48;

    private TileLevelLoader() {
        // Utility class.
    }

    /**
     * Load a {@link TileLevel} from a classpath resource stream.
     *
     * @param mapStream input stream for the map file (e.g. /Maps.txt)
     * @return TileLevel containing solid collision blocks + raw tile grid
     * @throws IllegalArgumentException if mapStream is null
     */
    public static TileLevel load(InputStream mapStream) {
        if (mapStream == null) {
            throw new IllegalArgumentException("mapStream cannot be null (resource not found?)");
        }

        List<SolidBlock> solids = new ArrayList<>();

        // preserve raw lines for tile rendering
        List<String> lines = new ArrayList<>();

        try (Scanner scanner = new Scanner(mapStream)) {
            int row = 0;

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                lines.add(line);

                for (int col = 0; col < line.length(); col++) {
                    char tile = line.charAt(col);

                    // V1 rule: '0' means empty space (skip)
                    if (tile == '0') continue;

                    solids.add(new SolidBlock(
                            col * TILE_SIZE,
                            row * TILE_SIZE,
                            TILE_SIZE,
                            TILE_SIZE
                    ));
                }


                row++;
            }
        }

        // Build raw tile grid (char[][]) from lines
        char[][] tiles = new char[lines.size()][];
        for (int r = 0; r < lines.size(); r++) {
            tiles[r] = lines.get(r).toCharArray();
        }

        List<EnemySpawn> enemySpawns = List.of(
                new EnemySpawn(1475, 230, 2),
                new EnemySpawn(2570, 196, 2),
                new EnemySpawn(2750, 320, 2),
                new EnemySpawn(3060, 470, 2),
                new EnemySpawn(4219, 100, 2),
                new EnemySpawn(4900, 530, 2),
                new EnemySpawn(4970, 530, 2),
                new EnemySpawn(5040, 539, 2),
                new EnemySpawn(6397, 196, 2),
                new EnemySpawn(6540, 520, 2),
                new EnemySpawn(6600, 520, 2),
                new EnemySpawn(6660, 520, 2),
                new EnemySpawn(6720, 520, 2)
        );

        // --- DEBUG: detect enemies that spawn inside solids ---
        final double ENEMY_W = 20;
        final double ENEMY_H = 20;

        for (int i = 0; i < enemySpawns.size(); i++) {
            EnemySpawn s = enemySpawns.get(i);

            boolean inSolid = rectHitsAnySolid(solids, s.x(), s.y(), ENEMY_W, ENEMY_H);
            if (inSolid) {
                System.out.println("ENEMY SPAWN IN SOLID: idx=" + i
                        + " x=" + s.x() + " y=" + s.y() + " hp=" + s.hp());
            }
        }

        return new TileLevel(solids, enemySpawns, tiles);
    }

    private static boolean rectHitsAnySolid(List<SolidBlock> solids, double x, double y, double w, double h) {
        double x2 = x + w;
        double y2 = y + h;

        for (SolidBlock b : solids) {
            double bx = b.x();
            double by = b.y();
            double bx2 = bx + b.width();
            double by2 = by + b.height();

            boolean widthIsPositive = Math.min(x2, bx2) > Math.max(x, bx);
            boolean heightIsPositive = Math.min(y2, by2) > Math.max(y, by);

            if (widthIsPositive && heightIsPositive) return true;
        }
        return false;
    }
}
