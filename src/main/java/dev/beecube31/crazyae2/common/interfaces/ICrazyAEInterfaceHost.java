package dev.beecube31.crazyae2.common.interfaces;

import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.networking.security.IActionHost;
import dev.beecube31.crazyae2.common.duality.PatternsInterfaceDuality;
import dev.beecube31.crazyae2.common.interfaces.upgrades.IUpgradesInfoProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import java.util.EnumSet;

public interface ICrazyAEInterfaceHost extends ICraftingProvider, IUpgradesInfoProvider, IActionHost {
    PatternsInterfaceDuality getInterfaceDuality();

    EnumSet<EnumFacing> getTargets();

    TileEntity getTileEntity();

    void saveChanges();
}
