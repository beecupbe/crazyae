package dev.beecube31.crazyae2.common.block.crafting;

import dev.beecube31.crazyae2.common.enums.crafting.QuantumCpuUnitType;
import dev.beecube31.crazyae2.common.tile.crafting.TileQuantumCPU;
import dev.beecube31.crazyae2.common.tile.networking.TileCraftingUnitsCombiner;
import net.minecraft.world.entity.player.Player;

public class BlockQuantumCPU extends BlockCraftingUnitsCombiner {
    public BlockQuantumCPU() {
        super(QuantumCpuUnitType.INSTANCE);
    }

    @Override
    protected void openMenu(Player player, TileCraftingUnitsCombiner be) {
        if (be instanceof TileQuantumCPU qcpu) {
            qcpu.openMenu(player);
        }
    }
}
