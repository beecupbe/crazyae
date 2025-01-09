package dev.beecube31.crazyae2.mixins.aefixes.crafting;

import appeng.api.config.Actionable;
import appeng.api.config.FuzzyMode;
import appeng.api.networking.IGrid;
import appeng.api.networking.crafting.*;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.core.AELog;
import appeng.crafting.CraftingLink;
import appeng.crafting.CraftingWatcher;
import appeng.crafting.MECraftingInventory;
import appeng.me.cache.CraftingGridCache;
import appeng.me.cluster.IAECluster;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import appeng.me.helpers.MachineSource;
import appeng.util.Platform;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.beecube31.crazyae2.common.interfaces.crafting.IFastCraftingHandler;
import dev.beecube31.crazyae2.common.util.AEUtils;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(value = CraftingCPUCluster.class, remap = false)
public abstract class MixinCraftingCPUCluster implements IAECluster, ICraftingCPU {

    @Shadow protected abstract void postChange(IAEItemStack diff, IActionSource src);

    @Shadow private MachineSource machineSrc;

    @Shadow private MECraftingInventory inventory;

    @Shadow protected abstract World getWorld();

    @Shadow private IItemList<IAEItemStack> waitingFor;

    @Shadow private boolean somethingChanged;

    @Shadow private int remainingOperations;

    @Shadow protected abstract IGrid getGrid();

    @Shadow protected abstract void markDirty();

    @Shadow private ICraftingLink myLastLink;

    @Shadow private IAEItemStack finalOutput;

    @Shadow private long startItemCount;

    @Shadow @Final private static String LOG_MARK_AS_COMPLETE;

    @Shadow private long remainingItemCount;

    @Shadow private long lastTime;

    @Shadow private long elapsedTime;

    @Shadow private boolean isComplete;

    @Shadow protected abstract void notifyRequester(boolean cancelled);

    @Shadow private UUID requestingPlayerUUID;

    @Shadow @Final private Map<?, ?> tasks;

    @Shadow private boolean waiting;

    @Inject(
            method = "executeCrafting",
            at = @At(
                    value = "INVOKE",
                    target = "Lappeng/api/networking/energy/IEnergyGrid;extractAEPower(DLappeng/api/config/Actionable;Lappeng/api/config/PowerMultiplier;)D",
                    shift = At.Shift.BEFORE
            ),
            remap = false,
            cancellable = true
    )
    private void injectFastExecuting(
            IEnergyGrid eg,
            CraftingGridCache cc,
            CallbackInfo ci,
            @Local ICraftingMedium m,
            @Local ICraftingPatternDetails details,
            @Local LocalRef<Map.Entry<?, ?>> e
    ) {
        if (details.isCraftable() && m instanceof IFastCraftingHandler handler) {
            boolean found = false;
            final IAEItemStack[] inputs = details.getCondensedInputs();
            Collection<IAEItemStack> extractedItems = new ArrayList<>(inputs.length);

            for (int slot = 0; slot < inputs.length; slot++) {
                IAEItemStack input = inputs[slot];
                if (input != null) {
                    Collection<IAEItemStack> itemList = crazyae$getMatchingItems(input, details, slot);

                    for (IAEItemStack candidate : itemList) {
                        IAEItemStack copy = candidate.copy();
                        copy.setStackSize(input.getStackSize());

                        final IAEItemStack ais = this.inventory.extractItems(copy, Actionable.MODULATE, this.machineSrc);

                        if (ais != null) {
                            this.postChange(ais, this.machineSrc);
                            extractedItems.add(ais);
                            found = true;
                            break;
                        }
                    }
                }
            }

            if (!found) {
                crazyae$returnItems(extractedItems);
                ci.cancel();
                return;
            }

            if (handler.fastPushPattern(details)) {
                crazyae$handleSuccessfulCrafting(details, e, extractedItems);
                this.markDirty();
                ci.cancel();
            }
        }
    }

    @Unique
    private Collection<IAEItemStack> crazyae$getMatchingItems(IAEItemStack input, ICraftingPatternDetails patternDetails, int slot) {
        Collection<IAEItemStack> itemList;
        if (patternDetails.canSubstitute()) {
            final List<IAEItemStack> substitutes = patternDetails.getSubstituteInputs(slot);
            itemList = new ArrayList<>(substitutes.size());
            for (IAEItemStack stack : substitutes) {
                itemList.addAll(this.inventory.getItemList().findFuzzy(stack, FuzzyMode.IGNORE_ALL));
            }
        } else {
            itemList = new ArrayList<>(1);
            final IAEItemStack precise = this.inventory.getItemList().findPrecise(input);
            if (precise != null) {
                itemList.add(precise);
            } else if (input.getDefinition().getItem().isDamageable() || Platform.isGTDamageableItem(input.getDefinition().getItem())) {
                itemList.addAll(this.inventory.getItemList().findFuzzy(input, FuzzyMode.IGNORE_ALL));
            }
        }
        return itemList;
    }

    @Unique
    private void crazyae$returnItems(Collection<IAEItemStack> extractedItems) {
        for (IAEItemStack extractedItem : extractedItems) {
            this.inventory.injectItems(extractedItem, Actionable.MODULATE, this.machineSrc);
        }
    }

    @Unique
    private void crazyae$handleSuccessfulCrafting(ICraftingPatternDetails patternDetails, LocalRef<Map.Entry<?, ?>> e, Collection<IAEItemStack> extractedItems) {
        this.somethingChanged = true;
        this.remainingOperations--;

        Collection<IAEItemStack> condensedOutputs = Arrays.asList(patternDetails.getCondensedOutputs());
        crazyae$postCraftingOutputs(condensedOutputs, false);

//        crazyae$postCraftingOutputs(extractedItems, false);

        this.markDirty();

        try {
            long currentValue = AEUtils.fValue.getLong(e.get().getValue());
            AEUtils.fValue.setLong(e.get().getValue(), currentValue - 1);

            if (currentValue - 1 <= 0) {
                return;
            }
        } catch (IllegalAccessException ex) {
            // :(
        }
    }

    @Unique
    private void crazyae$postCraftingOutputs(Collection<IAEItemStack> outputs, boolean createCopy) {
        CraftingGridCache gridCache = this.getGrid().getCache(ICraftingGrid.class);
        for (IAEItemStack output : outputs) {
            if (createCopy) {
                output = output.copy();
            }

            if (gridCache.getInterestManager().containsKey(output)) {
                for (CraftingWatcher watcher : gridCache.getInterestManager().get(output)) {
                    watcher.getHost().onRequestChange(gridCache, output);
                }
            }

            this.postChange(output, this.machineSrc);
            this.waitingFor.add(output);
        }
    }
}

