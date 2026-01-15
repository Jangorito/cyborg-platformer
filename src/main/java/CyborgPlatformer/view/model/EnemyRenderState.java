package CyborgPlatformer.view.model;

public record EnemyRenderState(
        boolean alive,
        boolean running,
        boolean damaged,
        boolean facingRight,
        double vx
) {}
