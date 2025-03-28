package dev.beecube31.crazyae2.common.compat;

import com.denfop.api.energy.IEnergyTile;
import com.denfop.api.energy.event.EnergyTileLoadEvent;
import com.denfop.api.energy.event.EnergyTileUnLoadEvent;
import com.denfop.api.sytem.EnergyEvent;
import com.denfop.api.sytem.EnergyType;
import com.denfop.api.sytem.EnumTypeEvent;
import com.denfop.api.sytem.ITile;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class IUCompat {
    public static void addEFTileToWorld(World world, IEnergyTile energyTile1) {
        MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(world, energyTile1));
    }

    public static void removeEFTileFromWorld(World world, IEnergyTile energyTile1) {
        MinecraftForge.EVENT_BUS.post(new EnergyTileUnLoadEvent(world, energyTile1));
    }

    public static void addMultiTileToWorld(final World world, EnergyType energyType, ITile tile) {
        MinecraftForge.EVENT_BUS.post(new EnergyEvent(world, EnumTypeEvent.LOAD, energyType, tile));
    }

    public static void removeMultiTileFromWorld(final World world, EnergyType energyType, ITile tile) {
        MinecraftForge.EVENT_BUS.post(new EnergyEvent(world, EnumTypeEvent.UNLOAD, energyType, tile));
    }
}
