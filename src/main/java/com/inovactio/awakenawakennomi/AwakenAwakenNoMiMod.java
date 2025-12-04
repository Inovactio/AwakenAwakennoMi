package com.inovactio.awakenawakennomi;

import com.inovactio.awakenawakennomi.init.ModAnimations;
import com.inovactio.awakenawakennomi.network.ModNetwork;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


// The value here should match an entry in the META-INF/mods.toml file
@Mod("awakenawakennomi")
public class AwakenAwakenNoMiMod
{
    public static String MODID = "awakenawakennomi";
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public AwakenAwakenNoMiMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        //ModAbilities.register(modEventBus);
        ModAnimations.clientInit();
        // Register ourselves for server and other game events we are interested in
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        ModNetwork.register();
    }
}
