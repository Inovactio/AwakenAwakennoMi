package com.inovactio.awakenawakennomi.init;

import com.inovactio.awakenawakennomi.effects.GroundDigEffect;
import com.inovactio.awakenawakennomi.effects.GroundSwimEffect;
import net.minecraft.potion.Effect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import xyz.pixelatedw.mineminenomi.effects.VanishEffect;
import xyz.pixelatedw.mineminenomi.wypi.WyRegistry;

public class ModEffects {
    public static final RegistryObject<Effect> GROUND_SWIM = WyRegistry.registerEffect("Ground Swim", GroundSwimEffect::new);
    public static final RegistryObject<Effect> GROUND_DIG = WyRegistry.registerEffect("Ground Dig", GroundDigEffect::new);

    public static void register(IEventBus eventBus) {
        WyRegistry.EFFECTS.register(eventBus);
    }
}
