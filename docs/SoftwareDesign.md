# Version 1

## V1 Class Diagram
![V1 Class Diagram](SoftwareDesignImages/V1%20UML%20Diagram.png)

## High-level overview
As shown in the Version 1 class diagram, certain classes form the backbone of the system's architecture.
The level can be completed without errors, however it's structural integrity is suboptimal due to an overuse of
global state and tightly coupled responsibilities.


### `CyborgPlatformer.legacy.CyborgPlatform.main`
`CyborgPlatformer.legacy.CyborgPlatform.main` is the entry point whose classifier contains a `global static` `CyborgPlatformer.legacy.Game` variable - `game`.
`CyborgPlatformer.legacy.CyborgPlatform` is a launcher and doesn't manage gameplay logic.

The `CyborgPlatformer.legacy.Game` and `CyborgPlatformer.legacy.canvas` objects are created, after which `CyborgPlatformer.legacy.canvas` is attached to a `JFrame` object.
The shared `CyborgPlatformer.legacy.Game` resource governs images, CyborgPlatformer.legacy.entity spawning & the render loop.

`CyborgPlatformer.legacy.CyborgPlatform` uses global access for other objects but, as conveyed in the diagram, this introduces dependencies
between unrelated parts of the system. 

### `CyborgPlatformer.legacy.canvas`
`CyborgPlatformer.legacy.canvas` renders assets (world & controllable `entities`) using the `paint(Graphics g)` method for `cameraOffset`.
The only method of simulation advancement is calling update methods on global static states.
Inputs are handled through the `KeyPressed` method where `KeyEvent`s translate into `keysPressed[]` booleans,
mapping input to game functionality whilst updating relevant states.

As highlighted by the red dependencies arrows in the V1 diagram, multiple gameplay entities depend directly on `CyborgPlatformer.legacy.canvas`,
despite it being a UI component.
It acts as a God Object with too many distinct responsibilities; update and render logic are tightly coupled violating
the Single Responsibility Principle.

### Interactions between globally coupled entities
Gameplay behaviour is dictated though CyborgPlatformer.legacy.entity interactions which albeit conceptually sound, presents critical coupling 
issues.

Without a dedicated game state controller, global static states stored in `CyborgPlatformer.legacy.canvas` govern these interactions.
E.G, in the `Bullet` workflow, `CyborgPlatformer.legacy.Player` creates `Bullet` instances and globally calls `CyborgPlatformer.legacy.canvas.activeBullets.add()`.
Meanwhile, `Bullet`'s `update()` loop checks for collisions before a global call to `CyborgPlatformer.legacy.canvas.activeBullets.()`.

These coupling issues are highlighted by the V1 diagram; gameplay entities rely on `CyborgPlatformer.legacy.canvas` for instance lifecycle
management and game logic.
This tightly binds CyborgPlatformer.legacy.entity behaviour to a specific runtime context and makes the game flow difficult to interpret, test
and extend.

### CyborgPlatformer.legacy.Game world & collision handling
`CyborgPlatformer.legacy.MapBlocks`, initialised during start up: stores the world, fetches collision and renders the level through global
calls from various places during runtime.

`Entity` (sub)classes govern collision detection and movement logic by checking instance copies against a new
candidate positions' collision status.
This tightly couples map/collision implementation with movement logic, violating convention and motivating a modular 
class structure where different aspects of the game are built upon independently.

`CyborgPlatformer.legacy.canvas` has the most varying responsibilities, violating core design principles like Single Responsibility and 
separation of concerns. This limits extensibility, the success of testing and safe refactoring. V2's
design addresses these issues by introducing conventional ownership boundaries between rendering, game logic, input and
world state.

## V2 Proposed Class Diagram
![V2 Proposed Class Diagram](SoftwareDesignImages/V2%20UML%20Diagram.png)

## V2 Planned Refactoring

The Version 2 design restructures the system by addressing global coupling and mixed responsibilities identified in V1. 
As shown in the Version 2 class diagram, core functionality is decomposed into smaller, single-responsibility components 
with clear ownership boundaries.

`CyborgPlatformer.legacy.CyborgPlatform` remains the application entry point but is limited to startup and wiring system components. 
CyborgPlatformer.legacy.Game flow and state transitions are delegated to a `GameController`, which coordinates the lifecycle of: 
- `World`
- `GameLoop`
- `Renderer`
- `InputHandler`

This alleviates the need for global static access through a centralised control logic coordinator.

Simulation advancement is decoupled from V1's `CyborgPlatformer.legacy.canvas` by introducing `GameLoop`, which calls updates to `World` per 
update tick. `World` owns all entities and level data, managing updates and interactions without relying on UI 
components. 
Global CyborgPlatformer.legacy.entity collections are eliminated in favour of explicit ownership.

Rendering is handled exclusively by `Renderer`, which draws the current world state by read-only before applying camera 
transformations via a dedicated `Camera` class. 
User input is similarly decoupled through `InputHandler` and `InputState`, allowing `CyborgPlatformer.legacy.Player` to respond to input without
direct dependence on UI events.

Overall, V2 improves modularity, testability, and extensibility by enforcing a separation of concerns and removing
hidden dependencies present in V1.

# Version 2

## V2 Refactoring Summary

The refactor from Version 1 to Version 2 focussed on addressing some of the key aforementioned concerns. 
Namely, removing global coupling splitting the canvas driven runtime into single responsibility components.
In V1, `canvas`, supposedly a UI layer, acted as a renderer and simulation driver whilst entities' lifecycles were driven 
by global static state. This made the codebase difficult to understand and build on.

The first step was to create a package structure that would facilitate the migration into Version 2, including putting 
the legacy code in a legacy package **(commit cc3e4046)**. 
This allowed regression behaviour to remain accessible while new V2 classes could be introduced incrementally without 
interleaving concerns across the same namespace.

Next, like in the above diagram, the `World` class was built to act as the authoritative container for entity updates, 
interactions and lifecycle. In addition, collision responsibilities were extracted into classes such as `TileLevel`, 
`SolidBlock` and `TileLevelLoader`, migrating environment data away from UI driven classes **(commit 0dd1497a)**. 
This also replaced the collision logic, tightly coupled with movement through copy and test checks.

JavaFX was later integrated using a `FxLauncher` as a bootstrap responsible for starting and wiring the application with
no game logic included **(commit a75d58a5)**. 
This also helped decouple rendering and simulation because the launcher only needed to construct and wire the systems
with no logic necessary. 

To further address V1's canvas situation, `GameController` was created and repurposed into the main simulation 
coordinator, responsible for mediating between input state and World's update step **(commit c2376548)**. 
This was the home of the previously scattered game flow.

Before `GameController` became too beefy, entity lifecycle responsibilities were tightened further by giving the 
responsibility of spawning enemies to `EnemySpawn` as a part of the `Level` contract **(commit d3c06b84**).

Rendering was then extracted, previously living in `FxLauncher`, `Renderer` in the `view` package allowed the codebase 
to realise the V2 goal of rendering being completely decoupled from simulation, only to read state
but not mutate them **(commit ae7ca559)**.
`AssetManager` was also introduced to centralise and support a clean view pipeline such that render code didn't have
messy, but instead modular references to render items **(commit b9f49bc9)**. 
Finally, camera behaviour was isolated into `Camera`, abstracting camera logic away from update code or entity logic
**(commit 1be9bc70)**.

Overall, these changes convert V1’s global, UI-driven control flow into a structured, testable architecture where 
`World` owns `simulation`, `GameController` orchestrates updates, and `Renderer` + `Camera` render the current state 
with centralised assets.

## V2 Final Structure Overview

![V2 Actual Class Diagram](SoftwareDesignImages/V2%20Final%20UML.drawio.png)

The final Version 2 architecture is organised around a clear separation of responsibilities between application 
bootstrap, rendering, control flow, simulation state and input handling - all illustrated in the above diagram.

At the top level, `CyberPlatformerApp`, launched by `FxLauncher`, serves as the `JavaFX` entry point. 
This is where the main window is constructed whilst core system components are wired.
There is no gameplay logic or simulation update at this level.

Game flow is coordinated by the main orchestrator `GameController`. It advances simulation, mediating between input and
model, as well as providing controlled read only world state access for rendering.

THe simulation model is governed by `World`, where gameplay states are contained. 
`World` owns the current `TileLevel` and all the active entities. It is responsible for updating entities, resolving 
interactions and enforcing lifecycle boundaries.
`TileLevel` encapsulates level layout and collision data, decoupling environmental logic from entity behaviour.

Rendering is handled exclusively by `Renderer`, which reads world state without mutating it. 
Visual transformations are applied through `Camera` which tracks viewport position independently of simulation logic. 
All assets used by `Renderer` are provided through `AssetManager`, centralising resource ownership and access.

User input is decoupled from UI events via `InputState`, which stores a unified snapshot of current input. 
This allows entities, particularly `Player`, to respond to input through well-defined interfaces rather than 
directly depending on `JavaFX` event handling.


# Version 3
Let's add new features one day.

