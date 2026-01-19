package CyborgPlatformer.model.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tile-based level collision model.
 *
 * Responsibilities:
 * - Stores solid collision geometry for the level as rectangles.
 * - Stores the raw tile grid for rendering.
 * - Answers collision queries via {@link #isSolidRect(double, double, double, double)}.
 *
 * Notes:
 * - Does not render tiles.
 * - Does not load files or resources.
 * - Does not store images.
 *
 */
public class TileLevel implements Level {

    private final List<SolidBlock> solids;
    private final List<EnemySpawn> enemySpawns;

    // raw tile characters for renderer.
    private final char[][] tiles;

    public TileLevel(List<SolidBlock> solids, List<EnemySpawn> enemySpawns, char[][] tiles) {
        this.solids = new ArrayList<>(solids);
        this.enemySpawns = new ArrayList<>(enemySpawns);
        this.tiles = tiles;
    }

    public List<SolidBlock> getSolids() {
        return Collections.unmodifiableList(solids);
    }

    public List<EnemySpawn> getEnemySpawns() {
        return Collections.unmodifiableList(enemySpawns);
    }

    /**
     * Raw tile grid from the map file.
     * '0' = empty, other chars follow V1 legend (1-9, A-J).
     *
     * Renderer reads this.
     */
    public char[][] getTiles() {
        return tiles;
    }

    @Override
    public boolean isSolidRect(double x, double y, double w, double h) {
        double x2 = x + w;
        double y2 = y + h;

        for (SolidBlock b : solids) {
            double bx = b.x();
            double by = b.y();
            double bx2 = bx + b.width();
            double by2 = by + b.height();

            boolean widthIsPositive = Math.min(x2, bx2) > Math.max(x, bx);
            boolean heightIsPositive = Math.min(y2, by2) > Math.max(y, by);

            if (widthIsPositive && heightIsPositive) {
                return true;
            }
        }
        return false;
    }
}
