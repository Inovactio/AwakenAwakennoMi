package com.inovactio.awakenawakennomi.entities.projectiles.suke;

import net.minecraft.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import xyz.pixelatedw.mineminenomi.entities.projectiles.suke.ShishaNoTeProjectile;
import xyz.pixelatedw.mineminenomi.models.abilities.CubeModel;
import xyz.pixelatedw.mineminenomi.models.abilities.SphereModel;
import xyz.pixelatedw.mineminenomi.renderers.abilities.AbilityProjectileRenderer;
import xyz.pixelatedw.mineminenomi.renderers.abilities.StretchingProjectileRenderer;
import xyz.pixelatedw.mineminenomi.wypi.WyRegistry;

@Mod.EventBusSubscriber(
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public class SukeProjectiles {
    public static final RegistryObject<EntityType<DiffractionProjectileBlue>> DIFFRACTION_BLUE = WyRegistry.registerEntityType("DiffractionBlue", () -> WyRegistry.createEntityType(DiffractionProjectileBlue::new).sized(0.5F, 0.5F).build("awakenawakennomi:diffraction_blue"));
    public static final RegistryObject<EntityType<DiffractionProjectileIndigo>> DIFFRACTION_INDIGO = WyRegistry.registerEntityType("DiffractionIndigo", () -> WyRegistry.createEntityType(DiffractionProjectileIndigo::new).sized(0.5F, 0.5F).build("awakenawakennomi:diffraction_indigo"));
    public static final RegistryObject<EntityType<DiffractionProjectileViolet>> DIFFRACTION_VIOLET = WyRegistry.registerEntityType("DiffractionViolet", () -> WyRegistry.createEntityType(DiffractionProjectileViolet::new).sized(0.5F, 0.5F).build("awakenawakennomi:diffraction_violet"));
    public static final RegistryObject<EntityType<DiffractionProjectileRed>> DIFFRACTION_RED = WyRegistry.registerEntityType("DiffractionRed", () -> WyRegistry.createEntityType(DiffractionProjectileRed::new).sized(0.5F, 0.5F).build("awakenawakennomi:diffraction_red"));
    public static final RegistryObject<EntityType<DiffractionProjectileOrange>> DIFFRACTION_ORANGE = WyRegistry.registerEntityType("DiffractionOrange", () -> WyRegistry.createEntityType(DiffractionProjectileOrange::new).sized(0.5F, 0.5F).build("awakenawakennomi:diffraction_orange"));
    public static final RegistryObject<EntityType<DiffractionProjectileYellow>> DIFFRACTION_YELLOW = WyRegistry.registerEntityType("DiffractionYellow", () -> WyRegistry.createEntityType(DiffractionProjectileYellow::new).sized(0.5F, 0.5F).build("awakenawakennomi:diffraction_yellow"));
    public static final RegistryObject<EntityType<DiffractionProjectileGreen>> DIFFRACTION_GREEN = WyRegistry.registerEntityType("DiffractionGreen", () -> WyRegistry.createEntityType(DiffractionProjectileGreen::new).sized(0.5F, 0.5F).build("awakenawakennomi:diffraction_green"));


    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerEntityRenderers(FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler((EntityType)DIFFRACTION_BLUE.get(), (new AbilityProjectileRenderer.Factory(new CubeModel())).setGlowing().setColor("#0000FF").setScale((double)0.25F, (double)0.25F, (double)50.0F));
        RenderingRegistry.registerEntityRenderingHandler((EntityType)DIFFRACTION_INDIGO.get(), (new AbilityProjectileRenderer.Factory(new CubeModel())).setGlowing().setColor("#4B0082").setScale((double)0.25F, (double)0.25F, (double)50.0F));
        RenderingRegistry.registerEntityRenderingHandler((EntityType)DIFFRACTION_VIOLET.get(), (new AbilityProjectileRenderer.Factory(new CubeModel())).setGlowing().setColor("#EE82EE").setScale((double)0.25F, (double)0.25F, (double)50.0F));
        RenderingRegistry.registerEntityRenderingHandler((EntityType)DIFFRACTION_RED.get(), (new AbilityProjectileRenderer.Factory(new CubeModel())).setGlowing().setColor("#FF0000").setScale((double)0.25F, (double)0.25F, (double)50.0F));
        RenderingRegistry.registerEntityRenderingHandler((EntityType)DIFFRACTION_ORANGE.get(), (new AbilityProjectileRenderer.Factory(new CubeModel())).setGlowing().setColor("#FFA500").setScale((double)0.25F, (double)0.25F, (double)50.0F));
        RenderingRegistry.registerEntityRenderingHandler((EntityType)DIFFRACTION_YELLOW.get(), (new AbilityProjectileRenderer.Factory(new CubeModel())).setGlowing().setColor("#FFFF00").setScale((double)0.25F, (double)0.25F, (double)50.0F));
        RenderingRegistry.registerEntityRenderingHandler((EntityType)DIFFRACTION_GREEN.get(), (new AbilityProjectileRenderer.Factory(new CubeModel())).setGlowing().setColor("#00FF00").setScale((double)0.25F, (double)0.25F, (double)50.0F));
    }
}
