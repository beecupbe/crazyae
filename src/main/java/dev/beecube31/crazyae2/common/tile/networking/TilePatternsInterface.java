package dev.beecube31.crazyae2.common.tile.networking;

import appeng.api.definitions.IItemDefinition;
import appeng.core.sync.GuiBridge;
import appeng.tile.misc.TileInterface;
import dev.beecube31.crazyae2.common.duality.PatternsInterfaceDuality;
import dev.beecube31.crazyae2.common.interfaces.gui.IPriHostExtender;
import dev.beecube31.crazyae2.common.interfaces.gui.IPriHostGuiOverrider;
import dev.beecube31.crazyae2.common.interfaces.upgrades.IUpgradesInfoProvider;
import dev.beecube31.crazyae2.common.sync.CrazyAEGuiBridge;
import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class TilePatternsInterface extends TileInterface implements IPriHostGuiOverrider, IPriHostExtender, IUpgradesInfoProvider {

    public TilePatternsInterface() {
        ObfuscationReflectionHelper.setPrivateValue(TileInterface.class, this, new PatternsInterfaceDuality(this.getProxy(), this), "duality");
    }

    @Override
    public GuiBridge getGuiBridge() {
        return GuiBridge.GUI_Handler; //stub, use getOverrideGui();
    }

    @Override
    public CrazyAEGuiBridge getOverrideGui() {
        return CrazyAEGuiBridge.PATTERN_INTERFACE;
    }

    @Override
    public ItemStack getItemStackRepresentation() {
        return CrazyAE.definitions().blocks().patternsInterface().maybeStack(1).orElse(ItemStack.EMPTY);
    }

    @Override
    public IItemDefinition getBlock() {
        return CrazyAE.definitions().blocks().patternsInterface();
    }

    @Override
    public int getConfigSlots() {
        return 0;
    }

    @Override
    public int getStorageSlots() {
        return 9;
    }

    @Override
    public int getPatternsSlots() {
        return PatternsInterfaceDuality.NUMBER_OF_PATTERN_SLOTS;
    }
}
