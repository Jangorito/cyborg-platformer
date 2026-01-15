package CyborgPlatformer.view.model;

import CyborgPlatformer.model.entities.Enemy;

public final class EnemyRenderStateFactory {

    public static EnemyRenderState from(Enemy e) {
        return new EnemyRenderState(
                e.isAlive(),
                e.isRunning(),
                e.isDamaged(),
                e.isFacingRight(),
                e.getVX()
        );
    }
}