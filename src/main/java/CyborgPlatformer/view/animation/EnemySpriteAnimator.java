package CyborgPlatformer.view.animation;

import CyborgPlatformer.assets.AssetManager;
import CyborgPlatformer.view.model.EnemyRenderState;
import javafx.scene.image.Image;

public final class EnemySpriteAnimator {

    private static final long IDLE_MS = 250;
    private static final long WALK_MS = 180;
    private static final long RUN_MS  = 130;

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

        // Reset animation when switching modes (prevents popping)
        if (nextMode != mode) {
            mode = nextMode;
            frame = 0;
            lastFrameTime = now;
        }

        // Resolve current frame
        return switch (mode) {
            case HURT -> hurt;
            case RUN  -> animate(run, RUN_MS, now);
            case WALK -> animate(walk, WALK_MS, now);
            case IDLE -> animate(idle, IDLE_MS, now);
        };
    }

    private Image animate(Image[] frames, long duration, long now) {
        if (frames.length == 0) return null;

        if (now - lastFrameTime > duration) {
            frame = (frame + 1) % frames.length;
            lastFrameTime = now;
        }
        return frames[frame];
    }
}
