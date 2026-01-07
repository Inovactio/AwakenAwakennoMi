package com.inovactio.awakenawakennomi.events.client;

import com.inovactio.awakenawakennomi.AwakenAwakenNoMiMod;
import com.inovactio.awakenawakennomi.init.AwakenAttributes;
import com.inovactio.awakenawakennomi.util.SizeScale;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.pixelatedw.mineminenomi.api.events.SetCameraZoomEvent;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = "awakenawakennomi", value = Dist.CLIENT)
public final class SizeRenderEvents {

    private static final float EPSILON = 1.0E-4f;
    private static final float VIS_MIN = 0.1f;

    private SizeRenderEvents() {}

    @SubscribeEvent
    public static void onRenderLivingPre(RenderLivingEvent.Pre<?, ?> event) {
        LivingEntity living = event.getEntity();
        if (living == null) return;

        // Évite de scaler les morphs (si votre pipeline gère déjà le rendu)
        if (living.getPersistentData().getBoolean("awaken_is_morphed")) return;

        // Si CartAddon est installé, on évite le double scaling du player (RaceEventsSmall le fait déjà)
        if (AwakenAwakenNoMiMod.hasCartAddonInstalled() && living instanceof PlayerEntity) return;

        float scale = SizeScale.get(living);
        if (Math.abs(scale - 1.0f) <= EPSILON) return;

        float vis = Math.max(VIS_MIN, scale);

        event.getMatrixStack().pushPose();
        event.getMatrixStack().scale(vis, vis, vis);
    }

    @SubscribeEvent
    public static void onRenderLivingPost(RenderLivingEvent.Post<?, ?> event) {
        LivingEntity living = event.getEntity();
        if (living == null) return;

        if (living.getPersistentData().getBoolean("awaken_is_morphed")) return;
        if (AwakenAwakenNoMiMod.hasCartAddonInstalled() && living instanceof PlayerEntity) return;

        float scale = SizeScale.get(living);
        if (Math.abs(scale - 1.0f) <= EPSILON) return;

        event.getMatrixStack().popPose();
    }
}
