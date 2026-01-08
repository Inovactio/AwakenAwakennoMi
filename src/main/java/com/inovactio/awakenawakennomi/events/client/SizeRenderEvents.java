package com.inovactio.awakenawakennomi.events.client;

import com.inovactio.awakenawakennomi.AwakenAwakenNoMiMod;
import com.inovactio.awakenawakennomi.util.SizeScale;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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

        if (living.getPersistentData().getBoolean("awaken_is_morphed")) return;

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
