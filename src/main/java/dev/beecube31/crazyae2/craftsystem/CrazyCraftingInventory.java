package dev.beecube31.crazyae2.craftsystem;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.PacketInformPlayer;
import appeng.util.inv.ItemListIgnoreCrafting;
import net.minecraft.entity.player.EntityPlayerMP;

import java.io.IOException;

public class CrazyCraftingInventory implements IMEInventory<IAEItemStack> {

    private final CrazyCraftingInventory par;
    private final IMEInventory<IAEItemStack> target;
    private final IItemList<IAEItemStack> localCache;
    private final boolean logExtracted;
    private final IItemList<IAEItemStack> extractedCache;
    private final boolean logInjections;
    private final IItemList<IAEItemStack> injectedCache;
    private final boolean logMissing;
    private final IItemList<IAEItemStack> missingCache;

    public CrazyCraftingInventory() {
        this.localCache = new ItemListIgnoreCrafting<>(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createList());
        this.extractedCache = null;
        this.injectedCache = null;
        this.missingCache = null;
        this.logExtracted = false;
        this.logInjections = false;
        this.logMissing = false;
        this.target = null;
        this.par = null;
    }

    public CrazyCraftingInventory(final CrazyCraftingInventory parent) {
        this.target = parent;
        this.logExtracted = parent.logExtracted;
        this.logInjections = parent.logInjections;
        this.logMissing = parent.logMissing;

        if (this.logMissing) {
            this.missingCache = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createList();
        } else {
            this.missingCache = null;
        }

        if (this.logExtracted) {
            this.extractedCache = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createList();
        } else {
            this.extractedCache = null;
        }

        if (this.logInjections) {
            this.injectedCache = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createList();
        } else {
            this.injectedCache = null;
        }

        this.localCache = this.target.getAvailableItems(new ItemListIgnoreCrafting<>(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createList()));

        this.par = parent;
    }

    public CrazyCraftingInventory(final IMEMonitor<IAEItemStack> target, final IActionSource src, final boolean logExtracted, final boolean logInjections, final boolean logMissing) {
        this.target = target;
        this.logExtracted = logExtracted;
        this.logInjections = logInjections;
        this.logMissing = logMissing;

        if (logMissing) {
            this.missingCache = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createList();
        } else {
            this.missingCache = null;
        }

        if (logExtracted) {
            this.extractedCache = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createList();
        } else {
            this.extractedCache = null;
        }

        if (logInjections) {
            this.injectedCache = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createList();
        } else {
            this.injectedCache = null;
        }

        this.localCache = new ItemListIgnoreCrafting<>(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createList());
        for (final IAEItemStack is : target.getStorageList()) {
            this.localCache.add(target.extractItems(is, Actionable.SIMULATE, src));
        }

        this.par = null;
    }

    public CrazyCraftingInventory(final IMEInventory<IAEItemStack> target, final boolean logExtracted, final boolean logInjections, final boolean logMissing) {
        this.target = target;
        this.logExtracted = logExtracted;
        this.logInjections = logInjections;
        this.logMissing = logMissing;

        if (logMissing) {
            this.missingCache = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createList();
        } else {
            this.missingCache = null;
        }

        if (logExtracted) {
            this.extractedCache = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createList();
        } else {
            this.extractedCache = null;
        }

        if (logInjections) {
            this.injectedCache = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createList();
        } else {
            this.injectedCache = null;
        }

        this.localCache = target.getAvailableItems(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createList());
        this.par = null;
    }

    public CrazyCraftingInventory(final IItemList<IAEItemStack> itemList) {
        this.localCache = new ItemListIgnoreCrafting<>(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createList());
        this.target = null;
        this.logExtracted = false;
        this.logInjections = false;
        this.logMissing = false;
        this.missingCache = null;
        this.extractedCache = null;
        this.injectedCache = null;

        for (IAEItemStack iaeItemStack : itemList) {
            this.localCache.add(iaeItemStack);
        }

        this.par = null;
    }

    @Override
    public IAEItemStack injectItems(final IAEItemStack input, final Actionable mode, final IActionSource src) {
        if (input == null) {
            return null;
        }

        if (mode == Actionable.MODULATE) {
            if (this.logInjections) {
                this.injectedCache.add(input);
            }
            this.localCache.add(input);
        }

        return null;
    }

    public IAEItemStack injectItems(final ICraftingPatternDetails outputs, final Actionable mode, final IActionSource src) {
        if (outputs == null) {
            return null;
        }

        if (mode == Actionable.MODULATE) {
            if (this.logInjections) {
                for (IAEItemStack ais : outputs.getCondensedOutputs()) {
                    this.injectedCache.add(ais);
                }
            }
            for (IAEItemStack ais : outputs.getCondensedOutputs()) {
                this.localCache.add(ais);
            }
        }

        return null;
    }

    @Override
    public IAEItemStack extractItems(final IAEItemStack request, final Actionable mode, final IActionSource src) {
        if (request == null) {
            return null;
        }

        final IAEItemStack list = this.localCache.findPrecise(request);
        if (list == null || list.getStackSize() == 0) {
            return null;
        }

        if (list.getStackSize() >= request.getStackSize()) {
            if (mode == Actionable.MODULATE) {
                list.decStackSize(request.getStackSize());
                if (this.logExtracted) {
                    this.extractedCache.add(request);
                }
            }

            return request;
        }

        final IAEItemStack ret = request.copy();
        ret.setStackSize(list.getStackSize());

        if (mode == Actionable.MODULATE) {
            list.reset();
            if (this.logExtracted) {
                this.extractedCache.add(ret);
            }
        }

        return ret;
    }

    @Override
    public IItemList<IAEItemStack> getAvailableItems(final IItemList<IAEItemStack> out) {
        for (final IAEItemStack is : this.localCache) {
            out.add(is);
        }

        return out;
    }

    @Override
    public IStorageChannel getChannel() {
        return AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class);
    }

    public IItemList<IAEItemStack> getItemList() {
        return this.localCache;
    }

    public boolean commit(final IActionSource src) {
        final IItemList<IAEItemStack> added = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createList();
        final IItemList<IAEItemStack> pulled = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createList();
        boolean failed = false;

        if (this.logInjections) {
            for (final IAEItemStack inject : this.injectedCache) {
                IAEItemStack result = null;
                added.add(result = this.target.injectItems(inject, Actionable.MODULATE, src));

                if (result != null) {
                    failed = true;
                    break;
                }
            }
        }

        if (failed) {
            for (final IAEItemStack is : added) {
                this.target.extractItems(is, Actionable.MODULATE, src);
            }

            return false;
        }

        if (this.logExtracted) {
            for (final IAEItemStack extra : this.extractedCache) {
                IAEItemStack result = null;
                pulled.add(result = this.target.extractItems(extra, Actionable.MODULATE, src));

                if (result == null || result.getStackSize() != extra.getStackSize()) {
                    if (src.player().isPresent()) {
                        try {
                            if (result == null) {
                                NetworkHandler.instance().sendTo(new PacketInformPlayer(extra, null, PacketInformPlayer.InfoType.NO_ITEMS_EXTRACTED), (EntityPlayerMP) src.player().get());
                            } else {
                                NetworkHandler.instance().sendTo(new PacketInformPlayer(extra, result, PacketInformPlayer.InfoType.PARTIAL_ITEM_EXTRACTION), (EntityPlayerMP) src.player().get());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    failed = true;
                }
            }
        }

        if (failed) {
            for (final IAEItemStack is : added) {
                this.target.extractItems(is, Actionable.MODULATE, src);
            }

            for (final IAEItemStack is : pulled) {
                this.target.injectItems(is, Actionable.MODULATE, src);
            }

            return false;
        }

        if (this.logMissing && this.par != null) {
            for (final IAEItemStack extra : this.missingCache) {
                this.par.addMissing(extra);
            }
        }

        return true;
    }

    private void addMissing(final IAEItemStack extra) {
        this.missingCache.add(extra);
    }

    void ignore(final IAEItemStack what) {
        final IAEItemStack list = this.localCache.findPrecise(what);
        if (list != null) {
            list.setStackSize(0);
        }
    }
}
