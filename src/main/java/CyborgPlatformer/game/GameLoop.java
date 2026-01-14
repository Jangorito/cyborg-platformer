package CyborgPlatformer.game;

/**
 * Abstraction for controlling the simulation loop .
 *
 * Responsibilities:
 * - Defines lifecycle operations for loop: {@code start()}, {@code stop()}, {@code isRunning()}.
 * - Allows multiple loop strategies (fixed timestep, variable timestep, JavaFX AnimationTimer, etc.).
 *
 * Non-goals:
 * - Does not contain gameplay logic; it only orchestrates calls into an {@link Updatable}.
 * - Does not render or handle input.
 */

public interface GameLoop {
    void start();
    void stop();
    boolean isRunning();
}
