package dev.beecube31.crazyae2.common.util.locator;

import appeng.menu.locator.MenuHostLocator;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record SimpleLocator(int inventorySlot) implements MenuHostLocator {

    @Nullable
    @Override
    public <T> T locate(Player player, Class<T> hostInterface) {
        if (hostInterface == ItemStack.class) {
            ItemStack stack = player.getInventory().getItem(inventorySlot);
            if (!stack.isEmpty()) {
                return hostInterface.cast(stack);
            }
        }
        return null;
    }

    public static void writeToPacket(SimpleLocator locator, FriendlyByteBuf buf) {
        buf.writeInt(locator.inventorySlot());
    }

    public static SimpleLocator readFromPacket(FriendlyByteBuf buf) {
        return new SimpleLocator(buf.readInt());
    }

    @Override
    public String toString() {
        return "SimpleLocator{slot=" + inventorySlot + "}";
    }
}