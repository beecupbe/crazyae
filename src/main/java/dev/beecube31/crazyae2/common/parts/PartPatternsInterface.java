package dev.beecube31.crazyae2.common.parts;

import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.api.stacks.AEItemKey;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.items.parts.PartModels;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuHostLocator;
import appeng.parts.PartModel;
import appeng.parts.crafting.PatternProviderPart;
import dev.beecube31.crazyae2.common.tile.networking.TilePatternsInterface;
import dev.beecube31.crazyae2.common.util.ContainerOpener;
import dev.beecube31.crazyae2.core.CrazyAE;
import dev.beecube31.crazyae2.core.registries.server.CrazyContainers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class PartPatternsInterface extends PatternProviderPart {
    @PartModels
    public static final ResourceLocation MODEL_BASE = CrazyAE.makeId("part/patterns_interface_base");
    @PartModels
    public static final PartModel MODELS_OFF = new PartModel(MODEL_BASE, ResourceLocation.fromNamespaceAndPath("ae2", "part/interface_off"));
    @PartModels
    public static final PartModel MODELS_ON = new PartModel(MODEL_BASE, ResourceLocation.fromNamespaceAndPath("ae2", "part/interface_on"));
    @PartModels
    public static final PartModel MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE, ResourceLocation.fromNamespaceAndPath("ae2", "part/interface_has_channel"));

    public PartPatternsInterface(IPartItem<?> partItem) {
        super(partItem);
    }

    @Override
    protected PatternProviderLogic createLogic() {
        return new PatternProviderLogic(getMainNode(), this, TilePatternsInterface.SLOTS);
    }

    @Override
    public IPartModel getStaticModels() {
        if (this.isActive() && this.isPowered()) {
            return MODELS_HAS_CHANNEL;
        } else if (this.isPowered()) {
            return MODELS_ON;
        }
        return MODELS_OFF;
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
