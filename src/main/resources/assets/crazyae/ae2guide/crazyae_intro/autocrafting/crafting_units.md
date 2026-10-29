---
navigation:
  title: "Co-processors and crafting storages"
  parent: crazyae_intro/autocrafting/autocrafting.md
  icon: crazyae:4x_crafting_accelerator
  position: 3
item_ids:
  - 4x_crafting_accelerator
  - 16x_crafting_accelerator
  - 64x_crafting_accelerator
  - 256x_crafting_accelerator
  - 1024x_crafting_accelerator
  - 4096x_crafting_accelerator
  - 16384x_crafting_accelerator
  - 65536x_crafting_accelerator
  - 262144x_crafting_accelerator
  - 1048576x_crafting_accelerator
  - 4194304x_crafting_accelerator
  - creative_crafting_accelerator
  - 1mb_crafting_storage
  - 4mb_crafting_storage
  - 16mb_crafting_storage
  - 64mb_crafting_storage
  - 256mb_crafting_storage
  - 1gb_crafting_storage
  - 2gb_crafting_storage
  - 8gb_crafting_storage
  - 32gb_crafting_storage
  - 128gb_crafting_storage
  - creative_crafting_storage
---

# Co-processors and crafting storages

Bigger versions of AE2 crafting units. They work everywhere AE2 units work:
in a normal multiblock [crafting CPU](ae2:items-blocks-machines/crafting_cpu_multiblock.md),
inside the [Crafting Units Combiner](crafting_units_combiner.md) and inside the [ME Quantum CPU](quantum_cpu.md).

## Co-processing units

The number in the name is how many threads the unit adds. More threads = more patterns pushed per tick.

<ItemGrid>
  <ItemIcon id="4x_crafting_accelerator" />
  <ItemIcon id="16x_crafting_accelerator" />
  <ItemIcon id="64x_crafting_accelerator" />
  <ItemIcon id="256x_crafting_accelerator" />
  <ItemIcon id="1024x_crafting_accelerator" />
  <ItemIcon id="4096x_crafting_accelerator" />
  <ItemIcon id="16384x_crafting_accelerator" />
  <ItemIcon id="65536x_crafting_accelerator" />
  <ItemIcon id="262144x_crafting_accelerator" />
  <ItemIcon id="1048576x_crafting_accelerator" />
  <ItemIcon id="4194304x_crafting_accelerator" />
  <ItemIcon id="creative_crafting_accelerator" />
</ItemGrid>

Each tier is crafted from units of the previous tier; the first one from the AE2 co-processing unit.

<Row>
  <RecipeFor id="4x_crafting_accelerator" />
  <RecipeFor id="16x_crafting_accelerator" />
</Row>

## Crafting storages

The storage decides how big a job the CPU can accept: from 1 MB up to 128 GB.
Each one is an AE2 crafting unit combined with a storage component of the same size.

<ItemGrid>
  <ItemIcon id="1mb_crafting_storage" />
  <ItemIcon id="4mb_crafting_storage" />
  <ItemIcon id="16mb_crafting_storage" />
  <ItemIcon id="64mb_crafting_storage" />
  <ItemIcon id="256mb_crafting_storage" />
  <ItemIcon id="1gb_crafting_storage" />
  <ItemIcon id="2gb_crafting_storage" />
  <ItemIcon id="8gb_crafting_storage" />
  <ItemIcon id="32gb_crafting_storage" />
  <ItemIcon id="128gb_crafting_storage" />
  <ItemIcon id="creative_crafting_storage" />
</ItemGrid>

<Row>
  <RecipeFor id="1mb_crafting_storage" />
  <RecipeFor id="4mb_crafting_storage" />
</Row>

The creative units are available only in creative mode and are practically unlimited.
