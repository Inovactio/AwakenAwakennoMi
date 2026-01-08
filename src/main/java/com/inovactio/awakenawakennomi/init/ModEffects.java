package com.inovactio.awakenawakennomi.init;

import com.inovactio.awakenawakennomi.effects.GroundDigEffect;
import com.inovactio.awakenawakennomi.effects.GroundDigEffectBreak;
import com.inovactio.awakenawakennomi.effects.GroundSwimEffect;
import com.inovactio.awakenawakennomi.effects.size.GulliversShrinkEffect;
import com.inovactio.awakenawakennomi.effects.sliding.SmoothWorldSlidingEffect;
import com.inovactio.awakenawakennomi.util.AwakenRegistry;
import net.minecraft.potion.Effect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;

public class ModEffects {
    public static final RegistryObject<Effect> GROUND_SWIM = AwakenRegistry.registerEffect("Ground Swim", GroundSwimEffect::new);
    public static final RegistryObject<Effect> GROUND_DIG = AwakenRegistry.registerEffect("Ground Dig", GroundDigEffect::new);
    public static final RegistryObject<Effect> GROUND_DIG_BREAK = AwakenRegistry.registerEffect("Ground Dig Break", GroundDigEffectBreak::new);
    public static final RegistryObject<Effect> SMOOTH_WORLD_SLIDING = AwakenRegistry.registerEffect("Smooth World Sliding", SmoothWorldSlidingEffect::new);
    public static final RegistryObject<Effect> GULLIVERS_SHRINK = AwakenRegistry.registerEffect("Gullivers Shrink", GulliversShrinkEffect::new);

    public static void register(IEventBus eventBus) {
        AwakenRegistry.EFFECTS.register(eventBus);
    }
}
