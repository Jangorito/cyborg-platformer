# Version 1
Current (Legacy) Version


The system is a 2D platform shooter written in Java Swing. Its rules and objectives are defined in the project README file.
This section will illustrate how it currently functions and highlight the limitations that inform the proposed changes for Version 2.


### High-level overview
As shown in the Version 1 class diagram, there are a few classes that for the backbone of the system's architecture:
- `CyborgPlatform`
- `Game`
- `canvas`
  After some light, functional refactoring the game is able to launch and the first level can be completed without execution errors, however it's structural integrity is suboptimal due to an overuse of global states and tightly coupled responsibilities.


### `CyborgPlatform.main`
`CyborgPlatform.main` is the application's entry point whose classifier contains a `global static` `Game` variable - `game`.
`CyborgPlatform` is a launcher and does not manage gameplay logic. It creates a `JFrame` object, the main Swing window and initialises it with parameters governing size and exit functionality.


The `Game` and `canvas` objects are created, after which `canvas` is attached to the `JFrame` object.
The shared `Game` resource is then used to load the images, spawn the entities represented by those images and start the render loop.
Map data is also initialised through a call to the global `MapBlocks` class via `MapBlocks.getMap()`.


`CyborgPlatform` uses global access to other objects but, as conveyed in the diagram, this introduces dependencies between unrelated parts of the system.
Its functionality should be to serve solely as the entry point to the application facilitated by the creation of other classes that will take on some of its current superfluous responsibilities.


### `canvas`
`canvas` renders assets using the `paint(Graphics g)` method which implements a simple camera using `cameraOffset`, this is the only way simulation is advanced.
The assets consist of the world (`Background` and `MapBlocks`), the controllable `player` and `enemies` stored in an `ArrayList` of `Enemy`'s.
`canvas` also handles the advancement of the game by calling update methods on the global static states: the `entity` objects. Furthermore, inputs also are handled within this class through the `KeyPressed` method which takes `KeyEvent`s as the parameter.
These `KeyEvent`s are translated into `keysPressed[]` states that map keyboard input to game functionality (such as movement and game navigation) as well as updating relevant states.


As highlighted by the red dependencies arrows in the diagram, multiple gameplay entities depend directly on `canvas`, despite it being a UI component.
It acts a God Object in V1 with too many distinct responsibilities; update and render logic are tightly coupled, which violates the Single Responsibility Principle. `canvas` should be separated into single responsibility classes that will be elaborated in V2.


### Interactions between globally coupled entities
Gameplay behaviour is dictated though interactions between the `Player`, `Enemy` and `Bullet` entities. This is conceptually sound as it allows the player to fire bullets which damage enemies and allows the enemy to damage the player upon collision.
However, the current V1 implementation presents critical coupling issues.


In the absence of a dedicated game state controller, the global static states stored in the UI component `canvas` govern
the aforementioned interactions.
An example of this is the `Bullet` workflow, the `Player` class creates a new `Bullet` instances and globally accesses
`canvas` to add this bullet to the global `activeBullets` list via the `canvas.activeBullets.add()` method.
Meanwhile, `Bullet`'s `update()` loop queries the instance's collision status before removing it from the global list
within `Bullet` via global call to the `canvas.activeBullets.()` method.
Similarly, instances of `Enemy` globally access `canvas.player` to apply damage to the player to manually mutate the
global `Entity` collections.


These coupling issues are highlighted by the red dependencies in the V1 diagram as gameplay entities rely on `canvas`,
a UI component for instance lifecycle management and critical game logic.
This tightly binds entity behaviour to a specific runtime context and makes the game flow difficult to interpret,
The structure also doesn't behove testing or extending the software - which would prove more practical with the
introduction of clear ownership boundaries.




### Game world & collision handling
The `MapBlocks` class stores the world's layout and aesthetics, it is responsible for fetching the collision data queried
by the entities and rendering the level.
`CyborgPlatform.main` initialises it at start up and then globally accessed from various places during runtime.


The `Entity` class and subclasses govern collision detection and movement logic by predicting collision checks via a
temporary instance copy that is queried against its potential new position with regards to intersection.
This workflow works conceptually but tightly coupling map and collision implementation with movement logic also defies
convention.


As seen in the V1 diagram, entities access `MapBlocks` to check for collision information and `Player` also accesses
`MapBlocks` for camera movement. This combines the responsibility of the world data with entity specific logic, which
makes increasing the amount of levels very rigid and difficult.
This motivates the introduction of a more modular class structure in which the different aspects of the game overall can
be built upon independently of one another.


Overall, while V1's implementation functions well enough to play through a level to completion, its architecture relies
too heavily on global state and tightly coupled components with mixed responsibilities. The diagram highlights `canvas`
as the class with the largest amount of varying responsibilities, violating core design principles like Single
Responsibility and separation of concerns. This limits extensibility, the success of testing and safe refactoring. V2's
design addresses these issues by introducing conventional ownership boundaries between rendering, game logic, input and
world state.


# Version 2
We need to move to JavaFX at some point

# Version 3
Let's add new features one day.

