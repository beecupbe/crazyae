package dev.beecube31.crazyae2.common.containers.base.slot;

import appeng.api.AEApi;
import appeng.api.implementations.items.IStorageComponent;
import appeng.api.implementations.items.IUpgradeModule;
import appeng.block.crafting.ItemCraftingStorage;
import appeng.items.misc.ItemEncodedPattern;
import dev.beecube31.crazyae2.client.gui.CrazyAESlot;
import dev.beecube31.crazyae2.client.gui.sprites.StateSprite;
import dev.beecube31.crazyae2.common.interfaces.ICrazyAEUpgradeModule;
import dev.beecube31.crazyae2.common.items.CrazyAEUpgradeModule;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

public class RestrictedSlot extends CrazyAESlot {

    private final PlaceableItemType which;
    private final InventoryPlayer p;
    private boolean allowEdit = true;
    private int stackLimit = -1;

    public RestrictedSlot(final PlaceableItemType valid, final IItemHandler i, final int slotIndex, final int x, final int y, final InventoryPlayer p) {
        super(i, slotIndex, x, y);
        this.which = valid;
        this.setIIcon(valid.IIcon);
        this.p = p;
    }

    public PlaceableItemType getPlaceableItemType() {
        return which;
    }

    public CrazyAESlot setStackLimit(int i) {
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

    public boolean isValid(final ItemStack is, final World theWorld) {
        return true;
    }

    @Override
    public boolean isItemValid(final ItemStack i) {
        if (!this.getContainer().isValidForSlot(this, i)) return false;
        if (i.isEmpty()) return false;
        if (i.getItem() == Items.AIR) return false;
        if (!super.isItemValid(i)) return false;
        if (!this.isAllowEdit()) return false;

        return switch (this.which) {
            case NONE -> false;
            case TRASH ->
                    !AEApi.instance().registries().cell().isCellHandled(i)
                    && !(
                            i.getItem() instanceof IStorageComponent
                            && ((IStorageComponent) i.getItem()).isStorageComponent(i)
                    );

            case ENCODED_PATTERN -> i.getItem() instanceof ItemEncodedPattern;
            case STORAGE_CELLS -> AEApi.instance().registries().cell().isCellHandled(i);
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

            case PETAL_BLANK_PATTERN ->
                    CrazyAE.definitions().materials().petalBlankPattern().isSameAs(i);
            case TERAPLATE_ENCODED_PATTERN ->
                    CrazyAE.definitions().items().teraplateEncodedPattern().isSameAs(i);
            case BREWERY_ENCODED_PATTERN ->
                    CrazyAE.definitions().items().breweryEncodedPattern().isSameAs(i);
            case PETAL_ENCODED_PATTERN ->
                    CrazyAE.definitions().items().petalEncodedPattern().isSameAs(i);

            case MANAPOOL_BLANK_PATTERN ->
                    CrazyAE.definitions().materials().manapoolBlankPattern().isSameAs(i);
            case MANAPOOL_ENCODED_PATTERN ->
                    CrazyAE.definitions().items().manapoolEncodedPattern().isSameAs(i);

            case RUNEALTAR_BLANK_PATTERN ->
                    CrazyAE.definitions().materials().runealtarBlankPattern().isSameAs(i);
            case RUNEALTAR_ENCODED_PATTERN ->
                    CrazyAE.definitions().items().runealtarEncodedPattern().isSameAs(i);
            case TERAPLATE_BLANK_PATTERN ->
                    CrazyAE.definitions().materials().teraplateBlankPattern().isSameAs(i);
            case BREWERY_BLANK_PATTERN ->
                    CrazyAE.definitions().materials().breweryBlankPattern().isSameAs(i);
            case PUREDAISY_BLANK_PATTERN ->
                    CrazyAE.definitions().materials().puredaisyBlankPattern().isSameAs(i);
            case PUREDAISY_ENCODED_PATTERN ->
                    CrazyAE.definitions().items().puredaisyEncodedPattern().isSameAs(i);

            case ELVENTRADE_BLANK_PATTERN ->
                    CrazyAE.definitions().materials().elventradeBlankPattern().isSameAs(i);
            case ELVENTRADE_ENCODED_PATTERN ->
                    CrazyAE.definitions().items().elventradeEncodedPattern().isSameAs(i);
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

    public enum PlaceableItemType {
        NONE(StateSprite.TRASH_SLOT),
        STORAGE_CELLS(StateSprite.STORAGE_CELLS_SLOT),
        ENCODED_PATTERN(StateSprite.PATTERNS_SLOT_OLD),
        TRASH(StateSprite.TRASH_SLOT),


        STORAGE_COMPONENT(StateSprite.STORAGE_COMPONENTS_SLOT),


        CRAFTING_ACCELERATORS(StateSprite.CRAFTING_ACCELERATORS_SLOT),
        CRAFTING_STORAGES(StateSprite.CRAFTING_STORAGES_SLOT),
        CRAFTING_UNITS(StateSprite.CRAFTING_BLOCKS_SLOT),
        CERTUS_QUARTZ_CRYSTALS(StateSprite.CERTUS_QUARTZ),
        CHARGED_CERTUS_QUARTZ_CRYSTALS(StateSprite.CHARGED_CERTUS_QUARTZ),
        AE_UPGRADES(StateSprite.AE_UPGRADE_CARDS_SLOT),
        CRAZYAE_UPGRADES(StateSprite.CRAZYAE_UPGRADE_CARDS_SLOT),
        UPGRADES(StateSprite.CRAZYAE_UPGRADE_CARDS_SLOT),


        ELVENTRADE_BLANK_PATTERN(StateSprite.PATTERNS_SLOT_NEW),
        MANAPOOL_BLANK_PATTERN(StateSprite.PATTERNS_SLOT_NEW),
        RUNEALTAR_BLANK_PATTERN(StateSprite.PATTERNS_SLOT_NEW),
        TERAPLATE_BLANK_PATTERN(StateSprite.PATTERNS_SLOT_NEW),
        BREWERY_BLANK_PATTERN(StateSprite.PATTERNS_SLOT_NEW),
        PUREDAISY_BLANK_PATTERN(StateSprite.PATTERNS_SLOT_NEW),
        PETAL_BLANK_PATTERN(StateSprite.PATTERNS_SLOT_NEW),

        ELVENTRADE_ENCODED_PATTERN(StateSprite.PATTERNS_SLOT_NEW),
        MANAPOOL_ENCODED_PATTERN(StateSprite.PATTERNS_SLOT_NEW),
        RUNEALTAR_ENCODED_PATTERN(StateSprite.PATTERNS_SLOT_NEW),
        PUREDAISY_ENCODED_PATTERN(StateSprite.PATTERNS_SLOT_NEW),
        TERAPLATE_ENCODED_PATTERN(StateSprite.PATTERNS_SLOT_NEW),
        BREWERY_ENCODED_PATTERN(StateSprite.PATTERNS_SLOT_NEW),
        PETAL_ENCODED_PATTERN(StateSprite.PATTERNS_SLOT_NEW);



        public final StateSprite IIcon;

        PlaceableItemType(final StateSprite o) {
            this.IIcon = o;
        }
    }
}
