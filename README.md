# BCReborn

English | [日本語](README.ja.md)

An unofficial mod that reimplements the pipes, engines and Builders features of BuildCraft 1.7.10 for Minecraft 1.20.1 / Forge 47.x.

---

## Overview

BCReborn aims to bring back the following features of classic BuildCraft on a modern Forge environment.

- **Pipe system** — transport of items, fluids and energy
- **Engine system** — MJ/FE generation with wood, stone and iron engines
- **Builders module** — automated building and mining with the Quarry, Filler, Architect Table and more
- **Silicon module** — automation extensions such as the Assembly Table and gates

---

## Requirements

| Item | Version |
|------|---------|
| Minecraft | 1.20.1 |
| Forge | 47.4.8 |
| Java | 17 |
| License | Minecraft Mod Public License (MMPL) 1.0.1 |

---

## Modules

| Mod ID | Name | Contents |
|--------|------|----------|
| `bcreborncore` | BCReborn-Core | Shared API, registries and utilities |
| `bcrebornenergy` | BCReborn-Energy | Engines and energy management |
| `bcrebornbuilders` | BCReborn-Builders | Quarry, Filler, Architect Table |
| `bcreborntransport` | BCReborn-Transport | Pipe transport system |

---

## Implemented Features

### Pipe system

- **Item pipes** — behaviours for all 16 kinds, including Wood / Stone / Cobblestone / Iron / Golden / Diamond / Emerald / Obsidian / Void
- **Fluid pipes** — behaviours for the main kinds, including Wood / Stone / Iron / Diamond
- **Energy pipes** — behaviours for the main kinds, including Wood / Stone / Iron / Golden / Diamond
- **Gates** — triggers (pipe contents, wire signals, redstone, ...) and actions (redstone output, machine Off/On/Loop, iron pipe output direction, valve, lapis colour, power limiter, ...)
- **Pipe wires** — four colours, carrying signals between gates
- `TravelingItem` lifecycle managed by `ItemTransportModule`
- Transport logic in `RoutingHelper` / `MovementHelper` / `SpeedHelper`

### Engine system

- **Wood Engine** — basic operation
- **Stone Engine** — fuel, PI control, overheating, cooling, explosion
- **Iron Engine** — liquid fuels and coolants
- **Creative Engine** — unlimited energy (for development)

### Builders module

- **Landmark** — marks the area for the Quarry, Filler and Architect Table
- **Quarry** — automated mining
- **Filler** — automated building with various patterns
- **Architect Table** — scans structures
- **Builder** — automated building from schematics

---

## Building

### Prerequisites

- JDK 17
- An internet connection (Gradle downloads the dependencies)

### Build

```powershell
# Windows PowerShell
./gradlew.bat build
```

The output is written to `build/libs/`.

### Development setup

```powershell
# Generate IntelliJ IDEA run configurations
./gradlew.bat genIntellijRuns
```

### Data generation

```powershell
./gradlew.bat runData
```

Generated resources are written to `src/generated/resources/`.

### Running GameTests

```powershell
./gradlew.bat runGameTestServer
```

---

## Package Layout

```
com.peco2282.bcreborn
├── api/            — public API (stable interfaces)
├── common/         — shared registries and utilities
├── energy/         — engine and energy module
├── builder/        — Builders module
└── transport/      — pipe transport module
    ├── block/      — PipeBlock / PipeBlockEntity
    └── pipe/       — behaviours, transport, extraction
```

---

## Development Status

See [ROADMAP.md](ROADMAP.md) (in Japanese) for detailed progress and plans.

### 🚀 Current focus
- **Client verification**: check the implemented GUIs and rendering in a real client (the checklist is in ROADMAP.md)
- **Emzuli / Daizuli pipes**: rework them to match the original
- **Builders leftovers**: Filler settings GUI, Quarry chunk loading

---

## License

BC Reborn is a modernization and continuation of concepts and code
derived from BuildCraft.

Copyright (c) 2011-2017, SpaceToad and the BuildCraft Team
Copyright (c) 2025-2026 peco2282

Show details [LICENSE](LICENSE)
