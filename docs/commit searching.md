- cc3e404654dd74bf626f1e7ccd7b83a71fe35d68
  - Task 4: Created V2 package skeleton, moved V1 code & tests into legacy package
- 0dd1497ad89c045bb97a41588463dee9a57b033f
  - Task 4: World.java --> added bullet and enemy functionality. Created SolidBlock.java. Created TileLevel.java --> collision model for levels. Created TileLevelLoader.java --> loads collision data from text map.
- 2010c6f6ac88b0c529e54675a5d91553d23a4a29
  - Task 4: Updated PhysicsSystem.java --> extended gravity + collision functionality
- a75d58a569e2f36a51af3c9a62bcc66aa10c1e71
  - Task 4: FxLauncher.java created, app bootstraps
- c23765487e643f9479351c09df73d6706c09e694
  - Task 4: GameController.java: more improvements. + Enemy.java integration 
  - `public void think(World world, Player player, double dt) {`...
- 3b742a10a6d1b39f4321938f693e20e2819f1a4d
  - Task 4: tuned knockback functionality, changed signature for render in FxLauncher.java + Added modular debugging panel. Added flags for idle enemy
- d3c06b84d8b981d9cec8ee62eeed1f22f348a398
  - CyberPlatformerApp.java: Moved Enemy spawning to appropriate place and fixed disparities with legacy positioning
    Created EnemySpawn.java contract
    GameController.java: Removed Enemy Spawning
    TileLevel.java: Added enemy spawn support
    TileLevelLoader.java: Added enemy spawn support
    TODO list: updated
- ae7ca55943185dd28bec065360ec302ece0276e7
  - Task 4: Migrated FxLauncher.java's render method into view package
- b46539f8015305d3837d528fe5c1c28ff0eb2b52
  - Task 4: Gave AssetManager to Renderer.java
- b9f49bc9c76b58c3a1fbd29543f03587e92ef8f2
  - Task 4: Centralised asset ownership
- 8e5bbbd63e12bdf72874d72de0e3de6085e1c179
  - AssetManager.java: Updated img paths
    Renderer.java: Draw tile images using V1 legend mapping
    TileLevelLoader.java: Build raw tile grid (char[][]) from lines
    TileLevel.java: Kept raw tile characters for renderer
- 1be9bc70e8f96d227b4386d1280cb2114b537034
  - Camera.java: Emulate V1 camera
    FxLauncher.java: Added pixel maths
    Renderer.java: Reflected camera changes + some HUD debugging values
- f60865a0cb1b5f3ef9de244d4674129efb5d62e5
  - Player Sprite Rendering & Animation Parity...
    GameController.java: Included moveIntent and direction getters + computation
    Player.java: Added getters
    PlayerRenderState.java: Created view-only record class that holds player states
    PlayerSpriteAnimator.java: Created view-only animation resolver for image -> current frame resolution
    PlayerRenderStateFactory.java: Created to capture snapshot using player and controller
    Renderer.java: Deployed new classes, added method to replace rects with real sprite + resolved sprite sizing issue
- 7c2febeb34a9dc907c2e72e8c9f39e1cabed1cb3
  - GameController.java: Added more comprehensive state flags & getters
    Renderer.java: Created a DrawHUD function
- 
