package dev.beecube31.crazyae2.common.parts;

import appeng.api.parts.IPartItem;
import appeng.api.stacks.AEItemKey;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuHostLocator;
import appeng.parts.crafting.PatternProviderPart;
import dev.beecube31.crazyae2.common.tile.networking.TilePatternsInterface;
import dev.beecube31.crazyae2.common.util.ContainerOpener;
import dev.beecube31.crazyae2.core.CrazyAE;
import dev.beecube31.crazyae2.core.registries.server.CrazyContainers;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class PartPatternsInterface extends PatternProviderPart {

    public PartPatternsInterface(IPartItem<?> partItem) {
        super(partItem);
    }

    @Override
    protected PatternProviderLogic createLogic() {
        return new PatternProviderLogic(getMainNode(), this, TilePatternsInterface.SLOTS);
    }


    @Override
    public AEItemKey getTerminalIcon() {
        return AEItemKey.of(getPartItem());
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return new ItemStack(getPartItem());
    }

    @Override
    public void openMenu(Player player, MenuHostLocator locator) {
        ContainerOpener.open(CrazyContainers.PATTERNS_INTERFACE.get(), player, locator);
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu subMenu) {
        ContainerOpener.returnTo(CrazyContainers.PATTERNS_INTERFACE.get(), player, subMenu.getLocator());
    }
}
