package dev.beecube31.crazyae2.mixins.core.crafting;

import appeng.api.util.WorldCoord;
import appeng.me.cluster.IAEMultiBlock;
import appeng.me.cluster.implementations.CraftingCPUCalculator;
import appeng.tile.crafting.TileCraftingTile;
import dev.beecube31.crazyae2.common.interfaces.ICrazyCraftingTile;
import dev.beecube31.crazyae2.common.tile.networking.TileCraftingUnitsCombiner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = CraftingCPUCalculator.class, remap = false)
public class MixinCraftingCPUCalculator {

    /**
     * @author Beecube31
     * @reason Support dense crafting blocks
     * @since v0.6
     */
    @Overwrite
    public boolean verifyInternalStructure(final World w, final WorldCoord min, final WorldCoord max) {
        boolean storage = false;

        for (int x = min.x; x <= max.x; x++) {
            for (int y = min.y; y <= max.y; y++) {
                for (int z = min.z; z <= max.z; z++) {
                    final IAEMultiBlock te = (IAEMultiBlock) w.getTileEntity(new BlockPos(x, y, z));

                    if (te == null || !te.isValid()) {
                        return false;
                    }

                    if (!storage && te instanceof ICrazyCraftingTile r) {
                        storage = r.getStorageCnt() > 0;
                        continue;
                    }

                    if (!storage && te instanceof TileCraftingTile) {
                        storage = ((TileCraftingTile) te).getStorageBytes() > 0;
                    }

                    if (!storage && te instanceof TileCraftingUnitsCombiner combiner) {
                        storage = combiner.getStorageAmt() > 0;
                    }
                }
            }
        }

        return storage;
    }
}
