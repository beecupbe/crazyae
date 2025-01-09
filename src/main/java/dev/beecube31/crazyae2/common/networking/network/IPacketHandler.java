package dev.beecube31.crazyae2.common.networking.network;

import appeng.core.sync.network.INetworkInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

public interface IPacketHandler {

    void onPacketData(INetworkInfo manager, INetHandler handler, FMLProxyPacket packet, EntityPlayer player);

}
