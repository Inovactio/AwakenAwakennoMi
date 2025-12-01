package com.inovactio.awakenawakennomi.api.common;

import net.minecraft.util.math.BlockPos;
import java.util.HashSet;
import java.util.Set;

public class InvisibleBlockManager {
    private static final Set<BlockPos> INVISIBLE_BLOCKS = new HashSet<>();

    public static void setInvisible(BlockPos pos, boolean invisible) {
        if (invisible) INVISIBLE_BLOCKS.add(pos);
        else INVISIBLE_BLOCKS.remove(pos);
    }

    public static boolean isInvisible(BlockPos pos) {
        return INVISIBLE_BLOCKS.contains(pos);
    }
}

