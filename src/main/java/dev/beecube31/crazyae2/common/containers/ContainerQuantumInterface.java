package dev.beecube31.crazyae2.common.containers;

import appeng.api.config.SecurityPermissions;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.util.IConfigManager;
import appeng.container.slot.IOptionalSlotHost;
import appeng.helpers.IInterfaceHost;
import dev.beecube31.crazyae2.client.gui.CrazyAESlot;
import dev.beecube31.crazyae2.common.containers.base.slot.RestrictedSlot;
import dev.beecube31.crazyae2.common.containers.guisync.GuiSync;
import dev.beecube31.crazyae2.common.interfaces.upgrades.IUpgradesInfoProvider;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerQuantumInterface extends ContainerCrazyAEUpgradeable implements IOptionalSlotHost {

    @GuiSync(3)
    public YesNo bMode = YesNo.NO;

    @GuiSync(4)
    public YesNo iTermMode = YesNo.YES;

    public ContainerQuantumInterface(final InventoryPlayer ip, final IInterfaceHost te) {
        super(ip, te instanceof IUpgradesInfoProvider r ? r : null);

        for (int row = 0; row < 8; ++row) {
            for (int x = 0; x < 9; x++) {
                this.addSlotToContainer(new RestrictedSlot(RestrictedSlot.PlaceableItemType.ENCODED_PATTERN, te.getInterfaceDuality()
                        .getPatterns(), x + row * 9, 8 + 18 * x, 25 + (18 * row), this.getInventoryPlayer()).setStackLimit(1));
            }
        }
        for (int i = 0; i < 3; i++) {
            for (int x = 0; x < 3; x++) {
                this.addSlotToContainer(new CrazyAESlot(te.getInterfaceDuality().getStorage(), x + i * 3, 187 + (18 * x), 99 + (18 * i)));
            }
        }
    }

    @Override
    protected int getHeight() {
        return 256;
    }

    @Override
    protected void setupConfig() {
        this.setupUpgrades();
    }

    @Override
    public boolean hasOptionSideButton() {
        return true;
    }

    @Override
    public boolean isSlotEnabled(final int idx) {
        return true;
    }

    @Override
    public void detectAndSendChanges() {
        this.verifyPermissions(SecurityPermissions.BUILD, false);
        super.detectAndSendChanges();
    }

    @Override
    protected void loadSettingsFromHost(final IConfigManager cm) {
        this.setBlockingMode((YesNo) cm.getSetting(Settings.BLOCK));
        this.setInterfaceTerminalMode((YesNo) cm.getSetting(Settings.INTERFACE_TERMINAL));
    }

    public YesNo getBlockingMode() {
        return this.bMode;
    }

    private void setBlockingMode(final YesNo bMode) {
        this.bMode = bMode;
    }

    public YesNo getInterfaceTerminalMode() {
        return this.iTermMode;
    }

    private void setInterfaceTerminalMode(final YesNo iTermMode) {
        this.iTermMode = iTermMode;
    }

}
