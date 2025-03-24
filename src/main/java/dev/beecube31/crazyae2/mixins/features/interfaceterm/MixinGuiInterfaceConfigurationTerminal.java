package dev.beecube31.crazyae2.mixins.features.interfaceterm;

import appeng.api.config.ActionItems;
import appeng.api.config.Settings;
import appeng.client.gui.AEBaseGui;
import appeng.client.gui.implementations.GuiInterfaceConfigurationTerminal;
import appeng.client.gui.widgets.GuiImgButton;
import appeng.client.gui.widgets.MEGuiTextField;
import appeng.client.me.ClientDCInternalInv;
import appeng.client.me.SlotDisconnected;
import appeng.core.localization.GuiText;
import com.google.common.collect.HashMultimap;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.*;

import java.util.*;

import static appeng.helpers.ItemStackHelper.stackFromNBT;

@Mixin(value = GuiInterfaceConfigurationTerminal.class, remap = false, priority = 990)
@SideOnly(Side.CLIENT)
public abstract class MixinGuiInterfaceConfigurationTerminal extends AEBaseGui {

    @Shadow @Final private ArrayList<String> names;

    @Shadow @Final private HashMap<Long, ClientDCInternalInv> byId;

    @Shadow @Final private static int LINES_ON_PAGE;

    @Shadow @Final private ArrayList<Object> lines;

    @Shadow @Final private int offsetX;

    @Shadow @Final private HashMap<GuiButton, ClientDCInternalInv> guiButtonHashMap;

    @Shadow @Final private Map<ClientDCInternalInv, Integer> numUpgradesMap;

    @Shadow @Final private Set<Object> matchedStacks;

    @Shadow @Final private Set<ClientDCInternalInv> matchedInterfaces;

    @Shadow @Final private HashMultimap<String, ClientDCInternalInv> byName;

    @Shadow private MEGuiTextField searchFieldInputs;

    @Shadow private boolean refreshList;

    @Shadow @Final private HashMap<ClientDCInternalInv, BlockPos> blockPosHashMap;

    @Shadow @Final private HashMap<ClientDCInternalInv, Integer> dimHashMap;

    @Shadow @Final private Map<String, Set<Object>> cachedSearches;

    @Shadow protected abstract Set<Object> getCacheForSearchTerm(String searchTerm);

    @Shadow protected abstract boolean itemStackMatchesSearchTerm(ItemStack itemStack, String searchTerm);

    @Shadow protected abstract int getMaxRows();

    @Unique @Final private final Map<ClientDCInternalInv, Boolean> crazyae$perfect = new HashMap<>();

    public MixinGuiInterfaceConfigurationTerminal(Container container) {
        super(container);
    }

    /**
     * @author Beecube31
     * @reason Support Perfect interface
     */
    @Override
    @Overwrite
    public void drawFG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        this.buttonList.clear();

        this.fontRenderer.drawString(this.getGuiDisplayName(GuiText.InterfaceConfigurationTerminal.getLocal()), 8, 6, 4210752);
        this.fontRenderer.drawString(GuiText.inventory.getLocal(), this.offsetX + 2, this.ySize - 96 + 3, 4210752);

        final int currentScroll = this.getScrollBar().getCurrentScroll();

        this.inventorySlots.inventorySlots.removeIf(slot -> slot instanceof SlotDisconnected);

        int offset = 30;
        int linesDraw = 0;
        for (int x = 0; x < LINES_ON_PAGE && linesDraw < LINES_ON_PAGE && currentScroll + x < this.lines.size(); x++) {
            final Object lineObj = this.lines.get(currentScroll + x);
            if (lineObj instanceof ClientDCInternalInv inv) {

                GuiButton guiButton = new GuiImgButton(guiLeft + 4, guiTop + offset, Settings.ACTIONS, ActionItems.HIGHLIGHT_INTERFACE);
                guiButtonHashMap.put(guiButton, inv);
                this.buttonList.add(guiButton);
                int extraLines = numUpgradesMap.get(inv);
                boolean isPerfect = crazyae$perfect.get(inv);

                for (int row = 0; row < 1 + (isPerfect ? 3 : extraLines) && linesDraw < LINES_ON_PAGE; ++row) {
                    for (int z = 0; z < 9; z++) {
                        this.inventorySlots.inventorySlots.add(new SlotDisconnected(inv, z + (row * 9), (z * 18 + 22), offset));
                        if (this.matchedStacks.contains(inv.getInventory().getStackInSlot(z + (row * 9)))) {
                            drawRect(z * 18 + 22, offset, z * 18 + 22 + 16, offset + 16, 0x8A00FF00);
                        } else if (!matchedInterfaces.contains(inv)) {
                            drawRect(z * 18 + 22, offset, z * 18 + 22 + 16, offset + 16, 0x6A000000);
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

                while (name.length() > 2 && this.fontRenderer.getStringWidth(name) > 155) {
                    name = name.substring(0, name.length() - 1);
                }
                this.fontRenderer.drawString(name, this.offsetX + 2, 5 + offset, 4210752);
                linesDraw++;
                offset += 18;
            }
        }

        if (searchFieldInputs.isMouseIn(mouseX, mouseY)) {
            drawTooltip(Mouse.getEventX() * this.width / this.mc.displayWidth - offsetX, mouseY - guiTop, "Inputs OR names");
        }
    }

    /**
     * @author Beecube31
     * @reason Support Perfect interface
     */
    @Overwrite
    @Override
    public void drawBG(final int offsetX, final int offsetY, final int mouseX, final int mouseY) {
        this.bindTexture("guis/interfaceconfigurationterminal.png");
        this.drawTexturedModalRect(offsetX, offsetY, 0, 0, this.xSize, this.ySize);

        int offset = 29;
        final int ex = this.getScrollBar().getCurrentScroll();
        int linesDraw = 0;
        for (int x = 0; x < LINES_ON_PAGE && linesDraw < LINES_ON_PAGE && ex + x < this.lines.size(); x++) {
            final Object lineObj = this.lines.get(ex + x);
            if (lineObj instanceof ClientDCInternalInv) {
                GlStateManager.color(1, 1, 1, 1);
                final int width = 9 * 18;

                int extraLines = numUpgradesMap.get(lineObj);
                boolean isPerfect = crazyae$perfect.get(lineObj);

                for (int row = 0; row < 1 + (isPerfect ? 3 : extraLines) && linesDraw < LINES_ON_PAGE; ++row) {
                    this.drawTexturedModalRect(offsetX + 20, offsetY + offset, 20, 170, width, 18);
                    offset += 18;
                    linesDraw++;
                }
            } else {
                offset += 18;
                linesDraw++;
            }
        }

        if (this.searchFieldInputs != null) {
            this.searchFieldInputs.drawTextBox();
        }
    }

    /**
     * @author Beecube31
     * @reason Support Perfect interface
     */
    @Overwrite
    public void postUpdate(final NBTTagCompound in) {
        if (in.getBoolean("clear")) {
            this.byId.clear();
            this.refreshList = true;
        }

        for (final Object oKey : in.getKeySet()) {
            final String key = (String) oKey;
            if (key.startsWith("=")) {
                try {
                    final long id = Long.parseLong(key.substring(1), Character.MAX_RADIX);
                    final NBTTagCompound invData = in.getCompoundTag(key);
                    final ClientDCInternalInv current = this.crazyae$getById(invData.getInteger("num"), id, invData.getLong("sortBy"), invData.getString("un"));
                    blockPosHashMap.put(current, NBTUtil.getPosFromTag(invData.getCompoundTag("pos")));
                    dimHashMap.put(current, invData.getInteger("dim"));
                    numUpgradesMap.put(current, invData.getInteger("numUpgrades"));
                    crazyae$perfect.put(current, invData.getInteger("forcedNum") == 36);

                    for (int x = 0; x < current.getInventory().getSlots(); x++) {
                        final String which = Integer.toString(x);
                        if (invData.hasKey(which)) {
                            current.getInventory().setStackInSlot(x, stackFromNBT(invData.getCompoundTag(which)));
                        }
                    }
                } catch (final NumberFormatException ignored) {
                }
            }
        }

        if (this.refreshList) {
            this.refreshList = false;
            // invalid caches on refresh
            this.cachedSearches.clear();
            this.refreshList();
        }
    }

    /**
     * @author Beecube31
     * @reason Support Perfect interface
     */
    @Overwrite
    private void refreshList() {
        this.byName.clear();
        this.buttonList.clear();
        this.matchedStacks.clear();
        this.matchedInterfaces.clear();

        final String searchFieldInputs = this.searchFieldInputs.getText().toLowerCase();

        final Set<Object> cachedSearch = this.getCacheForSearchTerm(searchFieldInputs);
        final boolean rebuild = cachedSearch.isEmpty();

        for (final ClientDCInternalInv entry : this.byId.values()) {
            // ignore inventory if not doing a full rebuild and cache already marks it as miss.
            if (!rebuild && !cachedSearch.contains(entry)) {
                continue;
            }

            // Shortcut to skip any filter if search term is ""/empty

            boolean found = searchFieldInputs.isEmpty();

            // Search if the current inventory holds a pattern containing the search term.
            if (!found) {
                int slot = 0;
                for (final ItemStack itemStack : entry.getInventory()) {
                    if (crazyae$perfect.get(entry) != null ? slot > 36 : slot > 8 + numUpgradesMap.get(entry) * 9) {
                        break;
                    }
                    if (this.itemStackMatchesSearchTerm(itemStack, searchFieldInputs)) {
                        found = true;
                        matchedStacks.add(itemStack);
                    }
                    slot++;
                }
            }
            // if found, filter skipped or machine name matching the search term, add it
            if (searchFieldInputs.isEmpty() || entry.getName().toLowerCase().contains(searchFieldInputs)) {
                this.matchedInterfaces.add(entry);
                found = true;
            }
            if (found) {
                this.byName.put(entry.getName(), entry);
                cachedSearch.add(entry);
            } else {
                cachedSearch.remove(entry);
            }
        }

        this.names.clear();
        this.names.addAll(this.byName.keySet());

        Collections.sort(this.names);

        this.lines.clear();
        this.lines.ensureCapacity(this.getMaxRows());

        for (final String n : this.names) {
            this.lines.add(n);

            final ArrayList<ClientDCInternalInv> clientInventories = new ArrayList<>(this.byName.get(n));

            Collections.sort(clientInventories);
            this.lines.addAll(clientInventories);
        }

        this.getScrollBar().setRange(0, this.lines.size() - 1, 1);
    }

    @Unique
    private ClientDCInternalInv crazyae$getById(int num, final long id, final long sortBy, final String string) {
        ClientDCInternalInv o = this.byId.get(id);

        if (o == null) {
            this.byId.put(id, o = new ClientDCInternalInv(num, id, sortBy, string, 8192));
            this.refreshList = true;
        }

        return o;
    }
}
