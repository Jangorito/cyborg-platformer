package CyborgPlatformer.view.model;

public record PlayerRenderState(
        double x, double y, double w, double h,
        boolean grounded,
        boolean moving,
        boolean hurt,
        boolean shooting,
        boolean facingRight,
        double vy
) {}
