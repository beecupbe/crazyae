package dev.beecube31.crazyae2.common.tile.networking;

import appeng.blockentity.misc.InterfaceBlockEntity;
import appeng.helpers.InterfaceLogic;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuHostLocator;
import dev.beecube31.crazyae2.common.definitions.CrazyBlocks;
import dev.beecube31.crazyae2.common.util.ContainerOpener;
import dev.beecube31.crazyae2.core.registries.server.CrazyContainers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TilePerfectInterface extends InterfaceBlockEntity {
    public static final int SLOTS = 36;

    public TilePerfectInterface(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected InterfaceLogic createLogic() {
        return new InterfaceLogic(getMainNode(), this, getItemFromBlockEntity().asItem(), SLOTS);
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return new ItemStack(CrazyBlocks.PERFECT_INTERFACE.get());
    }

    @Override
    public void openMenu(Player player, MenuHostLocator locator) {
        ContainerOpener.open(CrazyContainers.PERFECT_INTERFACE.get(), player, locator);
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu subMenu) {
        ContainerOpener.returnTo(CrazyContainers.PERFECT_INTERFACE.get(), player, subMenu.getLocator());
    }
}
