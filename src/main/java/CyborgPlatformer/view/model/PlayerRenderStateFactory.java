package CyborgPlatformer.view.model;

import CyborgPlatformer.controller.GameController;
import CyborgPlatformer.model.entities.Player;

import java.util.Objects;

public final class PlayerRenderStateFactory {

    public PlayerRenderState build(Player p, GameController c) {
        Objects.requireNonNull(p, "player");
        Objects.requireNonNull(c, "controller");

        return new PlayerRenderState(
                p.getX(),
                p.getY(),
                p.getWidth(),
                p.getHeight(),

                // grounded
                p.isGrounded(),

                // moving (V1-style intent, not velocity)
                c.moveIntent(),

                // hurt (invulnerability window)
                p.isInKnockback(),

                // shooting visual (maps to V1 justShot)
                p.isShootingVisualActive(),

                // facing direction (comes from controller, like V1 keys)
                c.isFacingRight(),

                // vertical velocity (used for aerial logic)
                p.getVY()
        );
    }
}
