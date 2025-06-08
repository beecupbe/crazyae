package dev.beecube31.crazyae2.mixins.core.crafting.container;

import appeng.api.networking.IGrid;
import appeng.api.networking.crafting.ICraftingCPU;
import appeng.api.networking.crafting.ICraftingGrid;
import appeng.container.implementations.ContainerCraftingCPU;
import appeng.container.implementations.ContainerCraftingStatus;
import appeng.container.implementations.CraftingCPUStatus;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import appeng.util.Platform;
import dev.beecube31.crazyae2.common.interfaces.mixin.crafting.IMixinContainerCraftingCPU;
import dev.beecube31.crazyae2.common.interfaces.mixin.crafting.IMixinCraftingCPUStatus;
import dev.beecube31.crazyae2.core.cache.ICrazyAutocraftingSystem;
import net.minecraft.entity.player.InventoryPlayer;
import org.spongepowered.asm.mixin.*;

import java.util.*;

@Mixin(value = ContainerCraftingStatus.class, remap = false, priority = 996)
public abstract class MixinContainerCraftingStatus extends ContainerCraftingCPU {

    @Shadow private int lastUpdate;

    @Shadow protected abstract void sendCPUs();

    @Shadow public int selectedCpuSerial;

    @Shadow private List<CraftingCPUStatus> cpus;

    @Shadow @Final private WeakHashMap<ICraftingCPU, Integer> cpuSerialMap;

    @Shadow protected abstract int getOrAssignCpuSerial(ICraftingCPU cpu);

    @Unique private Set<ICraftingCPU> crazyae$lastKnownCpuSet = new HashSet<>();

    @Shadow @Final private static Comparator<CraftingCPUStatus> CPU_COMPARATOR;


    public MixinContainerCraftingStatus(InventoryPlayer ip, Object te) {
        super(ip, te);
    }

    /**
     * @author Beecube31
     * @reason Support dense crafting blocks
     * @since v0.6
     */
    @Overwrite
    @Override
    public void detectAndSendChanges() {
        IGrid network = ((AccessorContainerCraftingCPU) this).getNetwork();

        if (Platform.isServer() && network != null) {
            final ICraftingGrid cc = network.getCache(ICraftingGrid.class);
            final ICrazyAutocraftingSystem sys = network.getCache(ICrazyAutocraftingSystem.class);

            Set<ICraftingCPU> currentCpuSet = new HashSet<>();
            currentCpuSet.addAll(cc.getCpus());
            currentCpuSet.addAll(sys.getWorkers());

            ++this.lastUpdate;
            if (!currentCpuSet.equals(this.crazyae$lastKnownCpuSet) || this.lastUpdate > 20) {
                this.lastUpdate = 0;
                this.crazyae$lastKnownCpuSet = currentCpuSet;
                this.updateCpuList();
                this.sendCPUs();
            }
        }

        if (this.selectedCpuSerial != -1) {
            if (this.cpus.stream().noneMatch(c -> c.getSerial() == this.selectedCpuSerial)) {
                this.selectCPU(-1);
            }
        }

        if (this.selectedCpuSerial == -1) {
            this.cpus.stream()
                    .filter(c -> c.getRemainingItems() > 0)
                    .findFirst()
                    .ifPresent(c -> this.selectCPU(c.getSerial()));

            if (this.selectedCpuSerial == -1 && !this.cpus.isEmpty()) {
                this.selectCPU(this.cpus.get(0).getSerial());
            }
        }

        super.detectAndSendChanges();
    }

    /**
     * @author Beecube31
     * @reason Support dense crafting blocks
     * @since v0.6
     */
    @Overwrite
    private void updateCpuList() {
        this.cpus.clear();
        for (ICraftingCPU cpu : this.crazyae$lastKnownCpuSet) {
            int serial = getOrAssignCpuSerial(cpu);
            IMixinCraftingCPUStatus status = ((IMixinCraftingCPUStatus) new CraftingCPUStatus(cpu, serial));
            status.crazyae$setWhenJobStarted(cpu instanceof IMixinCraftingCPUStatus s ? s.crazyae$whenJobStarted() : -1);
            status.crazyae$setJobInitiator(cpu instanceof IMixinCraftingCPUStatus s ? s.crazyae$jobInitiator() : "N/A");
            this.cpus.add(((CraftingCPUStatus) status));
        }

        this.cpus.sort(CPU_COMPARATOR);
    }

    /**
     * @author Beecube31
     * @reason Support dense crafting blocks
     * @since v0.6
     */
    @Overwrite
    public void selectCPU(int serial) {
        if (Platform.isServer()) {
            this.selectedCpuSerial = serial;

            ICraftingCPU newSelectedCpu = null;
            if (serial != -1) {
                for (ICraftingCPU cpu : this.crazyae$lastKnownCpuSet) {
                    if (this.cpuSerialMap.getOrDefault(cpu, -1) == serial) {
                        newSelectedCpu = cpu;
                        break;
                    }
                }
            }

            CraftingCPUCluster monitor = ((AccessorContainerCraftingCPU) this).getMonitor();

            ICraftingCPU currentlySelected = monitor != null ? monitor : ((IMixinContainerCraftingCPU) this).crazyae$getCurrentWorker();

            if (newSelectedCpu != currentlySelected) {
                this.setCPU(newSelectedCpu);
            }
        }
    }
}
