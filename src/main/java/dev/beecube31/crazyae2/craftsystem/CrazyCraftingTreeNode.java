package dev.beecube31.crazyae2.craftsystem;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.config.FuzzyMode;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.PacketInformPlayer;
import appeng.crafting.CraftBranchFailure;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import appeng.util.item.MeaningfulItemIterator;
import dev.beecube31.crazyae2.core.cache.ICrazyAutocraftingSystem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class CrazyCraftingTreeNode {

    private final int slot;
    private final CrazyCraftingJob job;
    private final IItemList<IAEItemStack> used = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createList();
    private final CrazyCraftingTreeProcess parent;
    private final World world;
    private final IAEItemStack what;
    private final ArrayList<CrazyCraftingTreeProcess> nodes = new ArrayList<>();
    private final ICrazyAutocraftingSystem cc;
    private final int depth;
    private int bytes = 0;
    private boolean canEmit = false;
    private long missing = 0;
    private long howManyEmitted = 0;
    private boolean exhausted = false;

    public CrazyCraftingTreeNode(final ICrazyAutocraftingSystem cc, final CrazyCraftingJob job, final IAEItemStack wat, final CrazyCraftingTreeProcess par, final int slot, final int depth) {
        this.what = wat;
        this.parent = par;
        this.slot = slot;
        this.world = job.getWorld();
        this.job = job;
        this.cc = cc;
        this.depth = depth;

        this.canEmit = cc.canEmitFor(this.what);
    }

    public void addNode() {
        if (!nodes.isEmpty()) {
            return;
        }

        if (this.canEmit) {
            return;
        }

        for (final ICraftingPatternDetails details : cc.getCraftingFor(this.what, this.parent == null ? null : this.parent.details, slot, this.world)) {
            if (this.parent == null || notRecursive(details) && this.parent.details != details) {
                this.nodes.add(new CrazyCraftingTreeProcess(cc, job, details, this, depth + 1));
            }
        }
    }

    IAEItemStack request(final CrazyCraftingInventory inv, long l, final IActionSource src) throws CraftBranchFailure, InterruptedException {
        addNode();
        this.job.handlePausing();

        final IItemList<IAEItemStack> inventoryList = inv.getItemList();
        final List<IAEItemStack> thingsUsed = new ArrayList<>();

        this.what.setStackSize(l);

        if (this.getSlot() >= 0 && this.parent != null && this.parent.details.isCraftable()) {
            LinkedList<IAEItemStack> itemList = new LinkedList<>();

            boolean damageableItem = this.what.getItem().isDamageable() || Platform.isGTDamageableItem(this.what.getItem());

            if (this.parent.details.canSubstitute()) {
                for (IAEItemStack subs : this.parent.details.getSubstituteInputs(this.slot)) {
                    if (damageableItem) {
                        Iterator<IAEItemStack> it = new MeaningfulItemIterator<>(inventoryList.findFuzzy(this.what, FuzzyMode.IGNORE_ALL));
                        while (it.hasNext()) {
                            IAEItemStack i = it.next();
                            if (i.getStackSize() > 0) {
                                itemList.add(i);
                            }
                        }
                    }
                    subs = inventoryList.findPrecise(subs);
                    if (subs != null && subs.getStackSize() > 0) {
                        itemList.add(subs);
                    }
                }
            } else {
                if (damageableItem) {
                    Iterator<IAEItemStack> it = new MeaningfulItemIterator<>(inventoryList.findFuzzy(this.what, FuzzyMode.IGNORE_ALL));
                    while (it.hasNext()) {
                        IAEItemStack i = it.next();
                        if (i.getStackSize() > 0) {
                            itemList.add(i);
                        }
                    }
                } else {
                    final IAEItemStack item = inventoryList.findPrecise(this.what);
                    if (item != null && item.getStackSize() > 0) {
                        itemList.add(item);
                    }
                }
            }

            for (IAEItemStack fuzz : itemList) {
                if (this.parent.details.isValidItemForSlot(this.getSlot(), fuzz.getDefinition(), this.world)) {
                    fuzz = fuzz.copy();
                    fuzz.setStackSize(l);

                    final IAEItemStack available = inv.extractItems(fuzz, Actionable.MODULATE, src);

                    if (available != null) {
                        if (available.getItem().hasContainerItem(available.getDefinition())) {
                            final ItemStack is2 = Platform.getContainerItem(available.createItemStack());
                            final IAEItemStack o = AEItemStack.fromItemStack(is2);

                            if (o != null) {
                                this.parent.addContainers(o);
                            }
                        }

                        if (!this.exhausted) {
                            final IAEItemStack is = this.job.checkUse(available);

                            if (is != null) {
                                thingsUsed.add(is.copy());
                                this.used.add(is);
                            }
                        }

                        this.bytes += available.getStackSize();
                        l -= available.getStackSize();

                        if (l == 0) {
                            return available;
                        }
                    }
                }
            }
        } else {
            final IAEItemStack available = inv.extractItems(this.what, Actionable.MODULATE, src);

            if (available != null) {
                if (!this.exhausted) {
                    final IAEItemStack is = this.job.checkUse(available);

                    if (is != null) {
                        thingsUsed.add(is.copy());
                        this.used.add(is);
                    }
                }

                this.bytes += available.getStackSize();
                l -= available.getStackSize();

                if (l == 0) {
                    return available;
                }
            }
        }

        if (this.canEmit) {
            final IAEItemStack wat = this.what.copy();
            wat.setStackSize(l);

            this.howManyEmitted = wat.getStackSize();
            this.bytes += wat.getStackSize();

            return wat;
        }

        this.exhausted = true;

        if (this.nodes.size() == 1) {
            final CrazyCraftingTreeProcess pro = this.nodes.get(0);

            while (pro.possible && l > 0) {
                final IAEItemStack madeWhat = pro.getAmountCrafted(this.what);
                pro.request(inv, pro.getTimes(l, madeWhat.getStackSize()), src);

                madeWhat.setStackSize(l);
                final IAEItemStack available = inv.extractItems(madeWhat, Actionable.MODULATE, src);

                if (available != null) {

                    if (parent != null && available.getItem().hasContainerItem(available.getDefinition())) {
                        final ItemStack is2 = Platform.getContainerItem(available.createItemStack());
                        final IAEItemStack o = AEItemStack.fromItemStack(is2);

                        if (o != null) {
                            this.parent.addContainers(o);
                        }
                    }

                    this.bytes += available.getStackSize();
                    l -= available.getStackSize();

                    if (l <= 0) {
                        return available;
                    }
                } else {
                    pro.possible = false; // ;P
                }
            }
        } else if (this.nodes.size() > 1) {
            for (final CrazyCraftingTreeProcess pro : this.nodes) {
                try {
                    while (pro.possible && l > 0) {
                        final CrazyCraftingInventory subInv = new CrazyCraftingInventory(inv, true, true, true);
                        pro.request(subInv, 1, src);

                        this.what.setStackSize(l);
                        final IAEItemStack available = subInv.extractItems(this.what, Actionable.MODULATE, src);

                        if (available != null) {
                            if (!subInv.commit(src)) {
                                throw new CraftBranchFailure(this.what, l);
                            }

                            this.bytes += available.getStackSize();
                            l -= available.getStackSize();

                            if (l <= 0) {
                                return available;
                            }
                        } else {
                            pro.possible = false; // ;P
                        }
                    }
                } catch (final CraftBranchFailure fail) {
                    pro.possible = true;
                }
            }
        }

        if (job.isSimulation()) {
            this.bytes += l;
            if (parent != null && this.what.getItem().hasContainerItem(this.what.getDefinition())) {
                final ItemStack is2 = Platform.getContainerItem(this.what.copy().setStackSize(1).createItemStack());
                final IAEItemStack o = AEItemStack.fromItemStack(is2);

                if (o != null) {
                    this.parent.addContainers(o);
                }
            }
            this.missing += l;
            final IAEItemStack rv = this.what.copy();
            rv.setStackSize(l);
            return rv;
        }

        for (final IAEItemStack o : thingsUsed) {
            this.job.refund(o.copy());
            o.setStackSize(-o.getStackSize());
            this.used.add(o);
        }

        throw new CraftBranchFailure(this.what, l);
    }

    boolean notRecursive(ICraftingPatternDetails details) {
        if (this.parent == null) {
            return true;
        }
        if (this.parent.details == details) {
            return false;
        }
        return this.parent.notRecursive(details);
    }

    void dive(final CrazyCraftingJob job) {
        if (this.missing > 0) {
            job.addMissing(this.getStack(this.missing));
        }
        // missing = 0;

        job.addBytes(this.bytes);

        for (final CrazyCraftingTreeProcess pro : this.nodes) {
            pro.dive(job);
        }
    }

    IAEItemStack getStack(final long size) {
        final IAEItemStack is = this.what.copy();
        is.setStackSize(size);
        return is;
    }

    void setSimulate() {
        this.missing = 0;
        this.bytes = 0;
        this.used.resetStatus();
        this.exhausted = false;

        for (final CrazyCraftingTreeProcess pro : this.nodes) {
            pro.setSimulate();
        }
    }

    public void setJob(final CrazyCraftingInventory storage, final CrazyCraftContainer craftingCPUCluster, final IActionSource src) throws CraftBranchFailure {
        for (final IAEItemStack i : this.used) {
            final IAEItemStack actuallyExtracted = storage.extractItems(i, Actionable.MODULATE, src);

            if (actuallyExtracted == null || actuallyExtracted.getStackSize() != i.getStackSize()) {
                if (src.player().isPresent()) {
                    try {
                        if (actuallyExtracted == null) {
                            NetworkHandler.instance().sendTo(new PacketInformPlayer(i, null, PacketInformPlayer.InfoType.NO_ITEMS_EXTRACTED), (EntityPlayerMP) src.player().get());
                        } else {
                            NetworkHandler.instance().sendTo(new PacketInformPlayer(i, actuallyExtracted, PacketInformPlayer.InfoType.PARTIAL_ITEM_EXTRACTION), (EntityPlayerMP) src.player().get());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                throw new CraftBranchFailure(i, i.getStackSize());
            }

            craftingCPUCluster.addStorage(actuallyExtracted);
        }

        if (this.howManyEmitted > 0) {
            final IAEItemStack i = this.what.copy().reset();
            i.setStackSize(this.howManyEmitted);
            craftingCPUCluster.addEmitable(i);
        }

        for (final CrazyCraftingTreeProcess pro : this.nodes) {
            pro.setJob(storage, craftingCPUCluster, src);
        }
    }

    void getPlan(final IItemList<IAEItemStack> plan) {
        if (this.missing > 0) {
            final IAEItemStack o = this.what.copy();
            o.setStackSize(this.missing);
            plan.add(o);
        }

        if (this.howManyEmitted > 0) {
            final IAEItemStack i = this.what.copy();
            i.setCountRequestable(this.howManyEmitted);
            plan.addRequestable(i);
        }

        for (final IAEItemStack i : this.used) {
            plan.add(i.copy());
        }

        for (final CrazyCraftingTreeProcess pro : this.nodes) {
            pro.getPlan(plan);
        }
    }

    int getSlot() {
        return this.slot;
    }
}
