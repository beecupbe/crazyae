package dev.beecube31.crazyae2.common.parts;

import appeng.api.parts.IPartItem;
import appeng.menu.MenuOpener;
import appeng.menu.implementations.IOBusMenu;
import appeng.menu.locator.MenuLocators;
import appeng.parts.automation.ImportBusPart;
import dev.beecube31.crazyae2.common.interfaces.parts.ICrazyPart;
import dev.beecube31.crazyae2.common.util.ContainerOpener;
import dev.beecube31.crazyae2.core.CrazyAE;
import dev.beecube31.crazyae2.core.registries.server.CrazyContainers;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.phys.Vec3;

public class PartImprovedImportBus extends ImportBusPart implements ICrazyPart {

    public PartImprovedImportBus(IPartItem<?> partItem) {
        super(partItem);
    }

    @Override
    public boolean onCrazyPartActivate(Player player, InteractionHand hand, Vec3 pos) {
        if (!isClientSide()) {
            ContainerOpener.open(getMenuType(), player, MenuLocators.forPart(this));
        }
        return true;
    }

    @Override
    protected MenuType<?> getMenuType() {
        return CrazyContainers.IMPROVED_IMPORT_BUS.get();
    }

    @Override
    protected int getOperationsPerTick() {
        return CrazyBusSpeed.operations(getUpgrades());
    }

}
