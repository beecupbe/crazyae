package dev.beecube31.crazyae2.common.util;

import dev.beecube31.crazyae2.common.i18n.CrazyAETooltip;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.text.DecimalFormat;
import java.util.List;

public final class CrazyTooltips {
    private static final DecimalFormat NUMBER = new DecimalFormat("#,##0.##");

    private CrazyTooltips() {}

    public static String format(double v) {
        return NUMBER.format(v);
    }

    public static void addReqAePerTick(List<Component> tooltip, double val) {
        tooltip.add(CrazyAETooltip.REQUIRE_AE_PER_TICK.getComponentWithArgs(format(val)).withStyle(ChatFormatting.GRAY));
    }

    public static void addReqAePerJob(List<Component> tooltip, double val) {
        tooltip.add(CrazyAETooltip.REQUIRE_AE_PER_JOB.getComponentWithArgs(format(val)).withStyle(ChatFormatting.GRAY));
    }

    public static void addReqChannel(List<Component> tooltip) {
        tooltip.add(CrazyAETooltip.REQUIRE_CHANNEL.getComponent().withStyle(ChatFormatting.GRAY));
    }

    public static void addMachineAttributes(List<Component> tooltip, MachineAttributes attrib) {
        if (attrib.aePerTick() > 0) {
            addReqAePerTick(tooltip, attrib.aePerTick());
        }
        if (attrib.aePerJob() > 0) {
            addReqAePerJob(tooltip, attrib.aePerJob());
        }
        if (attrib.requireChannel()) {
            addReqChannel(tooltip);
        }
    }

    public record MachineAttributes(double aePerTick, double aePerJob, boolean requireChannel) {
        public static MachineAttributes channel() {
            return new MachineAttributes(0, 0, true);
        }

        public static MachineAttributes perTick(double ae, boolean channel) {
            return new MachineAttributes(ae, 0, channel);
        }
    }
}
