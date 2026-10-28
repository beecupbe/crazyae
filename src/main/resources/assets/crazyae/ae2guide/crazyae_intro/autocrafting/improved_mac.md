---
navigation:
  title: "Improved Molecular Assembler"
  parent: crazyae_intro/autocrafting/autocrafting.md
  icon: crazyae:improved_mac
  position: 4
item_ids:
  - improved_mac
---

# Improved Molecular Assembler

<BlockImage id="improved_mac" scale="4" />

A [molecular assembler](ae2:items-blocks-machines/molecular_assembler.md) that does not need a pattern provider.
Put up to **45 crafting patterns** into it and connect it to the network — every CPU can use them right away.

- Crafts **8 items per tick** without upgrades.
- <ItemLink id="stacks_card" /> raise that number: 16 / 32 / 64 / 96 / 128 crafts per tick with 1–5 cards
  (configurable in `crazyae-machines.toml`).
- Accepts only crafting patterns (crafting table recipes). For smithing, stonecutting and huge pattern counts use the [ME Quantum CPU](quantum_cpu.md).
- Results go straight into the network.

<RecipeFor id="improved_mac" />
