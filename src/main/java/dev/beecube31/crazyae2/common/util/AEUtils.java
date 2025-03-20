package dev.beecube31.crazyae2.common.util;

import appeng.api.config.Actionable;
import appeng.api.definitions.IItemDefinition;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.core.stats.Stats;
import appeng.util.item.AEItemStack;
import com.google.common.base.Preconditions;
import net.minecraft.item.ItemStack;

public class AEUtils {

    /**
     * Creates IAEItemStack and sets its stack size.
     */
    public static IAEItemStack createAEStackFromItemstack(ItemStack is, long amt) {
        Preconditions.checkNotNull(is);
        if (amt < 0) return null;

        return !is.isEmpty() ? AEItemStack.fromItemStack(is).setStackSize(amt) : null;
    }

    /**
     * Creates IAEItemStack from IItemDefinition and sets its stack size.
     */
    public static IAEItemStack createAEStackFromDefinition(IItemDefinition is, long amt) {
        Preconditions.checkNotNull(is);
        if (amt < 0) return null;

        ItemStack out = is.maybeStack(1).orElse(ItemStack.EMPTY);

        return !out.isEmpty() ? AEItemStack.fromItemStack(out).setStackSize(amt) : null;
    }

    /**
     * Same as {@link appeng.util.Platform#poweredExtraction(IEnergySource, IMEInventory, IAEStack, IActionSource, Actionable)  poweredExtraction}, but its UNPOWERED
     */
    public static <T extends IAEStack<T>> T extractFromME(final IMEInventory<T> cell, final T request, final IActionSource src, final Actionable mode) {
        if (cell == null) return null;
        if (request == null) return null;
        if (src == null) return null;
        if (mode == null) return null;

        final T possible = cell.extractItems(request.copy(), Actionable.SIMULATE, src);

        long retrieved = 0;
        if (possible != null) {
            retrieved = possible.getStackSize();
        }

        if (retrieved > 0) {
            if (mode == Actionable.MODULATE) {
                possible.setStackSize(retrieved);
                final T ret = cell.extractItems(possible, Actionable.MODULATE, src);

                if (ret != null) {
                    src.player().ifPresent(player ->
                            Stats.ItemsExtracted.addToPlayer(player, (int) ret.getStackSize())
                    );
                }
                return ret;
            } else {
                return possible.setStackSize(retrieved);
            }
        }

        return null;
    }

    /**
     * Same as {@link appeng.util.Platform#poweredInsert(IEnergySource, IMEInventory, IAEStack, IActionSource, Actionable) poweredInsert}, but its UNPOWERED
     */
    public static <T extends IAEStack<T>> T injectToME(final IMEInventory<T> cell, final T input, final IActionSource src, final Actionable mode) {
        if (cell == null) return input;
        if (input == null) return null;
        if (src == null) return input;
        if (mode == null) return input;


        final T possible = cell.injectItems(input, Actionable.SIMULATE, src);

        long stored = input.getStackSize();
        if (possible != null) {
            stored -= possible.getStackSize();
        }

        if (stored > 0) {
            if (mode == Actionable.MODULATE) {
                if (stored < input.getStackSize()) {
                    final long original = input.getStackSize();
                    final T leftover = input.copy();
                    final T split = input.copy();

                    leftover.decStackSize(stored);
                    split.setStackSize(stored);
                    leftover.add(cell.injectItems(split, Actionable.MODULATE, src));

                    src.player().ifPresent(player ->
                    {
                        final long diff = original - leftover.getStackSize();
                        Stats.ItemsInserted.addToPlayer(player, (int) diff);
                    });

                    return leftover;
                }

                final T ret = cell.injectItems(input, Actionable.MODULATE, src);

                src.player().ifPresent(player ->
                {
                    final long diff = ret == null ? input.getStackSize() : input.getStackSize() - ret.getStackSize();
                    Stats.ItemsInserted.addToPlayer(player, (int) diff);
                });

                return ret;
            } else {
                final T ret = input.copy().setStackSize(input.getStackSize() - stored);
                return (ret != null && ret.getStackSize() > 0) ? ret : null;
            }
        }

        return input;
    }
}
