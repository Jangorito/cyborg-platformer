Add notes to:
- PlayerShootingTest.java
- BulletBehaviourTest.java
- GameControllerTest
- PhysicsSystem
- PhysicsSystemGravityTest.java
- PhysicsSystemJumpTest
- BulletBehaviourTest
- TileLevel


Making game runnable:
- `CyborgPlatformerApp`
    - currently does:
        - `player.setPosition(100, 100);`
    - should this be
        - `x=48*2, y=48*2`?

- `GameController`:
    - `for (Enemy e : world.getEnemies()) {
        physics.applyGravity(e, dt);
        }`
      - there's currently no `Enemy` in `GameController`
    - `applyInput(...)`:
      - change the condition so jumpCounter matters because we're doing double jump

- Create: `CyborgPlatformer.app.HeadlessMain`
    - will be the runner/launcher that starts the game

- Make `InputState` mutable for real keyboard later

- problems with the enemy right now:
- they jump too high to attack me
- they don't do enough damage, two hits I should be done
- if they run into me, they're basically stuck to me which makes me think 
  - they're too fast
  - there prolly needs to be a knockback effect on contact 