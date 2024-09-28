package dev.beecube31.crazyae2.common.parts.implementations.fluid;

import appeng.api.config.*;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.IPartModel;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.data.IAEFluidStack;
import appeng.core.settings.TickRates;
import appeng.fluids.util.AEFluidStack;
import appeng.items.parts.PartModels;
import appeng.me.GridAccessException;
import appeng.me.helpers.MachineSource;
import appeng.parts.PartModel;
import dev.beecube31.crazyae2.Tags;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;

public class PartFluidImportBusImp extends CrazyAEPartSharedFluidBus {
    public static final ResourceLocation MODEL_BASE = new ResourceLocation(Tags.MODID, "part/fluid_import_bus_base_diamond");
    @PartModels
    public static final IPartModel MODELS_OFF = new PartModel(MODEL_BASE, new ResourceLocation(Tags.MODID, "part/fluid_import_bus_off_diamond"));
    @PartModels
    public static final IPartModel MODELS_ON = new PartModel(MODEL_BASE, new ResourceLocation(Tags.MODID, "part/fluid_import_bus_on_diamond"));
    @PartModels
    public static final IPartModel MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE, new ResourceLocation(Tags.MODID, "part/fluid_import_bus_has_channel_diamond"));

    private final IActionSource source;

    public PartFluidImportBusImp(ItemStack is) {
        super(is);
        this.getConfigManager().registerSetting(Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE);
        this.getConfigManager().registerSetting(Settings.FUZZY_MODE, FuzzyMode.IGNORE_ALL);
        this.getConfigManager().registerSetting(Settings.CRAFT_ONLY, YesNo.NO);
        this.getConfigManager().registerSetting(Settings.SCHEDULING_MODE, SchedulingMode.DEFAULT);
        this.source = new MachineSource(this);
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        return new TickingRequest(1, TickRates.FluidImportBus.getMax(), this.isSleeping(), false);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        return this.canDoBusWork() ? this.doBusWork() : TickRateModulation.IDLE;
    }

    @Override
    protected TickRateModulation doBusWork() {
        if (!this.canDoBusWork()) {
            return TickRateModulation.IDLE;
        }

        final TileEntity te = this.getConnectedTE();

        if (te != null && te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, this.getSide().getFacing().getOpposite())) {
            try {
                final IFluidHandler fh = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, this.getSide().getFacing().getOpposite());
                final IMEMonitor<IAEFluidStack> inv = this.getProxy().getStorage().getInventory(this.getChannel());

                if (fh != null) {
                    final FluidStack fluidStack = fh.drain(this.calculateItemsToSend(), false);

                    if (this.filterEnabled() && !this.isInFilter(fluidStack)) {
                        return TickRateModulation.SLOWER;
                    }

                    final AEFluidStack aeFluidStack = AEFluidStack.fromFluidStack(fluidStack);

                    if (aeFluidStack != null) {
                        final IAEFluidStack notInserted = inv.injectItems(aeFluidStack, Actionable.MODULATE, this.source);

                        if (notInserted != null && notInserted.getStackSize() > 0) {
                            aeFluidStack.decStackSize(notInserted.getStackSize());
                        }

                        fh.drain(aeFluidStack.getFluidStack(), true);

                        return TickRateModulation.URGENT;
                    }

                    return TickRateModulation.IDLE;
                }
            } catch (GridAccessException e) {
                e.printStackTrace();
            }
        }

        return TickRateModulation.SLEEP;
    }

    @Override
    protected boolean canDoBusWork() {
        return this.getProxy().isActive();
    }

    private boolean isInFilter(FluidStack fluid) {
        for (int i = 0; i < this.getConfig().getSlots(); i++) {
            final IAEFluidStack stack = this.getConfig().getFluidInSlot(i);
            if (stack != null && stack.equals(fluid)) {
                return true;
            }
        }
        return false;
    }

    private boolean filterEnabled() {
        for (int i = 0; i < this.getConfig().getSlots(); i++) {
            final IAEFluidStack stack = this.getConfig().getFluidInSlot(i);
            if (stack != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public RedstoneMode getRSMode() {
        return (RedstoneMode) this.getConfigManager().getSetting(Settings.REDSTONE_CONTROLLED);
    }

    @Nonnull
    @Override
    public IPartModel getStaticModels() {
        if (this.isActive() && this.isPowered()) {
            return MODELS_HAS_CHANNEL;
        } else if (this.isPowered()) {
            return MODELS_ON;
        } else {
            return MODELS_OFF;
        }
    }
}
