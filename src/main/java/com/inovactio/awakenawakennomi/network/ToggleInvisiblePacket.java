// java
package com.inovactio.awakenawakennomi.network;

import com.inovactio.awakenawakennomi.api.common.InvisibleBlockManager;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

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

    public static void encode(ToggleInvisiblePacket msg, PacketBuffer buf) {
        buf.writeBlockPos(msg.pos);
        buf.writeUUID(msg.player);
        buf.writeBoolean(msg.invisible);
    }

    public static ToggleInvisiblePacket decode(PacketBuffer buf) {
        BlockPos pos = buf.readBlockPos();
        UUID player = buf.readUUID();
        boolean invisible = buf.readBoolean();
        return new ToggleInvisiblePacket(pos, player, invisible);
    }

    public static void handle(ToggleInvisiblePacket msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            // Si le paquet est reçu côté serveur -> appliquer l'état demandé mais enregistrer l'auteur réel (sender)
            if (ctx.getDirection().getReceptionSide().isServer()) {
                ServerPlayerEntity sender = ctx.getSender();
                if (sender == null) return;
                ServerWorld serverWorld = sender.getLevel();
                if (serverWorld == null) return;

                // Appliquer l'état demandé par le client, mais utiliser l'UUID du sender comme propriétaire
                boolean newInvisible = msg.invisible;
                UUID owner = sender.getUUID();
                InvisibleBlockManager.setInvisible(serverWorld, msg.pos, newInvisible, owner);

                // broadcast résultat aux clients avec l'UUID réel du propriétaire
                ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new ToggleInvisiblePacket(msg.pos, owner, newInvisible));
            } else {
                // Réception côté client : mettre à jour cache client et rafraîchir rendu
                InvisibleBlockManager.setInvisible(msg.pos, msg.player, msg.invisible);
                Minecraft mc = Minecraft.getInstance();
                if (mc != null && mc.level != null) {
                    BlockState state = mc.level.getBlockState(msg.pos);
                    mc.level.sendBlockUpdated(msg.pos, state, state, 3);
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}
