package dev.beecube31.crazyae2.client.gui.implementations;

import appeng.api.config.SearchBoxMode;
import appeng.api.config.Settings;
import appeng.api.config.TerminalStyle;
import appeng.api.implementations.guiobjects.IPortableCell;
import appeng.api.implementations.tiles.IViewCellStorage;
import appeng.api.storage.ITerminalHost;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.IConfigManager;
import appeng.api.util.IConfigurableObject;
import appeng.client.ActionKey;
import appeng.client.gui.widgets.GuiImgButton;
import appeng.client.gui.widgets.GuiTabButton;
import appeng.client.gui.widgets.ISortSource;
import appeng.client.gui.widgets.MEGuiTextField;
import appeng.client.me.SlotME;
import appeng.container.implementations.ContainerMEMonitorable;
import appeng.container.slot.AppEngSlot;
import appeng.container.slot.SlotCraftingMatrix;
import appeng.container.slot.SlotFakeCraftingMatrix;
import appeng.core.AEConfig;
import appeng.core.AELog;
import appeng.core.AppEng;
import appeng.core.localization.GuiText;
import appeng.core.sync.GuiBridge;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.PacketSwitchGuis;
import appeng.core.sync.packets.PacketValueConfig;
import appeng.integration.Integrations;
import appeng.util.IConfigManagerHost;
import appeng.util.Platform;
import dev.beecube31.crazyae2.client.gui.CrazyAEBaseGui;
import dev.beecube31.crazyae2.client.gui.CrazyAEInternalMESlot;
import dev.beecube31.crazyae2.client.gui.widgets.Scrollbar;
import dev.beecube31.crazyae2.common.i18n.CrazyAEGuiText;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiMEMonitorableColorizeable extends CrazyAEBaseGui implements ISortSource, IConfigManagerHost {

    private static int craftingGridOffsetX;
    private static int craftingGridOffsetY;

    private static String memoryText = "";
    protected final dev.beecube31.crazyae2.client.me.ItemRepo repo;
    private final int offsetX = 9;
    private final int lowerTextureOffset = 0;
    private final IConfigManager configSrc;
    private final boolean viewCell;
    private final ItemStack[] myCurrentViewCells = new ItemStack[5];
    private final ContainerMEMonitorable monitorableContainer;
    private GuiTabButton craftingStatusBtn;
    private MEGuiTextField searchField;
    private CrazyAEGuiText myName;
    private int perRow = 9;
    private int reservedSpace = 0;
    private boolean customSortOrder = true;
    private int rows = 0;
    private int maxRows = Integer.MAX_VALUE;
    private int standardSize;
    private GuiImgButton ViewBox;
    private GuiImgButton SortByBox;
    private GuiImgButton SortDirBox;
    private GuiImgButton searchBoxSettings;
    private GuiImgButton terminalStyleBox;
    private boolean isAutoFocus = false;
    private int currentMouseX = 0;
    private int currentMouseY = 0;
    private boolean delayedUpdate;

    protected int jeiOffset = Loader.isModLoaded("jei") ? 24 : 0;

    public GuiMEMonitorableColorizeable(final InventoryPlayer inventoryPlayer, final ITerminalHost te) {
        this(inventoryPlayer, te, new ContainerMEMonitorable(inventoryPlayer, te));
    }

    public GuiMEMonitorableColorizeable(final InventoryPlayer inventoryPlayer, final ITerminalHost te, final ContainerMEMonitorable c) {

        super(c);

        final dev.beecube31.crazyae2.client.gui.widgets.Scrollbar scrollbar = new dev.beecube31.crazyae2.client.gui.widgets.Scrollbar(this.getGuiHue(), null);
        this.registerScrollSrc(scrollbar);
        this.repo = new dev.beecube31.crazyae2.client.me.ItemRepo(scrollbar, this);

        this.xSize = 185;
        this.ySize = 204;

        if (te instanceof IViewCellStorage) {
            this.xSize += 33;
        }

        this.standardSize = this.xSize;

        this.configSrc = ((IConfigurableObject) this.inventorySlots).getConfigManager();
        (this.monitorableContainer = (ContainerMEMonitorable) this.inventorySlots).setGui(this);

        this.viewCell = te instanceof IViewCellStorage;

        if (te instanceof IPortableCell) {
            this.myName = CrazyAEGuiText.DENSE_PORTABLE_CELL;
        }
    }

    public void postUpdate(final List<IAEItemStack> list) {
        for (final IAEItemStack is : list) {
            this.repo.postUpdate(is);
        }

        if (isShiftKeyDown()) {
            for (Slot slot : this.inventorySlots.inventorySlots) {
                if (slot instanceof SlotME) {
                    if (this.isPointInRegion(slot.xPos, slot.yPos, 18, 18, currentMouseX, currentMouseY)) {
                        this.delayedUpdate = true;
                        break;
                    }
                }
            }
        }

        if (!this.delayedUpdate) {
            this.repo.updateView();
            this.setScrollBar();
        }
    }

    private void setScrollBar() {
        ((Scrollbar) this.getScrollSrcList().get(0)).setTop(18).setLeft(175).setHeight(this.rows * 18 - 2);
        ((Scrollbar) this.getScrollSrcList().get(0)).setRange(0, (this.repo.size() + this.perRow - 1) / this.perRow - this.rows, Math.max(1, this.rows / 6));
    }

    @Override
    protected void actionPerformed(final @NotNull GuiButton btn) {
        if (btn == this.craftingStatusBtn) {
            NetworkHandler.instance().sendToServer(new PacketSwitchGuis(GuiBridge.GUI_CRAFTING_STATUS));
        }

        if (btn instanceof GuiImgButton iBtn) {
            final boolean backwards = Mouse.isButtonDown(1);

            if (iBtn.getSetting() != Settings.ACTIONS) {
                final Enum<?> cv = iBtn.getCurrentValue();
                final Enum<?> next = Platform.rotateEnum(cv, backwards, iBtn.getSetting().getPossibleValues());

                if (btn == this.terminalStyleBox) {
                    AEConfig.instance().getConfigManager().putSetting(iBtn.getSetting(), next);
                } else if (btn == this.searchBoxSettings) {
                    AEConfig.instance().getConfigManager().putSetting(iBtn.getSetting(), next);
                } else {
                    try {
                        NetworkHandler.instance().sendToServer(new PacketValueConfig(iBtn.getSetting().name(), next.name()));
                    } catch (final IOException e) {
                        AELog.debug(e);
                    }
                }

                iBtn.set(next);

                if (next.getClass() == SearchBoxMode.class || next.getClass() == TerminalStyle.class) {
                    this.reinitalize();
                }
            }
        }
    }

    private void reinitalize() {
        this.buttonList.clear();
        this.initGui();
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        this.maxRows = this.getMaxRows();
        this.perRow = AEConfig.instance()
                .getConfigManager()
                .getSetting(
                        Settings.TERMINAL_STYLE) != TerminalStyle.FULL ? 9 : 9 + ((this.width - this.standardSize) / 18);

        final int magicNumber = 114 + 1;
        final int extraSpace = this.height - magicNumber - this.reservedSpace;

        this.rows = (int) Math.floor(extraSpace / 18D);
        if (this.rows > this.maxRows) {
            this.rows = this.maxRows;
        }

        if (this.rows < 3) {
            this.rows = 3;
        }

        this.getMeSlots().clear();
        for (int y = 0; y < this.rows; y++) {
            for (int x = 0; x < this.perRow; x++) {
                this.getMeSlots().add(new CrazyAEInternalMESlot(this.repo, x + y * this.perRow, this.offsetX + x * 18, 18 + y * 18));
            }
        }

        if (AEConfig.instance().getConfigManager().getSetting(Settings.TERMINAL_STYLE) != TerminalStyle.FULL) {
            this.xSize = this.standardSize + ((this.perRow - 9) * 18);
        } else {
            this.xSize = this.standardSize;
        }

        super.initGui();

        this.ySize = magicNumber + this.rows * 18 + this.reservedSpace;
        final int unusedSpace = this.height - this.ySize;
        this.guiTop = (int) Math.floor(unusedSpace / (unusedSpace < 0 ? 3.8f : 2.0f));

        int offset = this.guiTop + 8 + jeiOffset;

        {
            if (this.customSortOrder) {
                this.buttonList
                        .add(this.SortByBox = new GuiImgButton(this.guiLeft - 18, offset, Settings.SORT_BY, this.configSrc.getSetting(Settings.SORT_BY)));
                offset += 20;
            }
        }

        if (this.viewCell) {
            this.buttonList
                    .add(this.ViewBox = new GuiImgButton(this.guiLeft - 18, offset, Settings.VIEW_MODE, this.configSrc.getSetting(Settings.VIEW_MODE)));
            offset += 20;
        }

        this.buttonList.add(this.SortDirBox = new GuiImgButton(this.guiLeft - 18, offset, Settings.SORT_DIRECTION, this.configSrc
                .getSetting(Settings.SORT_DIRECTION)));
        offset += 20;

        this.buttonList.add(
                this.searchBoxSettings = new GuiImgButton(this.guiLeft - 18, offset, Settings.SEARCH_MODE, AEConfig.instance()
                        .getConfigManager()
                        .getSetting(
                                Settings.SEARCH_MODE)));

        offset += 20;

        this.buttonList.add(this.terminalStyleBox = new GuiImgButton(this.guiLeft - 18, offset, Settings.TERMINAL_STYLE, AEConfig.instance()
                .getConfigManager()
                .getSetting(Settings.TERMINAL_STYLE)));

        this.searchField = new MEGuiTextField(this.fontRenderer, this.guiLeft + 80, this.guiTop + 4, 90, 12);
        this.searchField.setEnableBackgroundDrawing(false);
        this.searchField.setMaxStringLength(256);
        this.searchField.setTextColor(0xFFFFFF);
        this.searchField.setSelectionColor(0xFF008000);
        this.searchField.setVisible(true);

        if (this.viewCell) {
            this.buttonList.add(this.craftingStatusBtn = new GuiTabButton(this.guiLeft + 170, this.guiTop - 4, 2 + 11 * 16, GuiText.CraftingStatus
                    .getLocal(), this.itemRender));
            this.craftingStatusBtn.setHideEdge(13);
        }

        final Enum<?> searchModeSetting = AEConfig.instance().getConfigManager().getSetting(Settings.SEARCH_MODE);

        this.isAutoFocus = SearchBoxMode.AUTOSEARCH == searchModeSetting || SearchBoxMode.JEI_AUTOSEARCH == searchModeSetting || SearchBoxMode.AUTOSEARCH_KEEP == searchModeSetting || SearchBoxMode.JEI_AUTOSEARCH_KEEP == searchModeSetting;
        final boolean isKeepFilter = SearchBoxMode.AUTOSEARCH_KEEP == searchModeSetting || SearchBoxMode.JEI_AUTOSEARCH_KEEP == searchModeSetting || SearchBoxMode.MANUAL_SEARCH_KEEP == searchModeSetting || SearchBoxMode.JEI_MANUAL_SEARCH_KEEP == searchModeSetting;
        final boolean isJEIEnabled = SearchBoxMode.JEI_AUTOSEARCH == searchModeSetting || SearchBoxMode.JEI_MANUAL_SEARCH == searchModeSetting;

        this.searchField.setFocused(this.isAutoFocus);

        if (isJEIEnabled) {
            memoryText = Integrations.jei().getSearchText();
        }

        if (isKeepFilter && memoryText != null && !memoryText.isEmpty()) {
            this.searchField.setText(memoryText);
            this.searchField.selectAll();
            this.repo.setSearchString(memoryText);
            this.setScrollBar();
        }

        craftingGridOffsetX = Integer.MAX_VALUE;
        craftingGridOffsetY = Integer.MAX_VALUE;

        for (final Slot s : this.inventorySlots.inventorySlots) {
            if (s instanceof AppEngSlot) {
                if (s.xPos < 197) {
                    this.repositionSlot((AppEngSlot) s);
                }
            }

            if (s instanceof SlotCraftingMatrix || s instanceof SlotFakeCraftingMatrix) {
                if (s.xPos > 0 && s.yPos > 0) {
                    craftingGridOffsetX = Math.min(craftingGridOffsetX, s.xPos);
                    craftingGridOffsetY = Math.min(craftingGridOffsetY, s.yPos);
                }
            }
        }

        craftingGridOffsetX -= 25;
        craftingGridOffsetY -= 6;

    }

    @Override
    public List<Rectangle> getJEIExclusionArea() {
        List<Rectangle> exclusionArea = new ArrayList<>();

        int yOffset = guiTop + 8 + jeiOffset;

        int visibleButtons = (int) this.buttonList.stream().filter(v -> v.enabled && v.x < guiLeft).count();
        Rectangle sortDir = new Rectangle(guiLeft - 18, yOffset, 20, visibleButtons * 20 + visibleButtons - 2);
        exclusionArea.add(sortDir);

        if (this.viewCell) {
            Rectangle viewMode = new Rectangle(guiLeft + 205, yOffset - 4, 24, 19 * monitorableContainer.getViewCells().length);
            exclusionArea.add(viewMode);
        }

        return exclusionArea;
    }

    @Override
    public void drawFG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        this.fontRenderer.drawString(this.getGuiDisplayName(this.myName.getLocal()), 8, 6, 4210752);
        this.fontRenderer.drawString(GuiText.inventory.getLocal(), 8, this.ySize - 96 + 3, 4210752);

        this.currentMouseX = mouseX;
        this.currentMouseY = mouseY;
    }

    @Override
    protected void mouseClicked(final int xCoord, final int yCoord, final int btn) throws IOException {
        this.searchField.mouseClicked(xCoord, yCoord, btn);

        if (btn == 1 && this.searchField.isMouseIn(xCoord, yCoord)) {
            this.searchField.setText("");
            this.repo.setSearchString("");
            this.setScrollBar();
        }

        super.mouseClicked(xCoord, yCoord, btn);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
        memoryText = this.searchField.getText();
    }

    @Override
    public void drawBG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        super.drawBG(offsetX, offsetY, mouseX, mouseY);

        this.bindTexture(this.getBackground());
        final int x_width = 197;
        this.drawTexturedModalRect(offsetX, offsetY, 0, 0, x_width, 18);

        if (this.viewCell) {
            this.drawTexturedModalRect(offsetX + x_width, offsetY + jeiOffset, x_width, 0, 46, 128);
        }

        for (int x = 0; x < this.rows; x++) {
            this.drawTexturedModalRect(offsetX, offsetY + 18 + x * 18, 0, 18, x_width, 18);
        }

        this.drawTexturedModalRect(offsetX, offsetY + 16 + this.rows * 18 + this.lowerTextureOffset, 0, 106 - 18 - 18, x_width,
                99 + this.reservedSpace - this.lowerTextureOffset);

        if (this.viewCell) {
            boolean update = false;

            for (int i = 0; i < 5; i++) {
                if (this.myCurrentViewCells[i] != this.monitorableContainer.getCellViewSlot(i).getStack()) {
                    update = true;
                    this.myCurrentViewCells[i] = this.monitorableContainer.getCellViewSlot(i).getStack();
                }
            }

            if (update) {
                this.repo.setViewCell(this.myCurrentViewCells);
            }
        }

        if (this.searchField != null) {
            this.searchField.drawTextBox();
        }
    }

    protected String getBackground() {
        return "guis/terminal.png";
    }

    @Override
    protected boolean isPowered() {
        return this.repo.hasPower();
    }

    protected int getMaxRows() {
        return AEConfig.instance().getConfigManager().getSetting(Settings.TERMINAL_STYLE) == TerminalStyle.SMALL ? 6 : Integer.MAX_VALUE;
    }

    protected void repositionSlot(final AppEngSlot s) {
        s.yPos = s.getY() + this.ySize - 78 - 5;
    }

    @Override
    protected void keyTyped(final char character, final int key) throws IOException {

        if (!this.checkHotbarKeys(key)) {
            if (AppEng.proxy.isActionKey(ActionKey.TOGGLE_FOCUS, key)) {
                this.searchField.setFocused(!this.searchField.isFocused());
                return;
            }

            if (this.searchField.isFocused() && key == Keyboard.KEY_RETURN) {
                this.searchField.setFocused(false);
                return;
            }

            if (character == ' ' && this.searchField.getText().isEmpty()) {
                return;
            }

            final boolean mouseInGui = this.isPointInRegion(0, 0, this.xSize, this.ySize, this.currentMouseX, this.currentMouseY);
            final boolean wasSearchFieldFocused = this.searchField.isFocused();

            if (this.isAutoFocus && !this.searchField.isFocused() && mouseInGui) {
                this.searchField.setFocused(true);
            }

            if (this.searchField.textboxKeyTyped(character, key)) {
                this.repo.setSearchString(this.searchField.getText());
                this.setScrollBar();
                // tell forge the key event is handled and should not be sent out
                this.keyHandled = mouseInGui;
            } else {
                if (!wasSearchFieldFocused) {
                    // prevent unhandled keys (like shift) from focusing the search field
                    searchField.setFocused(false);
                }
                super.keyTyped(character, key);
            }
        }
    }

    @Override
    public void updateScreen() {
        this.repo.setPower(this.monitorableContainer.isPowered());
        if (this.delayedUpdate) {
            if (isShiftKeyDown()) {
                this.delayedUpdate = false;
                for (Slot slot : this.inventorySlots.inventorySlots) {
                    if (slot instanceof SlotME) {
                        if (this.isPointInRegion(slot.xPos, slot.yPos, 18, 18, currentMouseX, currentMouseY)) {
                            this.delayedUpdate = true;
                            break;
                        }
                    }
                }
            } else {
                this.delayedUpdate = false;
            }
        }
        if (!this.delayedUpdate) {
            this.repo.updateView();
            this.setScrollBar();
        }
        super.updateScreen();
    }

    @Override
    public Enum getSortBy() {
        return this.configSrc.getSetting(Settings.SORT_BY);
    }

    @Override
    public Enum getSortDir() {
        return this.configSrc.getSetting(Settings.SORT_DIRECTION);
    }

    @Override
    public Enum getSortDisplay() {
        return this.configSrc.getSetting(Settings.VIEW_MODE);
    }

    @Override
    public void updateSetting(final IConfigManager manager, final Enum settingName, final Enum newValue) {
        if (this.SortByBox != null) {
            this.SortByBox.set(this.configSrc.getSetting(Settings.SORT_BY));
        }

        if (this.SortDirBox != null) {
            this.SortDirBox.set(this.configSrc.getSetting(Settings.SORT_DIRECTION));
        }

        if (this.ViewBox != null) {
            this.ViewBox.set(this.configSrc.getSetting(Settings.VIEW_MODE));
        }

        this.repo.updateView();
    }

    int getReservedSpace() {
        return this.reservedSpace;
    }

    void setReservedSpace(final int reservedSpace) {
        this.reservedSpace = reservedSpace;
    }

    public boolean isCustomSortOrder() {
        return this.customSortOrder;
    }

    void setCustomSortOrder(final boolean customSortOrder) {
        this.customSortOrder = customSortOrder;
    }

    public int getStandardSize() {
        return this.standardSize;
    }

    void setStandardSize(final int standardSize) {
        this.standardSize = standardSize;
    }
}
