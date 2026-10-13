package dev.beecube31.crazyae2.common.util.locator;

import appeng.menu.locator.MenuLocator;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class CrazyLocators {
    private static final Map<String, CrazyLocators.Registration<?>> REGISTRY = new HashMap<>();

    public static synchronized <T extends MenuLocator> void register(Class<T> locatorClass,
                                                                     BiConsumer<T, FriendlyByteBuf> packetWriter,
                                                                     Function<FriendlyByteBuf, T> packetReader) {
        String classKey = locatorClass.getName();
        if (REGISTRY.containsKey(classKey)) {
            throw new IllegalStateException("CrazyMenuLocator type " + classKey + " is already registered.");
        }
        REGISTRY.put(classKey, new CrazyLocators.Registration<>(locatorClass, packetWriter, packetReader));
    }

    private synchronized static CrazyLocators.Registration<?> getRegistration(String classKey) {
        var registration = REGISTRY.get(classKey);
        if (registration == null) {
            throw new IllegalArgumentException("Unregistered menu locator class: " + classKey);
        }
        return registration;
    }

    @SuppressWarnings("unchecked")
    public static <T extends MenuLocator> void writeToPacket(FriendlyByteBuf buf, T locator) {
        var classKey = locator.getClass().getName();
        var registration = (CrazyLocators.Registration<T>) getRegistration(classKey);
        buf.writeUtf(classKey);
        registration.writeToPacket.accept(locator, buf);
    }

    public static MenuLocator readFromPacket(FriendlyByteBuf buf) {
        var classKey = buf.readUtf();

        var registration = getRegistration(classKey);
        return registration.readFromPacket.apply(buf);
    }

    public static MenuLocator forHand(Player player, InteractionHand hand) {
        int slot = getPlayerInventorySlotFromHand(player, hand);
        return new SimpleLocator(slot);
    }

    private static int getPlayerInventorySlotFromHand(Player player, InteractionHand hand) {
        ItemStack is = player.getItemInHand(hand);
        if (is.isEmpty()) {
            throw new IllegalArgumentException("Cannot open an item-inventory with empty hands");
        }
        int invSize = player.getInventory().getContainerSize();
        for (int i = 0; i < invSize; i++) {
            if (player.getInventory().getItem(i) == is) {
                return i;
            }
        }
        throw new IllegalArgumentException("Could not find item held in hand " + hand + " in player inventory");
    }

    private record Registration<T extends MenuLocator>(
            Class<T> locatorClass,
            BiConsumer<T, FriendlyByteBuf> writeToPacket,
            Function<FriendlyByteBuf, T> readFromPacket) {
    }
}
