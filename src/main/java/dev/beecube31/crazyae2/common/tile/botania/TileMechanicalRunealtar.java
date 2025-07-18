package dev.beecube31.crazyae2.common.tile.botania;

import appeng.api.definitions.IItemDefinition;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.storage.data.IAEItemStack;
import appeng.helpers.ItemStackHelper;
import appeng.me.helpers.MachineSource;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.util.item.AEItemStack;
import com.google.common.base.Preconditions;
import dev.beecube31.crazyae2.common.containers.base.slot.RestrictedSlot;
import dev.beecube31.crazyae2.common.enums.BotaniaMechanicalDeviceType;
import dev.beecube31.crazyae2.common.parts.implementations.CrazyAEBlockUpgradeInv;
import dev.beecube31.crazyae2.common.util.ModsChecker;
import dev.beecube31.crazyae2.common.util.NBTUtils;
import dev.beecube31.crazyae2.common.util.inv.CrazyAEInternalInv;
import dev.beecube31.crazyae2.common.util.patterns.crafting.RunealtarCraftingPatternDetails;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.recipe.RecipeRuneAltar;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.item.material.ItemRune;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class TileMechanicalRunealtar extends TileBotaniaMechanicalMachineBase {
    protected RecipeRuneAltar currentRecipe;
    protected List<IAEItemStack> outputRunes = new ArrayList<>();

    public TileMechanicalRunealtar() {
        super();

        this.craftingInputInv = new CrazyAEInternalInv(this, 16, 64);
        (this.craftingOutputInv = new CrazyAEInternalInv(this, 1, 64)).setFilter(new DisabledFilter());

        this.actionSource = new MachineSource(this);
        final Block block = CrazyAE.definitions().blocks().mechanicalRunealtar().maybeBlock().orElse(null);
        Preconditions.checkNotNull(block);
        this.upgrades = new CrazyAEBlockUpgradeInv(block, this, this.getUpgradeSlots());

        this.internalPatternsStorageInv.setItemFilter(RestrictedSlot.PlaceableItemType.RUNEALTAR_ENCODED_PATTERN.associatedFilter);
    }

    @Override
    public boolean pushPattern(ICraftingPatternDetails iCraftingPatternDetails, InventoryCrafting inventoryCrafting) {
        if (iCraftingPatternDetails instanceof RunealtarCraftingPatternDetails pd) {
            if (this.tasksQueued >= this.tasksMaxAmt) return false;

            this.tasksQueued++;
            this.queueMap.add(new RuneAltarCraftingTask(iCraftingPatternDetails.getCondensedOutputs(), 0, pd.getRequiredMana()));
            for (IAEItemStack ais : iCraftingPatternDetails.getCondensedInputs()) {
                Item item = ais.createItemStack().getItem();

                if (item instanceof ItemRune || ModsChecker.isItemMoreRune(item)) {
                    this.outputRunes.add(ais);
                }
            }
            this.pushOutRunes();
            return true;
        }
        return false;
    }

    @Override
    public NBTTagCompound writeToNBT(final NBTTagCompound data) {
        super.writeToNBT(data);
        NBTTagList nbtTagList = new NBTTagList();

        for (IAEItemStack i : this.outputRunes) {
            ItemStack is = i.createItemStack();
            if (!is.isEmpty()) {
                NBTTagCompound itemTag = ItemStackHelper.stackToNBT(is);
                nbtTagList.appendTag(itemTag);
            }
        }
        data.setTag("outputRunes", nbtTagList);
        return data;
    }

    @Override
    public void readFromNBT(final NBTTagCompound data) {
        super.readFromNBT(data);

        if (data.hasKey("outputRunes", 9)) {
            NBTTagList tagList = data.getTagList("outputRunes", 10);

            this.outputRunes.clear();

            for (int i = 0; i < tagList.tagCount(); ++i) {
                NBTTagCompound itemTags = tagList.getCompoundTagAt(i);
                this.outputRunes.add(AEItemStack.fromItemStack(ItemStackHelper.stackFromNBT(itemTags)));
            }
        }

        this.pushOutRunes();
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
            final RecipeRuneAltar recipe = getMatchingRecipe(this.craftingInputInv);
            if (recipe != null) {
                this.isRecipeValidated = true;
                this.currentRecipe = recipe;
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

        for (RecipeRuneAltar recipe : BotaniaAPI.runeAltarRecipes) {
            if (recipe.getOutput() == this.findSlot.getStackInSlot(0)) {
                this.isRecipeValidated = true;
                this.craftingOutputInv.setStackInSlot(0, recipe.getOutput());
                return true;
            }
        }

        return false;
    }

    @Nullable
    public static RecipeRuneAltar getMatchingRecipe(@Nonnull AppEngInternalInventory inv) {
        for (RecipeRuneAltar recipe : BotaniaAPI.runeAltarRecipes) {
            if (compareRecipes(recipe, inv)) {
                return recipe;
            }
        }

        return null;
    }

    private static boolean compareRecipes(RecipeRuneAltar recipe, @Nonnull AppEngInternalInventory inv) {
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

        this.pushOutRunes();
        return TickRateModulation.URGENT;
    }

    private void pushOutRunes() {
        if (!this.outputRunes.isEmpty()) {
            this.outputRunes.removeIf(ais -> this.pushItemsOut(ais) == null);
        }
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

            CrazyAE.definitions().items().runealtarEncodedPattern().maybeStack(1).ifPresent(maybePattern -> {
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

                tagIn.appendTag(NBTUtils.createItemTag(new ItemStack(ModBlocks.livingrock)));

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

    public RecipeRuneAltar getCurrentRecipe() {
        return this.currentRecipe;
    }

    @Override
    public BotaniaMechanicalDeviceType getType() {
        return BotaniaMechanicalDeviceType.RUNEALTAR;
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
        return CrazyAE.definitions().blocks().mechanicalRunealtar();
    }
}
