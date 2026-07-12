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

- **Java 17** is required (Gradle will auto-provision if missing).
- Generated resources output to `src/generated/resources/` — treat as a build artifact, commit when up to date.
- `run/` and `run-data/` are ephemeral; do not commit their contents.

## Testing

Tests use the **Forge GameTest** framework, not JUnit directly.

- Write tests in the main source set, annotate methods with `@GameTest` (from `net.minecraft.gametest.framework`).
- Run headless: `gradlew.bat runGameTestServer`
- Run in-game: `/test` command in a running client/server.
- Example: `com.peco2282.bcreborn.transport.test.TransportGameTests`

## Module Structure

| Mod ID | Package suffix | Responsibility |
|---|---|---|
| `bcreborncore` | `core` / `common` / `api` | Shared API, registries, utilities, base classes |
| `bcreborntransport` | `transport` | Pipe system (item/fluid/energy), gates, facades |
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

**TravelingItem key fields**: `stack`, `entryDirection` (came *from*, not going *to*), `nextDirection` (null until routing), `progress` (0→1, moves at ≥1.0), `speed` (default 0.05f), `bounceCount` (reserved for future jam detection).

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

See `ROADMAP.md` for full status. Active work (as of last update):
- **Transport UI**: Diamond Pipe filter GUI, Iron Pipe output-direction GUI.
- **Builder logic**: `QuarryBlockEntity` / `FillerBlockEntity` tick logic; Landmark range system.
- **Engine polish**: Piston animation client-server sync; Stone/Iron Engine GUI.
- **Item rendering**: Dynamic `TravelingItem` rendering inside pipes.
