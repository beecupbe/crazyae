package dev.beecube31.crazyae2.common.parts;

import appeng.api.parts.IPartItem;
import appeng.helpers.InterfaceLogic;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuHostLocator;
import appeng.parts.misc.InterfacePart;
import dev.beecube31.crazyae2.common.tile.networking.TilePerfectInterface;
import dev.beecube31.crazyae2.common.util.ContainerOpener;
import dev.beecube31.crazyae2.core.CrazyAE;
import dev.beecube31.crazyae2.core.registries.server.CrazyContainers;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class PartPerfectInterface extends InterfacePart {

    public PartPerfectInterface(IPartItem<?> partItem) {
        super(partItem);
    }

    @Override
    protected InterfaceLogic createLogic() {
        return new InterfaceLogic(getMainNode(), this, getPartItem().asItem(), TilePerfectInterface.SLOTS);
    }


    @Override
    public ItemStack getMainMenuIcon() {
        return new ItemStack(getPartItem());
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
