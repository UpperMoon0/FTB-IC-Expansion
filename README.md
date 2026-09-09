# FTB IC Expansion

![Minecraft](https://img.shields.io/badge/Minecraft-1.19.2%20%7C%2026.1.2-brightgreen)
![Loaders](https://img.shields.io/badge/loaders-Forge%20%7C%20NeoForge-orange)
![License](https://img.shields.io/badge/License-All%20Rights%20Reserved-lightgrey)

**FTB IC Expansion** is a maintained addon suite for [FTB Industrial Contraptions](https://www.curseforge.com/minecraft/mc-mods/ftb-industrial-contraptions-forge), extending its machine ecosystem while preserving its energy network, fuel recipes, battery behavior, and pack configurability.

This repository contains two separately released mods:

- **Industrial Contraptions Expansion: Core** (`ftbicec`) — shared infrastructure used by the expansion modules.
- **Industrial Contraptions Expansion: Generators** (`ftbiceg`) — upgraded fuel and geothermal generators built on top of Core and FTB Industrial Contraptions.

## Supported targets

| Minecraft | Loader | Java | FTB Industrial Contraptions | Status |
| :--- | :--- | ---: | :--- | :--- |
| 1.19.2 | Forge 43.2.8 | 17 | Legacy 1.19.x line | Supported legacy implementation |
| 26.1.2 | NeoForge 26.1.2.99 | 25 | 26.1.2.10 | Supported modern implementation |

Install the file matching your exact Minecraft version and loader. Files from one target are not cross-version or cross-loader artifacts.

## Modules and dependencies

### IC Expansion: Core

Core is the common support layer for the suite. It provides the registry and machine compatibility infrastructure used by expansion modules.

On NeoForge 26.1.2, the **Large Blast Furnace is intentionally a scaffold**. Its current implementation validates the multiblock structure and reports whether it is formed, but it does not yet process recipes, consume energy, track production progress, or expose a production inventory. Those gameplay systems are deliberately deferred rather than represented as finished functionality.

**Required:**

- FTB Industrial Contraptions — both supported targets.
- NsTut Lib 0.8.1+ within the 0.8.x compatibility line — NeoForge 26.1.2 only.

Core does **not** require Patchouli.

### IC Expansion: Generators

Generators adds the Advanced Generator and Advanced Geothermal Generator.

**Required:**

- FTB Industrial Contraptions
- IC Expansion: Core

JEI, Jade, Patchouli, and GuideME are integrations rather than hard requirements unless another installed mod or pack requires them independently.

## Generator features

### Advanced Generator

- **30 Zap/t by default** and operates at LV.
- Accepts the **same datapack-driven Basic Generator fuel recipes as FTB Industrial Contraptions**. It is not hardcoded to coal.
- Fuel duration scales from the configured Basic Generator output. At default balance it produces three times the Basic Generator throughput and burns fuel three times faster, preserving approximately the same total energy per fuel item.
- Pack-added or datapack-added FTBIC Basic Generator fuels work automatically.
- Charges FTBIC-compatible energy items through its battery slot.
- Exposes its fuel and battery inventory for automation.
- Outputs into the FTBIC Zap network and follows FTBIC's FE conversion rules.

### Advanced Geothermal Generator

- **60 Zap/t by default** and operates at MV.
- Uses lava, with consumption scaled against FTBIC's configured Geothermal Generator output so throughput changes do not silently create extra energy per mB.
- Has a **24,000 mB default tank** on the modern target; the modern tank size is configurable.
- Accepts direct lava-bucket interaction and exposes fluid automation on the modern NeoForge target.
- Charges FTBIC-compatible energy items through its battery slot.
- Outputs into the FTBIC Zap network and follows FTBIC's FE conversion rules.

Default numbers describe the shipped configuration. Modpacks may tune generator output and modern storage values through config.

## Optional integrations

| Integration | Forge 1.19.2 | NeoForge 26.1.2 |
| :--- | :--- | :--- |
| JEI | Generator/fuel recipe integration | Generator/fuel recipe integration |
| Jade | Energy, fuel and lava information | Energy, fuel and lava information |
| Patchouli | Extends FTBIC's in-game guide | Compatibility-tested optional dependency |
| GuideME | — | Extends FTBIC's current in-game guide |

The 1.19.2 Patchouli pages and 26.1.2 GuideME pages document the same gameplay rules using the guide system native to that FTBIC generation.

## Modpack compatibility

Generators deliberately follows upstream FTBIC behavior instead of maintaining a separate hardcoded fuel list:

- Advanced Generator fuel acceptance comes from FTBIC's Basic Generator fuel recipe type.
- Fuel duration is derived from FTBIC's configured Basic Generator output.
- Geothermal lava consumption is derived from FTBIC's configured Geothermal Generator output.
- Energy output uses FTBIC Zap transport behavior and FE conversion rules.

This means datapacks and packs that extend or rebalance FTBIC remain compatible without needing a second IC Expansion-specific fuel list.

See [Compatibility](docs/COMPATIBILITY.md) for the version-by-version integration contract.

## In-game documentation

- **1.19.2:** Generators extends FTB Industrial Contraptions' Patchouli guide with Advanced Generator and Advanced Geothermal Generator entries.
- **26.1.2:** Generators extends FTB Industrial Contraptions' GuideME guide with the corresponding modern pages.

Recipes remain visible through normal recipe viewers, with dedicated JEI integration where available.

## Repository layout

- `common/` — version-neutral rules and tests.
- `FTB-IC-Expansion-Core/` — Forge 1.19.2 Core implementation.
- `FTB-IC-Expansion-Generators/` — Forge 1.19.2 Generators implementation.
- `legacy-1.19.2/` — isolated Gradle 7.5.1 workspace for the legacy Forge projects.
- `neoforge-26.1.2-core/` — NeoForge 26.1.2 Core adapter.
- `neoforge-26.1.2-generators/` — NeoForge 26.1.2 Generators implementation and integrations.
- `docs/` — development, compatibility, and porting documentation.
- `curseforge/` — source-controlled project descriptions for the two CurseForge projects.
- `changelogs/` — per-mod, per-release notes used by publishing automation.

For architecture and migration rationale, see [Porting](docs/PORTING.md). For local builds, see [Development](docs/DEVELOPMENT.md).

## Building and testing

A full local verification can be started from the root workspace with:

```bash
./gradlew testAllVersions
```

The CI matrix validates shared code, Forge 1.19.2, and NeoForge 26.1.2 independently. The modern lane runs the explicit `verifyModern2612` gate, including an ephemeral Minecraft server bootstrap that asserts FTB Industrial Contraptions, Core, Generators, and NsTut Lib are loaded together.

Version numbers are owned by the root `gradle.properties`. Release notes for each changed module must exist at `changelogs/core/v<version>.md` or `changelogs/generators/v<version>.md`; release automation uses those files for CurseForge and GitHub release notes.

## Modpack use

You may freely include IC Expansion mods in modpacks.

## Credits

Thanks to the **FTB Team** for creating **FTB Industrial Contraptions** and the ecosystem this addon extends.

If you want to support the project, you can do so on [Patreon](https://patreon.com/FTBICExpansionMods).
