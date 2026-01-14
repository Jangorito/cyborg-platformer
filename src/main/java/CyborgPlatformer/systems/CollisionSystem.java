package CyborgPlatformer.systems;

import CyborgPlatformer.model.world.World;

public class CollisionSystem {

    private final World world;

    public CollisionSystem(World world) {
        this.world = world;
    }

    public boolean intersectsSolid(double x, double y, double w, double h) {
        return world.isSolidRect(x, y, w, h);
    }
}
