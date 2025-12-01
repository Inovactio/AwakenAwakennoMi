package com.inovactio.awakenawakennomi.api.common;

import com.inovactio.awakenawakennomi.data.InvisibleBlockSavedData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;

import java.util.HashSet;
import java.util.Set;

public class InvisibleBlockManager {
    // Cache côté client
    private static final Set<Long> CLIENT_INVISIBLE = new HashSet<>();

    // Version client : 2 arguments
    public static void setInvisible(BlockPos pos, boolean invisible) {
        long key = pos.asLong();
        if (invisible) CLIENT_INVISIBLE.add(key);
        else CLIENT_INVISIBLE.remove(key);
    }

    public static boolean isInvisible(BlockPos pos) {
        return CLIENT_INVISIBLE.contains(pos.asLong());
    }

    public static void setInvisible(World world, BlockPos pos, boolean invisible) {
        if (!(world instanceof ServerWorld)) return;
        ServerWorld serverWorld = (ServerWorld) world;
        DimensionSavedDataManager storage = serverWorld.getDataStorage();
        InvisibleBlockSavedData data = storage.computeIfAbsent(
                InvisibleBlockSavedData::new,
                "invisible_blocks"
        );
        data.setInvisible(pos, invisible);
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

