package dev.beecube31.crazyae2.common.tile.botania;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.definitions.IItemDefinition;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.me.GridAccessException;
import appeng.me.helpers.MachineSource;
import appeng.parts.automation.BlockUpgradeInventory;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.util.item.AEItemStack;
import com.aeternal.botaniverse.common.item.materials.ItemMoreRune;
import com.google.common.base.Preconditions;
import dev.beecube31.crazyae2.common.enums.BotaniaMechanicalDeviceType;
import dev.beecube31.crazyae2.common.util.ForgeUtils;
import dev.beecube31.crazyae2.common.util.NBTUtils;
import dev.beecube31.crazyae2.common.util.patterns.crafting.PuredaisyCraftingPatternDetails;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.recipe.RecipePureDaisy;
import vazkii.botania.common.item.material.ItemRune;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class TileMechanicalPuredaisy extends TileBotaniaMechanicalMachineBase {
    protected boolean reqBucket = false;

    public TileMechanicalPuredaisy() {
        super();

        this.craftingInputInv = new AppEngInternalInventory(this, 1, 64);
        (this.craftingOutputInv = new AppEngInternalInventory(this, 1, 64)).setFilter(new DisabledFilter());

        this.actionSource = new MachineSource(this);
        final Block block = CrazyAE.definitions().blocks().mechanicalPuredaisy().maybeBlock().orElse(null);
        Preconditions.checkNotNull(block);
        this.upgrades = new BlockUpgradeInventory(block, this, this.getUpgradeSlots());
    }

    @Override
    public boolean pushPattern(ICraftingPatternDetails iCraftingPatternDetails, InventoryCrafting inventoryCrafting) {
        if (iCraftingPatternDetails instanceof PuredaisyCraftingPatternDetails pd) {
            if (this.tasksQueued >= this.tasksMaxAmt) return false;

            this.tasksQueued++;
            this.queueMap.add(new PureDaisyCraftingTask(iCraftingPatternDetails.getCondensedOutputs(), 0, pd.requireOutputBucket()));
            return true;
        }
        return false;
    }

    @Override
    public boolean fastPushPattern(ICraftingPatternDetails iCraftingPatternDetails) {
        if (iCraftingPatternDetails instanceof PuredaisyCraftingPatternDetails pd) {
            if (this.tasksQueued >= this.tasksMaxAmt) return false;

            this.tasksQueued++;
            this.queueMap.add(new PureDaisyCraftingTask(iCraftingPatternDetails.getCondensedOutputs(), 0, pd.requireOutputBucket()));
            return true;
        }
        return false;
    }

    public boolean validateRecipe() {
        this.isRecipeValidated = false;
        this.reqBucket = false;
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
            final RecipePureDaisy recipe = getMatchingRecipe(this.craftingInputInv);
            if (recipe != null) {
                this.isRecipeValidated = true;
                this.craftingOutputInv.setStackInSlot(0, new ItemStack(recipe.getOutputState().getBlock()));
                return true;
            }
        }

        return false;
    }

    @Nullable
    public RecipePureDaisy getMatchingRecipe(@Nonnull AppEngInternalInventory inv) {
        for (RecipePureDaisy recipe : BotaniaAPI.pureDaisyRecipes) {
            if (compareRecipes(recipe, inv)) {
                return recipe;
            }
        }

        return null;
    }

    private boolean compareRecipes(RecipePureDaisy recipe, @Nonnull AppEngInternalInventory inv) {
        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                Object input = recipe.getInput();
                if (input instanceof BlockLiquid) {
                    boolean matches = stack.getItem() == Items.WATER_BUCKET
                            || stack.getItem() == Items.LAVA_BUCKET;
                    this.reqBucket = matches;
                    return matches;
                } else if (input instanceof Block block) {
                    return Item.getItemFromBlock(block) == stack.getItem()
                            || stack.equals(new ItemStack(((Block) input)));
                } else if (input instanceof IBlockState) {
                    return stack.equals(new ItemStack(((IBlockState) input).getBlock()))
                            && stack.getMetadata() == ((IBlockState) input).getBlock().getMetaFromState((IBlockState) input);
                } else if (input instanceof String) {
                    return ForgeUtils.isOreDict(stack, (String) input);
                }
            }
        }
        return false;
    }

    private boolean tryOutputBucket() {
        try {
            IMEMonitor<IAEItemStack> storage = this.getProxy().getGrid().<IStorageGrid>getCache(IStorageGrid.class).getInventory(AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));

            IAEItemStack item = AEItemStack.fromItemStack(new ItemStack(Items.BUCKET, 1));

            IAEItemStack overflow = storage.injectItems(item, Actionable.SIMULATE, this.actionSource);

            if (overflow == null) {
                storage.injectItems(item, Actionable.MODULATE, this.actionSource);
                return true;
            }
        } catch (GridAccessException e) {
            // :(
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
                        if (s instanceof IPureDaisyTask t && t.requireOutputBucket()) {
                            if (!this.tryOutputBucket()) {
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

            CrazyAE.definitions().items().puredaisyEncodedPattern().maybeStack(1).ifPresent(maybePattern -> {
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

                encodedValue.setBoolean("reqBucket", this.reqBucket);

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
        return BotaniaMechanicalDeviceType.PUREDAISY;
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
        return CrazyAE.definitions().blocks().mechanicalPuredaisy();
    }
}
