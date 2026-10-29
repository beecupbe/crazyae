package dev.beecube31.crazyae2.common.containers.implementations;

import appeng.api.config.LockCraftingMode;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.stacks.GenericStack;
import appeng.helpers.externalstorage.GenericStackInv;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.helpers.patternprovider.PatternProviderReturnInventory;
import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.slot.AppEngSlot;
import appeng.menu.slot.RestrictedInputSlot;
import dev.beecube31.crazyae2.common.containers.CrazyAEBaseContainer;
import dev.beecube31.crazyae2.common.containers.slots.RestrictedSlot;
import dev.beecube31.crazyae2.core.builder.ContainerTypeBuilder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class ContainerPatternProvider extends CrazyAEBaseContainer {

    public static final MenuType<ContainerPatternProvider> TYPE = ContainerTypeBuilder
            .create((id, inv, host) -> new ContainerPatternProvider(id, inv, host),
                    PatternProviderLogicHost.class)
            .build("pattern_provider");

    protected final PatternProviderLogic logic;

    @GuiSync(3)
    public YesNo blockingMode = YesNo.NO;
    @GuiSync(4)
    public YesNo showInAccessTerminal = YesNo.YES;
    @GuiSync(5)
    public LockCraftingMode lockCraftingMode = LockCraftingMode.NONE;
    @GuiSync(6)
    public LockCraftingMode craftingLockedReason = LockCraftingMode.NONE;
    @GuiSync(7)
    public GenericStack unlockStack = null;

    public ContainerPatternProvider(int id, Inventory playerInventory, PatternProviderLogicHost host) {
        this(TYPE, id, playerInventory, host);
    }

    protected ContainerPatternProvider(MenuType<? extends ContainerPatternProvider> menuType, int id, Inventory playerInventory,
                                       PatternProviderLogicHost host) {
        super(menuType, id, playerInventory, host);
        this.createPlayerInventorySlots(playerInventory);

        this.logic = host.getLogic();

        var patternInv = logic.getPatternInv();
        for (int x = 0; x < patternInv.size(); x++) {
            this.addSlot(new RestrictedSlot(RestrictedSlot.PlaceableItemType.ENCODED_PATTERN,
                    patternInv, x),
                    SlotSemantics.ENCODED_PATTERN);
        }

        // Show first few entries of the return inv
        var returnInv = logic.getReturnInv().createMenuWrapper();
        for (int i = 0; i < PatternProviderReturnInventory.NUMBER_OF_SLOTS; i++) {
            if (i < returnInv.size()) {
                this.addSlot(new AppEngSlot(returnInv, i), SlotSemantics.STORAGE);
            }
        }
    }

    @Override
    public void broadcastChanges() {
        if (isServerSide()) {
            blockingMode = logic.getConfigManager().getSetting(Settings.BLOCKING_MODE);
            showInAccessTerminal = logic.getConfigManager().getSetting(Settings.PATTERN_ACCESS_TERMINAL);
            lockCraftingMode = logic.getConfigManager().getSetting(Settings.LOCK_CRAFTING_MODE);
            craftingLockedReason = logic.getCraftingLockedReason();
            unlockStack = logic.getUnlockStack();
        }

        super.broadcastChanges();
    }

    public GenericStackInv getReturnInv() {
        return logic.getReturnInv();
    }

    public YesNo getBlockingMode() {
        return blockingMode;
    }

    public LockCraftingMode getLockCraftingMode() {
        return lockCraftingMode;
    }

    public LockCraftingMode getCraftingLockedReason() {
        return craftingLockedReason;
    }

    public GenericStack getUnlockStack() {
        return unlockStack;
    }

    public YesNo getShowInAccessTerminal() {
        return showInAccessTerminal;
    }
}
