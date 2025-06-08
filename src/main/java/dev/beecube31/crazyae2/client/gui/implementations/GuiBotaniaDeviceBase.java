package dev.beecube31.crazyae2.client.gui.implementations;

import appeng.core.localization.ButtonToolTips;
import appeng.items.tools.ToolNetworkTool;
import dev.beecube31.crazyae2.client.gui.sprites.StateSprite;
import dev.beecube31.crazyae2.client.gui.widgets.OptionSideButton;
import dev.beecube31.crazyae2.client.gui.widgets.StaticImageButton;
import dev.beecube31.crazyae2.common.containers.ContainerMechanicalBotaniaTileBase;
import dev.beecube31.crazyae2.common.enums.BotaniaMechanicalDeviceType;
import dev.beecube31.crazyae2.common.networking.network.NetworkHandler;
import dev.beecube31.crazyae2.common.networking.packets.PacketSwitchGuis;
import dev.beecube31.crazyae2.common.networking.packets.PacketToggleGuiObject;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiBridge;
import dev.beecube31.crazyae2.common.i18n.CrazyAEGuiText;
import dev.beecube31.crazyae2.common.tile.botania.TileBotaniaMechanicalMachineBase;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

import java.io.IOException;

public abstract class GuiBotaniaDeviceBase extends GuiCrazyAEUpgradeable {

    protected StaticImageButton encodeBtn;
    protected OptionSideButton patternInvBtn;

    protected final TileBotaniaMechanicalMachineBase tile;
    protected final BotaniaMechanicalDeviceType type;
    protected final ContainerMechanicalBotaniaTileBase container;

    public GuiBotaniaDeviceBase(final InventoryPlayer inventoryPlayer, final TileBotaniaMechanicalMachineBase te, final ContainerMechanicalBotaniaTileBase container) {
        super(container);
        this.container = (ContainerMechanicalBotaniaTileBase)this.inventorySlots;
        this.tile = te;
        this.type = te.getType();
        this.ySize = 210;
        this.xSize += 26;
        this.myOffsetX += 26;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.add(this.encodeBtn = new StaticImageButton(
                this.guiLeft + 90,
                this.guiTop + (this.type == BotaniaMechanicalDeviceType.BREWERY ? 108 : 106),
                StateSprite.WHITE_ARROW_DOWN,
                ButtonToolTips.Encode.getLocal(),
                this.getGuiHue(),
                123
        ));

        int x = this.guiLeft + this.xSize - 63;

        for (Slot s : this.inventorySlots.inventorySlots) {
            if (s.getStack().getItem() instanceof ToolNetworkTool) {
                x = this.guiLeft + this.xSize - 98;
            }
        }

        this.buttonList.add(this.patternInvBtn = new OptionSideButton(
                x,
                this.guiTop,
                this.type == BotaniaMechanicalDeviceType.ELVENTRADE ? StateSprite.ELVENTRADE_ENCODED_PATTERN
                        : this.type == BotaniaMechanicalDeviceType.MANAPOOL ? StateSprite.MANAPOOL_ENCODED_PATTERN
                        : this.type == BotaniaMechanicalDeviceType.PETAL ? StateSprite.PETAL_ENCODED_PATTERN
                        : this.type == BotaniaMechanicalDeviceType.PUREDAISY ? StateSprite.PUREDAISY_ENCODED_PATTERN
                        : this.type == BotaniaMechanicalDeviceType.RUNEALTAR ? StateSprite.RUNEALTAR_ENCODED_PATTERN
                        : this.type == BotaniaMechanicalDeviceType.BREWERY ? StateSprite.BREWERY_ENCODED_PATTERN
                        : this.type == BotaniaMechanicalDeviceType.TERAPLATE ? StateSprite.TERAPLATE_ENCODED_PATTERN
                        : StateSprite.CHECKBOX_OFF,
                CrazyAEGuiText.OPEN_PATTERN_STORAGE.getLocal(),
                "",
                this.itemRender,
                this.getGuiHue(),
                this.getTextHue(),
                0,
                OptionSideButton.ButtonType.DEFAULT
        ));
    }

    @Override protected void addButtons() {}

    @Override protected abstract String getBackground();

    @Override
    protected void actionPerformed(final GuiButton btn) throws IOException {
        super.actionPerformed(btn);

        if (btn == this.encodeBtn) {
            NetworkHandler.instance().sendToServer(new PacketToggleGuiObject("CRAZYAE.GUI.encodeBtn.pressed", "elventrade"));
        }

        if (btn == this.patternInvBtn && this.type != null) {
            NetworkHandler.instance().sendToServer(new PacketSwitchGuis(CrazyAEGuiBridge.GUI_MECHANICAL_DEVICE_PATTERN_INV));
        }
    }
}
