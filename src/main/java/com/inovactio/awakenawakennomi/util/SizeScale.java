package com.inovactio.awakenawakennomi.util;


import com.inovactio.awakenawakennomi.init.AwakenAttributes;
import net.minecraft.entity.LivingEntity;

public final class SizeScale {
    private static final float MIN_SCALE = 0.05f;
    private static final float MAX_SCALE = 256.0f;

    private SizeScale() {}

    public static float get(LivingEntity living) {
        if (living == null) return 1.0f;
        if (living.getAttributes() == null) return 1.0f;
        if (living.getAttribute(AwakenAttributes.SIZE.get()) == null) return 1.0f;

        float scale = (float) living.getAttributeValue(AwakenAttributes.SIZE.get());

        if (Float.isNaN(scale) || Float.isInfinite(scale)) return 1.0f;

        if (scale < MIN_SCALE) scale = MIN_SCALE;
        if (scale > MAX_SCALE) scale = MAX_SCALE;

        return scale;
    }
}
