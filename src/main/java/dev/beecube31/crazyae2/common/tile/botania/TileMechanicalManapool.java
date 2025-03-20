package dev.beecube31.crazyae2.common.tile.botania;

import appeng.api.definitions.IItemDefinition;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.storage.data.IAEItemStack;
import appeng.me.helpers.MachineSource;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.util.inv.InvOperation;
import com.google.common.base.Preconditions;
import dev.beecube31.crazyae2.common.enums.BotaniaMechanicalDeviceType;
import dev.beecube31.crazyae2.common.parts.implementations.CrazyAEBlockUpgradeInv;
import dev.beecube31.crazyae2.common.util.NBTUtils;
import dev.beecube31.crazyae2.common.util.inv.CrazyAEInternalInv;
import dev.beecube31.crazyae2.common.util.patterns.crafting.ManapoolCraftingPatternDetails;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.recipe.RecipeManaInfusion;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class TileMechanicalManapool extends TileBotaniaMechanicalMachineBase {
    protected RecipeManaInfusion currentRecipe;

    public TileMechanicalManapool() {
        super();

        this.craftingInputInv = new CrazyAEInternalInv(this, 2, 64);
        (this.craftingOutputInv = new CrazyAEInternalInv(this, 1, 64)).setFilter(new DisabledFilter());

        this.actionSource = new MachineSource(this);
        final Block block = CrazyAE.definitions().blocks().mechanicalManapool().maybeBlock().orElse(null);
        Preconditions.checkNotNull(block);
        this.upgrades = new CrazyAEBlockUpgradeInv(block, this, this.getUpgradeSlots());
    }

    @Override
    public boolean pushPattern(ICraftingPatternDetails iCraftingPatternDetails, InventoryCrafting inventoryCrafting) {
        if (iCraftingPatternDetails instanceof ManapoolCraftingPatternDetails pd) {
            if (this.tasksQueued >= this.tasksMaxAmt) return false;

            this.tasksQueued++;
            this.queueMap.add(new ManaCraftingTask(iCraftingPatternDetails.getCondensedOutputs(), 0, pd.getRequiredMana()));
            return true;
        }
        return false;
    }

    @Override
    public boolean fastPushPattern(ICraftingPatternDetails iCraftingPatternDetails) {
        if (iCraftingPatternDetails instanceof ManapoolCraftingPatternDetails pd) {
            if (this.tasksQueued >= this.tasksMaxAmt) return false;

            this.tasksQueued++;
            this.queueMap.add(new ManaCraftingTask(iCraftingPatternDetails.getCondensedOutputs(), 0, pd.getRequiredMana()));
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
        List<ItemStack> input = new ArrayList<>();
        for (ItemStack s : this.craftingInputInv) {
            if (!s.isEmpty()) {
                input.add(s);
            }
        }

        if (!input.isEmpty()) {
            if (!this.craftingInputInv.getStackInSlot(1).isEmpty()) {
                this.craftingInputInv.getStackInSlot(1).setCount(1);
            }
            final RecipeManaInfusion recipe = getMatchingRecipe(this.craftingInputInv);
            if (recipe != null) {
                this.isRecipeValidated = true;
                this.currentRecipe = recipe;
                this.craftingOutputInv.setStackInSlot(0, recipe.getOutput());
                return true;
            }
        }

        return false;
    }

    @Nullable
    public static RecipeManaInfusion getMatchingRecipe(@Nonnull AppEngInternalInventory inv) {
        for (RecipeManaInfusion recipe : BotaniaAPI.manaInfusionRecipes) {
            if (compareRecipes(recipe, inv)) {
                return recipe;
            }
        }

        return null;
    }

    private static boolean compareRecipes(RecipeManaInfusion recipe, @Nonnull AppEngInternalInventory inv) {
        if (recipe.matches(inv.getStackInSlot(0)) && checkCatalyst(inv.getStackInSlot(1), recipe)) {
            if (recipe.getInput() instanceof ItemStack s) {
                inv.getStackInSlot(0).setCount(s.getCount());
            }
            return true;
        }
        return false;
    }

    private static boolean checkCatalyst(ItemStack is, RecipeManaInfusion recipe) {
        if (recipe.getCatalyst() == null) return true;
        if (is.isEmpty()) return false;
        if (is.getCount() < 1) return false;

        return Item.getItemFromBlock(recipe.getCatalyst().getBlock()) == is.getItem();
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
            ArrayList<ItemStack> outputs = new ArrayList<>();
            for (ItemStack s : this.craftingOutputInv) {
                if (!s.isEmpty()) {
                    outputs.add(s);
                }
            }

            CrazyAE.definitions().items().manapoolEncodedPattern().maybeStack(1).ifPresent(maybePattern -> {
                final ItemStack[] out = outputs.toArray(new ItemStack[0]);
                final NBTTagCompound encodedValue = new NBTTagCompound();

                final NBTTagList tagIn = new NBTTagList();
                final NBTTagList tagOut = new NBTTagList();

                for (final ItemStack i : out) {
                    tagOut.appendTag(NBTUtils.createItemTag(i));
                }

                tagIn.appendTag(NBTUtils.createItemTag(this.craftingInputInv.getStackInSlot(0)));

                encodedValue.setTag("input", tagIn);
                encodedValue.setTag("output", tagOut);
                encodedValue.setBoolean("crafting", true);
                encodedValue.setBoolean("substitute", false);
                encodedValue.setInteger("reqMana", this.currentRecipe.getManaToConsume());

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

    public RecipeManaInfusion getCurrentRecipe() {
        return this.currentRecipe;
    }

    @Override
    public BotaniaMechanicalDeviceType getType() {
        return BotaniaMechanicalDeviceType.MANAPOOL;
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
        return CrazyAE.definitions().blocks().mechanicalManapool();
    }

    @Override
    public void onChangeInventory(final IItemHandler inv, final int slot, final InvOperation mc, final ItemStack removed, final ItemStack added) {
        if (inv == this.craftingInputInv && (!removed.isEmpty() || !added.isEmpty())) {
            this.validateRecipe();
        }

        if (inv == this.internalPatternsStorageInv && (!removed.isEmpty() || !added.isEmpty())) {
            this.updateCraftingList();
        }

        if (inv == this.upgrades && (!removed.isEmpty() || !added.isEmpty())) {
            this.checkUpgrades();
        }
    }
}
