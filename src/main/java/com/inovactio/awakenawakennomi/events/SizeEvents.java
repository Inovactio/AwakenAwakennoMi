package com.inovactio.awakenawakennomi.events;

import com.inovactio.awakenawakennomi.AwakenAwakenNoMiMod;
import com.inovactio.awakenawakennomi.init.AwakenAttributes;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;



@Mod.EventBusSubscriber(modid = "awakenawakennomi")
public final class SizeEvents {

    private SizeEvents() {}

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onHitboxRescaleLiving(EntityEvent.Size event) {
        if (!(event.getEntity() instanceof LivingEntity)) return;

        LivingEntity entity = (LivingEntity) event.getEntity();

        if (AwakenAwakenNoMiMod.hasCartAddonInstalled() && entity instanceof PlayerEntity) return;

        if (entity.getPersistentData().getBoolean("awaken_is_morphed")) return;

        double scale = 1.0;
        try {
            if (AwakenAttributes.SIZE.get() != null && entity.getAttributes() != null) {
                scale = entity.getAttributeValue(AwakenAttributes.SIZE.get());
            }
        } catch (Throwable ignored) {}

        scale = safe(scale, 1.0, 0.05, 10.0);
        float s = (float) scale;

        EntitySize base = event.getNewSize();
        event.setNewSize(base.scale(s), true);

        float eye = Math.max(0.05F, base.height * s * 0.85F);
        event.setNewEyeHeight(eye);
    }

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entity = event.getEntityLiving();

        if (shouldSkip(entity)) return;

        double targetScale = 1.0;
        try {
            if (AwakenAttributes.SIZE.get() != null && entity.getAttributes() != null) {
                targetScale = entity.getAttributeValue(AwakenAttributes.SIZE.get());
            }
        } catch (Exception ignored) {
            return;
        }

        float lastScale = entity.getPersistentData().getFloat("awaken_last_scale");
        if (lastScale == 0) lastScale = 1.0f;

        if (Math.abs(targetScale - lastScale) > 0.01) {
            entity.getPersistentData().putFloat("awaken_last_scale", (float) targetScale);
            entity.refreshDimensions(); // C'est CELA qui déclenche l'event onHitboxRescaleLiving ci-dessous
        }
    }

    private static boolean shouldSkip(LivingEntity entity) {
        if (AwakenAwakenNoMiMod.hasCartAddonInstalled() && entity instanceof PlayerEntity) return true;
        return entity.getPersistentData().getBoolean("awaken_is_morphed");
    }

    private static double safe(double v, double def, double min, double max) {
        if (Double.isNaN(v) || Double.isInfinite(v)) v = def;
        if (v < min) v = min;
        if (v > max) v = max;
        return v;
    }
}