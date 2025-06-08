package dev.beecube31.crazyae2.common.tile.botania;

import appeng.api.definitions.IItemDefinition;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.storage.data.IAEItemStack;
import appeng.me.helpers.MachineSource;
import appeng.util.inv.InvOperation;
import com.google.common.base.Preconditions;
import dev.beecube31.crazyae2.common.containers.base.slot.RestrictedSlot;
import dev.beecube31.crazyae2.common.enums.BotaniaMechanicalDeviceType;
import dev.beecube31.crazyae2.common.parts.implementations.CrazyAEBlockUpgradeInv;
import dev.beecube31.crazyae2.common.util.NBTUtils;
import dev.beecube31.crazyae2.common.util.inv.CrazyAEInternalInv;
import dev.beecube31.crazyae2.common.util.patterns.crafting.BreweryCraftingPatternDetails;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.recipe.RecipeBrew;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class TileMechanicalBrewery extends TileBotaniaMechanicalMachineBase {
    protected RecipeBrew currentRecipe;
    protected CrazyAEInternalInv craftingBottleSlot;

    public TileMechanicalBrewery() {
        super();

        this.craftingInputInv = new CrazyAEInternalInv(this, 16, 64);
        this.craftingBottleSlot = new CrazyAEInternalInv(this, 1, 64);
        (this.craftingOutputInv = new CrazyAEInternalInv(this, 1, 64)).setFilter(new DisabledFilter());

        this.actionSource = new MachineSource(this);
        final Block block = CrazyAE.definitions().blocks().mechanicalBrewery().maybeBlock().orElse(null);
        Preconditions.checkNotNull(block);
        this.upgrades = new CrazyAEBlockUpgradeInv(block, this, this.getUpgradeSlots());

        this.internalPatternsStorageInv.setItemFilter(RestrictedSlot.PlaceableItemType.BREWERY_ENCODED_PATTERN.associatedFilter);
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound data) {
        super.writeToNBT(data);
        this.craftingBottleSlot.writeToNBT(data, "bottleInv");
        return data;
    }

    @Override
    public void readFromNBT(final NBTTagCompound data) {
        super.readFromNBT(data);
        this.craftingBottleSlot.readFromNBT(data, "bottleInv");
    }

    @Override
    public boolean pushPattern(ICraftingPatternDetails iCraftingPatternDetails, InventoryCrafting inventoryCrafting) {
        return this.acceptPattern(iCraftingPatternDetails);
    }

    private boolean acceptPattern(ICraftingPatternDetails iCraftingPatternDetails) {
        if (iCraftingPatternDetails instanceof BreweryCraftingPatternDetails pd) {
            if (this.tasksQueued >= this.tasksMaxAmt) return false;

            this.tasksQueued++;
            this.queueMap.add(new BreweryCraftingTask(iCraftingPatternDetails.getCondensedOutputs(), 0, pd.getRequiredMana()));
            return true;
        }
        return false;
    }

    @Override
    public void onChangeInventory(final IItemHandler inv, final int slot, final InvOperation mc, final ItemStack removed, final ItemStack added) {
        if ((inv == this.craftingInputInv || inv == this.craftingBottleSlot) && (!removed.isEmpty() || !added.isEmpty())) {
            this.validateRecipe();
        }

        if (inv == this.internalPatternsStorageInv && (!removed.isEmpty() || !added.isEmpty())) {
            this.updateCraftingList();
            this.notifyPatternsChanged();
        }

        if (inv == this.upgrades && (!removed.isEmpty() || !added.isEmpty())) {
            this.checkUpgrades();
        }
    }

    @Override
    public IItemHandler getInventoryByName(final String name) {
        return switch (name) {
            case "upgrades" -> this.upgrades;
            case "input" -> this.craftingInputInv;
            case "output" -> this.craftingOutputInv;
            case "patterns" -> this.patternsInv;
            case "patternsInternal" -> this.internalPatternsStorageInv;
            case "findSlot" -> this.findSlot;
            case "bottle" -> this.craftingBottleSlot;
            default -> null;
        };
    }

    public boolean validateRecipe() {
        this.isRecipeValidated = false;
        this.currentRecipe = null;
        for (int i = 0; i < this.craftingOutputInv.getSlots(); i++) {
            this.craftingOutputInv.setStackInSlot(i, ItemStack.EMPTY);
        }

        if (this.craftingBottleSlot.getStackInSlot(0).isEmpty()) {
            return false;
        }

        if (!this.craftingInputInv.getStacks().isEmpty()) {
            final RecipeBrew recipe = getMatchingRecipe(this.craftingInputInv);
            if (recipe != null) {
                this.isRecipeValidated = true;
                this.currentRecipe = recipe;
                this.craftingOutputInv.setStackInSlot(0, recipe.getOutput(this.craftingBottleSlot.getStackInSlot(0)));
                return true;
            }
        }

        return false;
    }

    @Nullable
    public static RecipeBrew getMatchingRecipe(@Nonnull CrazyAEInternalInv inv) {
        for (RecipeBrew recipe : BotaniaAPI.brewRecipes) {
            if (compareRecipes(recipe, inv)) {
                return recipe;
            }
        }

        return null;
    }

    private static boolean compareRecipes(RecipeBrew recipe, @Nonnull CrazyAEInternalInv inv) {
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
        if (!this.patternsInv.getStackInSlot(0).isEmpty() && !this.craftingBottleSlot.getStackInSlot(0).isEmpty() && this.currentRecipe != null) {
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

            CrazyAE.definitions().items().breweryEncodedPattern().maybeStack(1).ifPresent(maybePattern -> {
                final ItemStack[] in = ArrayUtils.add(inputs.toArray(new ItemStack[0]), this.craftingBottleSlot.getStackInSlot(0));
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
                encodedValue.setInteger("reqMana", this.currentRecipe.getManaUsage());

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

    public RecipeBrew getCurrentRecipe() {
        return this.currentRecipe;
    }

    @Override
    public BotaniaMechanicalDeviceType getType() {
        return BotaniaMechanicalDeviceType.BREWERY;
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
        return CrazyAE.definitions().blocks().mechanicalBrewery();
    }
}