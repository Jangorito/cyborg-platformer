package CyborgPlatformer.model.world;

/**
 * Basic level collision.
 * Implementation will be expanded later with MapBlocks-style tile data.
 */
public interface Level {
    boolean isSolidRect(double x, double y, double w, double h);
}
