package net.bdew.lib.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * A small envelope payload. bdlib's networking API is codec-based and dynamic;
 * this wrapper preserves that design on top of NeoForge's typed payload system.
 */
public record NetPayload(String channel, int id, byte[] data) implements CustomPacketPayload {
    public static final Type<NetPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("advgenerators", "net"));

    public static final StreamCodec<RegistryFriendlyByteBuf, NetPayload> CODEC = StreamCodec.of(
            (buf, msg) -> {
                buf.writeUtf(msg.channel());
                buf.writeVarInt(msg.id());
                buf.writeByteArray(msg.data());
            },
            buf -> new NetPayload(buf.readUtf(), buf.readVarInt(), buf.readByteArray())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
