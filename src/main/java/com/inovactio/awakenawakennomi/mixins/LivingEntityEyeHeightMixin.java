package com.inovactio.awakenawakennomi.mixins;

import com.inovactio.awakenawakennomi.init.AwakenAttributes;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityEyeHeightMixin {
    private static final float EPSILON = 1.0E-4f;

    // Borne basse \+ haute pour éviter les valeurs “instables” (0, négatives, énormes) qui causent du jitter.
    private static final float MIN_SCALE = 0.05f;
    private static final float MAX_SCALE = 256.0f;

    private static final float MIN_EYE_HEIGHT = 0.22f;

    @Inject(method = "getEyeHeight", at = @At("RETURN"), cancellable = true)
    private void awakenawakennomi$scaleEyeHeight(CallbackInfoReturnable<Float> cir) {
        LivingEntity living = (LivingEntity) (Object) this;

        if (living.getAttributes() == null) return;
        if (living.getAttribute(AwakenAttributes.SIZE.get()) == null) return;

        float scale = (float) living.getAttributeValue(AwakenAttributes.SIZE.get());

        // Clamp stable au lieu de “<= 0 => 1” (qui peut provoquer un aller-retour visible).
        if (scale < MIN_SCALE) scale = MIN_SCALE;
        if (scale > MAX_SCALE) scale = MAX_SCALE;

        if (Math.abs(scale - 1.0f) <= EPSILON) return;

        float scaled = cir.getReturnValue() * scale;
        if (scaled < MIN_EYE_HEIGHT) scaled = MIN_EYE_HEIGHT;

        cir.setReturnValue(scaled);
    }
}
