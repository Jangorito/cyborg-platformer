# Version 1

## V1 Class Diagram
![V1 Class Diagram](SoftwareDesignImages/V1%20UML%20Diagram.png)

## High-level overview
As shown in the Version 1 class diagram, certain classes form the backbone of the system's architecture.
The level can be completed without errors, however it's structural integrity is suboptimal due to an overuse of
global state and tightly coupled responsibilities.


### `CyborgPlatform.main`
`CyborgPlatform.main` is the entry point whose classifier contains a `global static` `Game` variable - `game`.
`CyborgPlatform` is a launcher and doesn't manage gameplay logic.

The `Game` and `canvas` objects are created, after which `canvas` is attached to a `JFrame` object.
The shared `Game` resource governs images, entity spawning & the render loop.

`CyborgPlatform` uses global access for other objects but, as conveyed in the diagram, this introduces dependencies
between unrelated parts of the system. 

### `canvas`
`canvas` renders assets (world & controllable `entities`) using the `paint(Graphics g)` method for `cameraOffset`.
The only method of simulation advancement is calling update methods on global static states.
Inputs are handled through the `KeyPressed` method where `KeyEvent`s translate into `keysPressed[]` booleans,
mapping input to game functionality whilst updating relevant states.

As highlighted by the red dependencies arrows in the V1 diagram, multiple gameplay entities depend directly on `canvas`,
despite it being a UI component.
It acts as a God Object with too many distinct responsibilities; update and render logic are tightly coupled violating
the Single Responsibility Principle.

### Interactions between globally coupled entities
Gameplay behaviour is dictated though entity interactions which albeit conceptually sound, presents critical coupling 
issues.

Without a dedicated game state controller, global static states stored in `canvas` govern these interactions.
E.G, in the `Bullet` workflow, `Player` creates `Bullet` instances and globally calls `canvas.activeBullets.add()`.
Meanwhile, `Bullet`'s `update()` loop checks for collisions before a global call to `canvas.activeBullets.()`.

These coupling issues are highlighted by the V1 diagram; gameplay entities rely on `canvas` for instance lifecycle
management and game logic.
This tightly binds entity behaviour to a specific runtime context and makes the game flow difficult to interpret, test
and extend.

### Game world & collision handling
`MapBlocks`, initialised during start up: stores the world, fetches collision and renders the level through global
calls from various places during runtime.

`Entity` (sub)classes govern collision detection and movement logic by checking instance copies against a new
candidate positions' collision status.
This tightly couples map/collision implementation with movement logic, violating convention and motivating a modular 
class structure where different aspects of the game are built upon independently.

`canvas` has the most varying responsibilities, violating core design principles like Single Responsibility and 
separation of concerns. This limits extensibility, the success of testing and safe refactoring. V2's
design addresses these issues by introducing conventional ownership boundaries between rendering, game logic, input and
world state.

## V2 Proposed Class Diagram
![V2 Proposed Class Diagram](SoftwareDesignImages/V2%20UML%20Diagram.png)

## V2 Planned Refactoring

The Version 2 design restructures the system by addressing global coupling and mixed responsibilities identified in V1. 
As shown in the Version 2 class diagram, core functionality is decomposed into smaller, single-responsibility components 
with clear ownership boundaries.

`CyborgPlatform` remains the application entry point but is limited to startup and wiring system components. 
Game flow and state transitions are delegated to a `GameController`, which coordinates the lifecycle of: 
- `World`
- `GameLoop`
- `Renderer`
- `InputHandler`

This alleviates the need for global static access through a centralised control logic coordinator.

Simulation advancement is decoupled from V1's `canvas` by introducing `GameLoop`, which calls updates to `World` per 
update tick. `World` owns all entities and level data, managing updates and interactions without relying on UI 
components. 
Global entity collections are eliminated in favour of explicit ownership.

Rendering is handled exclusively by `Renderer`, which draws the current world state by read-only before applying camera 
transformations via a dedicated `Camera` class. 
User input is similarly decoupled through `InputHandler` and `InputState`, allowing `Player` to respond to input without
direct dependence on UI events.

Overall, V2 improves modularity, testability, and extensibility by enforcing a separation of concerns and removing
hidden dependencies present in V1.

# Version 2
We need to move to JavaFX at some point

# Version 3
Let's add new features one day.

