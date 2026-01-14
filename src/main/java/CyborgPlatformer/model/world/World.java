package CyborgPlatformer.model.world;

import CyborgPlatformer.game.Updatable;

public class World implements Updatable {

    private int tickCount = 0;

    @Override
    public void update(double dt) {
        // placeholder deterministic state change
        tickCount++;
    }

    public int getTickCount() {
        return tickCount;
    }
}
