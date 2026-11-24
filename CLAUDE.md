# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

BCReborn is a Minecraft Forge 1.20.1 mod that reimplements BuildCraft 1.7.10 features (pipes, engines, builders, automation). It ships as a single JAR containing seven logical modules, each with its own mod ID.

## Build Commands

Use the Gradle wrapper (`gradlew.bat` on Windows, `./gradlew` on Linux/Mac):

```bash
gradlew.bat build                  # Full build → build/libs/bcreborn.jar
gradlew.bat genIntellijRuns        # Generate IntelliJ run configs (one-time setup)
gradlew.bat runClient              # Launch Minecraft client for manual testing
gradlew.bat runServer              # Launch dedicated server
gradlew.bat runGameTestServer      # Run all GameTests headless (CI equivalent)
gradlew.bat runData                # Re-generate blockstates/models/lang/recipes
```

- **Java 17** is required (Gradle will auto-provision if missing). Gradle 8.8 itself must also run on Java 17 or 21: on Java 25 it cannot compile the build script (`Unsupported class file major version 69`), so set `JAVA_HOME` accordingly.
- Mixins: `bcreborn.mixins.json` (package `com.peco2282.bcreborn.mixin`), wired by MixinGradle (`mixin { }` in `build.gradle`, which writes `bcreborn.refmap.json` and the `MixinConfigs` manifest entry). Only `MultiNoiseBiomeSourceMixin` exists: it paints the oil field biomes, since Forge 1.20.1 has no API to add overworld biomes.
- Generated resources output to `src/generated/resources/` — treat as a build artifact, commit when up to date.
- `run/` and `run-data/` are ephemeral; do not commit their contents.

## Testing

Tests use the **Forge GameTest** framework, not JUnit directly.

- Write tests in the main source set, annotate methods with `@GameTest` (from `net.minecraft.gametest.framework`).
- Run headless: `gradlew.bat runGameTestServer`
- Run in-game: `/test` command in a running client/server.
- Example: `com.peco2282.bcreborn.transport.test.TransportGameTests`
- Only the `bcreborntransport` GameTest namespace is enabled, so every test class uses `@GameTestHolder(BCRebornTransport.MODID)` and lives under `transport/test/`, whatever module it covers.
- Templates live in `src/main/resources/data/bcreborntransport/gameteststructures/` (`empty_3x3`, `quarry_13x8x13`); `build.gradle` stages them into `run/` before `runGameTestServer`.
- The GameTest world in `run/` is reused between runs, so blocks from an earlier (aborted) run can still be there. Tests that search an area (e.g. landmarks, 64 blocks) must not depend on the surroundings being empty.
- The IntelliJ `runGameTestServer` configuration needs "Build and run using: IntelliJ IDEA" and a Java 17 SDK; with Gradle delegation the module path is dropped and the launch fails.

## Module Structure

| Mod ID | Package suffix | Responsibility |
|---|---|---|
| `bcreborncore` | `core` / `common` / `api` | Shared API, registries, utilities, base classes |
| `bcreborntransport` | `transport` | Pipe system (item/fluid/energy/structure), gates, facades, plugs, lenses |
| `bcrebornenergy` | `energy` | Engines (Wood/Stone/Iron/Creative), fuel, FE power |
| `bcrebornbuilders` | `builders` | Quarry, Filler, Architect Table, schematics |
| `bcrebornfactory` | `factory` | Mining Well and future advanced machinery |
| `bcrebornsilicon` | `silicon` | Assembly Table, Integration Table, chips |
| `bcrebornrobotics` | `robotics` | Robot entities, AI boards, stations |

Each module has its own entry point class (`BCReborn<Module>.java`), registries, event handlers, and data generators. All module coordinates are defined in `gradle.properties`.

## Architecture

### Layer Model

```
api/          — Stable public interfaces; keep backward-compatible
common/       — BCRegistry, ContextProcessor, shared base classes, packets, utils
<module>/     — Module-specific implementation
  block/      — Block classes (appearance only, no runtime state)
  block/entity/ — BlockEntity classes (all runtime state lives here)
  pipe/ or equivalent — Logic modules, behaviours, helpers
  data/       — Data generators
  event/      — Forge event handlers
  test/       — GameTests
```

### Registry System

`BCRegistry` (in `common/`) is the central registration hub. Each module calls `BCRegistry.getRegistry(modid)` to get its own `DeferredRegister`-backed registry for blocks, items, fluids, block entities, menus, entities, etc. Annotation-based initialization via `ContextProcessor` auto-wires classes marked with `@InitRegister` and similar annotations.

### Pipe System (Transport Module)

The pipe system follows strict responsibility boundaries — **do not blur these**:

| Class | Owns |
|---|---|
| `PipeBlock` | Block definition; **appearance only** — connection shape, pipe type in BlockState |
| `PipeBlockEntity` | NBT serialization, capability exposure, tick delegation, module ownership, sync |
| `ItemTransportModule` | `TravelingItem` lifecycle: tick, movement, bounce, inject, drop, serialization |
| `PipeBehaviour` / `ItemPipeBehaviour` | Per-material routing strategy via `chooseNextDirection()` |
| `MovementHelper`, `SpeedHelper` | Stateless utilities — `final class`, private constructor, static methods only |
| `RoutingHelper` | Direction resolution logic |

**TravelingItem key fields**: `stack`, `entryDirection` (came *from*, not going *to*), `nextDirection` (null until routing), `progress` (0→1, moves at ≥1.0), `speed` (default 0.05f), `bounceCount` (saved but unused: the original has no jam counter). Jams follow the original `PipeTransportItems`: only sides that can take the item are destinations; with none the item is dropped at the centre, except on pipes whose `ItemPipeBehaviour.allowsBouncing()` is true (Iron, Daizuli, Clay); overfull pipes group items and then burst.

**Pluggables** (`PipePluggable`: facade, gate, robot station, plug, lens / filter): a blocking pluggable closes its side for both pipes (`PipeBlock.canConnectTo`), and `PipeBlockEntity.setPipePluggable` recomputes the connections. Lens filters are applied in `RoutingHelper` (and to a behaviour's own choice in `ItemTransportModule.chooseDestination`) through `LensFilterHandler`.

**Design rules that must not be violated:**
- BlockState = appearance only. No runtime state in BlockState.
- All runtime state lives in BlockEntity.
- No `PipeNetwork` graph, no capability cache, no async tick, no `DeferredRegister` for pipe types — these are explicitly deferred decisions.
- `getTravelingItems()` must return `Collections.unmodifiableList(...)`.
- Wire propagation uses `Set<BlockPos> visited` DFS with `level.isLoaded(pos)` guard.
- Use `Level.random`, not `new Random()`.

### Resource / Data Workflow

Placeholders in `META-INF/mods.toml` and `pack.mcmeta` are expanded from `gradle.properties` via `processResources`. When adding new placeholders, extend the `replaceProperties` map in `build.gradle`.

After adding blocks, items, or recipes, run `gradlew.bat runData` to regenerate assets, then commit the output under `src/generated/resources/`.

### Code Style

- Java 17; no preview features. UTF-8 source encoding (enforced by compiler options).
- All source files require the MMPL 1.0 license header (`HEADER.txt`). Spotless enforces this.
- Package root: `com.peco2282.bcreborn.<module>`.
- Public API under `api/` should remain stable.

## Current Focus Areas

See `ROADMAP.md` for full status (including the client-side verification checklist). Active work (as of last update):
- **Client verification**: GUIs and rendering (Diamond / Emzuli Pipe GUIs, wires, gates, Landmarks, Assembly Table, engines) are implemented and GameTest-verified server-side, but not yet checked in a real client.
- **Unverified client visuals**: plug / lens renderers and lens item tint, the rewritten `FluidPipeRenderer`, `ColoredPipeModel` (Lapis / Daizuli texture by colour, via `PipeBlockEntity.PIPE_COLOR` model data), `StainedPipeModel` (the stained glass overlay of every pipe, via `PipeBlockEntity.GLASS_COLOR`; the glass colour is not the Lapis colour), the painted-item box, phased facade switching, the Integration Table preview slot, the Zone Planner / Electronic Library / Requester / Filtered Buffer screens, the Gate Copier icon and the Power Adapter renderer are GameTest-verified on the server side only. The Electronic Library's list lives on the client (`BCRebornBuilders.getClientDB()`, the game folder's `blueprints`); `BlueprintLibraryClient` is its client half.
- **Robot / power regression tests**: ROADMAP §8-2 lists what each robot and power test covers and what is left. Robot coverage is split between `RobotGameTests` (station, charging, carriers), `RobotBoardGameTests` (one test per board, delivery reservations and reloads) and `RobotEdgeCaseGameTests` (station sides, capacity, broken stations, power limits, boards with nothing to do). A robot only takes a stack into a slot if all of it fits there (original `isItemValidForSlot`), and a shut-down robot does not restart where it is.
- **Robot boards**: every board is run-verified in `RobotBoardGameTests` (the Bomber only up to priming the TNT). Robot tests must give the robot a work area (a robot without one searches 64 blocks around and works on other tests' structures). The test structures are dark and closed to the sky (crops need a light source), and a mob that is hit must be penned in or it is knocked out of the work area. Do not `switch` on a client-only enum in a class that also runs on the server: it breaks every enum switch of that class on a dedicated server.
