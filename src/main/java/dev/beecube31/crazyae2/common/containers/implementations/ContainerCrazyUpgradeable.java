package dev.beecube31.crazyae2.common.containers.implementations;

import appeng.api.config.*;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.util.IConfigManager;
import appeng.api.util.IConfigurableObject;
import appeng.core.definitions.AEItems;
import appeng.helpers.externalstorage.GenericStackInv;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.slot.IOptionalSlotHost;
import dev.beecube31.crazyae2.common.containers.ContainerCrazyToolbox;
import dev.beecube31.crazyae2.common.containers.CrazyAEBaseContainer;
import dev.beecube31.crazyae2.common.containers.slots.FakeSlot;
import dev.beecube31.crazyae2.common.containers.slots.OptionalFakeSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.ApiStatus;

public abstract class ContainerCrazyUpgradeable<T extends IUpgradeableObject> extends CrazyAEBaseContainer implements IOptionalSlotHost {

    private final T host;
    @GuiSync(0)
    public RedstoneMode rsMode = RedstoneMode.IGNORE;
    @GuiSync(1)
    public FuzzyMode fzMode = FuzzyMode.IGNORE_ALL;
    @GuiSync(5)
    public YesNo cMode = YesNo.NO;
    @GuiSync(6)
    public SchedulingMode schedulingMode = SchedulingMode.DEFAULT;

    private final ContainerCrazyToolbox toolbox;

    public ContainerCrazyUpgradeable(MenuType<?> menuType, int id, Inventory ip, T host) {
        super(menuType, id, ip, host);
        this.host = host;

        this.toolbox = new ContainerCrazyToolbox(this);

        this.setupInventorySlots();

        this.setupUpgrades();
        this.setupConfig();

        this.createPlayerInventorySlots(ip);
    }


    @ApiStatus.OverrideOnly
    protected void setupInventorySlots() {}

    @ApiStatus.OverrideOnly
    protected void setupConfig() {}

    @ApiStatus.OverrideOnly
    protected void setupUpgrades() {
        setupUpgrades(this.getHost().getUpgrades());
    }

    protected final void addExpandableConfigSlots(GenericStackInv config, int rows, int cols, int optionalRows) {
        var inv = config.createMenuWrapper();

        for (int y = 0; y < rows + optionalRows; y++) {
            for (int x = 0; x < cols; x++) {
                int invIdx = y * cols + x;
                if (y < rows) {
                    this.addSlot(new FakeSlot(inv, invIdx), SlotSemantics.CONFIG);
                } else {
                    this.addSlot(new OptionalFakeSlot(inv, this, invIdx, y - rows), SlotSemantics.CONFIG);
                }
            }
        }
    }

    public ContainerCrazyToolbox getToolbox() {
        return toolbox;
    }

    @Override
    public void broadcastChanges() {
        if (isServerSide() && getHost() instanceof IConfigurableObject configurableObject) {
            this.loadSettingsFromHost(configurableObject.getConfigManager());
        }

        toolbox.tick();

        for (Object o : this.slots) {
            if (o instanceof OptionalFakeSlot fs) {
                if (!fs.isSlotEnabled() && !fs.getDisplayStack().isEmpty()) {
                    fs.clearStack();
                }
            }
        }

        this.standardDetectAndSendChanges();
    }

    protected void loadSettingsFromHost(IConfigManager cm) {
        this.setFuzzyMode(cm.getSetting(Settings.FUZZY_MODE));
        this.setRedStoneMode(cm.getSetting(Settings.REDSTONE_CONTROLLED));
        if (cm.hasSetting(Settings.CRAFT_ONLY)) {
            this.setCraftingMode(cm.getSetting(Settings.CRAFT_ONLY));
        }
        if (cm.hasSetting(Settings.SCHEDULING_MODE)) {
            this.setSchedulingMode(cm.getSetting(Settings.SCHEDULING_MODE));
        }
    }

    protected void standardDetectAndSendChanges() {
        super.broadcastChanges();
    }

    @Override
    public boolean isSlotEnabled(int idx) {
        int capacityUpgrades = this.getHost().getUpgrades().getInstalledUpgrades(AEItems.CAPACITY_CARD);
        return idx == 1 && capacityUpgrades >= 1
                || idx == 2 && capacityUpgrades >= 2;
    }

    public FuzzyMode getFuzzyMode() {
        return this.fzMode;
    }

    public void setFuzzyMode(FuzzyMode fzMode) {
        this.fzMode = fzMode;
    }

    public YesNo getCraftingMode() {
        return this.cMode;
    }

    public void setCraftingMode(YesNo cMode) {
        this.cMode = cMode;
    }

    public RedstoneMode getRedStoneMode() {
        return this.rsMode;
    }

    public void setRedStoneMode(RedstoneMode rsMode) {
        this.rsMode = rsMode;
    }

    public SchedulingMode getSchedulingMode() {
        return this.schedulingMode;
    }

    private void setSchedulingMode(SchedulingMode schedulingMode) {
        this.schedulingMode = schedulingMode;
    }

    public final T getHost() {
        return this.host;
    }

    public final IUpgradeInventory getUpgrades() {
        return getHost().getUpgrades();
    }

    public final boolean hasUpgrade(ItemLike upgradeCard) {
        return getUpgrades().isInstalled(upgradeCard);
    }

}
