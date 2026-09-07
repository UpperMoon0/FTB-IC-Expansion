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

  An LV throughput upgrade to the <ItemLink id="ftbic:basic_generator" />. Its default output is **30 zap/t**.
</Column>

<ItemImage id="minecraft:air" scale="0.25"/>

<RecipeFor id="ftbiceg:advanced_generator" />

<ItemImage id="minecraft:air" scale="0.25"/>
***

## Fuel and efficiency

The Advanced Generator uses FTBIC's datapack-driven **Basic Generator fuel recipes**. It does not maintain a hardcoded coal/fuel whitelist, so fuels added to FTBIC by a datapack or another mod work here automatically.

Fuel duration scales against FTBIC's configured Basic Generator output. With the default 30 zap/t Advanced output and 10 zap/t Basic output, the generator produces three times the power per tick and burns the fuel for one third as many ticks. This preserves approximately the same total energy per fuel item while increasing throughput.

## Compatibility

* Outputs through FTBIC Zap cables and exposes FE output through the same conversion rules.
* Charges compatible FTBIC energy items through its battery slot.
* Exposes both fuel and battery inventory to item automation.
* JEI registers this block as a crafting station for FTBIC's Basic Generator fuel category.
* Jade shows stored energy and remaining fuel time.
