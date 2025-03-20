package dev.beecube31.crazyae2.common.containers;

import appeng.api.config.*;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.util.AEPartLocation;
import appeng.helpers.IContainerCraftingPacket;
import dev.beecube31.crazyae2.common.enums.BotaniaMechanicalDeviceType;
import dev.beecube31.crazyae2.common.tile.botania.TileBotaniaMechanicalMachineBase;
import net.minecraft.entity.player.InventoryPlayer;

public abstract class ContainerMechanicalBotaniaTileBase extends ContainerCrazyAEUpgradeable implements IContainerCraftingPacket {

    protected final TileBotaniaMechanicalMachineBase tile;
    protected final BotaniaMechanicalDeviceType type;

    protected final IGridNode networkNode;

    public ContainerMechanicalBotaniaTileBase(InventoryPlayer ip, TileBotaniaMechanicalMachineBase te) {
        super(ip, te);
        this.tile = te;
        this.type = te.getType();
        this.addMyOffsetX(26);
        this.networkNode = ((IGridHost) te).getGridNode(AEPartLocation.INTERNAL);

        this.initSlots();
    }

    protected abstract void initSlots();

    @Override
    protected int getHeight() {
        return 210;
    }

    @Override
    protected void setupConfig() {}

    public boolean validateRecipe() {
        return this.tile.validateRecipe();
    }

    public void encodePattern() {
        this.tile.encodePattern();
    }

    public TileBotaniaMechanicalMachineBase getTile() {
        return this.tile;
    }

    public void syncClientOnFirstLoad() {
        this.tile.validateRecipe();
    }

    @Override
    protected boolean supportCapacity() {
        return false;
    }

    @Override
    public int availableUpgrades() {
        return 5;
    }

    @Override
    public void detectAndSendChanges() {
        this.verifyPermissions(SecurityPermissions.BUILD, false);

        this.standardDetectAndSendChanges();
    }
}
