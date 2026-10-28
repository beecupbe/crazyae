---
navigation:
  title: "ME Quantum CPU"
  parent: crazyae_intro/autocrafting/autocrafting.md
  icon: crazyae:quantum_cpu
  position: 1
item_ids:
  - quantum_cpu
---

# ME Quantum CPU

<BlockImage id="quantum_cpu" scale="4" />

The Quantum CPU is the top of Crazy AE autocrafting. It is **one block that does three jobs at once**:

1. **A crafting CPU.** It runs crafting jobs like an AE2 [crafting CPU](ae2:items-blocks-machines/crafting_cpu_multiblock.md),
   but you do not build a multiblock: co-processors and crafting storages go *inside* it, 18 slots of each.
2. **An instant crafter.** It holds up to **1024 crafting patterns** (crafting table, smithing table, stonecutter).
   Every CPU of the network can use these patterns, and the crafts finish instantly, in bulk, with no molecular assemblers at all.
3. **A batch dispatcher.** When a job runs *on the Quantum CPU itself*, processing patterns are sent to
   [pattern providers](ae2:items-blocks-machines/pattern_provider.md) in batches: dozens of crafts in one push instead of one.

## Building it

<GameScene zoom="5" interactive={true}>
  <ImportStructure src="../assets/quantum_cpu_setup.snbt" />

  <BoxAnnotation color="#a855f7" min="2 0 0" max="3 1 1">
    **ME Quantum CPU** — a CPU and an instant crafter in one block. Just connect it to the network.
  </BoxAnnotation>

  <BoxAnnotation color="#33dd33" min="4 0 0" max="5 1 1">
    **Pattern provider** with processing patterns (here: smelting). Jobs on the Quantum CPU push them in batches.
  </BoxAnnotation>

  <BoxAnnotation color="#dddddd" min="0 0 0" max="1 1 1">
    **ME Controller**. The Quantum CPU uses a channel and 4096 AE/t.
  </BoxAnnotation>

  <IsometricCamera yaw="195" pitch="30" />
</GameScene>

1. Place the Quantum CPU anywhere on your network. It needs **one channel** and draws **4096 AE/t**.
   It does *not* merge with neighbouring crafting units, so it can stand right next to other CPUs.
2. Open it (right-click) and put your **crafting patterns** into the grid.
3. Click the button with the co-processor icon on the right side of the GUI and insert
   [co-processors and crafting storages](crafting_units.md). Without storage the CPU cannot accept jobs;
   co-processors decide how many crafts it performs per tick.
4. Done. Request something in a terminal and choose the Quantum CPU, or let AE2 pick a CPU automatically.

## The interface

### Pattern grid

- The grid shows **64 slots per page**, **16 pages** — 1024 patterns in total.
- Switch pages with the arrows at the bottom or with the **mouse wheel** over the grid. The current page is at the top left.
- The counter at the bottom right shows how many patterns are inside, e.g. `37/1024`.
- Only crafting, smithing and stonecutting patterns fit. Processing patterns go into pattern providers as usual.

### Crafting blocks

The side button opens the second screen with two rows of 9 slots:

- **upper rows** — co-processing units (AE2 and Crazy AE ones, up to 64 per slot);
- **lower rows** — crafting storages.

Hover over the big icons on the left to see the total number of threads and bytes. The button on the right returns you to the pattern grid.

## How a craft goes

Imagine you order **2000 pistons** and the Quantum CPU has their pattern:

| Step | What happens |
|------|--------------|
| 1 | AE2 calculates the job as usual and picks a CPU (any CPU, not necessarily the Quantum one). |
| 2 | The CPU sees that the Quantum CPU offers the piston pattern and sends it **as many crafts as it can per tick** — limited by that CPU's co-processors. |
| 3 | The Quantum CPU finishes them instantly and puts the pistons, plus leftover containers like empty buckets, straight into ME storage. |
| 4 | Ingredients that are themselves crafting patterns (planks, cobblestone recipes, iron ingots → ...) are crafted the same way. |

If the job runs **on the Quantum CPU**, processing patterns are also sped up:
instead of one smelting craft per push, a pattern provider receives a whole batch, e.g. 64 iron ores at once.
The batch size is limited by the co-processors inside the Quantum CPU, so do not leave it without them.

## Batches to pattern providers: when they work

A pattern provider gets batches only when **all** of these are true:

- the job runs on a Quantum CPU;
- the provider has **blocking mode off** and **no crafting lock** (redstone or result locks);
- batching is enabled in the config (`quantumCpu.batchToPatternProviders`, on by default).

One push contains at most `quantumCpu.maxBatchPerProvider` crafts (65536 by default).
If a machine behind a provider must get exactly one craft at a time, turn on blocking mode on that provider —
the Quantum CPU will then send it single crafts like any other CPU.

## Good to know

- **Priority.** Patterns in the Quantum CPU have priority 1, higher than the default pattern provider priority.
  If the same recipe exists in a provider too, the Quantum CPU is used first.
- **Storage full?** Crafted items wait inside the Quantum CPU and it reports itself busy until there is room in ME storage.
  Nothing is lost.
- **Breaking it** drops all patterns, inserted crafting blocks and any results that were still waiting.
- **No more Quantum Interface.** On 1.12.2 the CPU needed a separate Quantum Interface and special setup.
  Now it is a single block: place it, insert patterns and crafting blocks, and it works.

## Recipe

<RecipeFor id="quantum_cpu" />
