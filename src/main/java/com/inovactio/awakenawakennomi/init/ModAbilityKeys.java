package com.inovactio.awakenawakennomi.init;

import com.inovactio.awakenawakennomi.api.abilities.components.BlockTriggerComponent;
import net.minecraft.util.ResourceLocation;
import xyz.pixelatedw.mineminenomi.api.abilities.components.AbilityComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.components.AbilityComponentKey;

public class ModAbilityKeys {
    public static final AbilityComponentKey<BlockTriggerComponent> BLOCK_TRIGGER = register("block_trigger");

    private static <C extends AbilityComponent<?>> AbilityComponentKey<C> register(String name) {
        return AbilityComponentKey.key(new ResourceLocation("awakenawakennomi", name));
    }
}
