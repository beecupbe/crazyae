package dev.beecube31.crazyae2.mixins.features.termnbt;

import appeng.api.storage.ITerminalHost;
import appeng.container.implementations.ContainerMEMonitorable;
import appeng.container.implementations.ContainerPatternEncoder;
import appeng.container.slot.IOptionalSlotHost;
import appeng.helpers.IContainerCraftingPacket;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.util.inv.IAEAppEngInventory;
import dev.beecube31.crazyae2.common.interfaces.mixin.container.IMixinContainerPatternEncoder;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = ContainerPatternEncoder.class, remap = false)
public abstract class MixinContainerPatternEncoder extends ContainerMEMonitorable implements IAEAppEngInventory, IOptionalSlotHost, IContainerCraftingPacket, IMixinContainerPatternEncoder {
    @Shadow protected IItemHandler crafting;

    @Shadow protected IRecipe currentRecipe;

    @Shadow @Final private AppEngInternalInventory cOut;

    @Unique private boolean crazyae$blockUpdates;

    public MixinContainerPatternEncoder(InventoryPlayer ip, ITerminalHost monitorable) {
        super(ip, monitorable);
    }

    @ModifyArg(
            method = "getAndUpdateOutput",
            at = @At(
                    value = "INVOKE",
                    target = "Lappeng/tile/inventory/AppEngInternalInventory;setStackInSlot(ILnet/minecraft/item/ItemStack;)V"
            ),
            index = 1
    )
    private ItemStack crazyae$removeNbtOnUpdate(ItemStack stack) {
        stack.setTagCompound(null);
        return stack;
    }

    @Override
    public void crazyae$setBlockMode(boolean v) {
        this.crazyae$blockUpdates = v;
    }

    /**
     * @author Beecube31
     * @reason
     */
    @Overwrite
    protected ItemStack getAndUpdateOutput() {
        if (this.crazyae$blockUpdates) return ItemStack.EMPTY;

        final World world = this.getPlayerInv().player.world;
        final InventoryCrafting ic = new InventoryCrafting(this, 3, 3);

        for (int x = 0; x < ic.getSizeInventory(); x++) {
            ic.setInventorySlotContents(x, this.crafting.getStackInSlot(x));
        }

        if (this.currentRecipe == null || !this.currentRecipe.matches(ic, world)) {
            this.currentRecipe = CraftingManager.findMatchingRecipe(ic, world);
        }

        final ItemStack is;

        if (this.currentRecipe == null) {
            is = ItemStack.EMPTY;
        } else {
            is = this.currentRecipe.getCraftingResult(ic);
        }

        this.cOut.setStackInSlot(0, is);
        return is;
    }
}