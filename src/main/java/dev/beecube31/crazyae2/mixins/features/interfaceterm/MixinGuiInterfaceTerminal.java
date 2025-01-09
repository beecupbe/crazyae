package dev.beecube31.crazyae2.mixins.features.interfaceterm;

import appeng.api.config.ActionItems;
import appeng.api.config.Settings;
import appeng.api.config.TerminalStyle;
import appeng.client.gui.AEBaseGui;
import appeng.client.gui.implementations.GuiInterfaceTerminal;
import appeng.client.gui.widgets.GuiImgButton;
import appeng.client.gui.widgets.MEGuiTooltipTextField;
import appeng.client.me.ClientDCInternalInv;
import appeng.client.me.SlotDisconnected;
import appeng.core.AEConfig;
import appeng.core.localization.GuiText;
import appeng.helpers.DualityInterface;
import com.google.common.collect.HashMultimap;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.*;

import java.util.*;

@Mixin(value = GuiInterfaceTerminal.class, remap = false, priority = 990)
public abstract class MixinGuiInterfaceTerminal extends AEBaseGui {

    @Shadow @Final private HashMap<Long, ClientDCInternalInv> byId;

    @Shadow private boolean refreshList;

    @Shadow @Final private Map<String, Set<Object>> cachedSearches;

    @Shadow @Final private HashMultimap<String, ClientDCInternalInv> byName;

    @Shadow @Final private ArrayList<String> names;

    @Shadow @Final private MEGuiTooltipTextField searchFieldInputs;

    @Shadow @Final private MEGuiTooltipTextField searchFieldOutputs;

    @Shadow private int rows;

    @Shadow @Final private ArrayList<Object> lines;

    @Shadow @Final private Map<ClientDCInternalInv, Integer> numUpgradesMap;

    @Shadow @Final private HashMap<ClientDCInternalInv, BlockPos> blockPosHashMap;

    @Shadow @Final private Map<ClientDCInternalInv, Integer> dimHashMap;

    @Shadow @Final private Set<Object> matchedStacks;

    @Shadow @Final private MEGuiTooltipTextField searchFieldNames;

    @Shadow @Final private HashMap<GuiButton, ClientDCInternalInv> guiButtonHashMap;

    @Shadow @Final private GuiImgButton guiButtonAssemblersOnly;

    @Shadow private boolean onlyMolecularAssemblers;

    @Shadow @Final private GuiImgButton guiButtonHideFull;

    @Shadow private boolean onlyShowWithSpace;

    @Shadow private boolean onlyBrokenRecipes;

    @Shadow @Final private GuiImgButton guiButtonBrokenRecipes;

    @Shadow @Final private GuiImgButton terminalStyleBox;

    @Shadow protected abstract boolean recipeIsBroken(ItemStack stack);

    @Shadow protected abstract boolean itemStackMatchesSearchTerm(ItemStack itemStack, String searchTerm, int pass);

    @Shadow @Final private static String MOLECULAR_ASSEMBLER;

    @Shadow protected abstract void setScrollBar();

    @Shadow protected abstract Set<Object> getCacheForSearchTerm(String searchTerm);

    @Unique @Final private final Map<ClientDCInternalInv, Boolean> crazyae$patterns = new HashMap<>();

    @Unique @Final private final Map<ClientDCInternalInv, Boolean> crazyae$macs = new HashMap<>();

    public MixinGuiInterfaceTerminal(Container container) {
        super(container);
    }

    /**
     * @author Beecube31
     * @reason Support Patterns interface
     */
    @Overwrite
    private int getMaxRows() {
        return AEConfig.instance().getConfigManager().getSetting(Settings.TERMINAL_STYLE) != TerminalStyle.TALL ? 8 : Integer.MAX_VALUE;
    }

    /**
     * @author Beecube31
     * @reason Support Patterns interface
     */
    @Override
    @Overwrite
    public void drawFG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {

        this.fontRenderer.drawString(this.getGuiDisplayName(GuiText.InterfaceTerminal.getLocal()), 8, 6, 4210752);
        this.fontRenderer.drawString(GuiText.inventory.getLocal(), 24, this.ySize - 96, 4210752);

        final int currentScroll = this.getScrollBar().getCurrentScroll();

        int offset = 51;
        int linesDraw = 0;

        for (int x = 0; x < rows && linesDraw < rows && currentScroll + x < this.lines.size(); x++) {
            final Object lineObj = this.lines.get(currentScroll + x);
            if (lineObj instanceof ClientDCInternalInv inv) {

                final int extraLines = numUpgradesMap.get(inv);
                boolean isPatternTile = crazyae$patterns.get(inv);
                boolean isMAC = crazyae$macs.get(inv);
                for (int row = 0; row < 1 + (isPatternTile ? 7 : isMAC ? 4 : extraLines) && linesDraw < rows; ++row) {
                    for (int z = 0; z < 9; z++) {
                        if (this.matchedStacks.contains(inv.getInventory().getStackInSlot(z + (row * 9)))) {
                            drawRect(z * 18 + 22, 1 + offset, z * 18 + 22 + 16, 1 + offset + 16, 0x2A00FF00);
                        }
                    }
                    linesDraw++;
                    offset += 18;
                }
            } else if (lineObj instanceof String name) {
                final int rows = this.byName.get(name).size();
                if (rows > 1) {
                    name = name + " (" + rows + ')';
                }

                while (name.length() > 2 && this.fontRenderer.getStringWidth(name) > 158) {
                    name = name.substring(0, name.length() - 1);
                }
                this.fontRenderer.drawString(name, 24, 6 + offset, 4210752);
                linesDraw++;
                offset += 18;
            }
        }
    }

    /**
     * @author Beecube31
     * @reason Support Patterns interface
     */
    @Overwrite
    @Override
    public void drawBG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        this.bindTexture("guis/newinterfaceterminal.png");

        // draw the top portion of the background, above the interface list
        this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, 53);

        for (int x = 0; x < this.rows; x++) {
            // draw the background of the rows in the interface list
            this.drawTexturedModalRect(offsetX, offsetY + 53 + x * 18, 0, 52, this.xSize, 18);
        }

        int offset = 51;
        final int ex = this.getScrollBar().getCurrentScroll();
        int linesDraw = 0;
        for (int x = 0; x < rows && linesDraw < rows && ex + x < this.lines.size(); x++) {
            final Object lineObj = this.lines.get(ex + x);
            if (lineObj instanceof ClientDCInternalInv) {
                GlStateManager.color(1, 1, 1, 1);
                final int width = 9 * 18;

                int extraLines = numUpgradesMap.get(lineObj);
                boolean isPatternTile = crazyae$patterns.get(lineObj);
                boolean isMAC = crazyae$macs.get(lineObj);

                for (int row = 0; row < 1 + (isPatternTile ? 7 : isMAC ? 4 : extraLines) && linesDraw < rows; ++row) {
                    this.drawTexturedModalRect(offsetX + 20, offsetY + offset, 20, 173, width, 18);

                    offset += 18;
                    linesDraw++;
                }
            } else {
                offset += 18;
                linesDraw++;
            }
        }

        // draw the background below the interface list
        this.drawTexturedModalRect(offsetX, offsetY + 50 + this.rows * 18, 0, 158, this.xSize, 99);

        // draw the text boxes
        this.searchFieldInputs.drawTextBox();
        this.searchFieldOutputs.drawTextBox();
        this.searchFieldNames.drawTextBox();
    }

    /**
     * @author Beecube31
     * @reason Support Patterns interface
     */
    @Overwrite
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        buttonList.clear();
        guiButtonHashMap.clear();
        inventorySlots.inventorySlots.removeIf(slot -> slot instanceof SlotDisconnected);

        guiButtonAssemblersOnly.set(onlyMolecularAssemblers ? ActionItems.MOLECULAR_ASSEMBLERS_ON : ActionItems.MOLECULAR_ASSEMBLERS_OFF);
        guiButtonHideFull.set(onlyShowWithSpace ? ActionItems.TOGGLE_SHOW_FULL_INTERFACES_OFF : ActionItems.TOGGLE_SHOW_FULL_INTERFACES_ON);
        guiButtonBrokenRecipes.set(onlyBrokenRecipes ? ActionItems.TOGGLE_SHOW_ONLY_INVALID_PATTERNS_ON : ActionItems.TOGGLE_SHOW_ONLY_INVALID_PATTERNS_OFF);
        terminalStyleBox.set(AEConfig.instance().getConfigManager().getSetting(Settings.TERMINAL_STYLE));

        buttonList.add(guiButtonAssemblersOnly);
        buttonList.add(guiButtonHideFull);
        buttonList.add(guiButtonBrokenRecipes);
        buttonList.add(terminalStyleBox);

        int offset = 51;
        final int currentScroll = this.getScrollBar().getCurrentScroll();
        int linesDraw = 0;

        for (int x = 0; x < rows && linesDraw < rows && currentScroll + x < this.lines.size(); x++) {
            final Object lineObj = this.lines.get(currentScroll + x);
            if (lineObj instanceof ClientDCInternalInv inv) {

                GuiButton guiButton = new GuiImgButton(guiLeft + 4, guiTop + offset + 1, Settings.ACTIONS, ActionItems.HIGHLIGHT_INTERFACE);
                guiButtonHashMap.put(guiButton, inv);
                this.buttonList.add(guiButton);

                int extraLines = numUpgradesMap.get(lineObj);
                boolean isPatternTile = crazyae$patterns.get(lineObj);
                boolean isMAC = crazyae$macs.get(lineObj);

                for (int row = 0; row < 1 + (isPatternTile ? 7 : isMAC ? 4 : extraLines) && linesDraw < rows; ++row) {
                    for (int z = 0; z < 9; z++) {
                        this.inventorySlots.inventorySlots.add(new SlotDisconnected(inv, z + (row * 9), z * 18 + 22, 1 + offset));
                    }
                    linesDraw++;
                    offset += 18;
                }
            } else if (lineObj instanceof String) {
                linesDraw++;
                offset += 18;
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);

        drawTooltip(searchFieldInputs, mouseX, mouseY);
        drawTooltip(searchFieldOutputs, mouseX, mouseY);
        drawTooltip(searchFieldNames, mouseX, mouseY);
    }

    /**
     * @author Beecube31
     * @reason Support Patterns interface
     */
    @Overwrite
    public void postUpdate(final NBTTagCompound in) {

        if (in.getBoolean("clear")) {
            this.byId.clear();
            this.refreshList = true;
        }

        for (final String oKey : in.getKeySet()) {
            if (oKey.startsWith("=")) {
                try {
                    final long id = Long.parseLong(oKey.substring(1), Character.MAX_RADIX);
                    final NBTTagCompound invData = in.getCompoundTag(oKey);
                    final ClientDCInternalInv current = this.crazyae$getById(invData.getBoolean("isPatternInterface"), invData.getBoolean("isMAC"), id, invData.getLong("sortBy"), invData.getString("un"));

                    blockPosHashMap.put(current, NBTUtil.getPosFromTag(invData.getCompoundTag("pos")));
                    dimHashMap.put(current, invData.getInteger("dim"));
                    numUpgradesMap.put(current, invData.getInteger("numUpgrades"));
                    crazyae$patterns.put(current, invData.getBoolean("isPatternInterface"));
                    crazyae$macs.put(current, invData.getBoolean("isMAC"));

                    for (int x = 0; x < current.getInventory().getSlots(); x++) {
                        final String which = Integer.toString(x);
                        if (invData.hasKey(which)) {
                            current.getInventory().setStackInSlot(x, new ItemStack(invData.getCompoundTag(which)));
                        }
                    }
                } catch (final NumberFormatException ignored) {}
            }
        }

        if (this.refreshList) {
            this.refreshList = false;
            this.cachedSearches.clear();
            this.refreshList();
        }
    }

    /**
     * @author Beecube31
     * @reason Support Patterns interface
     */
    @Overwrite
    private void refreshList() {
        this.byName.clear();
        this.buttonList.clear();
        this.matchedStacks.clear();

        final String searchFieldInputs = this.searchFieldInputs.getText().toLowerCase();
        final String searchFieldOutputs = this.searchFieldOutputs.getText().toLowerCase();
        final String searchFieldNames = this.searchFieldNames.getText().toLowerCase();

        final Set<Object> cachedSearch = this.getCacheForSearchTerm("IN:" + searchFieldInputs + " OUT:" + searchFieldOutputs
                + "NAME:" + searchFieldNames + onlyShowWithSpace + onlyMolecularAssemblers + onlyBrokenRecipes);
        final boolean rebuild = cachedSearch.isEmpty();

        for (final ClientDCInternalInv entry : this.byId.values()) {
            // ignore inventory if not doing a full rebuild and cache already marks it as miss.
            if (!rebuild && !cachedSearch.contains(entry)) {
                continue;
            }

            // Shortcut to skip any filter if search term is ""/empty

            boolean found = searchFieldInputs.isEmpty() && searchFieldOutputs.isEmpty();
            boolean interfaceHasFreeSlots = false;
            boolean interfaceHasBrokenRecipes = false;

            // Search if the current inventory holds a pattern containing the search term.
            if (!found || onlyShowWithSpace || onlyBrokenRecipes) {
                int slot = 0;
                for (final ItemStack itemStack : entry.getInventory()) {
                    if (crazyae$patterns.get(entry) ? slot > 72 : crazyae$macs.get(entry) ? slot > 45 : slot > 8 + numUpgradesMap.get(entry) * 9) {
                        break;
                    }

                    if (itemStack.isEmpty()) {
                        interfaceHasFreeSlots = true;
                    }

                    if (onlyBrokenRecipes && recipeIsBroken(itemStack)) {
                        interfaceHasBrokenRecipes = true;
                    }

                    if ((!searchFieldInputs.isEmpty() && itemStackMatchesSearchTerm(itemStack, searchFieldInputs, 0))
                            || (!searchFieldOutputs.isEmpty() && itemStackMatchesSearchTerm(itemStack, searchFieldOutputs, 1))) {
                        found = true;
                        matchedStacks.add(itemStack);
                    }

                    slot++;
                }
            }

            // Exit if not found
            if (!found) {
                cachedSearch.remove(entry);
                continue;
            }
            // Exit if the interface does not match the name search
            if (!entry.getName().toLowerCase().contains(searchFieldNames)) {
                cachedSearch.remove(entry);
                continue;
            }
            // Exit if molecular assembler filter is on and this is not a molecular assembler
            if (onlyMolecularAssemblers && !entry.getName().toLowerCase().contains(MOLECULAR_ASSEMBLER)) {
                cachedSearch.remove(entry);
                continue;
            }
            // Exit if we are only showing interfaces with free slots and there are none free in this interface
            if (onlyShowWithSpace && !interfaceHasFreeSlots) {
                cachedSearch.remove(entry);
                continue;
            }
            // Exit if we are only showing interfaces with broken patterns and there are no broken patterns in this interface
            if (onlyBrokenRecipes && !interfaceHasBrokenRecipes) {
                cachedSearch.remove(entry);
                continue;
            }

            // Successful search
            this.byName.put(entry.getName(), entry);
            cachedSearch.add(entry);
        }

        this.names.clear();
        this.names.addAll(this.byName.keySet());
        Collections.sort(this.names);

        this.lines.clear();
        this.lines.ensureCapacity(this.names.size() + this.byId.size());

        for (final String n : this.names) {
            this.lines.add(n);
            final ArrayList<ClientDCInternalInv> clientInventories = new ArrayList<>(this.byName.get(n));
            Collections.sort(clientInventories);
            this.lines.addAll(clientInventories);
        }

        this.setScrollBar();
    }

    @Unique
    private ClientDCInternalInv crazyae$getById(final boolean isPatternInterface, final boolean isMAC, final long id, final long sortBy, final String string) {
        ClientDCInternalInv o = this.byId.get(id);
        if (o == null) {
            this.byId.put(id, o = new ClientDCInternalInv(isPatternInterface ? 72 : isMAC ? 45 : DualityInterface.NUMBER_OF_PATTERN_SLOTS, id, sortBy, string));
            this.refreshList = true;
        }
        return o;
    }
}
