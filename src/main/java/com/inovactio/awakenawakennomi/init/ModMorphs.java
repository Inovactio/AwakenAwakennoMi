package com.inovactio.awakenawakennomi.init;

import com.inovactio.awakenawakennomi.entities.morph.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import xyz.pixelatedw.mineminenomi.api.morph.MorphInfo;
import xyz.pixelatedw.mineminenomi.wypi.WyRegistry;

public class ModMorphs {
    public static final RegistryObject<MorphInfo> AWAKEN_DEKA = WyRegistry.registerMorph("awaken_deka", AwakenDekaMorphInfo::new);
    public static final RegistryObject<MorphInfo> AWAKEN_HUMAN = WyRegistry.registerMorph("awaken_human", AwakenHumanMorphInfo::new);
    public static final RegistryObject<MorphInfo> AWAKEN_KAME = WyRegistry.registerMorph("awaken_kame", AwakenKameWalkMorphInfo::new);
    public static final RegistryObject<MorphInfo> AWAKEN_GIRAFFE = WyRegistry.registerMorph("awaken_ushi", AwakenGiraffeHeavyMorphInfo::new);
    public static final RegistryObject<MorphInfo> AWAKEN_MOGU = WyRegistry.registerMorph("awaken_mogu", AwakenMoguHeavyMorphInfo::new);

    public static void register(IEventBus eventBus) {
        WyRegistry.MORPHS.register(eventBus);
    }
}
