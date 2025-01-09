package dev.beecube31.crazyae2.client.gui.implementations;

import appeng.api.config.SortDir;
import appeng.api.config.SortOrder;
import appeng.api.config.ViewItems;
import appeng.api.storage.ITerminalHost;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.IConfigManager;
import appeng.client.gui.widgets.*;
import appeng.core.localization.GuiText;
import appeng.util.IConfigManagerHost;
import dev.beecube31.crazyae2.client.gui.CrazyAEBaseGui;
import dev.beecube31.crazyae2.client.gui.CrazyAEBaseMEGui;
import dev.beecube31.crazyae2.client.gui.widgets.Scrollbar;
import dev.beecube31.crazyae2.client.me.ManaRepo;
import dev.beecube31.crazyae2.common.containers.ContainerManaTerminal;
import dev.beecube31.crazyae2.common.containers.base.slot.InternalManaSlotME;
import dev.beecube31.crazyae2.common.containers.base.slot.SlotManaME;
import dev.beecube31.crazyae2.common.interfaces.gui.IGuiElementsCallbackHandler;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiText;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiTooltip;
import dev.beecube31.crazyae2.core.client.CrazyAEClientConfig;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class GuiManaTerminal extends CrazyAEBaseMEGui implements ISortSource, IConfigManagerHost, IGuiElementsCallbackHandler {
    private final List<SlotManaME> meManaSlots = new LinkedList<>();
    private final ManaRepo repo;
    private final ContainerManaTerminal container;
    private final int rows = 6;
    private final int perRow = 9;

    protected ITerminalHost terminal;

    public GuiManaTerminal(final InventoryPlayer inventoryPlayer, final ITerminalHost te) {
        this(inventoryPlayer, te, new ContainerManaTerminal(inventoryPlayer, te));
    }

    public GuiManaTerminal(InventoryPlayer inventoryPlayer, final ITerminalHost te, final ContainerManaTerminal c) {
        super(c);
        this.terminal = te;
        this.xSize = 185;
        this.ySize = 222;
        final Scrollbar scrollbar = new Scrollbar(this.getGuiHue(), this);
        this.registerScrollSrc(scrollbar);
        this.repo = new ManaRepo(scrollbar);
        (this.container = (ContainerManaTerminal) this.inventorySlots).setGui(this);
    }

    @Override
    public void initGui() {
        this.mc.player.openContainer = this.inventorySlots;
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        for (int y = 0; y < this.rows; y++) {
            for (int x = 0; x < this.perRow; x++) {
                int offsetX = 9;
                SlotManaME slot = new SlotManaME(new InternalManaSlotME(this.repo, x + y * this.perRow, offsetX + x * 18, 18 + y * 18));
                this.getMeManaSlots().add(slot);
                this.inventorySlots.inventorySlots.add(slot);
            }
        }
        this.setScrollBar();

        this.getGuiHue().setParams(
                (float) CrazyAEClientConfig.getColorizerColorRed() / 255,
                (float) CrazyAEClientConfig.getColorizerColorGreen() / 255,
                (float) CrazyAEClientConfig.getColorizerColorBlue() / 255,
                1.0F
        );

        this.getTextHue().setParams(
                (float) CrazyAEClientConfig.getColorizerTextColorRed() / 255,
                (float) CrazyAEClientConfig.getColorizerTextColorGreen() / 255,
                (float) CrazyAEClientConfig.getColorizerTextColorBlue() / 255,
                1.0F
        );
    }

    @Override
    public void drawFG(int offsetX, int offsetY, int mouseX, int mouseY) {
        this.drawString(CrazyAEGuiText.MANA_TERMINAL.getLocal(), 8, 6);
        this.drawString(GuiText.inventory.getLocal(), 8, this.ySize - 96 + 3);
        this.getTextHue().endDrawHue();
    }

    @Override
    public void drawBG(int offsetX, int offsetY, int mouseX, int mouseY) {
        super.drawBG(offsetX, offsetY, mouseX, mouseY);
        this.bindTexture(this.getBackground());
        final int x_width = 197;
        this.drawTexturedModalRect(offsetX, offsetY, 0, 0, x_width, 18);

        for (int x = 0; x < 6; x++) {
            this.drawTexturedModalRect(offsetX, offsetY + 18 + x * 18, 0, 18, x_width, 18);
        }

        this.drawTexturedModalRect(offsetX, offsetY + 16 + 6 * 18, 0, 106 - 18 - 18, x_width, 99 + 77);
    }

    @Override
    public void updateScreen() {
        this.repo.setPower(this.container.isPowered());
        super.updateScreen();
    }

    @Override
    protected void renderHoveredToolTip(int mouseX, int mouseY) {
        final Slot slot = this.getSlot(mouseX, mouseY);

        if (slot instanceof final SlotManaME itemSlot && slot.isEnabled()) {

            IAEItemStack myStack = null;

            try {
                myStack = itemSlot.getAEStack();
            } catch (final Throwable ignore) {
            }

            if (myStack != null) {
                final List<String> list = new ArrayList<>();

                list.add(CrazyAEGuiTooltip.MANA.getLocal());
                list.add(String.format(
                        CrazyAEGuiTooltip.STORED_MANA.getLocal(),
                        NumberFormat.getNumberInstance(Locale.US).format(myStack.getStackSize()))
                );

                this.drawHoveringText(list, mouseX, mouseY);

                return;
            }
        }
        super.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void handleMouseClick(Slot slot, int slotIdx, int mouseButton, ClickType clickType) {
        //NO-OP
    }

    public void postUpdate(final List<IAEItemStack> list) {
        for (final IAEItemStack is : list) {
            this.repo.postUpdate(is);
        }

        this.repo.updateView();
        this.setScrollBar();
    }

    private void setScrollBar() {
        if (this.getScrollSrcList().stream().findFirst().get() instanceof Scrollbar s) {
            s.setTop(18).setLeft(175).setHeight(this.rows * 18 - 2);
            s.setRange(0, 0, 0);
        }
    }

    @Override
    public Enum<SortOrder> getSortBy() {
        return SortOrder.NAME;
    }

    @Override
    public Enum<SortDir> getSortDir() {
        return SortDir.ASCENDING;
    }

    @Override
    public Enum<ViewItems> getSortDisplay() {
        return ViewItems.ALL;
    }

    @Override
    public void updateSetting(IConfigManager manager, Enum settingName, Enum newValue) {
        this.repo.updateView();
    }

    protected List<SlotManaME> getMeManaSlots() {
        return this.meManaSlots;
    }

    @Override
    protected boolean isPowered() {
        return this.repo.hasPower();
    }

    protected String getBackground() {
        return "guis/terminal.png";
    }

    @Override
    public void onInteractionStart() {}

    @Override
    public void onInteractionUpdate() {}

    @Override
    public void onInteractionEnd() {}

    @Override
    public CrazyAEBaseGui getCallbackHandler() {
        return this;
    }
}
