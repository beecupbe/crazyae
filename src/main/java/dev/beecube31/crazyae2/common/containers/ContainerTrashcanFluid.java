package dev.beecube31.crazyae2.common.containers;

import appeng.api.config.SecurityPermissions;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.util.IConfigManager;
import appeng.fluids.container.IFluidSyncContainer;
import appeng.fluids.helper.FluidSyncHelper;
import appeng.fluids.util.AEFluidStack;
import appeng.fluids.util.IAEFluidTank;
import appeng.util.Platform;
import dev.beecube31.crazyae2.common.tile.trashcans.TileTrashcanFluids;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

import java.util.Collections;
import java.util.Map;

public class ContainerTrashcanFluid extends ContainerCrazyAEUpgradeable implements IFluidSyncContainer {
    private final TileTrashcanFluids tc;
    private FluidSyncHelper sync = null;

    public ContainerTrashcanFluid(InventoryPlayer ip, TileTrashcanFluids te) {
        super(ip, te);
        this.tc = te;
    }

    public IAEFluidTank getFluidConfigInventory() {
        return this.tc.getConfig();
    }

    @Override
    protected int getHeight() {
        return 256;
    }

    @Override
    protected boolean supportCapacity() {
        return false;
    }

    @Override
    protected ItemStack transferStackToContainer(ItemStack input) {
        FluidStack fs = FluidUtil.getFluidContained(input);
        if (fs != null) {
            final IAEFluidTank t = this.getFluidConfigInventory();
            final IAEFluidStack stack = AEFluidStack.fromFluidStack(fs);
            for (int i = 0; i < t.getSlots(); ++i) {
                if (t.getFluidInSlot(i) == null) {
                    t.setFluidInSlot(i, stack);
                    break;
                }
            }
        }
        return input;
    }

    private FluidSyncHelper getSynchHelper() {
        if (this.sync == null) {
            this.sync = new FluidSyncHelper(this.getFluidConfigInventory(), 0);
        }
        return this.sync;
    }

    @Override
    public void detectAndSendChanges() {
        this.verifyPermissions(SecurityPermissions.BUILD, false);

        if (Platform.isServer()) {
            final IConfigManager cm = this.getUpgradeable().getConfigManager();
            this.loadSettingsFromHost(cm);
        }

        this.checkToolbox();

        this.standardDetectAndSendChanges();
    }


    @Override
    protected void standardDetectAndSendChanges() {
        if (Platform.isServer()) {
            this.getSynchHelper().sendDiff(this.listeners);
        }
        super.standardDetectAndSendChanges();
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        this.getSynchHelper().sendFull(Collections.singleton(listener));
    }

    @Override
    protected void setupConfig() {
        this.setupUpgrades();
    }

    @Override
    public void receiveFluidSlots(Map<Integer, IAEFluidStack> fluids) {
        this.getSynchHelper().readPacket(fluids);
    }
}
