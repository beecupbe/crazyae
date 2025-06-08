package dev.beecube31.crazyae2.client.me;

import appeng.api.AEApi;
import appeng.api.config.*;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.client.gui.widgets.ISortSource;
import appeng.core.AEConfig;
import appeng.integration.Integrations;
import appeng.integration.modules.bogosorter.InventoryBogoSortModule;
import appeng.items.storage.ItemViewCell;
import appeng.util.ItemSorters;
import appeng.util.Platform;
import appeng.util.prioritylist.IPartitionList;
import dev.beecube31.crazyae2.common.interfaces.gui.IScrollSrc;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

public class ItemRepo {

    private final IItemList<IAEItemStack> list = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createList();
    private List<IAEItemStack> view = new ArrayList<>();
    private final IScrollSrc src;
    private final ISortSource sortSrc;

    private int rowSize = 9;

    private String searchString = "";
    private IPartitionList<IAEItemStack> myPartitionList;
    private String innerSearch = "";
    private boolean hasPower;

    private Enum lastView;
    private Enum lastSearchMode;
    private Enum lastSortBy;
    private Enum lastSortDir;
    private String lastSearch = "";

    private boolean resort = true;
    private boolean changed = false;


    public ItemRepo(final IScrollSrc src, final ISortSource sortSrc) {
        this.src = src;
        this.sortSrc = sortSrc;
    }

    public IAEItemStack getReferenceItem(int idx) {
        idx += (int) (this.src.getCurrentScroll() * this.rowSize);

        if (idx >= this.view.size()) {
            return null;
        }
        return this.view.get(idx);
    }

    void setSearch(final String search) {
        this.searchString = search == null ? "" : search;
    }

    public void postUpdate(final IAEItemStack is) {
        final IAEItemStack st = this.list.findPrecise(is);

        if (st != null) {
            st.reset();
            st.add(is);
        } else {
            this.list.add(is);
        }

        changed = true;
    }

    public long getItemCount(final IAEItemStack is) {
        IAEItemStack st = this.list.findPrecise(is);
        return st == null ? 0 : st.getStackSize();
    }

    public void setViewCell(final ItemStack[] list) {
        this.myPartitionList = ItemViewCell.createFilter(list);
        this.changed = true;
    }

    public void updateView() {

        final Enum viewMode = this.sortSrc.getSortDisplay();

        if (lastView != viewMode) {
            resort = true;
            lastView = viewMode;
        }

        final Enum searchMode = AEConfig.instance().getConfigManager().getSetting(Settings.SEARCH_MODE);
        if (lastSearchMode != searchMode) {
            resort = true;
            lastSearchMode = searchMode;
        }

        if (searchMode == SearchBoxMode.JEI_AUTOSEARCH || searchMode == SearchBoxMode.JEI_MANUAL_SEARCH || searchMode == SearchBoxMode.JEI_AUTOSEARCH_KEEP || searchMode == SearchBoxMode.JEI_MANUAL_SEARCH_KEEP) {
            this.updateJEI(this.searchString);
        }

        if (!lastSearch.equals(searchString)) {
            resort = true;
            lastSearch = searchString;
        }

        final Enum sortBy = this.sortSrc.getSortBy();
        final Enum sortDir = this.sortSrc.getSortDir();

        if (lastSortBy != sortBy) {
            resort = true;
            lastSortBy = sortBy;
        }

        if (lastSortDir != sortDir) {
            resort = true;
            lastSortDir = sortDir;
        }

        if (changed || resort) {
            changed = false;
            resort = false;

            view = new ArrayList<>();

            ItemSorters.setDirection((SortDir) sortDir);
            ItemSorters.init();

            Comparator<IAEItemStack> c = getComparator(sortBy);

            for (IAEItemStack is : this.list) {
                addIAE(is, viewMode);
            }

            view.sort(c);
        }
    }

    private static Comparator<IAEItemStack> getComparator(Enum sortBy) {
        Comparator<IAEItemStack> c;

        if (sortBy == SortOrder.MOD) {
            c = ItemSorters.CONFIG_BASED_SORT_BY_MOD;
        } else if (sortBy == SortOrder.AMOUNT) {
            c = ItemSorters.CONFIG_BASED_SORT_BY_SIZE;
        } else if (sortBy == SortOrder.INVTWEAKS) {
            if (InventoryBogoSortModule.isLoaded()) {
                c = InventoryBogoSortModule.COMPARATOR;
            } else {
                c = ItemSorters.CONFIG_BASED_SORT_BY_INV_TWEAKS;
            }
        } else {
            c = ItemSorters.CONFIG_BASED_SORT_BY_NAME;
        }
        return c;
    }

    private void addIAE(IAEItemStack is, Enum viewMode) {

        final boolean needsZeroCopy = viewMode == ViewItems.CRAFTABLE;

        final boolean terminalSearchToolTips = AEConfig.instance().getConfigManager().getSetting(Settings.SEARCH_TOOLTIPS) != YesNo.NO;

        boolean searchMod = false;

        this.innerSearch = searchString.toLowerCase();
        if (this.innerSearch.startsWith("@")) {
            searchMod = true;
            this.innerSearch = this.innerSearch.substring(1);
        }

        Pattern m = null;
        try {
            m = Pattern.compile(this.innerSearch, Pattern.CASE_INSENSITIVE);
        } catch (final Throwable ignore) {
            try {
                m = Pattern.compile(Pattern.quote(this.innerSearch), Pattern.CASE_INSENSITIVE);
            } catch (final Throwable __) {
                return;
            }
        }

        if (this.myPartitionList != null) {
            if (!this.myPartitionList.isListed(is)) {
                return;
            }
        }

        if (viewMode == ViewItems.CRAFTABLE && !is.isCraftable()) {
            return;
        }

        if (viewMode == ViewItems.STORED && is.getStackSize() == 0) {
            return;
        }

        final String dspName = (searchMod ? Platform.getModId(is) : Platform.getItemDisplayName(is)).toLowerCase();
        boolean foundMatchingItemStack = true;

        for (String term : innerSearch.split(" ")) {
            if (term.length() > 1 && (term.startsWith("-") || term.startsWith("!"))) {
                term = term.substring(1);
                if (dspName.contains(term)) {
                    foundMatchingItemStack = false;
                    break;
                }
            } else if (!dspName.contains(term)) {
                foundMatchingItemStack = false;
                break;
            }
        }

        if (terminalSearchToolTips && !foundMatchingItemStack) {
            final List<String> tooltip = Platform.getTooltip(is);
            for (final String line : tooltip) {
                if (m.matcher(line).find()) {
                    foundMatchingItemStack = true;
                    break;
                }
            }
        }

        if (foundMatchingItemStack) {
            if (needsZeroCopy) {
                is = is.copy();
                is.setStackSize(0);
            }
            this.view.add(is);
        }
    }

    private void updateJEI(String filter) {
        Integrations.jei().setSearchText(filter);
    }

    public int size() {
        return this.view.size();
    }

    public void clear() {
        this.list.resetStatus();
    }

    public boolean hasPower() {
        return this.hasPower;
    }

    public void setPower(final boolean hasPower) {
        this.hasPower = hasPower;
    }

    public int getRowSize() {
        return this.rowSize;
    }

    public void setRowSize(final int rowSize) {
        this.rowSize = rowSize;
    }

    public String getSearchString() {
        return this.searchString;
    }

    public void setSearchString(@Nonnull final String searchString) {
        this.searchString = searchString;
    }

    public IItemList<IAEItemStack> getList() {
        return list;
    }
}
