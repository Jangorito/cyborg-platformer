package CyborgPlatformer.model.world;

/**
 * Solid collision block.
 *
 * Responsibilities:
 * - Stores position and dimensions of a rectangle.
 *
 * Notes:
 * - Pure data holder (no behaviour).
 * - Used by {@link TileLevel} for collision detection.
 *
 * V2 note:
 * - Replaces legacy {@code MapBlocks} instances that mixed rendering and collision.
 */
public record SolidBlock(double x, double y, double width, double height) { }
