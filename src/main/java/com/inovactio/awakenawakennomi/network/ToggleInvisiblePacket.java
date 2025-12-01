package com.inovactio.awakenawakennomi.network;

import com.inovactio.awakenawakennomi.api.common.InvisibleBlockManager;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ToggleInvisiblePacket {
    private final BlockPos pos;
    private final boolean invisible;

    public ToggleInvisiblePacket(BlockPos pos, boolean invisible) {
        this.pos = pos;
        this.invisible = invisible;
    }

    // Encode vers le buffer
    public static void encode(ToggleInvisiblePacket msg, PacketBuffer buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeBoolean(msg.invisible);
    }

    // Decode depuis le buffer
    public static ToggleInvisiblePacket decode(PacketBuffer buf) {
        BlockPos pos = buf.readBlockPos();
        boolean invisible = buf.readBoolean();
        return new ToggleInvisiblePacket(pos, invisible);
    }

    // Handler côté client
    public static void handle(ToggleInvisiblePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            InvisibleBlockManager.setInvisible(msg.pos, msg.invisible);

            BlockPos pos = msg.pos;
            // Rafraîchit le rendu du bloc côté client
            BlockState state = Minecraft.getInstance().level.getBlockState(pos);
            Minecraft.getInstance().levelRenderer.setBlocksDirty(
                    pos.getX(), pos.getY(), pos.getZ(),
                    pos.getX(), pos.getY(), pos.getZ()
            );
        });
        ctx.get().setPacketHandled(true);
    }
}
