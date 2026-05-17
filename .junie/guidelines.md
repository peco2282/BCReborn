### BCReborn — Project‑specific Development Guidelines

#### Build and Configuration

- Toolchain
  - Java toolchain: Java 17. The Gradle build config enforces `java.toolchain` to 17; a locally installed JDK 17 is sufficient, but Gradle will provision one if needed.
  - Gradle: use the provided wrapper. On Windows PowerShell: `./gradlew.bat <task>`; on Git Bash/CMD: `gradlew.bat <task>`.
  - ForgeGradle 6.0.x with Minecraft 1.20.1 and Forge 47.x. Primary coordinates are defined in `gradle.properties`.

- Configuration surface (project‑specific)
  - `gradle.properties` controls all key mod coordinates and ranges:
    - `minecraft_version=1.20.1`, `forge_version=47.4.8`, mappings `mapping_channel=official`, `mapping_version=1.20.1`.
    - Mod ids and names are split into three modules by convention: `bcreborncore`, `bcrebornenergy`, `bcrebornbuilders`. Packaging follows `com/peco2282/bcreborn/<module>/...`.
    - Resource processing expands placeholders in `META-INF/mods.toml` and `pack.mcmeta` from these properties via `processResources`.
  - Access Transformer: `src/main/resources/META-INF/accesstransformer.cfg` is applied during dev and packaged into the final jar.
  - Mixed sources: Generated resources are included via `sourceSets.main.resources { srcDir 'src/generated/resources' }`. Treat `src/generated/resources` as a build artifact; re‑generate when required.

#### Testing

There are two distinct testing avenues in this project: classic unit tests (JUnit 5) and Forge GameTests.

- Forge GameTests
  - The project enables GameTest namespaces for `bcreborn` in `minecraft.runs`.
  - To write a GameTest, place classes under your main source set and annotate methods with `@GameTest` (from `net.minecraft.gametest.framework`).
  - Execute all registered GameTests headless: `./gradlew runGameTestServer`. This starts `GameTestServer`, runs tests, and exits with success/failure.
  - During normal client/server runs, use `/test` in-game to trigger GameTests for enabled namespaces.

#### Development Practices and Project‑specific Notes

- Code style
  - Follow existing package/module boundaries: `common`, `<module>`, `api`. Public API packages under `com.peco2282.bcreborn.api` should remain stable.
  - Java 17 language level; avoid preview features. Use UTF‑8 for sources (enforced by `JavaCompile` options).

- Resource/Data workflow
  - Run data generators with `./gradlew runData`. The run is configured to output for multiple namespaces: `bcreborncore,bcrebornbuilders,bcrebornenergy`.
  - Generated assets (blockstates, lang, models) are materialized under `src/generated/resources/...` and packaged via the resources sourceSet.
  - When committing generated resources, ensure they are up to date with the current code; stale assets often cause runtime missing‑model or registry issues.

- ProcessResources expansion
  - Placeholders in `META-INF/mods.toml` and `pack.mcmeta` are expanded from `gradle.properties`. If you add new placeholders, extend the `replaceProperties` map in `build.gradle` accordingly.

#### Cleanliness/Housekeeping

- Treat `run/` and `run-data/` directories as ephemeral. Avoid committing their contents.
- Generated resources can be committed if they are part of deliverables; otherwise prefer generating as part of CI.

---

#### Pipe System — Design Philosophy and Architecture

This section documents the BCReborn Pipe system design for AI-assisted development continuity.

##### Goal

Reimplement BuildCraft 1.7.10-style pipe behavior for Forge 1.20.1 using a modern but simple design.
Do **not** introduce over-abstraction, global registries, PipeNetwork graphs, async processing, or large capability caches at this stage.
Maintain a **simple runtime-object-based** approach that prioritizes stability, readability, and future extensibility.

##### Package Layout

```
com.peco2282.bcreborn.transport
├── block/
│   ├── PipeBlock.java                  — Block definition
│   └── entity/
│       └── PipeBlockEntity.java        — BlockEntity: NBT, capability, tick delegation, module ownership
├── pipe/
│   ├── PipeType.java                   — ITEM / FLUID / ENERGY
│   ├── PipeMaterial.java               — Material-specific constants (speed, etc.)
│   ├── TravelingItem.java              — In-transit item state
│   ├── behaviour/
│   │   ├── PipeBehaviour.java          — Base behaviour
│   │   ├── ItemPipeBehaviour.java      — chooseNextDirection() strategy
│   │   ├── FluidPipeBehaviour.java
│   │   ├── EnergyPipeBehaviour.java
│   │   └── impl/                       — Per-material behaviour overrides
│   ├── transport/
│   │   ├── ItemTransportModule.java    — TravelingItem lifecycle (tick, inject, drop, serialize)
│   │   ├── MovementHelper.java         — Stateless utility: final class, private constructor, static methods
│   │   ├── SpeedHelper.java            — Stateless utility: final class, private constructor, static methods
│   │   ├── RoutingHelper.java          — Next-direction resolution
│   │   └── MovementResult.java         — SUCCESS / NO_TARGET / BLOCKED
│   └── extraction/
│       ├── ExtractionModule.java
│       ├── StandardItemExtractionModule.java
│       ├── FilteredItemExtractionModule.java
│       └── FluidExtractionModule.java
```

##### Responsibility Boundaries

| Layer | Responsibility |
|---|---|
| `PipeBlockEntity` | NBT serialization, capability expose, tick delegation, module ownership, sync packet, pipe state |
| `ItemTransportModule` | `TravelingItem` lifecycle: tick, movement, bounce, inject, drop, serialization |
| `PipeBehaviour` / `ItemPipeBehaviour` | BuildCraft special behavior; `chooseNextDirection()` acts as routing strategy |
| `MovementHelper`, `SpeedHelper` | Stateless utilities only — `final class`, `private constructor`, `static` methods |
| `RoutingHelper` | Direction resolution logic, used by `ItemPipeBehaviour` |

##### TravelingItem Fields

- `ItemStack stack` — the item being transported
- `Direction entryDirection` — the direction this item **entered** the current pipe (not the travel direction); used for routing exclusion and bounce-back
- `Direction nextDirection` — resolved next hop; `null` until routing decides
- `float progress` — 0.0 to 1.0; incremented by `speed` each tick; item moves to next pipe when `>= 1.0`
- `float speed` — default `0.05f`; overridden by `PipeMaterial`
- `int bounceCount` — reserved for future jam/congestion detection; not used in routing logic yet

NBT key: `EntryDir` (legacy fallback: `LastDir` for backward compatibility).

##### Design Rules

1. **BlockState = appearance only** — connection shape, pipe type. No runtime state.
2. **All runtime state lives in BlockEntity** — traveling items, filters, energy, fluid, wire signals, routing.
3. **Transport logic lives in modules** — `ItemTransportModule`, future `FluidTransportModule`, `EnergyTransportModule`.
4. **Special behavior lives in `PipeBehaviour`** — per-material routing overrides via `chooseNextDirection()`.
5. **No over-abstraction** — avoid Clean Architecture / DDD / full registry / data-driven / async patterns.

##### Completed Improvements (do not revert)

- `MovementResult` enum (`SUCCESS` / `NO_TARGET` / `BLOCKED`) replaces raw `boolean` return from movement methods.
- `getTravelingItems()` returns `Collections.unmodifiableList(...)` — never expose the mutable backing list.
- Wire propagation uses `Set<BlockPos> visited` DFS with `level.isLoaded(pos)` guard — no infinite recursion.
- `Level.random` used instead of `new Random()`.
- `MovementHelper` and `SpeedHelper` are stateless utility classes (`final`, private constructor, static methods only).
- `bounceCount` field exists on `TravelingItem` for future jam detection — do not remove.
- `entryDirection` renamed from `lastDirection` — keep this name; it means "the direction this item came from".

##### Known Accepted Limitations (do not "fix" without discussion)

- **Same-tick multi-hop**: `PipeA.tick()` may inject into `PipeB`, which then ticks in the same game tick. Mitigated by snapshot iteration. Accepted as BuildCraft-compatible behavior.
- **Bounce loop** (`A ↔ B`): Accepted. Future `bounceCount` threshold will handle jam detection.
- **No capability cache**: Per-tick capability lookup is intentional. No premature optimization.
- **Basic network sync**: `sendBlockUpdated(...)` is sufficient at current scale.

##### What NOT to Implement (yet)

- `PipeNetwork` graph — chunk lifecycle complexity is not justified at current scale.
- `PendingInsertionQueue` — same-tick inject is acceptable.
- Codec / datapack / `DeferredRegister` for pipe types — runtime Java objects are sufficient.
- Async tick — Minecraft is fundamentally single-threaded.
- Capability caching — invalid lifecycle management overhead outweighs benefit now.

##### Next Implementation Priorities

1. **Diamond Pipe** — override `chooseNextDirection()` with filter-based routing.
2. **Iron Pipe** — forced output direction (`ironPipeOutput` field already on `PipeBlockEntity`).
3. **Gold Pipe** — speed modifier via `PipeMaterial`.
4. **Obsidian Pipe** — world item suction.
5. **`FluidTransportModule`** — mirror `ItemTransportModule` structure for fluid transport.
6. **`EnergyTransportModule`** — FE transport following same module pattern.
7. **Pipe rendering** — dynamic shape model, item-in-pipe rendering.
8. **Congestion system** — jam detection using `bounceCount`, overflow, bounce threshold.
