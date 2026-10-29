package dev.beecube31.crazyae2.common.network.packets;

import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import dev.beecube31.crazyae2.common.containers.CrazyAEBaseContainer;
import dev.beecube31.crazyae2.common.network.CrazyPacket;
import dev.beecube31.crazyae2.common.util.ContainerOpener;
import io.netty.buffer.Unpooled;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

public class PacketSwitchGuis extends CrazyPacket {

    @Nullable
    private final MenuType<?> newGui;

    public PacketSwitchGuis(FriendlyByteBuf stream) {
        if (stream.readBoolean()) {
            this.newGui = BuiltInRegistries.MENU.get(stream.readResourceLocation());
        } else {
            this.newGui = null;
        }
    }

    // api
    private PacketSwitchGuis(@Nullable MenuType<? extends ISubMenu> newGui) {
        this.newGui = null;

        var data = newBuffer();

        data.writeInt(this.getPacketID());
        if (newGui != null) {
            data.writeBoolean(true);
            data.writeResourceLocation(BuiltInRegistries.MENU.getKey(newGui));
        } else {
            data.writeBoolean(false);
        }

        this.configureWrite(data);
    }

    public static PacketSwitchGuis openSubMenu(MenuType<? extends ISubMenu> menuType) {
        return new PacketSwitchGuis(menuType);
    }

    public static PacketSwitchGuis returnToParentMenu() {
        return new PacketSwitchGuis((MenuType<? extends ISubMenu>) null);
    }

    @Override
    public void serverPacketData(ServerPlayer player) {
        if (this.newGui != null) {
            doOpenSubMenu(player);
        } else {
            doReturnToParentMenu(player);
        }
    }

    private void doOpenSubMenu(ServerPlayer player) {
        if (player.containerMenu instanceof CrazyAEBaseContainer bc) {
            var locator = bc.getLocator();
            if (locator != null) {
                if (ContainerOpener.isRegistered(newGui)) {
                    ContainerOpener.open(newGui, player, locator);
                } else {
                    MenuOpener.open(newGui, player, locator);
                }
            }
        }
    }

    private void doReturnToParentMenu(ServerPlayer player) {
        if (player.containerMenu instanceof ISubMenu subMenu) {
            subMenu.getHost().returnToMainMenu(player, subMenu);
        }
    }
}
