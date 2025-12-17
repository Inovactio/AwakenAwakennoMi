package com.inovactio.awakenawakennomi.entities.projectiles.bomu;

import net.minecraft.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import xyz.pixelatedw.mineminenomi.models.abilities.CubeModel;
import xyz.pixelatedw.mineminenomi.renderers.abilities.StretchingProjectileRenderer;
import xyz.pixelatedw.mineminenomi.wypi.WyRegistry;

@Mod.EventBusSubscriber(
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public class BomuProjectiles {
    public static final RegistryObject<EntityType<PiercingBlastProjectile>> PIERCING_BLAST = WyRegistry.registerEntityType("PiercingBlast", () -> WyRegistry.createEntityType(PiercingBlastProjectile::new).sized(1.0F, 1.0F).build("awakenawakennomi:piercing_blast"));

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerEntityRenderers(FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler((EntityType)PIERCING_BLAST.get(), (new StretchingProjectileRenderer.Factory(new CubeModel(), new CubeModel())).setStretchScale((double)0.25F, (double)0.25F).setGlowing().setColor("#FF8C00").setScale((double)0.25F, (double)0.25F, (double)3.0F));
    }
}
