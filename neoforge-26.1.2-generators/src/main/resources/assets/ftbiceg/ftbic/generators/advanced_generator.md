---
navigation:
  title: Advanced Generator
  icon: ftbiceg:advanced_generator
  parent: ftbic:generators/index.md
  position: 20
item_ids:
  - ftbiceg:advanced_generator
---

# <Color id="gold">Advanced Generator</Color>

<Column alignItems="center" fullWidth={true}>
  <ItemImage id="ftbiceg:advanced_generator" scale="2" />

  An LV throughput upgrade to the <ItemLink id="ftbic:basic_generator" />. Its default output is **30 zap/t** while fuel duration scales against the upstream Basic Generator output so changing FTBIC's generator config does not silently change fuel efficiency.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>

<RecipeFor id="ftbiceg:advanced_generator" />

<ItemImage id="minecraft:air" scale="0.25"/>
***

## Compatibility

* Uses FTBIC's datapack-driven Basic Generator fuel recipes.
* Outputs through FTBIC Zap cables and exposes FE output through the same conversion rules.
* Exposes its fuel inventory to automation.
* JEI registers this block as a crafting station for FTBIC's Basic Generator fuel category.
* Jade shows stored energy and remaining fuel time.
