package dev.beecube31.crazyae2.common.parts;

import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.helpers.InterfaceLogic;
import appeng.items.parts.PartModels;
import appeng.menu.ISubMenu;
import appeng.menu.locator.MenuLocator;
import appeng.parts.PartModel;
import appeng.parts.misc.InterfacePart;
import dev.beecube31.crazyae2.common.tile.networking.TilePerfectInterface;
import dev.beecube31.crazyae2.common.util.ContainerOpener;
import dev.beecube31.crazyae2.core.CrazyAE;
import dev.beecube31.crazyae2.core.registries.server.CrazyContainers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class PartPerfectInterface extends InterfacePart {
    @PartModels
    public static final ResourceLocation MODEL_BASE = CrazyAE.makeId("part/perfect_interface_base");
    @PartModels
    public static final PartModel MODELS_OFF = new PartModel(MODEL_BASE, new ResourceLocation("ae2", "part/interface_off"));
    @PartModels
    public static final PartModel MODELS_ON = new PartModel(MODEL_BASE, new ResourceLocation("ae2", "part/interface_on"));
    @PartModels
    public static final PartModel MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE, new ResourceLocation("ae2", "part/interface_has_channel"));

    public PartPerfectInterface(IPartItem<?> partItem) {
        super(partItem);
    }

    @Override
    protected InterfaceLogic createLogic() {
        return new InterfaceLogic(getMainNode(), this, getPartItem().asItem(), TilePerfectInterface.SLOTS);
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
    public ItemStack getMainMenuIcon() {
        return new ItemStack(getPartItem());
    }

    @Override
    public void openMenu(Player player, MenuLocator locator) {
        ContainerOpener.open(CrazyContainers.PERFECT_INTERFACE.get(), player, locator);
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu subMenu) {
        ContainerOpener.returnTo(CrazyContainers.PERFECT_INTERFACE.get(), player, subMenu.getLocator());
    }
}
