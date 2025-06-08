package dev.beecube31.crazyae2.client.gui.implementations;

import appeng.items.tools.ToolNetworkTool;
import dev.beecube31.crazyae2.client.gui.sprites.StateSprite;
import dev.beecube31.crazyae2.client.gui.widgets.OptionSideButton;
import dev.beecube31.crazyae2.common.containers.ContainerCrazyAEUpgradeable;
import dev.beecube31.crazyae2.common.interfaces.upgrades.IUpgradesInfoProvider;
import dev.beecube31.crazyae2.common.networking.network.NetworkHandler;
import dev.beecube31.crazyae2.common.networking.packets.PacketSwitchGuis;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiBridge;
import dev.beecube31.crazyae2.common.i18n.CrazyAEGuiText;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

import java.io.IOException;

public class GuiEnergyBus extends GuiCrazyAEUpgradeable {
    protected final ContainerCrazyAEUpgradeable container;
    protected OptionSideButton setupBtn;


    public GuiEnergyBus(InventoryPlayer inventoryPlayer, IUpgradesInfoProvider te) {
        super(inventoryPlayer, te);
        this.container = (ContainerCrazyAEUpgradeable)this.inventorySlots;
        this.xSize += 26;
    }

    @Override
    protected String getBackground() {
        return "guis/energybus.png";
    }

    @Override
    public void initGui() {
        super.initGui();

        int x = this.guiLeft + this.xSize - 82;

        for (Slot s : this.inventorySlots.inventorySlots) {
            if (s.getStack().getItem() instanceof ToolNetworkTool) {
                x = this.guiLeft + this.xSize - 118;
            }
        }

        this.buttonList.add(this.setupBtn = new OptionSideButton(
                x,
                this.guiTop,
                StateSprite.QUARTZ_WRENCH,
                CrazyAEGuiText.OPEN_ENERGY_SETTINGS_PAGE.getLocal(),
                "",
                this.itemRender,
                this.getGuiHue(),
                this.getTextHue(),
                0,
                OptionSideButton.ButtonType.ONLY_ICON
        ));
    }

    @Override protected void addButtons() {}

    @Override
    protected void actionPerformed(final GuiButton btn) throws IOException {
        super.actionPerformed(btn);

        if (btn == this.setupBtn) {
            NetworkHandler.instance().sendToServer(new PacketSwitchGuis(CrazyAEGuiBridge.GUI_ENERGY_BUS_SETTINGS));
        }
    }
}
