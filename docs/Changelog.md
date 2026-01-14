# Changelog

### 20/10/2025
 - Version 1 nearly ready. Code seems to be broken. 

## Task 1 – Maintenance baseline

### 2025-12-25
- Set up project in IntelliJ (imported Maven, configured SDK)
- Created `dev` branch and `task1-v1` working branch
- Fixed compilation/runtime issues to restore a runnable baseline:
    - `CyborgPlatformer.legacy.CyborgPlatform`/`CyborgPlatformer.legacy.Game`: corrected outdated method call `loadImgs()` → `loadImages()`
    - Fixed asset reference typo `clod.png` → `cloud.png`
    - `CyborgPlatformer.legacy.MapBlocks`: corrected map file name `Map.txt` → `Maps.txt`
- Verified the game runs successfully & baseline is restored

## Task 2 – Understanding the legacy code & V2 planning

### 2025-12-[26->29]
- added Javadocs and inline comments to the project
- designed a Class Diagram based on V1's current class structure

### 2025-12-30
- finalised SoftwareDesign.md
- Added some V1 regression tests for CyborgPlatformer.legacy.entity.java's core mechanics & reconfigured pom.xml file

### 2026-01-13
- Finalised regression tests including: `CyborgPlatformer.legacy.PlayerTest.java`, `CyborgPlatformer.legacy.BulletTest.java`, `CyborgPlatformer.legacy.BulletTest.java`, `CyborgPlatformer.legacy.EntityTest.java`. 
- Populated `Testing.md` with this information
- Created v2 branch, ran tests: 100% pass rate & 29% coverage