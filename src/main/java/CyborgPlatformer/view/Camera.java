package CyborgPlatformer.view;

/**
 * Camera converts world coordinates -> screen coordinates.
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
     * Follow player horizontally
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

    public double camX() { return camX; }

    public double worldToScreenX(double worldX) { return worldX - camX; }
    public double worldToScreenY(double worldY) { return worldY - camY; }

}
