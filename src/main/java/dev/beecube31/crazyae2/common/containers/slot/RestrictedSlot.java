package dev.beecube31.crazyae2.common.containers.slot;

import appeng.api.AEApi;
import appeng.api.implementations.items.IStorageComponent;
import appeng.api.implementations.items.IUpgradeModule;
import appeng.block.crafting.ItemCraftingStorage;
import appeng.container.slot.AppEngSlot;
import dev.beecube31.crazyae2.common.interfaces.ICrazyAEUpgradeModule;
import dev.beecube31.crazyae2.common.items.CrazyAEUpgradeModule;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class RestrictedSlot extends AppEngSlot {

    private final RestrictedSlot.PlacableItemType which;
    private final InventoryPlayer p;
    private boolean allowEdit = true;
    private int stackLimit = -1;

    public RestrictedSlot(final RestrictedSlot.PlacableItemType valid, final IItemHandler i, final int slotIndex, final int x, final int y, final InventoryPlayer p) {
        super(i, slotIndex, x, y);
        this.which = valid;
        this.setIIcon(valid.IIcon);
        this.p = p;
    }

    public Slot setStackLimit(int i) {
        this.stackLimit = i;
        return this;
    }

    @Override
    public int getSlotStackLimit() {
        if (this.stackLimit != -1) {
            return this.stackLimit;
        }
        return super.getSlotStackLimit();
    }

    @Override
    public boolean isItemValid(final ItemStack i) {
        if (!this.getContainer().isValidForSlot(this, i)) return false;
        if (i.isEmpty()) return false;
        if (i.getItem() == Items.AIR) return false;
        if (!super.isItemValid(i)) return false;
        if (!this.isAllowEdit()) return false;

        return switch (this.which) {
            case CRAFTING_UNITS ->
                    i.getItem() instanceof ItemCraftingStorage ||
                    AEApi.instance().definitions().blocks().craftingAccelerator().isSameAs(i) ||
                    CrazyAE.definitions().blocks().coprocessor4x().isSameAs(i) ||
                    CrazyAE.definitions().blocks().coprocessor16x().isSameAs(i) ||
                    CrazyAE.definitions().blocks().coprocessor64x().isSameAs(i) ||
                    CrazyAE.definitions().blocks().coprocessor256x().isSameAs(i) ||
                    CrazyAE.definitions().blocks().coprocessor1024x().isSameAs(i) ||
                    CrazyAE.definitions().blocks().coprocessor4096x().isSameAs(i) ||
                    CrazyAE.definitions().blocks().coprocessor16384x().isSameAs(i) ||
                    CrazyAE.definitions().blocks().coprocessor65536x().isSameAs(i);
            case CRAFTING_STORAGES ->
                    i.getItem() instanceof ItemCraftingStorage;
            case CRAFTING_ACCELERATORS ->
                    AEApi.instance().definitions().blocks().craftingAccelerator().isSameAs(i) ||
                    CrazyAE.definitions().blocks().coprocessor4x().isSameAs(i) ||
                    CrazyAE.definitions().blocks().coprocessor16x().isSameAs(i) ||
                    CrazyAE.definitions().blocks().coprocessor64x().isSameAs(i) ||
                    CrazyAE.definitions().blocks().coprocessor256x().isSameAs(i) ||
                    CrazyAE.definitions().blocks().coprocessor1024x().isSameAs(i) ||
                    CrazyAE.definitions().blocks().coprocessor4096x().isSameAs(i) ||
                    CrazyAE.definitions().blocks().coprocessor16384x().isSameAs(i) ||
                    CrazyAE.definitions().blocks().coprocessor65536x().isSameAs(i);
            case CERTUS_QUARTZ_CRYSTALS ->
                    AEApi.instance().definitions().materials().certusQuartzCrystal().isSameAs(i);
            case CHARGED_CERTUS_QUARTZ_CRYSTALS ->
                    AEApi.instance().definitions().materials().certusQuartzCrystalCharged().isSameAs(i);
            case AE_UPGRADES ->
                    i.getItem() instanceof IUpgradeModule && ((IUpgradeModule) i.getItem()).getType(i) != null;
            case CRAZYAE_UPGRADES ->
                    i.getItem() instanceof ICrazyAEUpgradeModule;
            case UPGRADES ->
                    i.getItem() instanceof IUpgradeModule && ((IUpgradeModule) i.getItem()).getType(i) != null
                    || i.getItem() instanceof CrazyAEUpgradeModule;

            case STORAGE_COMPONENT ->
                i.getItem() instanceof IStorageComponent && ((IStorageComponent)i.getItem()).isStorageComponent(i);
        };

    }

    @Override
    public boolean canTakeStack(final EntityPlayer par1EntityPlayer) {
        return this.isAllowEdit();
    }

    @Override
    public ItemStack getDisplayStack() {
        return super.getStack();
    }

    private boolean isAllowEdit() {
        return this.allowEdit;
    }

    public enum PlacableItemType {
        STORAGE_COMPONENT(63),


        CRAFTING_ACCELERATORS(5 * 16 + 14),
        CRAFTING_STORAGES(6 * 16 + 14),
        CRAFTING_UNITS(7 * 16 + 14),
        CERTUS_QUARTZ_CRYSTALS(8 * 16 + 14),
        CHARGED_CERTUS_QUARTZ_CRYSTALS(9 * 16 + 14),
        AE_UPGRADES(13 * 16 + 15),
        CRAZYAE_UPGRADES(13 * 16 + 15),
        UPGRADES(13 * 16 + 15);


        public final int IIcon;

        PlacableItemType(final int o) {
            this.IIcon = o;
        }
    }
}
