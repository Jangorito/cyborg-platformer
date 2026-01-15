package CyborgPlatformer.view;

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
    private final double levelWidth;

    private double camX;
    private final double camY;

    private static final double HUD_MARGIN_X = 30.0;


    public Camera(double viewportWidth, double viewportHeight, double levelWidth) {
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        this.levelWidth = levelWidth;

        this.camX = 0;
        this.camY = 0; // V1 parity: no vertical scrolling
    }

    /**
     * Follow player horizontally (V1 parity).
     * Camera is clamped to [0, levelWidth - viewportWidth].
     */
    public void followX(double playerX) {
        camX = playerX - (viewportWidth - HUD_MARGIN_X) / 2.0;

        if (camX < 0) camX = 0;

        double maxX = levelWidth - viewportWidth - HUD_MARGIN_X;
        if (maxX < 0) camX = 0;
        else if (camX > maxX) camX = maxX;
    }


    public double viewportWidth() { return viewportWidth; }
    public double viewportHeight() { return viewportHeight; }
    public double levelWidth() { return levelWidth; }

    public double camX() { return camX; }
    public double camY() { return camY; }

    public double worldToScreenX(double worldX) { return worldX - camX; }
    public double worldToScreenY(double worldY) { return worldY - camY; }

    /**
     * Returns a parallax-adjusted camera offset (useful for backgrounds).
     * factor = 1.0  -> same speed as camera
     * factor = 2.0  -> half speed
     * factor = 4.0  -> quarter speed, etc.
     */
    public double parallaxCamX(double factor) {
        return camX / factor;
    }
}
