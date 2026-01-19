package CyborgPlatformer.model.world;

/**
 * Solid collision block.
 *
 * Responsibilities:
 * - Stores position and dimensions of a rectangle.
 *
 * Notes:
 * - Pure data holder.
 * - Used by {@link TileLevel} for collision detection.
 *
 */
public record SolidBlock(double x, double y, double width, double height) { }
