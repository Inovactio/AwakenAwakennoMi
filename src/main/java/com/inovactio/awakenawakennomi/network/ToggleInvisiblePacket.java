package com.inovactio.awakenawakennomi.network;

import com.inovactio.awakenawakennomi.api.common.InvisibleBlockManager;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class ToggleInvisiblePacket {
    private final BlockPos pos;
    private final boolean invisible;
    private final UUID player;

    public ToggleInvisiblePacket(BlockPos pos, UUID player, boolean invisible) {
        this.pos = pos;
        this.player = player;
        this.invisible = invisible;
    }

    // Encode vers le buffer
    public static void encode(ToggleInvisiblePacket msg, PacketBuffer buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeUUID(msg.player);
        buf.writeBoolean(msg.invisible);
    }

    // Decode depuis le buffer
    public static ToggleInvisiblePacket decode(PacketBuffer buf) {
        BlockPos pos = buf.readBlockPos();
        UUID player = buf.readUUID();
        boolean invisible = buf.readBoolean();
        return new ToggleInvisiblePacket(pos, player, invisible);
    }

    public static void handle(ToggleInvisiblePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Mets à jour le cache client (version avec UUID du joueur)
            InvisibleBlockManager.setInvisible(msg.pos, msg.player, msg.invisible);

            // Rafraîchit le rendu du bloc côté client
            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null) {
                BlockPos pos = msg.pos;
                BlockState state = mc.level.getBlockState(pos);

                // Force la mise à jour du rendu
                mc.level.sendBlockUpdated(pos, state, state, 3);
            }
        });
        ctx.get().setPacketHandled(true);
    }

}
