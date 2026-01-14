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
 * - '0' = empty & other character = solid tile.
 *
 * How:
 * - Interprets the file as a grid of characters.
 * - Each non-'0' character becomes a {@link SolidBlock} at (col * TILE_SIZE, row * TILE_SIZE).
 * - Uses a fixed tile size consistent with legacy {@code MapBlocks.getMap()}.
 *
 * Notes:
 * - Does not load images or decide which sprite corresponds to each tile character.
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
        // Utility class: no instances.
    }

    /**
     * Load a {@link TileLevel} from a classpath resource stream.
     *
     * @param mapStream input stream for the map file (e.g. /Maps.txt)
     * @return TileLevel containing solid collision blocks
     * @throws IllegalArgumentException if mapStream is null
     */
    public static TileLevel load(InputStream mapStream) {
        if (mapStream == null) {
            throw new IllegalArgumentException("mapStream cannot be null (resource not found?)");
        }

        List<SolidBlock> solids = new ArrayList<>();

        try (Scanner scanner = new Scanner(mapStream)) {
            int row = 0;

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                // Legacy used split("") which treats each char as a tile.
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

        return new TileLevel(solids);
    }
}
