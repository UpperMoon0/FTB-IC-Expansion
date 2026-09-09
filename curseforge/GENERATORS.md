# Industrial Contraptions Expansion: Generators

**Industrial Contraptions Expansion: Generators** is an addon for [FTB Industrial Contraptions](https://www.curseforge.com/minecraft/mc-mods/ftb-industrial-contraptions-forge) that adds higher-throughput versions of its fuel and geothermal generators while keeping upstream fuel, energy-network, and pack-balancing behavior.

![Mod contents](https://i.imgur.com/Fxi336g.png)

## Supported versions

- **Minecraft 1.19.2** — Forge
- **Minecraft 26.1.2** — NeoForge

Install the file that matches your exact Minecraft version and loader.

## Required dependencies

- **FTB Industrial Contraptions**
- **Industrial Contraptions Expansion: Core**

## Features

### Advanced Generator

- Generates **30 Zap/t by default** at LV.
- Uses the **same Basic Generator fuel recipes as FTB Industrial Contraptions** — it is not limited to coal.
- Automatically supports datapack/mod-added fuels registered for FTBIC's Basic Generator.
- Fuel duration scales against FTBIC's configured Basic Generator output. With default settings, it produces three times the power per tick and consumes fuel three times faster, keeping approximately the same total energy per fuel item.
- Charges compatible FTBIC energy items through its battery slot.
- Supports item automation for fuel and battery handling.

### Advanced Geothermal Generator

- Generates **60 Zap/t by default** at MV.
- Uses lava and scales lava consumption against FTBIC's configured Geothermal Generator output, preserving the intended energy-per-mB balance when packs tune upstream values.
- Supports direct lava-bucket input and fluid automation on the modern NeoForge target.
- Uses a **24,000 mB default tank** on the modern target; modern tank size is configurable.
- Charges compatible FTBIC energy items through its battery slot.

Both generators output through FTBIC's **Zap** network and follow FTBIC's FE conversion rules.

## Integrations

- **JEI** — recipe and generator/fuel category integration.
- **Jade** — stored energy, remaining fuel time, and lava tank information.
- **Patchouli (1.19.2)** — extends FTB Industrial Contraptions' in-game guide with entries for both advanced generators.
- **GuideME (26.1.2)** — extends FTB Industrial Contraptions' current guide with modern entries, recipes, compatibility notes, and automation behavior.

These integrations are optional unless your modpack requires them independently.

## Pack and datapack compatibility

The Advanced Generator deliberately reads FTBIC's own Basic Generator fuel recipe type instead of maintaining a hardcoded fuel whitelist. If your pack adds a valid FTBIC Basic Generator fuel, the Advanced Generator can use it automatically.

Generator throughput values are configurable, and consumption scaling follows the corresponding upstream FTBIC generator configuration so changing output does not silently multiply total fuel efficiency.

## Maintained again

The old CurseForge description said this project was no longer maintained because FTB Industrial Contraptions had stopped updating. That is no longer true: FTBIC is active again and IC Expansion now supports both the legacy 1.19.2 line and NeoForge 26.1.2.

## Modpack use

You may freely include IC Expansion mods in modpacks.

## Credits

Thanks to the **FTB Team** for creating **FTB Industrial Contraptions**. Their work inspired this addon and the original IC Expansion project.

Support the project on [Patreon](https://patreon.com/FTBICExpansionMods).
