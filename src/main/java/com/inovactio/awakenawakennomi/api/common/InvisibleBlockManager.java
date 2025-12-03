package com.inovactio.awakenawakennomi.api.common;

import com.inovactio.awakenawakennomi.data.InvisibleBlockSavedData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InvisibleBlockManager {
    // Cache côté client : map blockKey -> joueur qui a rendu invisible
    private static final Map<Long, UUID> CLIENT_INVISIBLE = new ConcurrentHashMap<>();

    // Version client : spécifier le joueur (UUID)
    public static void setInvisible(BlockPos pos, UUID player, boolean invisible) {
        long key = pos.asLong();
        if (invisible) CLIENT_INVISIBLE.put(key, player);
        else CLIENT_INVISIBLE.remove(key);
    }

    // Récupérer l'auteur qui a rendu le bloc invisible (peut retourner null)
    public static UUID getInvisibleBy(BlockPos pos) {
        return CLIENT_INVISIBLE.get(pos.asLong());
    }

    // Vérifier si le bloc est invisible pour un joueur donné
    public static boolean isInvisible(BlockPos pos, UUID player) {
        UUID owner = CLIENT_INVISIBLE.get(pos.asLong());
        return player != null && player.equals(owner);
    }

    // Supprimer toutes les entrées associées à un joueur (client-side)
    public static void removeAllForPlayer(UUID player) {
        if (player == null) return;
        CLIENT_INVISIBLE.entrySet().removeIf(e -> player.equals(e.getValue()));
    }

    // --- API serveur : stockage global côté serveur (mettre à jour pour inclure l'owner UUID) ---
    public static void setInvisible(World world, BlockPos pos, boolean invisible, UUID owner) {
        if (!(world instanceof ServerWorld)) return;
        ServerWorld serverWorld = (ServerWorld) world;
        DimensionSavedDataManager storage = serverWorld.getDataStorage();
        InvisibleBlockSavedData data = storage.computeIfAbsent(
                InvisibleBlockSavedData::new,
                "invisible_blocks"
        );
        data.setInvisible(pos, invisible, owner);
    }

    public static boolean isInvisible(World world, BlockPos pos) {
        if (!(world instanceof ServerWorld)) return false;
        ServerWorld serverWorld = (ServerWorld) world;
        DimensionSavedDataManager storage = serverWorld.getDataStorage();
        InvisibleBlockSavedData data = storage.computeIfAbsent(
                InvisibleBlockSavedData::new,
                "invisible_blocks"
        );
        return data.isInvisible(pos);
    }
}
