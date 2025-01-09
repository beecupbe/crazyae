package dev.beecube31.crazyae2.common.tile.botania;

import appeng.api.definitions.IItemDefinition;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.storage.data.IAEItemStack;
import appeng.me.helpers.MachineSource;
import appeng.parts.automation.BlockUpgradeInventory;
import appeng.tile.inventory.AppEngInternalInventory;
import com.aeternal.botaniverse.common.item.materials.ItemMoreRune;
import com.google.common.base.Preconditions;
import dev.beecube31.crazyae2.common.enums.BotaniaMechanicalDeviceType;
import dev.beecube31.crazyae2.common.util.NBTUtils;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.recipe.RecipePetals;
import vazkii.botania.common.item.material.ItemRune;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class TileMechanicalPetal extends TileBotaniaMechanicalMachineBase {
    public TileMechanicalPetal() {
        super();

        this.craftingInputInv = new AppEngInternalInventory(this, 16, 64);
        (this.craftingOutputInv = new AppEngInternalInventory(this, 1, 64)).setFilter(new DisabledFilter());

        this.actionSource = new MachineSource(this);
        final Block block = CrazyAE.definitions().blocks().mechanicalPetal().maybeBlock().orElse(null);
        Preconditions.checkNotNull(block);
        this.upgrades = new BlockUpgradeInventory(block, this, this.getUpgradeSlots());
    }

    @Override
    public boolean pushPattern(ICraftingPatternDetails iCraftingPatternDetails, InventoryCrafting inventoryCrafting) {
        if (this.tasksQueued >= this.tasksMaxAmt) return false;

        this.tasksQueued++;
        this.queueMap.add(new CraftingTask(iCraftingPatternDetails.getCondensedOutputs(), 0));
        return true;
    }

    @Override
    public boolean fastPushPattern(ICraftingPatternDetails iCraftingPatternDetails) {
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
        ItemStack input = null;
        for (ItemStack s : this.craftingInputInv) {
            if (!s.isEmpty()) {
                input = s;
                break;
            }
        }

        if (input != null) {
            final RecipePetals recipe = getMatchingRecipe(this.craftingInputInv);
            if (recipe != null) {
                this.isRecipeValidated = true;
                this.craftingOutputInv.setStackInSlot(0, recipe.getOutput());
                return true;
            }
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

        for (RecipePetals recipe : BotaniaAPI.petalRecipes) {
            if (recipe.getOutput() == this.findSlot.getStackInSlot(0)) {
                this.isRecipeValidated = true;
                this.craftingOutputInv.setStackInSlot(0, recipe.getOutput());
                return true;
            }
        }

        return false;
    }

    @Nullable
    public static RecipePetals getMatchingRecipe(@Nonnull AppEngInternalInventory inv) {
        for (RecipePetals recipe : BotaniaAPI.petalRecipes) {
            if (compareRecipes(recipe, inv)) {
                return recipe;
            }
        }

        return null;
    }

    private static boolean compareRecipes(RecipePetals recipe, @Nonnull AppEngInternalInventory inv) {
        return recipe.matches(inv);
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

            CrazyAE.definitions().items().petalEncodedPattern().maybeStack(1).ifPresent(maybePattern -> {
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

                tagIn.appendTag(NBTUtils.createItemTag(new ItemStack(Items.WHEAT_SEEDS)));

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
        return BotaniaMechanicalDeviceType.PETAL;
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
        return CrazyAE.definitions().blocks().mechanicalPetal();
    }
}
