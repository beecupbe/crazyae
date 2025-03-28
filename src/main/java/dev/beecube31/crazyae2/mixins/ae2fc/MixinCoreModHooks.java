package dev.beecube31.crazyae2.mixins.ae2fc;

import com.glodblock.github.coremod.CoreModHooks;
import com.glodblock.github.inventory.FluidConvertingInventoryCrafting;
import com.glodblock.github.util.Ae2Reflect;
import net.minecraft.inventory.InventoryCrafting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = CoreModHooks.class, remap = false, priority = 1111)
public abstract class MixinCoreModHooks {
    /**
     * @author Beecube31
     * @reason why inventory size is HARDCODED?
     */
    @Overwrite
    public static InventoryCrafting wrapCraftingBuffer(InventoryCrafting inv) {
        return new FluidConvertingInventoryCrafting(Ae2Reflect.getCraftContainer(inv), inv.getWidth(), inv.getHeight());
    }
}
