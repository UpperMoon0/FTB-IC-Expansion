# Compatibility

This document defines the player- and pack-facing compatibility contract for FTB IC Expansion.

## Supported runtime targets

| Minecraft | Loader | Java | Core | Generators |
| :--- | :--- | ---: | :--- | :--- |
| 1.19.2 | Forge 43.2.8 | 17 | Supported | Supported |
| 26.1.2 | NeoForge 26.1.2.76 | 25 | Supported | Supported |

Both mods require FTB Industrial Contraptions. Generators additionally requires IC Expansion: Core.

## Advanced Generator fuel compatibility

The Advanced Generator does not maintain its own hardcoded list of coal-like items.

- On 1.19.2 it asks FTBIC's Basic Generator recipe cache for the fuel duration.
- On 26.1.2 it reads FTBIC's `BASIC_GENERATOR_FUEL` recipe type directly.

As a result, an item added by a datapack or another mod becomes valid in the Advanced Generator when it is valid for FTBIC's Basic Generator fuel recipes.

Fuel duration is divided by the Advanced/Basic output ratio. At the default 30 Zap/t Advanced output and 10 Zap/t Basic output, a fuel burns for one third as many ticks while producing three times the power per tick. Changing the upstream Basic Generator output changes the ratio rather than silently changing total fuel efficiency.

## Advanced Geothermal compatibility

The Advanced Geothermal Generator uses lava and derives its consumption rate from FTBIC's configured Geothermal Generator output. Its default output is 60 Zap/t.

On the modern target it exposes NeoForge fluid transfer and accepts direct lava bucket interaction. Its default modern tank capacity is 24,000 mB and is configurable.

## Energy and batteries

Expansion generators participate in the FTBIC Zap energy ecosystem and expose FE output using the same conversion behavior used by FTBIC.

Both advanced generators support charging compatible FTBIC energy items. The modern implementation exposes the battery slot to item automation; the Advanced Generator also exposes its fuel slot.

## Optional integrations

| Integration | 1.19.2 | 26.1.2 | Purpose |
| :--- | :--- | :--- | :--- |
| JEI | Supported | Supported | Generator fuel/recipe viewing |
| Jade | Supported | Supported | Energy, fuel and fluid information |
| Patchouli | Guide extension | Dev compatibility | FTBIC legacy guide integration |
| GuideME | — | Guide extension | FTBIC modern guide integration |

Optional integrations are not required for the core generator logic.

## Datapacks and modpacks

Pack authors can safely extend FTBIC's Basic Generator fuel recipes without duplicating those fuels into an IC Expansion-specific list.

When changing generator output values, keep in mind that IC Expansion intentionally derives consumption from the related upstream FTBIC output configuration. This protects the intended energy-per-fuel relationship while allowing packs to change throughput.

## Documentation contract

Player-visible behavior changes should be reflected in all relevant surfaces in the same pull request:

- root `README.md`;
- `curseforge/CORE.md` or `curseforge/GENERATORS.md`;
- the matching versioned changelog;
- 1.19.2 Patchouli pages when legacy gameplay changes;
- 26.1.2 GuideME pages when modern gameplay changes.
