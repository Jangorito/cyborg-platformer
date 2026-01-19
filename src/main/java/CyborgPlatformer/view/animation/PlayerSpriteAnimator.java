package CyborgPlatformer.view.animation;

import CyborgPlatformer.view.model.PlayerRenderState;
import CyborgPlatformer.view.skin.SkinnedPlayerAssets;
import javafx.scene.image.Image;

import java.util.Objects;

/**
 * Animation resolver for player.
 */
public final class PlayerSpriteAnimator {

    // frame durations in NANOSECONDS (originally ms)
    private static final long IDLE_FRAME_NS = 250L * 1_000_000L;
    private static final long RUN_FRAME_NS  = 180L * 1_000_000L;

    private static final int AERIAL_FRAME_INDEX = 5;
    private static final int HURT_FRAME_INDEX   = 1;

    private static final int IDLE_LENGTH   = 3;

    private final Image[] idle;
    private final Image[] run;
    private final Image[] hurt;
    private final Image shoot;

    public PlayerSpriteAnimator(SkinnedPlayerAssets assets) {
        Objects.requireNonNull(assets, "assets");

        this.idle  = requireLen(assets.idle(), IDLE_LENGTH, "idle");
        this.run   = requireLen(assets.run(), 6, "run");
        this.hurt  = requireLen(assets.hurt(), 2, "hurt");
        this.shoot = Objects.requireNonNull(assets.shoot(), "shoot");
    }

    public Image resolve(PlayerRenderState s, long nowNs) {
        Objects.requireNonNull(s, "state");

        if (s.shooting()) {
            return shoot;
        }
        if (s.hurt()) {
            return hurt[HURT_FRAME_INDEX];
        }
        if (!s.grounded()) {
            // only sets aerial frame while rising (vy < 0).
            return run[AERIAL_FRAME_INDEX];
        }
        if (s.moving()) {
            return frame(run, nowNs, RUN_FRAME_NS);
        }
        return frame(idle, nowNs, IDLE_FRAME_NS);
    }

    private Image frame(Image[] frames, long nowNs, long frameNs) {
        int idx = (int) ((nowNs / frameNs) % frames.length);
        return frames[idx];
    }

    private Image[] requireLen(Image[] arr, int len, String name) {
        if (arr == null) throw new IllegalStateException(name + " is null");
        if (arr.length != len) throw new IllegalStateException(name + " must have length " + len + " but was " + arr.length);
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == null) throw new IllegalStateException(name + "[" + i + "] is null");
        }
        return arr;
    }


}
