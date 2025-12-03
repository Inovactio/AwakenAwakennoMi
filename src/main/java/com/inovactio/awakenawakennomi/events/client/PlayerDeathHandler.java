package com.inovactio.awakenawakennomi.events.client;

import com.inovactio.awakenawakennomi.data.InvisibleBlockSavedData;
import com.inovactio.awakenawakennomi.network.ModNetwork;
import com.inovactio.awakenawakennomi.network.ToggleInvisiblePacket;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber
public class PlayerDeathHandler {

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayerEntity)) return;

        ServerPlayerEntity player = (ServerPlayerEntity) event.getEntity();
        ServerWorld world = player.getLevel();
        UUID playerUuid = player.getUUID();

        InvisibleBlockSavedData data = world.getDataStorage()
                .computeIfAbsent(InvisibleBlockSavedData::new, InvisibleBlockSavedData.NAME);

        // Supprime les entrées persistées appartenant au joueur et récupère les positions supprimées
        List<Long> removed = data.removeEntriesForPlayer(playerUuid);

        // Notifie les clients pour rendre les blocs visibles
        for (Long posLong : removed) {
            BlockPos pos = BlockPos.of(posLong);
            ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new ToggleInvisiblePacket(pos, playerUuid, false));
        }
    }
}
