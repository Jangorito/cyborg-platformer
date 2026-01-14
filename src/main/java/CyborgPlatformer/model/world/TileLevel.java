package CyborgPlatformer.model.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tile-based level collision model.
 *
 * Responsibilities:
 * - Stores solid collision geometry for the level as rectangles.
 * - Answers collision queries via {@link #isSolidRect(double, double, double, double)}.
 *
 *
 * Notes:
 * - Does not render tiles.
 * - Does not load files or resources.
 * - Does not store images or visual data.
 *
 * V2 note:
 * - Replaces static global {@code MapBlocks.map} with instance-owned level data.
 * - Enables multiple levels and testable collision logic.
 */
public class TileLevel implements Level {

    private final List<SolidBlock> solids;

    public TileLevel(List<SolidBlock> solids) {
        this.solids = new ArrayList<>(solids);
    }

    public List<SolidBlock> getSolids() {
        return Collections.unmodifiableList(solids);
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
