package dev.beecube31.crazyae2.common.interfaces;

import appeng.api.implementations.IUpgradeableHost;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.networking.security.IActionHost;
import dev.beecube31.crazyae2.common.duality.PatternsInterfaceDuality;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import java.util.EnumSet;

public interface ICrazyAEPatternsInterface extends ICraftingProvider, IUpgradeableHost, IActionHost {
    PatternsInterfaceDuality getInterfaceDuality();

    EnumSet<EnumFacing> getTargets();

    TileEntity getTileEntity();

    void saveChanges();
}
