package dev.beecube31.crazyae2.common.tile.botania;

import appeng.api.definitions.IItemDefinition;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.storage.data.IAEItemStack;
import appeng.me.helpers.MachineSource;
import appeng.tile.inventory.AppEngInternalInventory;
import com.google.common.base.Preconditions;
import dev.beecube31.crazyae2.common.containers.base.slot.RestrictedSlot;
import dev.beecube31.crazyae2.common.enums.BotaniaMechanicalDeviceType;
import dev.beecube31.crazyae2.common.parts.implementations.CrazyAEBlockUpgradeInv;
import dev.beecube31.crazyae2.common.util.NBTUtils;
import dev.beecube31.crazyae2.common.util.inv.CrazyAEInternalInv;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.recipe.RecipeElvenTrade;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class TileMechanicalElventrade extends TileBotaniaMechanicalMachineBase {
    public TileMechanicalElventrade() {
        super();

        this.craftingInputInv = new CrazyAEInternalInv(this, 16, 64);
        (this.craftingOutputInv = new CrazyAEInternalInv(this, 16, 64)).setFilter(new DisabledFilter());

        this.actionSource = new MachineSource(this);
        final Block block = CrazyAE.definitions().blocks().mechanicalElventrade().maybeBlock().orElse(null);
        Preconditions.checkNotNull(block);
        this.upgrades = new CrazyAEBlockUpgradeInv(block, this, this.getUpgradeSlots());

        this.internalPatternsStorageInv.setItemFilter(RestrictedSlot.PlaceableItemType.ELVENTRADE_ENCODED_PATTERN.associatedFilter);
    }

    @Override
    public boolean pushPattern(ICraftingPatternDetails iCraftingPatternDetails, InventoryCrafting inventoryCrafting) {
        if (this.tasksQueued >= this.tasksMaxAmt) return false;

        this.tasksQueued++;
        this.queueMap.add(new CraftingTask(iCraftingPatternDetails.getCondensedOutputs(), 0));
        return true;
    }

    public boolean validateRecipe() {
        this.isRecipeValidated = false;
        for (int i = 0; i < this.craftingOutputInv.getSlots(); i++) {
            this.craftingOutputInv.setStackInSlot(i, ItemStack.EMPTY);
        }
        List<ItemStack> input = new ArrayList<>();
        for (ItemStack s : this.craftingInputInv) {
            if (!s.isEmpty()) {
                input.add(s);
            }
        }

        final RecipeElvenTrade recipe = getMatchingRecipe(input, this.craftingInputInv);
        if (recipe != null) {
            this.isRecipeValidated = true;
            for (int i = 0; i < recipe.getOutputs().size(); i++) {
                this.craftingOutputInv.setStackInSlot(i, recipe.getOutputs().get(i));
            }
            return true;
        }

        return false;
    }

    public boolean findRecipe() {
        this.isRecipeValidated = false;
        for (int i = 0; i < this.craftingOutputInv.getSlots(); i++) {
            this.craftingOutputInv.setStackInSlot(i, ItemStack.EMPTY);
        }

        for (int i = 0; i < this.craftingInputInv.getSlots(); i++) {
            this.craftingInputInv.setStackInSlot(i, ItemStack.EMPTY);
        }

        for (RecipeElvenTrade recipe : BotaniaAPI.elvenTradeRecipes) {
            for (ItemStack output : recipe.getOutputs()) {
                if (output == this.findSlot.getStackInSlot(0)) {
                    this.isRecipeValidated = true;
                    for (int i = 0; i < recipe.getOutputs().size(); i++) {
                        this.craftingOutputInv.setStackInSlot(i, recipe.getOutputs().get(i));
                    }
                    return true;
                }
            }
        }

        return false;
    }

    @Nullable
    public static RecipeElvenTrade getMatchingRecipe(@Nonnull List<ItemStack> input, @Nonnull AppEngInternalInventory inv) {
        for (RecipeElvenTrade recipe : BotaniaAPI.elvenTradeRecipes) {
            if (compareRecipes(recipe, input, inv)) {
                return recipe;
            }
        }

        return null;
    }

    private static boolean compareRecipes(RecipeElvenTrade recipe, @Nonnull List<ItemStack> input, @Nonnull AppEngInternalInventory inv) {
        if (recipe.matches(input, false) && input.size() == recipe.getInputs().size()) {
            for (int i = 0; i < inv.getSlots(); i++) {
                if (i > recipe.getInputs().size() - 1) break;
                ItemStack invStack = inv.getStackInSlot(i);

                if (!invStack.isEmpty() && recipe.getInputs().get(i) instanceof ItemStack ris) {
                    invStack.setCount(ris.getCount());
                } else if (recipe.getInputs().get(i) instanceof String) {
                    invStack.setCount(1);
                }
            }
            return true;
        }

        return false;
    }

    @NotNull
    @Override
    public TickRateModulation tickingRequest(@NotNull IGridNode iGridNode, int ticksSinceLastCall) {
        if (!this.cached) {
            if (this.getProxy().isActive()) {
                this.notifyPatternsChanged();
            }
            return TickRateModulation.SLOWER;
        }

        if (!this.queueMap.isEmpty()) {
            int itemsAmt = this.itemsPerTick;

            for (int j = 0; j < this.queueMap.size(); j++) {
                if (itemsAmt >= j) {
                    CraftingTask s = this.queueMap.get(j);
                    IAEItemStack[] ais = s.getTaskItems();
                    final int progress = s.getProgress();

                    if (progress >= 100) {
                        if (!this.tryUseMana(500)) {
                            continue;
                        }

                        boolean accepted = true;
                        for (IAEItemStack stack : ais) {
                            IAEItemStack result = this.pushItemsOut(stack);
                            if (result != null) {
                                accepted = false;
                                break;
                            }
                        }

                        if (accepted) {
                            this.queueMap.remove(j);
                            this.tasksQueued--;
                            this.addCompletedOperations();
                        } else {
                            break;
                        }

                        continue;
                    }

                    s.addProgress(this.progressPerTick);
                } else {
                    break;
                }
            }
        }

        return TickRateModulation.URGENT;
    }

    public void encodePattern() {
        if (!this.patternsInv.getStackInSlot(0).isEmpty()) {
            ArrayList<ItemStack> inputs = new ArrayList<>();
            ArrayList<ItemStack> outputs = new ArrayList<>();
            for (ItemStack s : this.craftingInputInv) {
                if (!s.isEmpty()) {
                    inputs.add(s);
                }
            }
            for (ItemStack s : this.craftingOutputInv) {
                if (!s.isEmpty()) {
                    outputs.add(s);
                }
            }

            CrazyAE.definitions().items().elventradeEncodedPattern().maybeStack(1).ifPresent(maybePattern -> {
                final ItemStack[] in = inputs.toArray(new ItemStack[0]);
                final ItemStack[] out = outputs.toArray(new ItemStack[0]);
                final NBTTagCompound encodedValue = new NBTTagCompound();

                final NBTTagList tagIn = new NBTTagList();
                final NBTTagList tagOut = new NBTTagList();

                for (final ItemStack i : in) {
                    tagIn.appendTag(NBTUtils.createItemTag(i));
                }

                for (final ItemStack i : out) {
                    tagOut.appendTag(NBTUtils.createItemTag(i));
                }

                encodedValue.setTag("input", tagIn);
                encodedValue.setTag("output", tagOut);
                encodedValue.setBoolean("crafting", true);
                encodedValue.setBoolean("substitute", false);

                maybePattern.setTagCompound(encodedValue);

                for (int i = 0; i < this.internalPatternsStorageInv.getSlots(); i++) {
                    if (this.internalPatternsStorageInv.getStackInSlot(i).isEmpty()) {
                        this.internalPatternsStorageInv.setStackInSlot(i, maybePattern);
                        this.patternsInv.extractItem(0, 1, false);
                        break;
                    }
                }
                this.cached = false;
                this.updateCraftingList();
            });
        }
    }

    @Override
    public BotaniaMechanicalDeviceType getType() {
        return BotaniaMechanicalDeviceType.ELVENTRADE;
    }

    @Override
    public TileBotaniaMechanicalMachineBase getMechanicalTile() {
        return this;
    }

    @Override
    public List<CraftingTask> getQueueMap() {
        return this.queueMap;
    }

    @Override
    public int getTasksQueued() {
        return this.tasksQueued;
    }

    @Override
    public int getTasksMaxAmt() {
        return this.tasksMaxAmt;
    }

    @Override
    public int getProgressPerTick() {
        return this.progressPerTick;
    }

    @Override
    public int getItemsPerTick() {
        return this.itemsPerTick;
    }

    @Override
    public IItemDefinition getBlock() {
        return CrazyAE.definitions().blocks().mechanicalElventrade();
    }
}
