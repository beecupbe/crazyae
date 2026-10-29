package dev.beecube31.crazyae2.common.util;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.energy.IEnergyService;
import appeng.client.gui.style.Blitter;
import appeng.core.definitions.AEBlocks;
import com.google.common.base.Preconditions;
import dev.beecube31.crazyae2.client.components.ColorContainer;
import dev.beecube31.crazyae2.client.gui.CrazyAEBaseGui;
import dev.beecube31.crazyae2.common.enums.crafting.CrazyAECraftingUnitType;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.fml.ModList;

import javax.annotation.Nullable;

public class Utils {
    public static boolean canExtractEnergy(IEnergyService cache, double amt, Actionable mode, PowerMultiplier multiplier) {
        return cache.extractAEPower(amt, mode, multiplier) > 0;
    }

    public static ItemStack toStack(ItemLike something) {
        Preconditions.checkNotNull(something, "Cannot create ItemStack from null");
        return new ItemStack(something);
    }

    public static String getClassName(String clazz) {
        String[] split = clazz.split("\\.");
        return split[split.length - 1];
    }

    public static String getClassName(Class<?> clazz) {
        return getClassName(clazz.getName());
    }

    @Nullable
    public static ColorContainer getGuiColorContainerFromOpenedScreen() {
        if (Minecraft.getInstance().screen instanceof CrazyAEBaseGui<?> screen) {
            return screen.getGuiHue();
        }

        return null;
    }

    public static void fillFromContainer(Blitter blitter, ColorContainer container) {
        if (blitter == null || container == null) return;

        blitter.color(container.getRed(), container.getGreen(), container.getBlue());
    }
}
