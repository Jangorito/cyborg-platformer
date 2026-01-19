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


problems with the enemy right now:
- they jump too high to attack me
- they don't do enough damage, two hits I should be done
- if they run into me, they're basically stuck to me which makes me think 
  - they're too fast
  - there prolly needs to be a knockback effect on contact 




"Originally Intended":
- not rendering graphics in high fidelity
  - would be nice to have this toggable to increase enemy difficulty later
- camera starts with the player centered but looks ugly, start camera should allow the player to move before panning s.t. the level looks good and doesn't have useless white space to the left of the player

- not sure if enemies will jump towards new platform to kill
- Game isn't won upon reaching end point
- no lives count
- 0 HP doesn't kill player
- enemies not in same spots
- falling doesn't reset Player position
- shooting doesn't kill enemies

- pause button with title screen background
- options for difficulty
  - custom amount of zombies?
- death doesn't reset zombies
- capture the flag game mode


edit the ui such that:
- the first screen has just the presets including only normal and stealth then a button called advanced customisation
  (remove all the camera ui functionality from the class)
- the advanced button switches the ui menu that has:
  - HAIR
  - SKIN
  - VISOR
  - BELT
  - SUIT_BODY
  - SUIT_LED
- each of these should have a left and right arrow button with these options in the middle which will scroll through options for each
- when they're pressed the corresponding element on the player should change
- here are the options for the SUIT_BODY so that I can see trial how it looks/works:
  - SUIT_BODY:
    - dark:  #222A5C
    - mid:   #566A89
    - light: #8BABBF
  - SUIT_BODY:
    - dark:  #111827
    - mid:   #374151
    - light: #6B7280
  - SUIT_BODY:
    - dark:  #4A3B00
    - mid:   #8F7A1A
    - light: #E3C84A
  - SUIT_BODY:
    - dark:  #2A0F3D
    - mid:   #6B2FA3
    - light: #B46CFF
  - SUIT_BODY:
    - dark:  #6B7280
    - mid:   #CBD5E1
    - light: #F8FAFC
  - SUIT_BODY:
    - dark:  #3A0D0D
    - mid:   #8F2D2D
    - light: #D16C6C
The hexes shouldn't be visible to the player, I chose SUIT_BODY because it's a colour grouping so all 3 hexes should be edited at once 


- capture the flag mode!!
  - prolly includes custom 'win' condition
- edit title page to become a winner's page
- add high score functionality including: 
  - name 
  - custom skin
  - time taken
  - bullets
  - anything else...?
- those enemies that flag you before you can even drop down...
- tests...?
- vamos