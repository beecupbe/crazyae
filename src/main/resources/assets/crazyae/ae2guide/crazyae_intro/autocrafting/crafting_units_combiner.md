---
navigation:
  title: "Crafting Units Combiner"
  parent: crazyae_intro/autocrafting/autocrafting.md
  icon: crazyae:crafting_units_combiner
  position: 2
item_ids:
  - crafting_units_combiner
---

# ME Crafting Units Combiner

<GameScene zoom="6" interactive={true}>
  <ImportStructure src="../assets/combiner_setup.snbt" />
  <BoxAnnotation color="#33aaff" min="2 0 0" max="3 1 1">
    A whole crafting CPU in one block.
  </BoxAnnotation>
  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

A complete [crafting CPU](ae2:items-blocks-machines/crafting_cpu_multiblock.md) that fits into a single block.
Instead of building a multiblock, you **put crafting units into its GUI**:

- **12 slots** for co-processing units (up to 64 per slot);
- **12 slots** for crafting storages (up to 64 per slot).

Any co-processor or crafting storage works: from AE2, [Crazy AE](crafting_units.md), or other AE2 addons.
Hover over the big icons on the left of the GUI to see the total threads and bytes.

- Uses one channel and 1024 AE/t.
- Does not merge with neighbouring crafting blocks — place several of them side by side to get several CPUs.
- You cannot change the units while the CPU is crafting.
- Breaking the block drops everything inside.

Need even more? The [ME Quantum CPU](quantum_cpu.md) has 18 slots of each type and can craft by itself.

<RecipeFor id="crafting_units_combiner" />
