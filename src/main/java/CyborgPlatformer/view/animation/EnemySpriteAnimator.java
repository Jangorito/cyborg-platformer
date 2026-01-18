package CyborgPlatformer.view.animation;

import CyborgPlatformer.assets.AssetManager;
import CyborgPlatformer.view.model.EnemyRenderState;
import javafx.scene.image.Image;

/**
 * View-only animator for enemy sprites.
 *
 * Responsibilities:
 * - Resolve the correct enemy animation frame from render state
 * - Advance animation timing with V1-parity frame durations
 * - Reset animations cleanly when switching modes
 *
 * Not:
 * - Game logic or AI
 * - Physics or collision handling
 * - Simulation state mutation
 */
public final class EnemySpriteAnimator {

    // durations in nanoseconds
    private static final long IDLE_NS = 250L * 1_000_000L;
    private static final long WALK_NS = 180L * 1_000_000L;
    private static final long RUN_NS  = 130L * 1_000_000L;

    private enum Mode { IDLE, WALK, RUN, HURT }

    private final Image[] idle;
    private final Image[] walk;
    private final Image[] run;
    private final Image hurt;

    private Mode mode = null;

    private long lastFrameTime = 0;
    private int frame = 0;

    public EnemySpriteAnimator(AssetManager assets) {
        this.idle = assets.enemyIdle();
        this.walk = assets.enemyWalk();
        this.run  = assets.enemyRun();
        this.hurt = assets.enemyHurt();
    }

    public Image resolve(EnemyRenderState s, long now) {
        if (!s.alive()) return null;

        // Determine mode (priority: hurt > run > walk > idle)
        Mode nextMode;
        if (s.damaged()) {
            nextMode = Mode.HURT;
        } else if (s.running()) {
            nextMode = Mode.RUN;
        } else if (Math.abs(s.vx()) > 0.1) {
            nextMode = Mode.WALK;
        } else {
            nextMode = Mode.IDLE;
        }

        // Reset animation when switching modes
        if (nextMode != mode) {
            mode = nextMode;
            frame = 0;
            lastFrameTime = now;
        }

        // Resolve current frame
        return switch (mode) {
            case HURT -> hurt;
            case RUN  -> animate(run, RUN_NS, now);
            case WALK -> animate(walk, WALK_NS, now);
            case IDLE -> animate(idle, IDLE_NS, now);
        };
    }

    private Image animate(Image[] frames, long durationNs, long nowNs) {
        if (frames.length == 0) return null;

        if (nowNs - lastFrameTime > durationNs) {
            frame = (frame + 1) % frames.length;
            lastFrameTime = nowNs;
        }
        return frames[frame];
    }
}
