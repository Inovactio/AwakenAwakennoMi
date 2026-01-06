package com.inovactio.awakenawakennomi.events.client;

import com.inovactio.awakenawakennomi.init.AwakenAttributes;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "awakenawakennomi", value = Dist.CLIENT)
public final class SizeRenderEvents {
    private static final float EPSILON = 1.0E-4f;

    private SizeRenderEvents() {
    }

    @SubscribeEvent
    public static void onRenderLivingPre(RenderLivingEvent.Pre<?, ?> event) {
        LivingEntity living = event.getEntity();
        if (living.getAttributes() == null) return;
        if (living.getAttribute(AwakenAttributes.SIZE.get()) == null) return;

        float scale = (float) living.getAttributeValue(AwakenAttributes.SIZE.get());
        if (scale <= 0.0f) scale = 1.0f;
        if (Math.abs(scale - 1.0f) <= EPSILON) return;

        event.getMatrixStack().scale(scale, scale, scale);
    }
}
