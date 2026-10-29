package dev.beecube31.crazyae2.core.builder;

import appeng.menu.locator.MenuHostLocator;
import appeng.menu.locator.MenuLocators;
import com.google.common.base.Preconditions;
import dev.beecube31.crazyae2.common.containers.CrazyAEBaseContainer;
import dev.beecube31.crazyae2.common.util.ContainerOpener;
import dev.beecube31.crazyae2.common.util.locator.CrazyLocators;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
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
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
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

    /**
     * Specifies a custom strategy for obtaining a custom menu name.
     * <p>
     * The strategy should return {@link Component#empty()} if there's no custom name.
     */
    public ContainerTypeBuilder<M, I> withMenuTitle(Function<I, Component> menuTitleStrategy) {
        this.menuTitleStrategy = menuTitleStrategy;
        return this;
    }

    /**
     * Sets a serializer and deserializer for additional data that should be transmitted from server->client when the
     * menu is being first opened.
     */
    public ContainerTypeBuilder<M, I> withInitialData(InitialDataSerializer<I> initialDataSerializer,
                                                      InitialDataDeserializer<M, I> initialDataDeserializer) {
        this.initialDataSerializer = initialDataSerializer;
        this.initialDataDeserializer = initialDataDeserializer;
        return this;
    }

    /**
     * Opens a menu that is based around a single block entity. The block entity's position is encoded in the packet
     * buffer.
     */
    private M fromNetwork(int containerId, Inventory inv, RegistryFriendlyByteBuf packetBuf) {
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

    private boolean open(Player player, MenuHostLocator locator, boolean fromSubMenu) {
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

        ((ServerPlayer) player).openMenu(menu, buffer -> {
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
        menuType = IMenuTypeExtension.create(this::fromNetwork);
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

    /**
     * Strategy used to serialize initial data for opening the menu on the client-side into the packet that is sent to
     * the client.
     */
    @FunctionalInterface
    public interface InitialDataSerializer<I> {
        void serializeInitialData(I host, RegistryFriendlyByteBuf buffer);
    }

    /**
     * Strategy used to deserialize initial data for opening the menu on the client-side from the packet received by the
     * server.
     */
    @FunctionalInterface
    public interface InitialDataDeserializer<C, I> {
        void deserializeInitialData(I host, C menu, RegistryFriendlyByteBuf buffer);
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
