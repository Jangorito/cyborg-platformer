package CyborgPlatformer.game;

// Not for V2
public class FixedTimestepLoop implements GameLoop {
    private final Updatable target;
    private final double dt;
    private boolean running;

    public FixedTimestepLoop(Updatable target, double dt) {
        this.target = target;
        this.dt = dt;
    }

    /** Manual tick for tests / headless stepping */
    public void tickOnce() {
        target.update(dt);
    }

    @Override public void start() { running = true; }
    @Override public void stop() { running = false; }
    @Override public boolean isRunning() { return running; }
}
