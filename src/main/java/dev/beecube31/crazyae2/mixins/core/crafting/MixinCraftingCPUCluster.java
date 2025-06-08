package dev.beecube31.crazyae2.mixins.core.crafting;

import appeng.api.config.Actionable;
import appeng.api.networking.IGrid;
import appeng.api.networking.crafting.ICraftingJob;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.crafting.ICraftingRequester;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.crafting.MECraftingInventory;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import appeng.me.helpers.MachineSource;
import appeng.tile.crafting.TileCraftingMonitorTile;
import appeng.tile.crafting.TileCraftingTile;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.beecube31.crazyae2.common.interfaces.ICrazyCraftingTile;
import dev.beecube31.crazyae2.common.interfaces.crafting.ICrazyAECraftingPatternDetails;
import dev.beecube31.crazyae2.common.interfaces.mixin.crafting.IMixinCraftingCPUStatus;
import dev.beecube31.crazyae2.common.tile.networking.TileCraftingUnitsCombiner;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Set;

@Mixin(value = CraftingCPUCluster.class, remap = false, priority = 1111)
public abstract class MixinCraftingCPUCluster implements IMixinCraftingCPUStatus {
    @Shadow private int accelerator;

    @Shadow private long availableStorage;

    @Shadow private MachineSource machineSrc;

    @Shadow @Final private List<TileCraftingTile> tiles;

    @Shadow @Final private List<TileCraftingTile> storage;

    @Shadow @Final private List<TileCraftingMonitorTile> status;

    @Shadow protected abstract void postChange(IAEItemStack diff, IActionSource src);

    @Shadow private IItemList<IAEItemStack> waitingFor;

    @Shadow protected abstract void postCraftingStatusChange(IAEItemStack diff);

    @Shadow private MECraftingInventory inventory;

    @Unique private Set<IAEItemStack> crazyae$items;

    @Unique private long crazyae$millisWhenJobStarted;

    @Unique private String crazyae$jobInitiator;


    @Redirect(
            method = "executeCrafting",
            at = @At(value = "INVOKE", target = "Lappeng/api/networking/crafting/ICraftingPatternDetails;isValidItemForSlot(ILnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;)Z")
    )
    private boolean crazyae$injectItemValidation(ICraftingPatternDetails instance, int i, ItemStack itemStack, World world, @Local LocalRef<Boolean> found, @Local IAEItemStack fuzz) {
        final IAEItemStack ais = this.inventory.extractItems(fuzz, Actionable.MODULATE, this.machineSrc);
        this.postChange(ais, this.machineSrc);
        this.crazyae$items.add(ais);

        found.set(true);
        return false;
    }

    @Redirect(
            method = "executeCrafting",
            at = @At(
                    value = "NEW",
                    target = "net/minecraft/inventory/InventoryCrafting",
                    ordinal = 0
            ),
            remap = false
    )
    private InventoryCrafting redirectInventoryCraftingConstructor(
            Container container,
            int width,
            int height,
            @Local(name = "details") ICraftingPatternDetails details
    ) {
        if (details instanceof ICrazyAECraftingPatternDetails s && details.isCraftable()) {
            return new InventoryCrafting(container, s.getInventorySizeX(), s.getInventorySizeY());
        }

        return new InventoryCrafting(container, width, height);
    }

    @Redirect(
            method = "executeCrafting",
            at = @At(value = "INVOKE", target = "Lappeng/util/Platform;getContainerItem(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;", ordinal = 0)
    )
    private ItemStack crazyae$injectContainerItemGetter(ItemStack stackInSlot) {
        if (this.crazyae$items != null && !this.crazyae$items.isEmpty()) {
            for (IAEItemStack item : this.crazyae$items) {
                if (item != null) {
                    this.postChange(item, this.machineSrc);
                    this.waitingFor.add(item);
                    this.postCraftingStatusChange(item);
                }
            }

            this.crazyae$items.clear();
        }

        return ItemStack.EMPTY;
    }

    @Override
    public long crazyae$whenJobStarted() {
        return this.crazyae$millisWhenJobStarted;
    }

    @Override
    public void crazyae$setWhenJobStarted(long when) {
        this.crazyae$millisWhenJobStarted = when;
    }

    @Override
    public String crazyae$jobInitiator() {
        return this.crazyae$jobInitiator;
    }

    @Override
    public void crazyae$setJobInitiator(String player) {
        this.crazyae$jobInitiator = player;
    }

    @Inject(method = "writeToNBT", at = @At("TAIL"), remap = false)
    private void crazyae$writeAdditionalInfo(NBTTagCompound data, CallbackInfo ci) {
        if (this.crazyae$millisWhenJobStarted > 0) {
            data.setLong("millisJobStarted", this.crazyae$millisWhenJobStarted);
        }
        if (this.crazyae$jobInitiator != null && !this.crazyae$jobInitiator.isEmpty()) {
            data.setString("jobInitiator", this.crazyae$jobInitiator);
        }
    }

    @Inject(method = "readFromNBT", at = @At("TAIL"), remap = false)
    private void crazyae$readAdditionalInfo(NBTTagCompound data, CallbackInfo ci) {
        this.crazyae$millisWhenJobStarted = data.getLong("millisJobStarted");
        this.crazyae$jobInitiator = data.getString("jobInitiator");
    }

    @Inject(method = "submitJob", at = @At("RETURN"), remap = false)
    private void crazyae$addAdditionalInfo(IGrid g, ICraftingJob job, IActionSource src, ICraftingRequester requestingMachine, CallbackInfoReturnable<ICraftingLink> cir) {
        String initiator;

        if (src.player().isPresent()) {
            initiator = src.player().get().getName();
        } else if (src.machine().isPresent()) {
            initiator = src.machine().get().getActionableNode().getGridBlock().getMachineRepresentation().getDisplayName();
        } else {
            initiator = "N/A";
        }

        this.crazyae$jobInitiator = initiator;
        this.crazyae$millisWhenJobStarted = System.currentTimeMillis();
    }


    /**
     * @author Beecube31
     * @reason Support dense crafting blocks
     * @since v0.6
     */
    @Overwrite
    void addTile(final TileCraftingTile te) {
        if (this.machineSrc == null || te.isCoreBlock()) {
            this.machineSrc = new MachineSource(te);
        }

        te.setCoreBlock(false);
        te.saveChanges();
        this.tiles.add(0, te);

        if (te.isStorage()) {
            this.availableStorage += te.getStorageBytes();
            this.storage.add(te);
        } else if (te.isStatus()) {
            this.status.add((TileCraftingMonitorTile) te);
        } else if (te.isAccelerator()) {
            this.accelerator++;
        }

        if (te instanceof ICrazyCraftingTile r) {
            this.accelerator += Math.min(r.getAccelerationFactor(), Integer.MAX_VALUE - this.accelerator);
            this.availableStorage += Math.min(r.getStorageCnt(), Long.MAX_VALUE - this.availableStorage);
        }

        if (te instanceof TileCraftingUnitsCombiner combiner) {
            this.accelerator += Math.min(combiner.getAcceleratorAmt(), Integer.MAX_VALUE - this.accelerator);
            this.availableStorage += Math.min(combiner.getStorageAmt(), Long.MAX_VALUE - this.availableStorage);
        }

        this.crazyae$overflowProtection();
    }

    @Unique
    private void crazyae$overflowProtection() {
        if (this.accelerator == Integer.MAX_VALUE) {
            this.accelerator--;
        }
    }
}
