package dev.beecube31.crazyae2.client.render;

import appeng.api.orientation.BlockOrientation;
import appeng.client.api.renderer.parts.PartDynamicRenderState;
import appeng.client.api.AEKeyRenderState;
import net.minecraft.util.FormattedCharSequence;

public class ThroughputMonitorRenderState extends PartDynamicRenderState {
    public BlockOrientation orientation;
    public final AEKeyRenderState what = new AEKeyRenderState();

    public FormattedCharSequence amountText;
    public int amountWidth;
    public int amountColor;

    public FormattedCharSequence rateText;
    public int rateWidth;
    public int rateColor;
}
