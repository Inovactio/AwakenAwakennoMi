package com.inovactio.awakenawakennomi.entities.projectiles.hiso;

import com.inovactio.awakenawakennomi.entities.projectiles.bomu.PiercingBlastProjectile;
import net.minecraft.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import xyz.pixelatedw.mineminenomi.models.abilities.CubeModel;
import xyz.pixelatedw.mineminenomi.models.entities.projectiles.NoroNoroBeamModel;
import xyz.pixelatedw.mineminenomi.renderers.abilities.AbilityProjectileRenderer;
import xyz.pixelatedw.mineminenomi.renderers.abilities.StretchingProjectileRenderer;
import xyz.pixelatedw.mineminenomi.wypi.WyRegistry;

@Mod.EventBusSubscriber(
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public class HisoProjectiles {

    public static final RegistryObject<EntityType<EtherealWhisperProjectile>> ETHEREAL_WHISPERS = WyRegistry.registerEntityType("Ethereal Whisper", () -> WyRegistry.createEntityType(EtherealWhisperProjectile::new).sized(5.0F, 5.0F).build("awakenawakennomi:ethereal_whisper"));

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerEntityRenderers(FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler((EntityType)ETHEREAL_WHISPERS.get(), (new AbilityProjectileRenderer.Factory(new NoroNoroBeamModel())).setTexture("ethereal_whisper").setScale((double)5.0F).setTranslate((double)0.0F, 0.1, (double)0.0F));
    }
}
