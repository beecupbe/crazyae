package dev.beecube31.crazyae2.client.gui.implementations;

import appeng.api.config.LockCraftingMode;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.client.gui.Icon;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ToggleButton;
import appeng.core.localization.GuiText;
import dev.beecube31.crazyae2.client.gui.CrazyAEBaseGui;
import dev.beecube31.crazyae2.client.gui.widgets.PatternProviderLockReason;
import dev.beecube31.crazyae2.client.gui.widgets.buttons.CrazyServerSettingToggleButton;
import dev.beecube31.crazyae2.common.containers.implementations.ContainerPatternProvider;
import dev.beecube31.crazyae2.common.network.CrazyNetworkHandler;
import dev.beecube31.crazyae2.common.network.packets.PacketConfigButton;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GuiPatternProvider<C extends ContainerPatternProvider> extends CrazyAEBaseGui<C> {

    private final CrazyServerSettingToggleButton<YesNo> blockingModeButton;
    private final CrazyServerSettingToggleButton<LockCraftingMode> lockCraftingModeButton;
    private final ToggleButton showInPatternAccessTerminalButton;
    private final PatternProviderLockReason lockReason;

    public GuiPatternProvider(C menu, Inventory playerInventory, Component title,
                              ScreenStyle style) {
        super(menu, playerInventory, title, style);

        this.blockingModeButton = new CrazyServerSettingToggleButton<>(Settings.BLOCKING_MODE, YesNo.NO, this.getGuiHue());
        this.addToLeftToolbar(this.blockingModeButton);

        lockCraftingModeButton = new CrazyServerSettingToggleButton<>(Settings.LOCK_CRAFTING_MODE, LockCraftingMode.NONE, this.getGuiHue());
        this.addToLeftToolbar(lockCraftingModeButton);

        widgets.addOpenPriorityButton();

        this.showInPatternAccessTerminalButton = new ToggleButton(Icon.PATTERN_ACCESS_SHOW,
                Icon.PATTERN_ACCESS_HIDE,
                GuiText.PatternAccessTerminal.text(), GuiText.PatternAccessTerminalHint.text(),
                btn -> selectNextPatternProviderMode());
        this.addToLeftToolbar(this.showInPatternAccessTerminalButton);

        this.lockReason = new PatternProviderLockReason(this);
        widgets.add("lockReason", this.lockReason);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        this.lockReason.setVisible(menu.getLockCraftingMode() != LockCraftingMode.NONE);
        this.blockingModeButton.set(this.menu.getBlockingMode());
        this.lockCraftingModeButton.set(this.menu.getLockCraftingMode());
        this.showInPatternAccessTerminalButton.setState(this.menu.getShowInAccessTerminal() == YesNo.YES);
    }

    private void selectNextPatternProviderMode() {
        final boolean backwards = isHandlingRightClick();
        CrazyNetworkHandler.instance().sendToServer(new PacketConfigButton(Settings.PATTERN_ACCESS_TERMINAL, backwards));
    }
}
