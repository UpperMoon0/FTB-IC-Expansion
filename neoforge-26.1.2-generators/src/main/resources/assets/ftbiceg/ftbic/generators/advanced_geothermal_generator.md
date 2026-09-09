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

  An MV throughput upgrade to the <ItemLink id="ftbic:geothermal_generator" />. Its default output is **60 zap/t** and its default tank capacity is **24,000 mB**.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>

<RecipeFor id="ftbiceg:advanced_geothermal_generator" />

<ItemImage id="minecraft:air" scale="0.25"/>
***

## Lava and efficiency

Lava consumption scales against FTBIC's configured Geothermal Generator output, preserving the intended energy-per-mB relationship when a pack changes upstream generator balance. The modern tank size is configurable independently.

## Compatibility

* Accepts lava through NeoForge's fluid transfer API as well as direct bucket interaction.
* Outputs through FTBIC Zap cables and exposes FE output through the same conversion rules.
* Charges compatible FTBIC energy items through its battery slot and exposes that slot to item automation.
* JEI registers this block as a crafting station for FTBIC's Geothermal fuel category.
* Jade shows stored energy and lava tank fill level.
