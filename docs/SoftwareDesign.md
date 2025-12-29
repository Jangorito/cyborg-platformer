# Version 1
The system is a 2D platform shooter written in Java Swing. Its rules and objectives are defined in the project README file.
This section will illustrate how it currently functions and highlight the limitations that inform the proposed changes for Version 2.


### High-level overview
As shown in the Version 1 class diagram, there are classes form the backbone of the system's architecture:
The game's level1 can be completed without errors, however it's structural integrity is suboptimal due to an overuse of
global state and tightly coupled responsibilities.


### `CyborgPlatform.main`
`CyborgPlatform.main` is the entry point whose classifier contains a `global static` `Game` variable - `game`.
`CyborgPlatform` is a launcher and doesn't manage gameplay logic. It creates a `JFrame` object with some default
parameters.


The `Game` and `canvas` objects are created, after which `canvas` is attached to the `JFrame` object.
The shared `Game` resource is then used to load images, spawn  entities and start the render loop.


`CyborgPlatform` uses global access for other objects but, as conveyed in the diagram, this introduces dependencies
between unrelated parts of the system. It should only function as the entry point.


### `canvas`
`canvas` renders assets using the `paint(Graphics g)` method for `cameraOffset`. The assets are the world and
controllable `entities`.
The only method of simulation advancement is by calling update methods on the global static states.
Inputs also are handled here through the `KeyPressed` method where `KeyEvent`s are translated into `keysPressed[]`
mapping keyboard input to game functionality whilst updating relevant states.


As highlighted by the red dependencies arrows in the V1 diagram, multiple gameplay entities depend directly on `canvas`,
despite it being a UI component.
It acts as a God Object with too many distinct responsibilities; update and render logic are tightly coupled,
which violates the Single Responsibility Principle.
`canvas` should be separated into single responsibility classes.


### Interactions between globally coupled entities
Gameplay behaviour is dictated though interactions between the entities and while conceptually sound it presents
critical coupling issues.


Without a dedicated game state controller, the global static states stored in `canvas` govern these interactions.
E.G, in the `Bullet` workflow, the `Player` class creates a `Bullet` instances and globally calls
`canvas.activeBullets.add()`.
Meanwhile, `Bullet`'s `update()` loop checks for a collision before a global call to `canvas.activeBullets.()`.


These coupling issues are highlighted by the V1 diagram; gameplay entities rely on `canvas` for instance lifecycle
management and game logic.
This tightly binds entity behaviour to a specific runtime context and makes the game flow difficult to interpret.
It also doesn't behove testing or extending the software.




### Game world & collision handling
The `MapBlocks` class stores the world and is responsible for fetching collision and rendering the level.
`CyborgPlatform.main` initialises it at start up, but it's then globally accessed from various places during runtime.


The `Entity` (sub)classes govern collision detection and movement logic by checking an instance copy against a new
candidate positions' collision status.
This works conceptually but tightly coupling map and collision implementation with movement logic also violates
convention.


Entities access `MapBlocks` for collision information and `Player` accesses `MapBlocks` for camera movement.
This combines world data with entity specific logic, making adding levels difficult.
This motivates the introduction of a modular class structure where different aspects of the game can
be built upon independently.


The diagram highlights `canvas` as the class with the largest amount of varying responsibilities, violating core design principles like Single
Responsibility and separation of concerns. This limits extensibility, the success of testing and safe refactoring. V2's
design addresses these issues by introducing conventional ownership boundaries between rendering, game logic, input and
world state.


# Version 2
We need to move to JavaFX at some point

# Version 3
Let's add new features one day.

