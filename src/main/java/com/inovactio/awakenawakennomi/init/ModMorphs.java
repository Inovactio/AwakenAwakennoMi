package com.inovactio.awakenawakennomi.init;

import com.inovactio.awakenawakennomi.entities.morph.AwakenDekaMorphInfo;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import xyz.pixelatedw.mineminenomi.api.morph.MorphInfo;
import xyz.pixelatedw.mineminenomi.entities.zoan.MegaMorphInfo;
import xyz.pixelatedw.mineminenomi.wypi.WyRegistry;

public class ModMorphs {
    public static final RegistryObject<MorphInfo> AWAKEN_DEKA = WyRegistry.registerMorph("awaken_deka", AwakenDekaMorphInfo::new);

    public static void register(IEventBus eventBus) {
        WyRegistry.MORPHS.register(eventBus);
    }
}
