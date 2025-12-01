package com.inovactio.awakenawakennomi.events;

import com.inovactio.awakenawakennomi.data.InvisibleBlockSavedData;
import com.inovactio.awakenawakennomi.network.ModNetwork;
import com.inovactio.awakenawakennomi.network.ToggleInvisiblePacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

@Mod.EventBusSubscriber
public class PlayerLoginHandler {

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayerEntity)) return;

        ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
        ServerWorld world = player.getLevel();

        // Récupère les données persistées
        InvisibleBlockSavedData data = world.getDataStorage()
                .computeIfAbsent(() -> new InvisibleBlockSavedData(), "invisible_blocks");

        // Envoie un packet pour chaque bloc invisible au joueur
        for (long key : data.getInvisibleBlocks()) {
            BlockPos pos = BlockPos.of(key);
            ModNetwork.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new ToggleInvisiblePacket(pos, true)
            );
        }
    }
}
