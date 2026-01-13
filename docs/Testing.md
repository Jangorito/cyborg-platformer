# Testing Plan and Results

## Overview
Task 3 introduces automated testing using JUnit 5 to provide regression safety for the Version 1 (legacy) codebase and to establish a foundation for Version 2 refactoring.

## Version 1 Test plan table
| ID | Test Class | Version | Type | What it verifies | Notes |
|---|---|---|---|---|---|
| T1 | EntityTest | V1 | Unit / Regression | Constructor initialises core fields correctly | deterministic |
| T2 | EntityTest | V1 | Unit / Regression | `intersect()` returns false when no map block overlap occurs | resets `MapBlocks.map` |
| T3 | EntityTest | V1 | Unit / Regression | `intersect()` returns true when entity overlaps a map block | uses dummy map block |
| T4 | EntityTest | V1 | Unit / Regression | `jump()` first jump sets correct upward velocity | jumpCounter case |
| T5 | EntityTest | V1 | Unit / Regression | `jump()` second jump applies alternative jump behaviour | jumpCounter case |
| T6 | EntityTest | V1 | Unit / Regression | `copy(newX,newY)` preserves stats but changes position | regression baseline |
| T7 | EntityTest | V1 | Unit / Regression | `gravity()` causes falling movement when no collision occurs | dummy image + empty map |
| T8 | EntityTest | V1 | Unit / Regression | `gravity()` collision prevents falling and updates grounded state | map collision path |
| T9 | BulletTest | V1 | Unit / Regression | `travelledDistance()` is zero at initial position | pure maths |
| T10 | BulletTest | V1 | Unit / Regression | `travelledDistance()` computes correctly after movement | pure maths |
| T11 | BulletTest | V1 | Unit / Regression | `update()` moves bullet when unobstructed | avoids enemy damage |
| T12 | BulletTest | V1 | Unit / Regression | `update()` removes bullet after travelling ≥ 600 pixels | lifetime boundary |
| T13 | BulletTest | V1 | Unit / Regression | Bullet does not collide with enemies when none are present | resets `canvas.enemies` |
| T14 | PlayerTest | V1 | Unit / Regression | Constructor initialises position, health, ammo, and hitbox | deterministic |
| T15 | PlayerTest | V1 | Unit / Regression | `isMoving()` reflects left/right key input state | uses `canvas.keysPressed[]` |
| T16 | PlayerTest | V1 | Unit / Regression | `isJumping()` reflects jumpCounter and grounded state | deterministic |
| T17 | PlayerTest | V1 | Unit / Regression | `updateState()` sets running state when moving and grounded | legacy state logic |
| T18 | PlayerTest | V1 | Unit / Regression | `updateState()` sets aerial state when jumping or falling | legacy state logic |
| T19 | PlayerTest | V1 | Unit / Regression | `updateState()` sets hurt state when damaged | documents legacy behaviour |
| T20 | PlayerTest | V1 | Unit / Regression | `updateState()` prioritises shooting state over hurt as implemented | legacy priority order |
| T21 | PlayerTest | V1 | Unit / Regression | `shoot()` spawns bullet facing right with offset +47 and speed +10 | uses `Player.keys[]` + `canvas.isLastDirectionForwards` |
| T22 | PlayerTest | V1 | Unit / Regression | `shoot()` spawns bullet facing left with offset -25 and speed -10 | regression on offsets |
| T23 | PlayerTest | V1 | Unit / Regression | `shoot()` does nothing when ammo is zero | deterministic |
| T24 | EnemyTest | V1 | Unit / Regression | `damage()` reduces health and applies damaged/knockback behaviour | avoids UI |
| T25 | EnemyTest | V1 | Interaction | Bullet overlap triggers `Enemy.damage()` and bullet removal | deterministic cross-class interaction |

## Version 1 Test Results
Version 1 tests were executed using `mvn verify` and completed successfully.

- Total tests executed: 25
- Failing tests: 0
- Skipped tests: 0

The test suite validates core gameplay mechanics including entity movement, collision handling, projectile behaviour, and player state logic. 

UI rendering, animation, and real-time game loop behaviour were intentionally excluded due to tight coupling with Swing and reliance on graphics.

JaCoCo code coverage was generated as part of the Maven verification phase. Overall coverage:**_29%_**. This proves  limited testability of rendering-heavy legacy code. Coverage is concentrated on core logic classes (`Entity`, `Player`, `Bullet`, `Enemy`) which form the regression baseline for refactoring.

### Planned Tests (Version 2)
| ID | Test Class | Version | Type | What it will verify | Design rationale |
|---|---|---|---|---|---|
| V2-T1 | GameControllerTest | V2 | Unit | Game state transitions (menu → playing → win/lose) | Introduced to centralise flow previously implicit in `canvas` |
| V2-T2 | GameLoopTest | V2 | Unit | Fixed-timestep update calls `World.update()` deterministically | Replaces V1 render-driven simulation advancement |
| V2-T3 | WorldTest | V2 | Unit | World owns and updates entities without UI dependencies | Eliminates global static entity collections |
| V2-T4 | CollisionSystemTest | V2 | Unit | Collision resolution independent of entity movement logic | Decouples collision from `Entity.copy()` logic |
| V2-T5 | PlayerStateTest | V2 | Unit | Enum-based player state transitions replace string logic | Improves correctness and testability of state handling |
| V2-T6 | InputHandlerTest | V2 | Unit | Input events correctly update `InputState` | Removes reliance on `canvas.keysPressed[]` |
| V2-T7 | RendererTest | V2 | Integration | Renderer draws world state without mutating logic | Enforces read-only rendering responsibility |
| V2-T8 | CameraTest | V2 | Unit | Camera offset and transformations applied correctly | Separates camera logic from rendering |
| V2-T9 | BulletSystemTest | V2 | Unit | Projectile lifecycle managed by World, not entities | Removes global `canvas.activeBullets` access |
| V2-T10 | EnemyAITest | V2 | Unit | Enemy behaviour decisions independent of rendering context | Enables deterministic AI testing |

# CI/CD Setup
Todo late november. So far, just managed to setup an automatic junit test pipeline.
