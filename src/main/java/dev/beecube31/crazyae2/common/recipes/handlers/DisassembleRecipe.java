package dev.beecube31.crazyae2.common.recipes.handlers;

import appeng.api.AEApi;
import appeng.api.definitions.IItemDefinition;
import appeng.api.storage.data.IAEStack;
import appeng.api.storage.data.IItemList;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class DisassembleRecipe extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
	private static final ItemStack MISMATCHED_STACK = ItemStack.EMPTY;

	private final Map<IItemDefinition, IItemDefinition> cellMappings;
	private final Map<IItemDefinition, IItemDefinition> nonCellMappings;

	public DisassembleRecipe() {
		final var definitions = CrazyAE.definitions();
		final var blocks = definitions.blocks();
		final var items = definitions.items();
		final var mats = definitions.materials();

		this.cellMappings = new HashMap<>(15);
		this.nonCellMappings = new HashMap<>(11);

		this.cellMappings.put(items.storageCell256K(), mats.cellPart256K());
		this.cellMappings.put(items.storageCell1MB(), mats.cellPart1MB());
		this.cellMappings.put(items.storageCell4MB(), mats.cellPart4MB());
		this.cellMappings.put(items.storageCell16MB(), mats.cellPart16MB());
		this.cellMappings.put(items.storageCell64MB(), mats.cellPart64MB());
		this.cellMappings.put(items.storageCell256MB(), mats.cellPart256MB());
		this.cellMappings.put(items.storageCell1GB(), mats.cellPart1GB());
		this.cellMappings.put(items.storageCell2GB(), mats.cellPart2GB());

		this.cellMappings.put(items.fluidStorageCell256K(), mats.cellPart256K());
		this.cellMappings.put(items.fluidStorageCell1MB(), mats.cellPart1MB());
		this.cellMappings.put(items.fluidStorageCell4MB(), mats.cellPart4MB());
		this.cellMappings.put(items.fluidStorageCell16MB(), mats.cellPart16MB());
		this.cellMappings.put(items.fluidStorageCell64MB(), mats.cellPart64MB());
		this.cellMappings.put(items.fluidStorageCell256MB(), mats.cellPart256MB());
		this.cellMappings.put(items.fluidStorageCell1GB(), mats.cellPart1GB());
		this.cellMappings.put(items.fluidStorageCell2GB(), mats.cellPart2GB());

		this.nonCellMappings.put(blocks.craftingStorage256k(), mats.cellPart256K());
		this.nonCellMappings.put(blocks.craftingStorage1mb(), mats.cellPart1MB());
		this.nonCellMappings.put(blocks.craftingStorage4mb(), mats.cellPart4MB());
		this.nonCellMappings.put(blocks.craftingStorage16mb(), mats.cellPart16MB());
		this.nonCellMappings.put(blocks.craftingStorage64mb(), mats.cellPart64MB());
		this.nonCellMappings.put(blocks.craftingStorage256mb(), mats.cellPart256MB());
		this.nonCellMappings.put(blocks.craftingStorage1gb(), mats.cellPart1GB());
		this.nonCellMappings.put(blocks.craftingStorage2gb(), mats.cellPart2GB());
		this.nonCellMappings.put(blocks.craftingStorage8gb(), mats.cellPart8GB());
		this.nonCellMappings.put(blocks.craftingStorage32gb(), mats.cellPart32GB());
		this.nonCellMappings.put(blocks.craftingStorage128gb(), mats.cellPart128GB());
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends IAEStack<T>> IItemList<T> getStorageList(final ItemStack stack) {
		var item = (IAEStack<T>) stack.getItem();
		var channel = item.getChannel();

		// make sure the storage cell is empty...
		var cellInv = AEApi.instance()
			.registries()
			.cell()
			.getCellInventory(stack, null, channel);

		assert cellInv != null;
		return cellInv.getAvailableItems(channel.createList());
	}

	@Override
	public boolean matches(final @NotNull InventoryCrafting inv, final @NotNull World w) {
		var output = this.getOutput(inv);
		return output != null && !output.isEmpty();
	}

	private ItemStack getOutput(final IInventory inventory) {
		var itemCount = 0;
		var output = MISMATCHED_STACK;

		for (var slotIndex = 0; slotIndex < inventory.getSizeInventory(); slotIndex++) {
			final var stackInSlot = inventory.getStackInSlot(slotIndex);
			if (!stackInSlot.isEmpty()) {
				// needs a single input in the recipe
				itemCount++;
				if (itemCount > 1) {
					return MISMATCHED_STACK;
				}

				// handle storage cells
				var maybeCellOutput = this.getCellOutput(stackInSlot);
				if (maybeCellOutput.isPresent()) {
					var storageCellStack = maybeCellOutput.get();
					var storageList = getStorageList(storageCellStack);
					if (storageList.isEmpty()) {
						return MISMATCHED_STACK;
					}

					output = storageCellStack;
				}

				// handle crafting storage blocks
				output = this.getNonCellOutput(stackInSlot).orElse(output);
			}
		}

		return output;
	}

	@Nonnull
	private Optional<ItemStack> getCellOutput(final ItemStack compared) {
		for (final var entry : this.cellMappings.entrySet()) {
			if (entry.getKey().isSameAs(compared)) {
				return entry.getValue().maybeStack(1);
			}
		}

		return Optional.empty();
	}

	@Nonnull
	private Optional<ItemStack> getNonCellOutput(final ItemStack compared) {
		for (final var entry : this.nonCellMappings.entrySet()) {
			if (entry.getKey().isSameAs(compared)) {
				return entry.getValue().maybeStack(1);
			}
		}

		return Optional.empty();
	}

	@Nonnull
	@Override
	public ItemStack getCraftingResult(final @NotNull InventoryCrafting inv) {
		return this.getOutput(inv);
	}

	@Override
	public boolean canFit(int i, int i1) {
		return false;
	}

	@Nonnull
	@Override
	public ItemStack getRecipeOutput() // no default output..
	{
		return ItemStack.EMPTY;
	}
}