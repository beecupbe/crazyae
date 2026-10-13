package dev.beecube31.crazyae2.common.parts;

import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.core.settings.TickRates;
import appeng.items.parts.PartModels;
import appeng.menu.locator.MenuLocators;
import appeng.parts.PartModel;
import appeng.parts.automation.ExportBusPart;
import dev.beecube31.crazyae2.common.interfaces.parts.ICrazyPart;
import dev.beecube31.crazyae2.common.util.ContainerOpener;
import dev.beecube31.crazyae2.core.CrazyAE;
import dev.beecube31.crazyae2.core.registries.server.CrazyContainers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.phys.Vec3;

public class PartImprovedExportBus extends ExportBusPart implements ICrazyPart {
    @PartModels
    public static final ResourceLocation MODEL_BASE = CrazyAE.makeId("part/improved_export_bus_base");
    @PartModels
    public static final IPartModel MODELS_OFF = new PartModel(MODEL_BASE, new ResourceLocation("ae2", "part/export_bus_off"));
    @PartModels
    public static final IPartModel MODELS_ON = new PartModel(MODEL_BASE, new ResourceLocation("ae2", "part/export_bus_on"));
    @PartModels
    public static final IPartModel MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE, new ResourceLocation("ae2", "part/export_bus_has_channel"));

    public PartImprovedExportBus(IPartItem<?> partItem) {
        super(partItem);
    }

    @Override
    protected int getOperationsPerTick() {
        return CrazyBusSpeed.operations(getUpgrades());
    }

    @Override
    public boolean onCrazyPartActivate(Player player, InteractionHand hand, Vec3 pos) {
        if (!isClientSide()) {
            ContainerOpener.open(getMenuType(), player, MenuLocators.forPart(this));
        }
        return true;
    }

    @Override
    public TickingRequest getCrazyTickingRequest(IGridNode node) {
        return new TickingRequest(1, TickRates.ExportBus.getMax(), this.isSleeping(), true);
    }

    @Override
    protected MenuType<?> getMenuType() {
        return CrazyContainers.IMPROVED_EXPORT_BUS.get();
    }

    @Override
    public IPartModel getStaticModels() {
        if (this.isActive() && this.isPowered()) {
            return MODELS_HAS_CHANNEL;
        } else if (this.isPowered()) {
            return MODELS_ON;
        }
        return MODELS_OFF;
    }
}
