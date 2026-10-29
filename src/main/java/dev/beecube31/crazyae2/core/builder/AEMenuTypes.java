package dev.beecube31.crazyae2.core.builder;

import appeng.api.parts.IPart;
import appeng.menu.AEBaseMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuHostLocator;
import appeng.menu.locator.MenuLocators;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;

public final class AEMenuTypes {
    private AEMenuTypes() {}

    @FunctionalInterface
    public interface Factory<M extends AEBaseMenu, I> {
        M create(MenuType<M> type, int containerId, Inventory playerInv, I host);
    }

    public static <M extends AEBaseMenu, I> MenuType<M> create(String id, Class<I> hostClass, Factory<M, I> factory) {
        @SuppressWarnings("unchecked")
        final MenuType<M>[] holder = new MenuType[1];

        MenuType<M> type = IMenuTypeExtension.create((containerId, inv, buf) -> {
            MenuHostLocator locator = MenuLocators.readFromPacket(buf);
            I host = locator.locate(inv.player, hostClass);
            if (host == null) {
                throw new IllegalStateException("Couldn't find menu host at " + locator + " for crazyae:" + id);
            }
            M menu = factory.create(holder[0], containerId, inv, host);
            menu.setReturnedFromSubScreen(buf.readBoolean());
            return menu;
        });
        holder[0] = type;

        MenuOpener.addOpener(type, (player, locator, fromSubMenu) -> open(type, hostClass, factory, player, locator, fromSubMenu));
        return type;
    }

    private static <M extends AEBaseMenu, I> boolean open(MenuType<M> type, Class<I> hostClass, Factory<M, I> factory,
                                                          Player player, MenuHostLocator locator, boolean fromSubMenu) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return false;
        }
        I host = locator.locate(player, hostClass);
        if (host == null) {
            return false;
        }
        MenuProvider provider = new SimpleMenuProvider((wnd, inv, pl) -> {
            M m = factory.create(type, wnd, inv, host);
            m.setLocator(locator);
            return m;
        }, title(host));
        serverPlayer.openMenu(provider, buffer -> {
            MenuLocators.writeToPacket(buffer, locator);
            buffer.writeBoolean(fromSubMenu);
        });
        return true;
    }

    private static Component title(Object host) {
        if (host instanceof Nameable nameable && nameable.hasCustomName() && nameable.getCustomName() != null) {
            return nameable.getCustomName();
        }
        if (host instanceof IPart part) {
            return part.getPartItem().asItem().getDefaultInstance().getHoverName();
        }
        if (host instanceof BlockEntity be) {
            return be.getBlockState().getBlock().getName();
        }
        return Component.empty();
    }
}
