package CyborgPlatformer.view.animation;

import CyborgPlatformer.assets.AssetManager;
import CyborgPlatformer.view.model.PlayerRenderState;
import javafx.scene.image.Image;

import java.util.Objects;

/**
 * V1 parity animation resolver for player (view-only).
 *
 * V1 updateState priority:
 *   shooting > hurt > aerial > running > idle
 *
 * V1 timings:
 *   idle: 250ms per frame
 *   run:  180ms per frame
 *
 * V1 frames:
 *   aerial: running[5] when vy < 0 (we keep it for whole airborne for determinism)
 *   hurt:   hurt[1]
 *   shoot:  shootingSprite
 *
 * Facing handled by Renderer (flip).
 */
public final class PlayerSpriteAnimator {

    private static final long IDLE_FRAME_MS = 250L;
    private static final long RUN_FRAME_MS  = 180L;

    private static final int AERIAL_FRAME_INDEX = 5;
    private static final int HURT_FRAME_INDEX   = 1;

    private final Image[] idle;
    private final Image[] run;
    private final Image[] hurt;
    private final Image shoot;

    public PlayerSpriteAnimator(AssetManager assets) {
        Objects.requireNonNull(assets, "assets");
        this.idle  = requireLen(assets.playerIdle(), 4, "playerIdle");
        this.run   = requireLen(assets.playerRun(), 6, "playerRun");
        this.hurt  = requireLen(assets.playerHurt(), 2, "playerHurt");
        this.shoot = Objects.requireNonNull(assets.playerShoot(), "playerShoot");
    }

    public Image resolve(PlayerRenderState s, long nowMs) {
        Objects.requireNonNull(s, "state");

        if (s.shooting()) {
            return shoot;
        }
        if (s.hurt()) {
            return hurt[HURT_FRAME_INDEX];
        }
        if (!s.grounded()) {
            // V1: only sets aerial frame while rising (vy < 0). We keep it stable across airborne.
            return run[AERIAL_FRAME_INDEX];
        }
        if (s.moving()) {
            return frame(run, nowMs, RUN_FRAME_MS);
        }
        return frame(idle, nowMs, IDLE_FRAME_MS);
    }

    private Image frame(Image[] frames, long nowMs, long frameMs) {
        int idx = (int) ((nowMs / frameMs) % frames.length);
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
