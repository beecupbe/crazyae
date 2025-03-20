package dev.beecube31.crazyae2.common.tile.networking;

import appeng.api.definitions.IItemDefinition;
import appeng.core.sync.GuiBridge;
import appeng.tile.misc.TileInterface;
import dev.beecube31.crazyae2.common.duality.PerfectInterfaceDuality;
import dev.beecube31.crazyae2.common.interfaces.gui.IPriHostExtender;
import dev.beecube31.crazyae2.common.interfaces.gui.IPriHostGuiOverrider;
import dev.beecube31.crazyae2.common.interfaces.upgrades.IUpgradesInfoProvider;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiBridge;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class TilePerfectInterface extends TileInterface implements IPriHostGuiOverrider, IPriHostExtender, IUpgradesInfoProvider {

    public TilePerfectInterface() {
        ObfuscationReflectionHelper.setPrivateValue(TileInterface.class, this, new PerfectInterfaceDuality(this.getProxy(), this), "duality");
    }

    @Override
    public GuiBridge getGuiBridge() {
        return GuiBridge.GUI_Handler; //stub, use getOverrideGui();
    }

    @Override
    public CrazyAEGuiBridge getOverrideGui() {
        return CrazyAEGuiBridge.PERFECT_INTERFACE;
    }

    @Override
    public ItemStack getItemStackRepresentation() {
        return CrazyAE.definitions().blocks().perfectInterface().maybeStack(1).orElse(ItemStack.EMPTY);
    }

    @Override
    public IItemDefinition getBlock() {
        return CrazyAE.definitions().blocks().perfectInterface();
    }

    @Override
    public int getConfigSlots() {
        return 36;
    }

    @Override
    public int getStorageSlots() {
        return 36;
    }

    @Override
    public int getPatternsSlots() {
        return 0;
    }
}
