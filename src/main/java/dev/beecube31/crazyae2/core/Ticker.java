package dev.beecube31.crazyae2.core;

import appeng.core.AEConfig;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import dev.beecube31.crazyae2.craftsystem.CrazyCraftingJob;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.Type;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

import java.util.Collection;

public class Ticker {
    public static final Ticker INSTANCE = new Ticker();
    private final Multimap<World, CrazyCraftingJob> craftingJobs = LinkedListMultimap.create();

    @SubscribeEvent
    public void onTickEv(final TickEvent ev) {
        if (ev.type == Type.WORLD && ev.phase == Phase.END) {
            final WorldTickEvent wte = (WorldTickEvent) ev;
            synchronized (this.craftingJobs) {
                final Collection<CrazyCraftingJob> jobSet = this.craftingJobs.get(wte.world);
                if (!jobSet.isEmpty()) {
                    final int jobSize = jobSet.size();
                    final int microSecondsPerTick = AEConfig.instance().getCraftingCalculationTimePerTick() * 1000;
                    final int simTime = Math.max(1, microSecondsPerTick / jobSize);

                    jobSet.removeIf(cj -> !cj.simulateFor(simTime));
                }
            }

        }
    }

    public void registerCraftingSimulation(final World world, final CrazyCraftingJob craftingJob) {
        synchronized (this.craftingJobs) {
            this.craftingJobs.put(world, craftingJob);
        }
    }
}
