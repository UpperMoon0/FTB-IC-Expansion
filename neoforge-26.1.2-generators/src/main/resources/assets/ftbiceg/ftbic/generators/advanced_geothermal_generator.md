---
navigation:
  title: Advanced Geothermal Generator
  icon: ftbiceg:advanced_geothermal_generator
  parent: ftbic:generators/index.md
  position: 21
item_ids:
  - ftbiceg:advanced_geothermal_generator
---

# <Color id="gold">Advanced Geothermal Generator</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="ftbiceg:advanced_geothermal_generator" scale="2" />

  An MV throughput upgrade to the <ItemLink id="ftbic:geothermal_generator" />. Its default output is **60 zap/t**. Lava consumption scales against FTBIC's configured Geothermal Generator output, preserving the intended energy-per-mB ratio when packs tune upstream balance.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>

<RecipeFor id="ftbiceg:advanced_geothermal_generator" />

<ItemImage id="minecraft:air" scale="0.25"/>
***

## Compatibility

* Accepts lava through NeoForge's fluid capability as well as direct bucket interaction.
* Outputs through FTBIC Zap cables and exposes FE output through the same conversion rules.
* JEI registers this block as a crafting station for FTBIC's Geothermal fuel category.
* Jade shows stored energy and lava tank fill level.
