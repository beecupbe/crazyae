package dev.beecube31.crazyae2.craftsystem;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingCallback;
import appeng.api.networking.crafting.ICraftingJob;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.api.util.DimensionalCoord;
import appeng.core.AELog;
import appeng.crafting.CraftBranchFailure;
import appeng.crafting.CraftingCalculationFailure;
import appeng.me.cache.GridStorageCache;
import com.google.common.base.Stopwatch;
import dev.beecube31.crazyae2.core.Ticker;
import dev.beecube31.crazyae2.core.cache.ICrazyAutocraftingSystem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class CrazyCraftingJob implements Runnable, ICraftingJob {
    private static final String LOG_CRAFTING_JOB = "CrazyCraftingJob::logCraftingJob() : (%s) issued by %s requesting [%s] using %s bytes took %s us";
    private static final String LOG_MACHINE_SOURCE_DETAILS = "CrazyMachine[object=%s, %s]";

    private final CrazyCraftingInventory original;
    private final World world;
    private final IItemList<IAEItemStack> crafting = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createList();
    private final IItemList<IAEItemStack> missing = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createList();

    private final HashMap<String, TwoIntegers> opsAndMultiplier = new HashMap<>();
    private final Object monitor = new Object();
    private final Stopwatch tickSpreadingWatch = Stopwatch.createUnstarted();
    private final Stopwatch craftingTreeWatch = Stopwatch.createUnstarted();
    private final ICrazyAutocraftingSystem cc;
    private CrazyCraftingTreeNode tree;
    private final IAEItemStack output;
    private boolean simulate = false;
    private CrazyCraftingInventory availableCheck;
    private long bytes = 0;
    private final IActionSource actionSrc;
    private final ICraftingCallback callback;
    private boolean running = false;
    private boolean done = false;
    private int time;
    private int incTime;

    private World wrapWorld(final World w) {
        return w;
    }

    public CrazyCraftingJob(final World w, final IGrid grid, final IActionSource actionSrc, final IAEItemStack what, final ICraftingCallback callback) {
        this.world = this.wrapWorld(w);
        this.output = what.copy();
        this.actionSrc = actionSrc;

        this.callback = callback;

        this.cc = grid.getCache(ICrazyAutocraftingSystem.class);
        final GridStorageCache sg = grid.getCache(IStorageGrid.class);
        this.original = new CrazyCraftingInventory(sg.getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class)).getStorageList());

        this.setTree(this.getCraftingTree(cc, what));
        this.availableCheck = null;
    }

    private CrazyCraftingTreeNode getCraftingTree(final ICrazyAutocraftingSystem cc, final IAEItemStack what) {
        return new CrazyCraftingTreeNode(cc, this, what, null, -1, 0);
    }

    void refund(final IAEItemStack o) {
        this.availableCheck.injectItems(o, Actionable.MODULATE, this.actionSrc);
    }

    IAEItemStack checkUse(final IAEItemStack available) {
        return this.availableCheck.extractItems(available, Actionable.MODULATE, this.actionSrc);
    }

    IAEItemStack checkAvailable(final IAEItemStack available) {
        return this.availableCheck.extractItems(available, Actionable.SIMULATE, this.actionSrc);
    }

    void addTask(IAEItemStack what, final long crafts, final ICraftingPatternDetails details, final int depth) {
        if (crafts > 0) {
            what = what.copy();
            what.setStackSize(what.getStackSize() * crafts);
            this.crafting.add(what);
        }
    }

    void addMissing(IAEItemStack what) {
        what = what.copy();
        this.missing.add(what);
    }

    @Override
    public void run() {
        try {
            try {
                Ticker.INSTANCE.registerCraftingSimulation(this.world, this);
                this.handlePausing();

                final CrazyCraftingInventory craftingInventory = new CrazyCraftingInventory(this.original, true, false, true);
                craftingInventory.ignore(this.output);

                this.availableCheck = new CrazyCraftingInventory(this.original, false, false, false);
                craftingTreeWatch.reset().start();
                this.getTree().request(craftingInventory, this.output.getStackSize(), this.actionSrc);
                craftingTreeWatch.stop();
                this.getTree().dive(this);

                for (final String s : this.opsAndMultiplier.keySet()) {
                    final TwoIntegers ti = this.opsAndMultiplier.get(s);
                    AELog.crafting(s + " * " + ti.times + " = " + (ti.perOp * ti.times));
                }

                if (actionSrc.player().isPresent()) {
                    this.logCraftingJob("simulated, success", craftingTreeWatch);
                } else {
                    this.logCraftingJob("real, success", craftingTreeWatch);
                }
            } catch (final CraftBranchFailure e) {
                this.simulate = true;

                try {
                    if (actionSrc.player().isPresent()) {
                        final CrazyCraftingInventory craftingInventory = new CrazyCraftingInventory(this.original, true, false, true);
                        craftingInventory.ignore(this.output);

                        this.getTree().setSimulate();
                        this.availableCheck = new CrazyCraftingInventory(this.original, false, false, false);
                        craftingTreeWatch.reset().start();
                        this.getTree().request(craftingInventory, this.output.getStackSize(), this.actionSrc);
                        craftingTreeWatch.stop();
                        this.getTree().dive(this);

                        for (final String s : this.opsAndMultiplier.keySet()) {
                            final TwoIntegers ti = this.opsAndMultiplier.get(s);
                            AELog.crafting(s + " * " + ti.times + " = " + (ti.perOp * ti.times));
                        }

                        this.logCraftingJob("simulated, failed", craftingTreeWatch);
                    } else {
                        this.logCraftingJob("real, failed", craftingTreeWatch);
                    }
                } catch (final CraftBranchFailure | CraftingCalculationFailure e1) {
                    AELog.debug(e1);
                } catch (final InterruptedException e1) {
                    AELog.crafting("CrazyCraftingJob::run() : Calculation canceled.");
                    this.finish();
                    return;
                }
            } catch (final CraftingCalculationFailure f) {
                AELog.debug(f);
            } catch (final InterruptedException e1) {
                AELog.crafting("CrazyCraftingJob::run() : Calculation canceled.");
                this.finish();
                return;
            }

            AELog.craftingDebug("CrazyCraftingJob::run() : Done");
        } catch (final Throwable t) {
            this.finish();
            throw new IllegalStateException(t);
        }

        this.finish();
    }

    void handlePausing() throws InterruptedException {
        if (!this.actionSrc.player().isPresent() && this.incTime > 100) {
            this.incTime = 0;
            synchronized (this.monitor) {
                if (this.tickSpreadingWatch.elapsed(TimeUnit.MICROSECONDS) > this.time) {
                    this.running = false;
                    if (this.craftingTreeWatch.isRunning()) {
                        this.craftingTreeWatch.stop();
                    }

                    if (this.tickSpreadingWatch.isRunning()) {
                        this.tickSpreadingWatch.stop();
                    }

                    this.monitor.notify();
                }

                if (!this.running) {
                    AELog.craftingDebug("CrazyCraftingJob::handlePausing() : Crafting job will now sleep");

                    while (!this.running) {
                        this.monitor.wait();
                    }

                    AELog.craftingDebug("CrazyCraftingJob::handlePausing() : Crafting job now active");
                }
            }
        }

        if (Thread.interrupted()) {
            throw new InterruptedException();
        }

        this.incTime++;
    }

    private void finish() {
        if (this.callback != null) {
            this.callback.calculationComplete(this);
        }

        this.availableCheck = null;

        synchronized (this.monitor) {
            this.running = false;
            this.done = true;
            this.monitor.notify();
        }
    }

    @Override
    public boolean isSimulation() {
        return this.simulate;
    }

    @Override
    public long getByteTotal() {
        return this.bytes;
    }

    @Override
    public void populatePlan(final IItemList<IAEItemStack> plan) {
        if (this.getTree() != null) {
            this.getTree().getPlan(plan);
        }
    }

    @Override
    public IAEItemStack getOutput() {
        return this.output;
    }

    public boolean isDone() {
        return this.done;
    }

    World getWorld() {
        return this.world;
    }

    /**
     * @return true if this needs more simulation
     */
    public boolean simulateFor(final int milli) {
        this.time = milli;

        synchronized (this.monitor) {
            if (this.done) {
                return false;
            }
            if (!this.actionSrc.player().isPresent()) {
                this.tickSpreadingWatch.reset();
                this.tickSpreadingWatch.start();
                this.monitor.notify();
            }
            this.running = true;
        }

        return true;
    }

    void addBytes(final long crafts) {
        this.bytes += crafts;
    }

    public CrazyCraftingTreeNode getTree() {
        return this.tree;
    }

    private void setTree(final CrazyCraftingTreeNode tree) {
        this.tree = tree;
    }

    private void logCraftingJob(String type, Stopwatch timer) {
        if (AELog.isCraftingLogEnabled()) {
            final String itemToOutput = this.output.toString();
            final long elapsedTime = timer.elapsed(TimeUnit.MICROSECONDS);
            final String actionSource;

            if (this.actionSrc.player().isPresent()) {
                final EntityPlayer player = this.actionSrc.player().get();

                actionSource = player.toString();
            } else if (this.actionSrc.machine().isPresent()) {
                final IActionHost machineSource = this.actionSrc.machine().get();
                final IGridNode actionableNode = machineSource.getActionableNode();
                final IGridHost machine = actionableNode.getMachine();
                final DimensionalCoord location = actionableNode.getGridBlock().getLocation();

                actionSource = String.format(LOG_MACHINE_SOURCE_DETAILS, machine, location);
            } else {
                actionSource = "[unknown source]";
            }

            AELog.crafting(LOG_CRAFTING_JOB, type, actionSource, itemToOutput, this.bytes, elapsedTime);
        }
    }

    private static class TwoIntegers {
        private final long perOp = 0;
        private final long times = 0;
    }
}
