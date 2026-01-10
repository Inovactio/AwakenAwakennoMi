package com.inovactio.awakenawakennomi.entities.mobs.ability.kuku;

import com.inovactio.awakenawakennomi.renderers.entities.CakeGolemRenderer;
import com.inovactio.awakenawakennomi.util.AwakenRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public class KukuMobs {

    public static final RegistryObject<EntityType<CakeGolemEntity>> CAKE_GOLEM = AwakenRegistry.registerEntityType("Cake Golem", () -> AwakenRegistry.createEntityType(CakeGolemEntity::new).sized(1.4F, 2.7F).build("awakenawakennomi:cake_golem"));

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerEntityRenderers(FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler((EntityType)CAKE_GOLEM.get(), new CakeGolemRenderer.Factory(new ResourceLocation("awakenawakennomi", "textures/entity/cake_golem.png")));
    }

    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put((EntityType)CAKE_GOLEM.get(), CakeGolemEntity.createAttributes().build());
    }
}
