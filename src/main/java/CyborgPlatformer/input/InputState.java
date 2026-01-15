package CyborgPlatformer.input;

/**
 * Current input for the simulation tick.
 *
 * Responsibilities:
 * - Stores player intent (left/right/jump/shoot).
 *
 * Notes:
 * - Produced by an InputHandler (TODO: JavaFX later).
 * - Read by GameController.
 */
public final class InputState {

    private final boolean left;
    private final boolean right;
    private final boolean jump;
    private final boolean shoot;
    private final boolean reset;
    private final boolean kill;
    private final boolean cheat;

    public InputState(boolean left, boolean right, boolean jump, boolean shoot, boolean reset, boolean kill, boolean cheat) {
        this.left = left;
        this.right = right;
        this.jump = jump;
        this.shoot = shoot;
        this.reset = reset;
        this.kill = kill;
        this.cheat = cheat;
    }

    public boolean left() { return left; }
    public boolean right() { return right; }
    public boolean jump() { return jump; }
    public boolean shoot() { return shoot; }
    public boolean reset() { return reset; }
    public boolean kill() { return kill;}
    public boolean cheat() { return cheat;}

    public static InputState none() {
        return new InputState(false, false, false, false, false, false, false);
    }
}