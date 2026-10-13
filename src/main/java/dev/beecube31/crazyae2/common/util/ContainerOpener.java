package dev.beecube31.crazyae2.common.util;

import appeng.core.AELog;
import appeng.menu.locator.MenuLocator;
import com.google.common.base.Preconditions;
import dev.beecube31.crazyae2.common.containers.CrazyAEBaseContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

import java.util.HashMap;
import java.util.Map;

public final class ContainerOpener {

    private ContainerOpener() {}

    private static final Map<MenuType<? extends CrazyAEBaseContainer>, Opener> registry = new HashMap<>();

    public static <T extends CrazyAEBaseContainer> void addOpener(MenuType<T> type, Opener opener) {
        registry.put(type, opener);
    }

    public static boolean isRegistered(MenuType<?> type) {
        return registry.containsKey(type);
    }

    public static boolean returnTo(MenuType<?> type, Player player, MenuLocator locator) {
        return open(type, player, locator, true);
    }

    public static boolean open(MenuType<?> type, Player player, MenuLocator locator) {
        return open(type, player, locator, false);
    }

    public static boolean open(MenuType<?> type, Player player, MenuLocator locator, boolean fromSubMenu) {
        Preconditions.checkArgument(!player.level().isClientSide(), "Menus must be opened on the server.");
        Opener opener = registry.get(type);
        if (opener == null) {
            AELog.warn("Trying to open menu for unknown menu type {}", type);
            return false;
        }

        return opener.open(player, locator, fromSubMenu);
    }

    @FunctionalInterface
    public interface Opener {
        boolean open(Player player, MenuLocator locator, boolean fromSubMenu);
    }
}
