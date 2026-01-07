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

        // Ne pas doubler le scaling du joueur si CartAddon est chargé (RaceEventsSmall le fait déjà)
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

        // IMPORTANT: utiliser l’ancienne taille fournie par l’event comme base, pas entity.getDimensions(...)
        EntitySize base = event.getOldSize();
        event.setNewSize(base.scale(s), true);

        float eye = Math.max(0.05F, base.height * s * 0.85F);
        event.setNewEyeHeight(eye);
    }

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entity = event.getEntityLiving();

        // Optimisation : Ne rien faire si le monde est distant (client) car les attributs sont gérés par le serveur
        // Cependant, pour la fluidité, refreshDimensions() doit souvent se produire des deux côtés.
        // On laisse courir sur les deux sides pour éviter la désynchronisation.

        if (shouldSkip(entity)) return;

        double targetScale = 1.0;
        try {
            if (AwakenAttributes.SIZE.get() != null && entity.getAttributes() != null) {
                targetScale = entity.getAttributeValue(AwakenAttributes.SIZE.get());
            }
        } catch (Exception ignored) {
            return;
        }

        // On stocke la dernière échelle connue dans les données persistantes pour éviter de spammer refreshDimensions()
        float lastScale = entity.getPersistentData().getFloat("awaken_last_scale");
        if (lastScale == 0) lastScale = 1.0f;

        // Si l'échelle a changé significativement (plus de 0.01 de différence)
        if (Math.abs(targetScale - lastScale) > 0.01) {
            entity.getPersistentData().putFloat("awaken_last_scale", (float) targetScale);
            entity.refreshDimensions(); // C'est CELA qui déclenche l'event onHitboxRescaleLiving ci-dessous
        }
    }

    private static boolean shouldSkip(LivingEntity entity) {
        // Ne pas doubler le scaling du joueur si CartAddon est chargé
        if (AwakenAwakenNoMiMod.hasCartAddonInstalled() && entity instanceof PlayerEntity) return true;
        // Ignorer les entités morphées
        if (entity.getPersistentData().getBoolean("awaken_is_morphed")) return true;

        return false;
    }

    private static double safe(double v, double def, double min, double max) {
        if (Double.isNaN(v) || Double.isInfinite(v)) v = def;
        if (v < min) v = min;
        if (v > max) v = max;
        return v;
    }
}