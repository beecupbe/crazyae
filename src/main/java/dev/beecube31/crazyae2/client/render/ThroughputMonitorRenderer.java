package dev.beecube31.crazyae2.client.render;

import appeng.api.orientation.BlockOrientation;
import appeng.client.api.renderer.parts.PartRenderer;
import appeng.client.render.BlockEntityRenderHelper;
import appeng.parts.reporting.AbstractMonitorPart;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.beecube31.crazyae2.common.parts.IThroughputMonitorPart;
import dev.beecube31.crazyae2.common.util.ThroughputTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.util.LightCoordsUtil;

public class ThroughputMonitorRenderer<T extends AbstractMonitorPart & IThroughputMonitorPart>
        implements PartRenderer<T, ThroughputMonitorRenderState> {

    @Override
    public ThroughputMonitorRenderState createState() {
        return new ThroughputMonitorRenderState();
    }

    @Override
    public Class<ThroughputMonitorRenderState> stateClass() {
        return ThroughputMonitorRenderState.class;
    }

    @Override
    public void extract(T part, ThroughputMonitorRenderState state, float partialTicks) {
        var displayed = part.getDisplayed();
        if (!part.isActive() || displayed == null) {
            state.what.clear();
            state.amountText = null;
            state.rateText = null;
            return;
        }

        var font = Minecraft.getInstance().font;
        var tracker = part.getThroughputTracker();

        state.orientation = BlockOrientation.get(part.getSide(), part.getSpin());
        int seed = (int) part.getHost().getBlockEntity().getBlockPos().asLong();
        state.what.extract(displayed, part.getLevel(), seed);

        state.amountText = Component.literal(ThroughputTracker.formatAmount(displayed, part.getAmount())).getVisualOrderText();
        state.amountWidth = font.width(state.amountText);
        state.amountColor = part.getColor().contrastTextColor | 0xFF000000;

        state.rateText = Component.literal(tracker.formatRate()).getVisualOrderText();
        state.rateWidth = font.width(state.rateText);
        int rateColor = tracker.rateColor();
        state.rateColor = (rateColor & 0xFC000000) == 0 ? rateColor | 0xFF000000 : rateColor;
    }

    @Override
    public void submit(ThroughputMonitorRenderState state, PoseStack poseStack, SubmitNodeCollector nodes,
                       CameraRenderState cameraRenderState) {
        if (state.what.isEmpty()) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        BlockEntityRenderHelper.rotateToFace(poseStack, state.orientation);
        poseStack.translate(0, 0.05, 0.5);

        poseStack.pushPose();
        poseStack.translate(0, 0.0393, 0);
        BlockEntityRenderHelper.submitRenderItem2d(poseStack, state.what, 0.32f, nodes);
        poseStack.popPose();

        submitLine(poseStack, nodes, state.amountText, state.amountWidth, -0.1337f, 1.0f / 62.0f, state.amountColor);
        submitLine(poseStack, nodes, state.rateText, state.rateWidth, -0.2222f, 1.0f / 70.0f, state.rateColor);

        poseStack.popPose();
    }

    private static void submitLine(PoseStack poseStack, SubmitNodeCollector nodes,
                                   net.minecraft.util.FormattedCharSequence text, int width,
                                   float y, float scale, int color) {
        if (text == null) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0.0f, y, 0.02f);
        poseStack.scale(scale * 0.5f, -scale * 0.5f, scale * 0.5f);
        poseStack.translate(-0.5f * width, 0.0f, 0.5f);
        nodes.submitText(poseStack, 0, 0, text, false, Font.DisplayMode.NORMAL, LightCoordsUtil.FULL_BRIGHT,
                color, 0, 0);
        poseStack.popPose();
    }
}
