package dev.beecube31.crazyae2.common.parts;

import appeng.api.networking.IGridNode;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.orientation.BlockOrientation;
import appeng.api.parts.IPartItem;
import appeng.client.render.BlockEntityRenderHelper;
import appeng.items.tools.NetworkToolItem;
import appeng.parts.reporting.StorageMonitorPart;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.beecube31.crazyae2.common.util.ThroughputTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class PartThroughputStorageMonitor extends StorageMonitorPart implements IGridTickable {
    private final ThroughputTracker tracker = new ThroughputTracker();
    private Object lastKey;

    public PartThroughputStorageMonitor(IPartItem<?> partItem) {
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
                player.displayClientMessage(Component.translatable("message.crazyae.throughput_unit." + unit.name().toLowerCase()), true);
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
    public void writeToNBT(CompoundTag data, HolderLookup.Provider registries) {
        super.writeToNBT(data, registries);
        tracker.save(data);
    }

    @Override
    public void readFromNBT(CompoundTag data, HolderLookup.Provider registries) {
        super.readFromNBT(data, registries);
        tracker.load(data);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderDynamic(float partialTicks, PoseStack poseStack, MultiBufferSource buffers,
                              int combinedLightIn, int combinedOverlayIn) {
        var what = getDisplayed();
        if (!isActive() || what == null) {
            return;
        }

        poseStack.pushPose();
        var orientation = BlockOrientation.get(getSide(), getSpin());
        poseStack.translate(0.5, 0.5, 0.5);
        BlockEntityRenderHelper.rotateToFace(poseStack, orientation);
        poseStack.translate(0, 0.05, 0.5);

        poseStack.pushPose();
        poseStack.translate(0, 0.0393, 0);
        BlockEntityRenderHelper.renderItem2d(poseStack, buffers, what, 0.32f, LightTexture.FULL_BRIGHT, getLevel());
        poseStack.popPose();

        renderLine(poseStack, buffers, ThroughputTracker.formatAmount(what, getAmount()), -0.1337f, 1.0f / 62.0f,
                getColor().contrastTextColor);
        renderLine(poseStack, buffers, tracker.formatRate(), -0.2222f, 1.0f / 70.0f, tracker.rateColor());

        poseStack.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    private static void renderLine(PoseStack poseStack, MultiBufferSource buffers, String text, float y, float scale,
                                   int color) {
        var fr = Minecraft.getInstance().font;
        var width = fr.width(text);
        poseStack.pushPose();
        poseStack.translate(0.0f, y, 0.02f);
        poseStack.scale(scale * 0.5f, -scale * 0.5f, scale * 0.5f);
        poseStack.translate(-0.5f * width, 0.0f, 0.5f);
        fr.drawInBatch(text, 0, 0, color, false, poseStack.last().pose(), buffers, Font.DisplayMode.NORMAL, 0,
                LightTexture.FULL_BRIGHT);
        poseStack.popPose();
    }
}
