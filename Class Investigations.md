``### Convention (put this near the top of your notes)
- Diagram is **domain-based**, not “literal access-path based”.
- If code reaches objects via globals (e.g., `CyborgPlatformer.legacy.CyborgPlatform.game`, `CyborgPlatformer.legacy.canvas.player`), the diagram shows the **real dependency target** (CyborgPlatformer.legacy.Game, CyborgPlatformer.legacy.Player, etc.), and the **global path is documented as a V1 drawback** in notes.

## CyborgPlatformer.legacy.CyborgPlatform - - - - > CyborgPlatformer.legacy.Game
### V1:
- Global static CyborgPlatformer.legacy.Game game
- Tight coupling at startup
- No abstraction of lifecycle management
### V2:
- an entry point but:
  - less static state
  - clearer separation between launcher and runtime systems

## CyborgPlatformer.legacy.CyborgPlatform - - - - > CyborgPlatformer.legacy.canvas
### V1:
- “Acts as application bootstrap / entry point”
- “Initialises UI and starts game loop”
- “Uses global static access (CyborgPlatformer.legacy.Game, CyborgPlatformer.legacy.canvas.player)”

## CyborgPlatformer.legacy.Game < - - - - > CyborgPlatformer.legacy.canvas
### V1:
- CyborgPlatformer.legacy.Game depends on CyborgPlatformer.legacy.canvas via a method parameter:
  - game.startTimer(CyborgPlatformer.legacy.canvas); in CyborgPlatformer.legacy.CyborgPlatform.main 
  - so CyborgPlatformer.legacy.Game.startTimer(CyborgPlatformer.legacy.canvas) receives a CyborgPlatformer.legacy.canvas instance (dependency through parameter type).
- CyborgPlatformer.legacy.canvas is the “runtime host”:
  - CyborgPlatformer.legacy.canvas.paint(Graphics g) effectively runs the frame:
    - calls player.update()
    - calls enemy.doBehavior()
    - calls CyborgPlatformer.legacy.bullet.update()
    - draws everything 
- CyborgPlatformer.legacy.canvas reaches back into CyborgPlatformer.legacy.Game via global access:
  - if (!CyborgPlatformer.legacy.CyborgPlatform.game.isWon) { ... } else end(g);
  - so CyborgPlatformer.legacy.canvas directly reads CyborgPlatformer.legacy.CyborgPlatform.game.isWon 
- Resulting shape in V1:
  - CyborgPlatformer.legacy.CyborgPlatform creates both CyborgPlatformer.legacy.Game and CyborgPlatformer.legacy.canvas
  - CyborgPlatformer.legacy.Game starts the timing / loop using CyborgPlatformer.legacy.canvas
  - CyborgPlatformer.legacy.canvas uses global CyborgPlatformer.legacy.CyborgPlatform.game during rendering (win-state check, restart calls)
### V2:
- CyborgPlatformer.legacy.Game should not “drive” the loop through Swing paint
  - Instead, create a dedicated GameLoop (or TimerService) that ticks at a stable rate.
- CyborgPlatformer.legacy.canvas should become a pure rendering surface
  - paint(Graphics g) should only draw, not call update().
- Replace global access with injected state
  - Instead of CyborgPlatformer.legacy.CyborgPlatform.game.isWon, CyborgPlatformer.legacy.canvas gets:
    - a GameState object (or World/Session) passed in via constructor
  - Restart/win/exit events should be routed through a controller
    - CyborgPlatformer.legacy.canvas fires events (“restart requested”) rather than calling spawnEntities() itself.

## CyborgPlatformer.legacy.canvas —— CyborgPlatformer.legacy.Player / CyborgPlatformer.legacy.Enemy / CyborgPlatformer.legacy.bullet

### V1:
- Static global ownership
  - protected static CyborgPlatformer.legacy.Player player;
  - protected static ArrayList<CyborgPlatformer.legacy.Enemy> enemies;
  - protected static ArrayList<CyborgPlatformer.legacy.bullet> activeBullets;
- CyborgPlatformer.legacy.canvas directly controls lifecycle
  - CyborgPlatformer.legacy.Player, enemies, and bullets are:
    - updated inside CyborgPlatformer.legacy.canvas.paint(Graphics g)
    - rendered inside CyborgPlatformer.legacy.canvas.paint(Graphics g)
- CyborgPlatformer.legacy.canvas drives simulation
  - Calls:
    - player.update()
    - enemy.doBehavior()
    - CyborgPlatformer.legacy.bullet.update()
  - Simulation advances only when paint() is called
- Input tightly coupled to CyborgPlatformer.legacy.Player
  - keysPressed[] is:
    - stored statically in CyborgPlatformer.legacy.canvas
    - read directly by CyborgPlatformer.legacy.Player.update()
- Rendering logic mixed with game logic
  - Orientation logic (isFacingForwards)
  - Camera logic (cameraOffset)
  - Animation state (jump cloud, CyborgPlatformer.legacy.bullet direction)
- Resulting shape in V1
  - CyborgPlatformer.legacy.canvas acts as:
    - renderer
    - game loop
    - input handler
    - CyborgPlatformer.legacy.entity manager
    - partial controller
  - CyborgPlatformer.legacy.Player / CyborgPlatformer.legacy.Enemy / CyborgPlatformer.legacy.bullet are passive objects
    - they do not control when they update
    - they rely on CyborgPlatformer.legacy.canvas to drive them

### V2:
- Remove static global ownership
  - CyborgPlatformer.legacy.Player, enemies, bullets belong to a:
    - World
    - or EntityManager
- Separate update from render
  - GameLoop:
    - ticks at fixed timestep
    - calls update() on entities
  - Renderer (CyborgPlatformer.legacy.canvas):
    - only draws current state
- Entities become more autonomous
  - CyborgPlatformer.legacy.Player.update(InputState, deltaTime)
  - CyborgPlatformer.legacy.Enemy.update(WorldCon

## CyborgPlatformer.legacy.canvas - - - - > CyborgPlatformer.legacy.Background

### V1:
- Static utility-style relationship
  - CyborgPlatformer.legacy.Background exposes static resources and methods:
    - static Image[] background
    - static void drawBackground(Graphics g)
- CyborgPlatformer.legacy.canvas directly invokes rendering
  - CyborgPlatformer.legacy.Background.drawBackground(g) called inside CyborgPlatformer.legacy.canvas.paint(Graphics g)
- No instance ownership
  - CyborgPlatformer.legacy.canvas does not store a CyborgPlatformer.legacy.Background reference
  - No CyborgPlatformer.legacy.Background objects are instantiated
- Rendering responsibility is partially delegated
  - CyborgPlatformer.legacy.canvas controls when background is drawn
  - CyborgPlatformer.legacy.Background only knows how to draw itself
- Resulting shape in V1
  - CyborgPlatformer.legacy.Background acts as a static helper / resource holder
  - CyborgPlatformer.legacy.canvas remains the orchestrator of render order
  - Tight coupling via static method access

### V2:
- Convert CyborgPlatformer.legacy.Background into a renderable component
  - CyborgPlatformer.legacy.Background becomes:
    - an instance owned by World / Scene
    - or a RenderLayer
- Remove static global state
  - background images loaded via AssetManager
  - injected into CyborgPlatformer.legacy.Background instance
- Renderer controls draw order, not CyborgPlatformer.legacy.canvas
  - Renderer iterates over render layers:
    - CyborgPlatformer.legacy.Background layer
    - Entity layer
    - UI layer
- CyborgPlatformer.legacy.canvas becomes a pure rendering surface
  - no direct calls to CyborgPlatformer.legacy.Background
  - only delegates to Renderer.draw(scene)
- Cleaner dependency direction
  - Renderer depends on CyborgPlatformer.legacy.Background
  - CyborgPlatformer.legacy.canvas does not depend on concrete renderables

## CyborgPlatformer.legacy.canvas - - - - > CyborgPlatformer.legacy.MapBlocks

### V1:
- Static utility-style relationship
  - CyborgPlatformer.legacy.MapBlocks exposes static state and methods:
    - static ArrayList<CyborgPlatformer.legacy.MapBlocks> map
    - static Image[] mapImages
    - static int mapWidth
  - No CyborgPlatformer.legacy.MapBlocks instances are owned by CyborgPlatformer.legacy.canvas
- CyborgPlatformer.legacy.canvas directly invokes map rendering
  - CyborgPlatformer.legacy.MapBlocks.drawMap(g) called inside CyborgPlatformer.legacy.canvas.paint(Graphics g)
- Map data is globally accessible
  - CyborgPlatformer.legacy.MapBlocks.getMap() called during startup
  - Tile layout and collision data stored statically
- Collision and world logic implicitly tied to map
  - Entities rely on CyborgPlatformer.legacy.MapBlocks for collision checks
  - CyborgPlatformer.legacy.canvas assumes CyborgPlatformer.legacy.MapBlocks is globally initialised
- Resulting shape in V1
  - CyborgPlatformer.legacy.MapBlocks acts as:
    - level data store
    - tile renderer
    - collision reference
  - CyborgPlatformer.legacy.canvas orchestrates when the map is drawn
  - Tight coupling via static access and implicit initialisation order

### V2:
- Convert CyborgPlatformer.legacy.MapBlocks into a World/Level object
  - Map becomes:
    - an instance of Level or World
    - containing tile data, dimensions, and collision info
- Remove static global map state
  - Map data loaded via LevelLoader
  - Assets provided by AssetManager
- Separate responsibilities
  - Level:
    - stores tile layout and collision grid
  - Renderer:
    - draws the level tiles
  - Physics/CollisionSystem:
    - queries level collision data
- CyborgPlatformer.legacy.canvas no longer depends on CyborgPlatformer.legacy.MapBlocks
  - CyborgPlatformer.legacy.canvas only calls Renderer.draw(world)
  - no direct map access or static calls
- Cleaner dependency direction
  - GameLoop / PhysicsSystem → Level
  - Renderer → Level (read-only)

## CyborgPlatformer.legacy.MapBlocks < - - - - > Entity

### V1:
- Entities depend on CyborgPlatformer.legacy.MapBlocks for collision/world interaction
  - Entity exposes collision-related methods used against the map:
    - intersects(): boolean
    - gravity(): void
    - jump(): void
  - Collision checks are performed by comparing an Entity hitBox to map tiles
- CyborgPlatformer.legacy.MapBlocks is globally accessible and implicitly initialised
  - CyborgPlatformer.legacy.MapBlocks.getMap() loads/initialises the static map
  - Entities assume map data exists when update() runs
- Collision logic is tightly coupled to tile representation
  - CyborgPlatformer.legacy.MapBlocks stores tile layout + dimensions (e.g., mapWidth)
  - Entities compute movement, then "copy + test" against tile blocks
- No clean boundary between physics and level data
  - CyborgPlatformer.legacy.MapBlocks effectively serves as:
    - level data store
    - collision reference
  - Entity movement logic is responsible for enforcing collision constraints
- Resulting shape in V1
  - Entity movement and collision resolution are intertwined
  - Static map state creates hidden dependencies and ordering constraints

### V2:
- Introduce an explicit Collision/PhysicsSystem
  - Entities do not directly query tile structures
  - PhysicsSystem resolves:
    - movement integration (velocity, acceleration, gravity)
    - collision detection (AABB vs tile grid)
    - collision response (slide, stop, grounded)
- Level becomes a clean collision provider
  - Level exposes an interface like:
    - isSolid(x, y): boolean
    - getCollisionRects(area): List<Rect>
  - PhysicsSystem queries Level through that interface
- Entities become data + behaviour, not collision engines
  - Entity holds:
    - transform (position, velocity)
    - collider (bounds)
    - state (grounded, jumping)
  - Update methods request movement; system applies it safely
- Cleaner dependency direction
  - GameLoop → PhysicsSystem
  - PhysicsSystem → Level
  - Entities → (no direct dependency on Level/CyborgPlatformer.legacy.MapBlocks)

## CyborgPlatformer.legacy.Player - - - - > CyborgPlatformer.legacy.canvas

## CyborgPlatformer.legacy.bullet - - - - > CyborgPlatformer.legacy.Enemy : calls CyborgPlatformer.legacy.Enemy.damage()

## CyborgPlatformer.legacy.Player - - - - > CyborgPlatformer.legacy.bullet : shoot() constructs CyborgPlatformer.legacy.bullet

## CyborgPlatformer.legacy.Enemy - - - - > CyborgPlatformer.legacy.Player : calls CyborgPlatformer.legacy.Player.damage(CyborgPlatformer.legacy.Enemy)

## CyborgPlatformer.legacy.Player - - - - > CyborgPlatformer.legacy.MapBlocks : reads CyborgPlatformer.legacy.MapBlocks.mapWidth (camera clamp)
_________________________________________________________________________________________
## CyborgPlatformer.legacy.CyborgPlatform
**Responsibilities:**
- Application entry point
- Creates and wires core system components
- Launches the main window and renderer
- Does not manage gameplay logic

## GameController
**Responsibilities:**
- Owns overall game state (playing, won, restart)
- Coordinates game flow and transitions
- Connects input, world, and game loop
- Replaces global game control logic from V1

## GameLoop
**Responsibilities:**
- Advances simulation at a fixed timestep
- Calls update methods on world and entities
- Decouples simulation from rendering
- Replaces update logic embedded in paint()

## Renderer
**Responsibilities:**
- Renders world, entities, and UI
- Applies camera transformations
- Reads state without mutating it
- Replaces CyborgPlatformer.legacy.canvas as a pure rendering surface

## InputHandler
**Responsibilities:**
- Listens to keyboard and mouse events
- Translates input into a unified input state
- Decouples input from UI and entities

## InputState
**Responsibilities:**
- Stores current player input state
- Provides read-only input access to entities
- Replaces global keysPressed array

## World
**Responsibilities:**
- Owns and manages all entities
- Handles CyborgPlatformer.legacy.entity creation and removal
- Provides queries for entities and collisions
- Replaces global static CyborgPlatformer.legacy.entity collections

## World
**Responsibilities:**
- Owns and manages all entities
- Handles CyborgPlatformer.legacy.entity creation and removal
- Provides queries for entities and collisions
- Replaces global static CyborgPlatformer.legacy.entity collections

## CyborgPlatformer.legacy.Player
**Responsibilities:**
- Encapsulates player behaviour and state
- Responds to input via InputState
- Interacts with world through defined interfaces
- 
## CyborgPlatformer.legacy.Enemy
**Responsibilities:**
- Encapsulates enemy AI and behaviour
- Interacts with player via collision and damage
- Does not manage global state

## Bullet
**Responsibilities:**
- Represents projectile behaviour
- Handles movement and collision response
- Emits events instead of mutating global state

## CyborgPlatformer.legacy.MapBlocks (Level)
**Responsibilities:**
- Stores level layout and tile data
- Provides collision information
- Decoupled from CyborgPlatformer.legacy.entity lifecycle

## Camera
**Responsibilities:**
- Tracks view position within the world
- Applies viewport offsets during rendering
- Decouples camera logic from entities

``