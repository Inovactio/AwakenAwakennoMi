package com.inovactio.awakenawakennomi.entities.projectiles.deka;

import net.minecraft.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import xyz.pixelatedw.mineminenomi.models.abilities.CubeModel;
import xyz.pixelatedw.mineminenomi.models.abilities.EntityArmModel;
import xyz.pixelatedw.mineminenomi.renderers.abilities.StretchingProjectileRenderer;
import xyz.pixelatedw.mineminenomi.wypi.WyRegistry;

@Mod.EventBusSubscriber(
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public class DekaProjectiles {
    public static final RegistryObject<EntityType<TitanSmashProjectile>> TIMAN_SMASH = WyRegistry.registerEntityType("TitanSmash", () -> WyRegistry.createEntityType(TitanSmashProjectile::new).sized(1.0F, 1.0F).build("awakenawakennomi:titan_smash"));

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerEntityRenderers(FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler((EntityType)TIMAN_SMASH.get(), (new StretchingProjectileRenderer.Factory(new EntityArmModel()).setStretchScale((double)TitanSmashProjectile.PROJECTILE_SIZE, (double)TitanSmashProjectile.PROJECTILE_SIZE).setPlayerTexture().setScale((double)TitanSmashProjectile.PROJECTILE_SIZE, (double)TitanSmashProjectile.PROJECTILE_SIZE, (double)1F)));
    }
}
