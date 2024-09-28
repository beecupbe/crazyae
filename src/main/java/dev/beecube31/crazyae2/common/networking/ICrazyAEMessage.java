package dev.beecube31.crazyae2.common.networking;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public interface ICrazyAEMessage extends IMessage {
	void process(MessageContext ctx);
}
