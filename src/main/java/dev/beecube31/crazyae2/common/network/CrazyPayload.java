package dev.beecube31.crazyae2.common.network;

import dev.beecube31.crazyae2.core.CrazyAE;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record CrazyPayload(byte[] data) implements CustomPacketPayload {

    public static final Type<CrazyPayload> TYPE = new Type<>(CrazyAE.makeId("main"));

    public static final StreamCodec<FriendlyByteBuf, CrazyPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> buf.writeByteArray(payload.data),
            buf -> new CrazyPayload(buf.readByteArray()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
