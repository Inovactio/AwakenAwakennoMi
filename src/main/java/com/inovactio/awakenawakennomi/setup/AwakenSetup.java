package com.inovactio.awakenawakennomi.setup;

import com.inovactio.awakenawakennomi.renderers.layers.AwakenZoanSmokeLayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import xyz.pixelatedw.mineminenomi.ModMain;

import java.util.Map;

@Mod.EventBusSubscriber(
        modid = "awakenawakennomi",
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public class AwakenSetup {
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();

            for(Map.Entry entry : mc.getEntityRenderDispatcher().renderers.entrySet()) {
                EntityRenderer entityRenderer = (EntityRenderer)entry.getValue();
                if (entityRenderer instanceof LivingRenderer) {
                    LivingRenderer renderer = (LivingRenderer)entityRenderer;
                    if (renderer.getModel() instanceof BipedModel) {
                        renderer.addLayer(new AwakenZoanSmokeLayer(renderer));
                    }
                }
            }

            for(Map.Entry entry : mc.getEntityRenderDispatcher().getSkinMap().entrySet()) {
                PlayerRenderer rendererx = (PlayerRenderer)entry.getValue();
                rendererx.addLayer(new AwakenZoanSmokeLayer<>(rendererx));
            }
        });
    }
}
