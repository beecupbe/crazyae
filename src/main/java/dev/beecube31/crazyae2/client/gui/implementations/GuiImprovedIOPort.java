package dev.beecube31.crazyae2.client.gui.implementations;

import appeng.api.config.FullnessMode;
import appeng.api.config.OperationMode;
import appeng.api.config.RedstoneMode;
import appeng.api.config.Settings;
import appeng.client.gui.style.ScreenStyle;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import dev.beecube31.crazyae2.client.gui.sprite.StateSprite;
import dev.beecube31.crazyae2.client.gui.widgets.buttons.CrazyServerSettingToggleButton;
import dev.beecube31.crazyae2.common.containers.implementations.ContainerImprovedIOPort;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GuiImprovedIOPort extends GuiCrazyUpgradeable<ContainerImprovedIOPort> {
    private final CrazyServerSettingToggleButton<FullnessMode> fullMode;
    private final CrazyServerSettingToggleButton<OperationMode> operationMode;
    private final CrazyServerSettingToggleButton<RedstoneMode> redstoneMode;

    public GuiImprovedIOPort(ContainerImprovedIOPort menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        this.fullMode = new CrazyServerSettingToggleButton<>(Settings.FULLNESS_MODE, FullnessMode.EMPTY, this.getGuiHue(), StateSprite.IMAGE_BUTTON_CENTER);
        addToLeftToolbar(this.fullMode);
        this.redstoneMode = new CrazyServerSettingToggleButton<>(Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE, this.getGuiHue(), StateSprite.IMAGE_BUTTON_CENTER);
        addToLeftToolbar(this.redstoneMode);

        this.operationMode = new CrazyServerSettingToggleButton<>(Settings.OPERATION_MODE, OperationMode.EMPTY, this.getGuiHue(), StateSprite.IMAGE_BUTTON);
        this.widgets.add("operationMode", this.operationMode);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        this.redstoneMode.set(this.menu.getRedStoneMode());
        this.redstoneMode.setVisibility(menu.hasUpgrade(AEItems.REDSTONE_CARD));
        this.operationMode.set(this.menu.getOperationMode());
        this.fullMode.set(this.menu.getFullMode());
    }

    @Override
    public void drawBG(GuiGraphicsExtractor guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
        super.drawBG(guiGraphics, offsetX, offsetY, mouseX, mouseY, partialTicks);

        this.drawItem(guiGraphics, offsetX + 66 - 5, offsetY + 17, AEItems.ITEM_CELL_256K.stack());
        this.drawItem(guiGraphics, offsetX + 94 + 5, offsetY + 17, AEBlocks.DRIVE.stack());
    }
}
