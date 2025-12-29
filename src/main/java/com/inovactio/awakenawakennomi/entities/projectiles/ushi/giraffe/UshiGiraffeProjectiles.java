package com.inovactio.awakenawakennomi.entities.projectiles.ushi.giraffe;

import com.inovactio.awakenawakennomi.entities.projectiles.deka.TitanSmashProjectile;
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
public class UshiGiraffeProjectiles {
    public static final RegistryObject<EntityType<BiganProjectile>> BIGAN = WyRegistry.registerEntityType("ReworkedBigan", () -> WyRegistry.createEntityType(BiganProjectile::new).sized(1.0F, 1.0F).build("awakenawakennomi:bigan"));
    public static final RegistryObject<EntityType<AwakenBiganProjectile>> AWAKEN_BIGAN = WyRegistry.registerEntityType("ReworkedAwakenBigan", () -> WyRegistry.createEntityType(AwakenBiganProjectile::new).sized(1.0F, 1.0F).build("awakenawakennomi:awaken_bigan"));

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerEntityRenderers(FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler((EntityType)BIGAN.get(), (new StretchingProjectileRenderer.Factory(new CubeModel(), new CubeModel()).setStretchScale((double)BiganProjectile.PROJECTILE_SIZE, (double)BiganProjectile.PROJECTILE_SIZE).setColor("F2C46C").setScale((double)BiganProjectile.PROJECTILE_SIZE, (double)BiganProjectile.PROJECTILE_SIZE, (double)1F)));
        RenderingRegistry.registerEntityRenderingHandler((EntityType)AWAKEN_BIGAN.get(), (new StretchingProjectileRenderer.Factory(new CubeModel(), new CubeModel()).setStretchScale((double)AwakenBiganProjectile.PROJECTILE_SIZE, (double)AwakenBiganProjectile.PROJECTILE_SIZE).setColor("F2C46C").setScale((double)AwakenBiganProjectile.PROJECTILE_SIZE, (double)AwakenBiganProjectile.PROJECTILE_SIZE, (double)1F)));
    }
}
