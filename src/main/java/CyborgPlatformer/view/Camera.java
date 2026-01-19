package CyborgPlatformer.view;

/**
 * Camera converts world coordinates -> screen coordinates.
 */
public final class Camera {

    private final double viewportWidth;
    private final double viewportHeight;
    private final double levelWidth;

    private double camX;
    private double camY;

    private static final double HUD_MARGIN_X = 30.0;
    private boolean lockX = false;


    public Camera(double viewportWidth, double viewportHeight, double levelWidth) {
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        this.levelWidth = levelWidth;

        this.camX = 0;
        this.camY = 0;
    }

    /**
     * Manually set camera vertical offset (world Y of viewport top).
     */
    public void setCamY(double camY) { this.camY = camY; }

    /**
     * Manually set camera horizontal offset (world X of viewport left).
     * Note: calling followX will overwrite this value.
     */
    public void setCamX(double camX) { this.camX = camX; }

    /** Lock horizontal follow so callers can set camX directly. */
    public void setLockX(boolean lock) { this.lockX = lock; }

    /**
     * Follow player horizontally
     * Camera is clamped to [0, levelWidth - viewportWidth].
     */
    public void followX(double playerX) {
        if (lockX) return;
        camX = playerX - (viewportWidth - HUD_MARGIN_X) / 2.0;

        if (camX < 0) camX = 0;

        double maxX = levelWidth - viewportWidth - HUD_MARGIN_X;
        if (maxX < 0) camX = 0;
        else if (camX > maxX) camX = maxX;
    }

    public double viewportWidth() { return viewportWidth; }

    public double camX() { return camX; }

    public double worldToScreenX(double worldX) { return worldX - camX; }
    public double worldToScreenY(double worldY) { return worldY - camY; }

}
