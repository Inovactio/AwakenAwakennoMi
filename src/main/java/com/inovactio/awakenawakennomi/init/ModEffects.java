package com.inovactio.awakenawakennomi.init;

import com.inovactio.awakenawakennomi.effects.GroundDigEffect;
import com.inovactio.awakenawakennomi.effects.GroundDigEffectBreak;
import com.inovactio.awakenawakennomi.effects.GroundSwimEffect;
import com.inovactio.awakenawakennomi.effects.sliding.SmoothWorldSlidingEffect;
import net.minecraft.potion.Effect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import xyz.pixelatedw.mineminenomi.effects.VanishEffect;
import xyz.pixelatedw.mineminenomi.wypi.WyRegistry;

public class ModEffects {
    public static final RegistryObject<Effect> GROUND_SWIM = WyRegistry.registerEffect("Ground Swim", GroundSwimEffect::new);
    public static final RegistryObject<Effect> GROUND_DIG = WyRegistry.registerEffect("Ground Dig", GroundDigEffect::new);
    public static final RegistryObject<Effect> GROUND_DIG_BREAK = WyRegistry.registerEffect("Ground Dig Break", GroundDigEffectBreak::new);
    public static final RegistryObject<Effect> SMOOTH_WORLD_SLIDING = WyRegistry.registerEffect("Smooth World Sliding", SmoothWorldSlidingEffect::new);

    public static void register(IEventBus eventBus) {
        WyRegistry.EFFECTS.register(eventBus);
    }
}
