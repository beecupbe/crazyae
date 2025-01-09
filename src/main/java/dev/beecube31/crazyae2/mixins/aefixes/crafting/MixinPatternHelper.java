package dev.beecube31.crazyae2.mixins.aefixes.crafting;

import appeng.helpers.PatternHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = PatternHelper.class, remap = false)
public abstract class MixinPatternHelper {
    @Shadow @Final private boolean isCrafting;

    /**
     * @author Beecube31
     * @reason Autocrafting optimizations
     */
    @Overwrite
    public synchronized boolean isValidItemForSlot(int slotIndex, ItemStack i, World w) {
        if (!this.isCrafting) {
            throw new IllegalStateException("Only crafting recipes supported.");
        } else {
            return true;
        }
    }
}
