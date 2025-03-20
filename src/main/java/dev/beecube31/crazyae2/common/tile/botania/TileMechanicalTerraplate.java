package dev.beecube31.crazyae2.common.tile.botania;

import appeng.api.definitions.IItemDefinition;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.storage.data.IAEItemStack;
import appeng.me.helpers.MachineSource;
import com.google.common.base.Preconditions;
import dev.beecube31.crazyae2.common.enums.BotaniaMechanicalDeviceType;
import dev.beecube31.crazyae2.common.parts.implementations.CrazyAEBlockUpgradeInv;
import dev.beecube31.crazyae2.common.recipes.botania.RecipeRepo;
import dev.beecube31.crazyae2.common.recipes.botania.RecipeTerraplate;
import dev.beecube31.crazyae2.common.util.NBTUtils;
import dev.beecube31.crazyae2.common.util.inv.CrazyAEInternalInv;
import dev.beecube31.crazyae2.common.util.patterns.crafting.TeraplateCraftingPatternDetails;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TileMechanicalTerraplate extends TileBotaniaMechanicalMachineBase {
    protected RecipeTerraplate currentRecipe;

    public TileMechanicalTerraplate() {
        super();

        this.craftingInputInv = new CrazyAEInternalInv(this, 16, 64);
        (this.craftingOutputInv = new CrazyAEInternalInv(this, 1, 64)).setFilter(new DisabledFilter());

        this.actionSource = new MachineSource(this);
        final Block block = CrazyAE.definitions().blocks().mechanicalTeraplate().maybeBlock().orElse(null);
        Preconditions.checkNotNull(block);
        this.upgrades = new CrazyAEBlockUpgradeInv(block, this, this.getUpgradeSlots());
    }

    @Override
    public boolean pushPattern(ICraftingPatternDetails iCraftingPatternDetails, InventoryCrafting inventoryCrafting) {
        return this.acceptPattern(iCraftingPatternDetails);
    }

    @Override
    public boolean fastPushPattern(ICraftingPatternDetails iCraftingPatternDetails) {
        return this.acceptPattern(iCraftingPatternDetails);
    }

    private boolean acceptPattern(ICraftingPatternDetails iCraftingPatternDetails) {
        if (iCraftingPatternDetails instanceof TeraplateCraftingPatternDetails pd) {
            if (this.tasksQueued >= this.tasksMaxAmt) return false;

            this.tasksQueued++;
            this.queueMap.add(new TeraplateCraftingTask(iCraftingPatternDetails.getCondensedOutputs(), 0, pd.getRequiredMana()));
            return true;
        }
        return false;
    }

    public boolean validateRecipe() {
        this.isRecipeValidated = false;
        this.currentRecipe = null;
        for (int i = 0; i < this.craftingOutputInv.getSlots(); i++) {
            this.craftingOutputInv.setStackInSlot(i, ItemStack.EMPTY);
        }
        ItemStack input = null;
        for (ItemStack s : this.craftingInputInv) {
            if (!s.isEmpty()) {
                input = s;
                break;
            }
        }

        if (input != null) {
            final Optional<RecipeTerraplate> recipe = RecipeRepo.findMatchingRecipe(this.craftingInputInv);
            if (recipe.isPresent()) {
                this.isRecipeValidated = true;
                this.currentRecipe = recipe.get();
                this.craftingOutputInv.setStackInSlot(0, recipe.get().getOutput());
                return true;
            }
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
            return TickRateModulation.URGENT;
        }

        if (!this.queueMap.isEmpty()) {
            int itemsAmt = this.itemsPerTick;

            for (int j = 0; j < this.queueMap.size(); j++) {
                if (itemsAmt >= j) {
                    CraftingTask s = this.queueMap.get(j);
                    IAEItemStack[] ais = s.getTaskItems();
                    final int progress = s.getProgress();

                    if (progress >= 100) {
                        if (s instanceof IManaTask manaTask) {
                            if (!this.tryUseMana(manaTask.getRequiredMana())) {
                                continue;
                            }
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
        if (!this.patternsInv.getStackInSlot(0).isEmpty() && this.currentRecipe != null) {
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

            CrazyAE.definitions().items().teraplateEncodedPattern().maybeStack(1).ifPresent(maybePattern -> {
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
                encodedValue.setInteger("reqMana", this.currentRecipe.getManaCost());

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

    public RecipeTerraplate getCurrentRecipe() {
        return this.currentRecipe;
    }

    @Override
    public BotaniaMechanicalDeviceType getType() {
        return BotaniaMechanicalDeviceType.TERAPLATE;
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
        return CrazyAE.definitions().blocks().mechanicalTeraplate();
    }
}
