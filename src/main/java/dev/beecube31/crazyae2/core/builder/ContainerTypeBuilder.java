package dev.beecube31.crazyae2.core.builder;

import appeng.menu.locator.MenuLocator;
import appeng.menu.locator.MenuLocators;
import com.google.common.base.Preconditions;
import dev.beecube31.crazyae2.common.containers.CrazyAEBaseContainer;
import dev.beecube31.crazyae2.common.util.ContainerOpener;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public final class ContainerTypeBuilder<M extends CrazyAEBaseContainer, I> {

    @Nullable private ResourceLocation id;

    private final Class<I> hostInterface;

    private final MenuFactory<M, I> factory;

    private Function<I, Component> menuTitleStrategy = this::getDefaultMenuTitle;

    @Nullable private InitialDataSerializer<I> initialDataSerializer;

    @Nullable private InitialDataDeserializer<M, I> initialDataDeserializer;

    private MenuType<M> menuType;

    private ContainerTypeBuilder(Class<I> hostInterface, TypedMenuFactory<M, I> typedFactory) {
        this.hostInterface = hostInterface;
        this.factory = (containerId, playerInv, accessObj) -> typedFactory.create(menuType, containerId, playerInv,
                accessObj);
    }

    private ContainerTypeBuilder(Class<I> hostInterface, MenuFactory<M, I> factory) {
        this.hostInterface = hostInterface;
        this.factory = factory;
    }

    public static <C extends CrazyAEBaseContainer, I> ContainerTypeBuilder<C, I> create(MenuFactory<C, I> factory,
                                                                              Class<I> hostInterface) {
        return new ContainerTypeBuilder<>(hostInterface, factory);
    }

    public static <C extends CrazyAEBaseContainer, I> ContainerTypeBuilder<C, I> create(TypedMenuFactory<C, I> factory,
                                                                              Class<I> hostInterface) {
        return new ContainerTypeBuilder<>(hostInterface, factory);
    }

    public ContainerTypeBuilder<M, I> withMenuTitle(Function<I, Component> menuTitleStrategy) {
        this.menuTitleStrategy = menuTitleStrategy;
        return this;
    }

    public ContainerTypeBuilder<M, I> withInitialData(InitialDataSerializer<I> initialDataSerializer,
                                                      InitialDataDeserializer<M, I> initialDataDeserializer) {
        this.initialDataSerializer = initialDataSerializer;
        this.initialDataDeserializer = initialDataDeserializer;
        return this;
    }

    private M fromNetwork(int containerId, Inventory inv, FriendlyByteBuf packetBuf) {
        var locator = MenuLocators.readFromPacket(packetBuf);
        I host = locator.locate(inv.player, hostInterface);
        if (host == null) {
            var connection = Minecraft.getInstance().getConnection();
            if (connection != null) {
                connection.send(new ServerboundContainerClosePacket(containerId));
            }
            throw new IllegalStateException("Couldn't find menu host at " + locator + " for " + this.id
                    + " on client. Closing menu.");
        }
        M menu = factory.create(containerId, inv, host);
        menu.setReturnedFromSubScreen(packetBuf.readBoolean());
        if (initialDataDeserializer != null) {
            initialDataDeserializer.deserializeInitialData(host, menu, packetBuf);
        }
        return menu;
    }

    private boolean open(Player player, MenuLocator locator, boolean fromSubMenu) {
        if (!(player instanceof ServerPlayer)) {
            return false;
        }

        var accessInterface = locator.locate(player, hostInterface);

        if (accessInterface == null) {
            return false;
        }

        Component title = menuTitleStrategy.apply(accessInterface);

        MenuProvider menu = new SimpleMenuProvider((wnd, p, pl) -> {
            M m = factory.create(wnd, p, accessInterface);
            m.setLocator(locator);
            return m;
        }, title);

        NetworkHooks.openScreen((ServerPlayer) player, menu, buffer -> {
            MenuLocators.writeToPacket(buffer, locator);
            buffer.writeBoolean(fromSubMenu);
            if (initialDataSerializer != null) {
                initialDataSerializer.serializeInitialData(accessInterface, buffer);
            }
        });

        return true;
    }

    public MenuType<M> build(String id) {
        Preconditions.checkState(menuType == null, "build was already called");
        Preconditions.checkState(this.id == null, "id should not be set");

        this.id = CrazyAE.makeId(id);
        menuType = IForgeMenuType.create(this::fromNetwork);
        ContainerOpener.addOpener(menuType, this::open);
        return menuType;
    }

    @FunctionalInterface
    public interface MenuFactory<C, I> {
        C create(int containerId, Inventory playerInv, I accessObj);
    }

    @FunctionalInterface
    public interface TypedMenuFactory<C extends AbstractContainerMenu, I> {
        C create(MenuType<C> type, int containerId, Inventory playerInv, I accessObj);
    }

    @FunctionalInterface
    public interface InitialDataSerializer<I> {
        void serializeInitialData(I host, FriendlyByteBuf buffer);
    }

    @FunctionalInterface
    public interface InitialDataDeserializer<C, I> {
        void deserializeInitialData(I host, C menu, FriendlyByteBuf buffer);
    }

    private Component getDefaultMenuTitle(I accessInterface) {
        if (accessInterface instanceof Nameable nameable) {
            if (nameable.hasCustomName()) {
                return nameable.getCustomName();
            }
        }

        return Component.empty();
    }

}
