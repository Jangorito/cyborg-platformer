package CyborgPlatformer.app;

import CyborgPlatformer.controller.GameController;
import CyborgPlatformer.input.InputState;
import CyborgPlatformer.model.entities.Bullet;

import java.util.List;

public final class HeadlessMain {

    public static void main(String[] args) throws InterruptedException {
        CyborgPlatformerApp app = new CyborgPlatformerApp();
        GameController controller = app.getController();

        final double dt = 1.0 / 60.0; // seconds
        long start = System.currentTimeMillis();

        int tick = 0;
        while (System.currentTimeMillis() - start < 10_000) {
            double t = (System.currentTimeMillis() - start) / 1000.0;

            // Scripted "play":
            boolean right = t < 2.0;
            boolean left = false;

            // jump pulse
            boolean jump = (t > 2.2 && t < 2.2 + dt);

            // shoot in pulses
            boolean shoot = (t > 3.0 && t < 6.0) && (tick % 15 == 0);

            InputState input = new InputState(left, right, jump, shoot, false, false, false);

            controller.step(dt, input);

            if (tick % 15 == 0) {
                var p = controller.getPlayer();
                List<Bullet> bullets = controller.getWorld().getEntitiesOfType(Bullet.class);

                System.out.printf(
                        "t=%.2f  x=%.1f y=%.1f vy=%.2f grounded=%s ammo=%d bullets=%d%n",
                        t, p.getX(), p.getY(), p.getVY(), p.isGrounded(), p.getAmmo(), bullets.size()
                );
            }

            tick++;
            Thread.sleep(16);
        }

        System.out.println("Headless run complete.");
    }
}
