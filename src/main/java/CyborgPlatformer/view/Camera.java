package CyborgPlatformer.view;

import CyborgPlatformer.model.entities.Player;

/**
 * Camera converts world coordinates -> screen coordinates.
 *
 * Phase 1/2:
 * - Simple center-on-player camera (what FxLauncher currently does).
 *
 * Later (V1 parity):
 * - Clamp X using mapWidth and viewport.
 * - Support parallax offsets.
 */
public final class Camera {

    private final double viewportWidth;
    private final double viewportHeight;

    private double camX;
    private double camY;

    public Camera(double viewportWidth, double viewportHeight) {
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
    }

    public void centerOn(Player player) {
        camX = player.getX() - viewportWidth / 2.0;
        camY = player.getY() - viewportHeight / 2.0;
    }

    public double viewportWidth() { return viewportWidth; }
    public double viewportHeight() { return viewportHeight; }

    public double camX() { return camX; }
    public double camY() { return camY; }

    public double worldToScreenX(double worldX) { return worldX - camX; }
    public double worldToScreenY(double worldY) { return worldY - camY; }
}
