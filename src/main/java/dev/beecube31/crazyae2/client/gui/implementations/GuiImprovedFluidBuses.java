package dev.beecube31.crazyae2.client.gui.implementations;

import appeng.fluids.util.IAEFluidTank;
import dev.beecube31.crazyae2.client.gui.slot.AEFluidSlot;
import dev.beecube31.crazyae2.client.gui.slot.OptionalAEFluidSlot;
import dev.beecube31.crazyae2.common.containers.ContainerImprovedFluidBuses;
import dev.beecube31.crazyae2.common.parts.implementations.fluid.CrazyAEPartSharedFluidBus;
import dev.beecube31.crazyae2.common.parts.implementations.fluid.PartFluidImportBusImp;
import dev.beecube31.crazyae2.common.i18n.CrazyAEGuiText;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiImprovedFluidBuses extends GuiCrazyAEUpgradeable {
    private final CrazyAEPartSharedFluidBus bus;

    public GuiImprovedFluidBuses(InventoryPlayer inventoryPlayer, CrazyAEPartSharedFluidBus te) {
        super(new ContainerImprovedFluidBuses(inventoryPlayer, te));
        this.bus = te;
    }

    @Override
    public void initGui() {
        super.initGui();

        final ContainerImprovedFluidBuses container = (ContainerImprovedFluidBuses) this.inventorySlots;
        final IAEFluidTank inv = this.bus.getConfig();
        final int y = 40;
        final int x = 80;

        this.guiSlots.add(new AEFluidSlot(inv, 0, 0, x, y));
        this.guiSlots.add(new OptionalAEFluidSlot(inv, container, 1, 1, 1, x, y, -1, 0));
        this.guiSlots.add(new OptionalAEFluidSlot(inv, container, 2, 2, 1, x, y, 1, 0));
        this.guiSlots.add(new OptionalAEFluidSlot(inv, container, 3, 3, 1, x, y, 0, -1));
        this.guiSlots.add(new OptionalAEFluidSlot(inv, container, 4, 4, 1, x, y, 0, 1));

        this.guiSlots.add(new OptionalAEFluidSlot(inv, container, 5, 5, 2, x, y, -1, -1));
        this.guiSlots.add(new OptionalAEFluidSlot(inv, container, 6, 6, 2, x, y, 1, -1));
        this.guiSlots.add(new OptionalAEFluidSlot(inv, container, 7, 7, 2, x, y, -1, 1));
        this.guiSlots.add(new OptionalAEFluidSlot(inv, container, 8, 8, 2, x, y, 1, 1));
    }

    @Override
    protected CrazyAEGuiText getName() {
        return this.bc instanceof PartFluidImportBusImp ? CrazyAEGuiText.IMP_FLUID_IMPORT_BUS : CrazyAEGuiText.IMP_FLUID_EXPORT_BUS;
    }
}
