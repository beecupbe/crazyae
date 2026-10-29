package dev.beecube31.crazyae2.common.parts;

import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.core.HolderLookup;
import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.IPartItem;
import appeng.items.tools.NetworkToolItem;
import appeng.parts.reporting.ConversionMonitorPart;
import dev.beecube31.crazyae2.common.util.ThroughputTracker;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class PartThroughputConversionMonitor extends ConversionMonitorPart implements IGridTickable, IThroughputMonitorPart {
    private final ThroughputTracker tracker = new ThroughputTracker();
    private Object lastKey;

    public PartThroughputConversionMonitor(IPartItem<?> partItem) {
        super(partItem);
        getMainNode().addService(IGridTickable.class, this);
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode node) {
        return new TickingRequest(20, 20, false);
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        var displayed = getDisplayed();
        if (displayed == null) {
            tracker.reset();
            lastKey = null;
            return TickRateModulation.SAME;
        }
        if (!displayed.equals(lastKey)) {
            tracker.reset();
            lastKey = displayed;
        }
        if (tracker.sample(getAmount(), ticksSinceLastCall)) {
            getHost().markForUpdate();
        }
        return TickRateModulation.SAME;
    }

    @Override
    public boolean onUseItemOn(ItemStack heldItem, Player player, InteractionHand hand, Vec3 pos) {
        if (heldItem.getItem() instanceof NetworkToolItem) {
            if (!isClientSide()) {
                var unit = tracker.cycleUnit();
                getHost().markForUpdate();
                player.sendOverlayMessage(Component.translatable("message.crazyae.throughput_unit." + unit.name().toLowerCase()));
            }
            return true;
        }
        return super.onUseItemOn(heldItem, player, hand, pos);
    }

    @Override
    public void writeToStream(RegistryFriendlyByteBuf data) {
        super.writeToStream(data);
        tracker.write(data);
    }

    @Override
    public boolean readFromStream(RegistryFriendlyByteBuf data) {
        boolean redraw = super.readFromStream(data);
        return tracker.read(data) || redraw;
    }

    @Override
    public void writeToNBT(ValueOutput data) {
        super.writeToNBT(data);
        tracker.save(data);
    }

    @Override
    public void readFromNBT(ValueInput data) {
        super.readFromNBT(data);
        tracker.load(data);
    }

    @Override
    public ThroughputTracker getThroughputTracker() {
        return tracker;
    }
}
