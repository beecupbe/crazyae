package dev.beecube31.crazyae2.craftsystem;

import appeng.api.config.Actionable;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.core.AEConfig;
import appeng.crafting.CraftBranchFailure;
import com.google.common.collect.ImmutableCollection;
import dev.beecube31.crazyae2.core.cache.ICrazyAutocraftingSystem;
import it.unimi.dsi.fastutil.objects.Object2LongArrayMap;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Map.Entry;

public class CrazyCraftingTreeProcess {
    private final CrazyCraftingTreeNode parent;
    final ICraftingPatternDetails details;
    private final CrazyCraftingJob job;
    private final Object2LongArrayMap<CrazyCraftingTreeNode> nodes = new Object2LongArrayMap<>();
    private final int depth;
    private final ICrazyAutocraftingSystem cc;
    private final World world;
    boolean possible = true;
    private long crafts = 0;
    private long bytes = 0;
    private ArrayList<IAEItemStack> containers;

    public CrazyCraftingTreeProcess(final ICrazyAutocraftingSystem cc, final CrazyCraftingJob job, final ICraftingPatternDetails details, final CrazyCraftingTreeNode craftingTreeNode, final int depth) {
        this.parent = craftingTreeNode;
        this.details = details;
        this.job = job;
        this.depth = depth;
        this.cc = cc;
        this.world = job.getWorld();
    }

    public void addProcess() {
        if (!nodes.isEmpty()) {
            return;
        }

        final IAEItemStack[] list = details.getInputs();

        for (IAEItemStack part : details.getCondensedInputs()) {
            if (part == null) {
                continue;
            }
            for (int x = 0; x < list.length; x++) {
                final IAEItemStack comparePart = list[x];
                if (part.equals(comparePart)) {
                    boolean isPartContainer = false;
                    if (part.getItem().hasContainerItem(part.getDefinition())) {
                        part = list[x];
                        isPartContainer = true;
                    }

                    long wantedSize = part.getStackSize();

                    if (AEConfig.instance().getEnableCraftingSubstitutes()) {
                        IAEItemStack found;
                        long remaining;
                        long requestAmount;

                        if (details.canSubstitute()) {
                            for (IAEItemStack subs : details.getSubstituteInputs(x)) {
                                found = job.checkAvailable(subs);

                                if (found != null) {
                                    remaining = found.getStackSize();
                                } else {
                                    remaining = 0;
                                }

                                if (remaining > 0) {
                                    if (remaining >= wantedSize) {
                                        requestAmount = wantedSize;
                                        wantedSize = 0;
                                        //we have the items
                                    } else {
                                        requestAmount = remaining;
                                        wantedSize -= remaining;
                                    }
                                    subs = subs.copy().setStackSize(requestAmount);
                                    CrazyCraftingTreeNode node = new CrazyCraftingTreeNode(cc, job, subs, this, x, depth + 1);
                                    this.nodes.put(node, requestAmount);
                                    if (wantedSize == 0) {
                                        break;
                                    }
                                }
                            }
                        } else {
                            found = job.checkAvailable(part);

                            if (found != null) {
                                remaining = found.getStackSize();
                            } else {
                                remaining = 0;
                            }

                            if (remaining > 0) {
                                if (remaining >= wantedSize) {
                                    requestAmount = wantedSize;
                                    wantedSize = 0;
                                    //we have the items
                                } else {
                                    requestAmount = remaining;
                                    wantedSize -= remaining;
                                }
                                part = part.copy().setStackSize(requestAmount);
                                this.nodes.put(new CrazyCraftingTreeNode(cc, job, part, this, x, depth + 1), requestAmount);
                            }
                        }
                        if (wantedSize > 0) {
                            if (details.canSubstitute() && cc.getCraftingFor(part, details, x, world).isEmpty()) {
                                //try to order the crafting of a substitute
                                ICraftingPatternDetails prioritizedPattern = null;
                                IAEItemStack prioritizedIAE = null;
                                for (IAEItemStack subs : details.getSubstituteInputs(x)) {
                                    ImmutableCollection<ICraftingPatternDetails> detailCollection = cc.getCraftingFor(subs, details, x, world);

                                    for (ICraftingPatternDetails sp : detailCollection) {
                                        if (prioritizedPattern == null) {
                                            prioritizedPattern = sp;
                                            prioritizedIAE = subs;
                                        } else {
                                            if (sp.getPriority() > prioritizedPattern.getPriority()) {
                                                prioritizedPattern = sp;
                                            }
                                        }
                                    }
                                    if (prioritizedIAE != null) {
                                        subs = subs.copy().setStackSize(wantedSize);
                                        CrazyCraftingTreeNode node = new CrazyCraftingTreeNode(cc, job, subs, this, x, depth + 1);
                                        this.nodes.put(node, wantedSize);
                                        wantedSize = 0;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    if (wantedSize > 0) {
                        part = part.copy().setStackSize(wantedSize);
                        // use the first slot...
                        this.nodes.put(new CrazyCraftingTreeNode(cc, job, part, this, x, depth + 1), wantedSize);
                        wantedSize = 0;
                    }
                    if (!isPartContainer && wantedSize == 0) {
                        break;
                    }
                }
            }
        }
    }

    boolean notRecursive(ICraftingPatternDetails details) {
        return this.parent == null || this.parent.notRecursive(details);
    }

    long getTimes(final long remaining, final long stackSize) {
        for (final IAEItemStack part : details.getCondensedOutputs()) {
            for (final IAEItemStack o : details.getCondensedInputs()) {
                if (part.equals(o) || o.getItem().hasContainerItem(part.getDefinition())) {
                    return 1;
                }
            }
        }
        return (remaining / stackSize) + (remaining % stackSize != 0 ? 1 : 0);
    }

    void request(final CrazyCraftingInventory inv, final long amountOfTimes, final IActionSource src) throws CraftBranchFailure, InterruptedException {
        addProcess();
        this.job.handlePausing();

        // request and remove inputs...
        for (final Entry<CrazyCraftingTreeNode, Long> entry : this.nodes.object2LongEntrySet()) {
            final IAEItemStack stack = entry.getKey().request(inv, entry.getValue() * amountOfTimes, src);
        }

        if (this.containers != null) {
            for (IAEItemStack iae : containers) {
                inv.injectItems(iae, Actionable.MODULATE, src);
            }
            containers = null;
        }
        // assume its possible.

        // add crafting results..
        for (final IAEItemStack out : this.details.getCondensedOutputs()) {
            final IAEItemStack o = out.copy();
            o.setStackSize(o.getStackSize() * amountOfTimes);
            inv.injectItems(o, Actionable.MODULATE, src);
        }
        this.crafts += amountOfTimes;
    }

    public void addContainers(IAEItemStack container) {
        if (this.containers == null) {
            this.containers = new ArrayList<>();
        }
        this.containers.add(container);
    }

    void dive(final CrazyCraftingJob job) {
        job.addTask(this.getAmountCrafted(this.parent.getStack(1)), this.crafts, this.details, this.depth);
        for (final Entry<CrazyCraftingTreeNode, Long> entry : this.nodes.object2LongEntrySet()) {
            entry.getKey().dive(job);
        }

        job.addBytes(this.crafts * 8 + this.bytes);
    }

    IAEItemStack getAmountCrafted(IAEItemStack what2) {
        for (final IAEItemStack is : this.details.getCondensedOutputs()) {
            if (is.isSameType(what2)) {
                what2 = what2.copy();
                what2.setStackSize(is.getStackSize());
                return what2;
            }
        }

        // more fuzzy!
        for (final IAEItemStack is : this.details.getCondensedOutputs()) {
            if (is.getItem() == what2.getItem() && (is.getItem().isDamageable() || is.getItemDamage() == what2.getItemDamage())) {
                what2 = is.copy();
                what2.setStackSize(is.getStackSize());
                return what2;
            }
        }

        throw new IllegalStateException("Crafting Tree construction failed.");
    }

    void setSimulate() {
        this.crafts = 0;
        this.bytes = 0;

        for (final Entry<CrazyCraftingTreeNode, Long> entry : this.nodes.object2LongEntrySet()) {
            entry.getKey().setSimulate();
        }
    }

    void setJob(final CrazyCraftingInventory storage, final CrazyCraftContainer host, final IActionSource src) throws CraftBranchFailure {
        host.pushDetails(this.details, this.crafts);

        for (final Entry<CrazyCraftingTreeNode, Long> entry : this.nodes.object2LongEntrySet()) {
            entry.getKey().setJob(storage, host, src);
        }
    }

    void getPlan(final IItemList<IAEItemStack> plan) {
        for (IAEItemStack i : this.details.getOutputs()) {
            i = i.copy();
            i.setCountRequestable(i.getStackSize() * this.crafts);
            plan.addRequestable(i);
        }

        for (final Entry<CrazyCraftingTreeNode, Long> entry : this.nodes.object2LongEntrySet()) {
            entry.getKey().getPlan(plan);
        }
    }
}
