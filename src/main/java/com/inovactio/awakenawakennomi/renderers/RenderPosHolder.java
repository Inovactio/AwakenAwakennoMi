package com.inovactio.awakenawakennomi.renderers;

import net.minecraft.util.math.BlockPos;

public class RenderPosHolder {
    private static final ThreadLocal<BlockPos> POS = new ThreadLocal<>();

    public static void set(BlockPos pos) {
        POS.set(pos);
    }

    public static BlockPos get() {
        return POS.get();
    }

    public static void clear() {
        POS.remove();
    }
}

