package com.inovactio.awakenawakennomi.network;

import com.inovactio.awakenawakennomi.api.common.InvisibleBlockManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class RemovePlayerInvisibleBlocksPacket {
    private final UUID playerUuid;

    public RemovePlayerInvisibleBlocksPacket(UUID playerUuid) {
        this.playerUuid = playerUuid;
    }

    public static void encode(RemovePlayerInvisibleBlocksPacket pkt, PacketBuffer buf) {
        buf.writeUUID(pkt.playerUuid);
    }

    public static RemovePlayerInvisibleBlocksPacket decode(PacketBuffer buf) {
        return new RemovePlayerInvisibleBlocksPacket(buf.readUUID());
    }

    public static void handle(RemovePlayerInvisibleBlocksPacket pkt, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc != null) {
                InvisibleBlockManager.removeAllForPlayer(pkt.playerUuid);
            }
        });
        ctx.setPacketHandled(true);
    }
}
