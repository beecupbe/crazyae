package dev.beecube31.crazyae2.common.items.cells;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.storage.ICellInventoryHandler;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.core.Api;
import appeng.util.item.AEItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class ImprovedPortableCell extends DensePortableCell {

	public ImprovedPortableCell(double batteryCapacity, int bytes, int bytesPerType, double idleDrain) {
		super(batteryCapacity, bytes, bytesPerType, idleDrain);
	}

	@NotNull
	@Override
	public IItemStorageChannel getChannel() {
		return Api.INSTANCE.storage().getStorageChannel(IItemStorageChannel.class);
	}

	public boolean isAutoPickupEnabled(ItemStack cell) {
		return cell.getTagCompound() != null && cell.getTagCompound().getBoolean("autoPickup");
	}

	public boolean onItemPickup(EntityItemPickupEvent event, ItemStack stack) {
		ICellInventoryHandler<IAEItemStack> storage = AEApi.instance().registries().cell().getCellInventory(stack, null, AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class));

		if (storage != null) {
			ItemStack eventItem = event.getItem().getItem();
			AEItemStack item = AEItemStack.fromItemStack(eventItem);

			if (storage.canAccept(item)) {
				IAEItemStack overflow = storage.injectItems(item, Actionable.SIMULATE, null);
				if (overflow == null) {
					storage.injectItems(item, Actionable.MODULATE, null);
					EntityPlayer player = event.getEntityPlayer();
					Random rand = new Random();
					player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.08F, (rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.0F);
					eventItem.setCount(0);
					return true;
				}
			}
		}

		return false;
	}
}
