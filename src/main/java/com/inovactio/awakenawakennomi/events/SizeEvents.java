package com.inovactio.awakenawakennomi.events;

import com.inovactio.awakenawakennomi.init.AwakenAttributes;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "awakenawakennomi")
public final class SizeEvents {
    // Stocké sur l'entité pour détecter un changement de scale "effectif"
    private static final String NBT_KEY_LAST_EFFECTIVE_SCALE = "awakenawakennomi:last_effective_size_scale";
    private static final float EPSILON = 1.0E-4f;

    @SubscribeEvent
    public static void onEntitySize(EntityEvent.Size event) {
        if (!(event.getEntity() instanceof LivingEntity)) return;
        LivingEntity living = (LivingEntity) event.getEntity();

        // L'event peut arriver très tôt (constructeur), garder des gardes strictes
        if (living.getAttributes() == null) return;

        if (living.getAttribute(AwakenAttributes.SIZE.get()) == null) return;

        float scale = (float) living.getAttributeValue(AwakenAttributes.SIZE.get());
        if (scale <= 0.0f) scale = 1.0f;
        if (Math.abs(scale - 1.0f) <= EPSILON) return;

        Pose pose = living.getPose();

        // Important: event.getOldSize() correspond à la taille "vanilla" calculée par Minecraft pour ce pose
        EntitySize base = event.getOldSize();
        EntitySize scaled = EntitySize.scalable(base.width * scale, base.height * scale);
        event.setNewSize(scaled);

        float newEyeHeight = event.getOldEyeHeight() * scale;
        if (newEyeHeight < 0.22f) newEyeHeight = 0.22f;
        event.setNewEyeHeight(newEyeHeight);
    }

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        LivingEntity living = event.getEntityLiving();
        if (living.getAttributes() == null) return;
        if (living.getAttribute(AwakenAttributes.SIZE.get()) == null) return;

        // Valeur "effective" (clamp) pour éviter refresh en boucle si l'attribut tombe à 0
        float scale = (float) living.getAttributeValue(AwakenAttributes.SIZE.get());
        if (scale <= 0.0f) scale = 1.0f;

        CompoundNBT data = living.getPersistentData();
        float last = data.contains(NBT_KEY_LAST_EFFECTIVE_SCALE) ? data.getFloat(NBT_KEY_LAST_EFFECTIVE_SCALE) : 1.0f;

        if (Math.abs(scale - last) > EPSILON) {
            data.putFloat(NBT_KEY_LAST_EFFECTIVE_SCALE, scale);

            // C'est l'équivalent "apply runtime" façon ModAttributes:
            // force le recalcul des dimensions => Forge reposte EntityEvent.Size => onEntitySize applique le scale
            living.refreshDimensions();
            living.ejectPassengers();
        }
    }
}
