package dev.beecube31.crazyae2.mixins.aefixes.crafting;

import appeng.api.config.Actionable;
import appeng.api.networking.crafting.ICraftingCPU;
import appeng.api.networking.crafting.ICraftingMedium;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.crafting.MECraftingInventory;
import appeng.me.cluster.IAECluster;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import appeng.me.helpers.MachineSource;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.Map;
import java.util.Queue;

@Mixin(value = CraftingCPUCluster.class, remap = false)
public abstract class MixinCraftingCPUCluster implements IAECluster, ICraftingCPU {

    @Shadow protected abstract void postChange(IAEItemStack diff, IActionSource src);

    @Shadow private MachineSource machineSrc;

    @Shadow private MECraftingInventory inventory;

    @Shadow @Final private Map<ICraftingPatternDetails, Queue<ICraftingMedium>> visitedMediums;

    @Shadow protected abstract World getWorld();

    @Shadow private IItemList<IAEItemStack> waitingFor;

    @Shadow protected abstract void postCraftingStatusChange(IAEItemStack diff);

    @Unique private List<IAEItemStack> items;

    @Redirect(
            method = "executeCrafting",
            at = @At(value = "INVOKE", target = "Lappeng/api/networking/crafting/ICraftingPatternDetails;isValidItemForSlot(ILnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;)Z")
    )
    private boolean crazyae$injectItemValidation(ICraftingPatternDetails instance, int i, ItemStack itemStack, World world, @Local LocalRef<Boolean> found, @Local IAEItemStack fuzz) {
        final IAEItemStack ais = this.inventory.extractItems(fuzz, Actionable.MODULATE, this.machineSrc);
        this.postChange(ais, this.machineSrc);
        this.items.add(ais);

        found.set(true);
        return false;
    }

    @Redirect(
            method = "executeCrafting",
            at = @At(value = "INVOKE", target = "Lappeng/util/Platform;getContainerItem(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;", ordinal = 0)
    )
    private ItemStack crazyae$injectContainerItemGetter(ItemStack stackInSlot) {
        if (this.items != null && !this.items.isEmpty()) {
            for (IAEItemStack item : this.items) {
                if (item != null) {
                    this.postChange(item, this.machineSrc);
                    this.waitingFor.add(item);
                    this.postCraftingStatusChange(item);
                }
            }

            this.items.clear();
        }

        return ItemStack.EMPTY;
    }
}
