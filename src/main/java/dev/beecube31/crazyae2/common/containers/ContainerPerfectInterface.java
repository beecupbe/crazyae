package dev.beecube31.crazyae2.common.containers;

import appeng.api.config.SecurityPermissions;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.util.IConfigManager;
import appeng.container.slot.IOptionalSlotHost;
import appeng.helpers.DualityInterface;
import appeng.helpers.IInterfaceHost;
import dev.beecube31.crazyae2.common.containers.base.slot.SlotFake;
import dev.beecube31.crazyae2.common.containers.base.slot.SlotOversized;
import dev.beecube31.crazyae2.common.containers.guisync.GuiSync;
import dev.beecube31.crazyae2.common.interfaces.upgrades.IUpgradesInfoProvider;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerPerfectInterface extends ContainerCrazyAEUpgradeable implements IOptionalSlotHost {

    private final DualityInterface myDuality;

    @GuiSync(3)
    public YesNo bMode = YesNo.NO;

    @GuiSync(4)
    public YesNo iTermMode = YesNo.YES;

    public ContainerPerfectInterface(final InventoryPlayer ip, final IInterfaceHost te) {
        super(ip, te instanceof IUpgradesInfoProvider r ? r : null);

        this.myDuality = te.getInterfaceDuality();

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlotToContainer(new SlotFake(
                        this.myDuality.getConfig(),
                        col + row * 9,
                        8 + 18 * col,
                        23 + 36 * row
                ));
            }
        }

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlotToContainer(new SlotOversized(
                        this.myDuality.getStorage(),
                        col + row * 9,
                        8 + 18 * col,
                        23 + 36 * row + 18
                ));
            }
        }

    }

    @Override
    public boolean hasOptionSideButton() {
        return true;
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
