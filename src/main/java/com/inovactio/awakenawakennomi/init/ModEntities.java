package com.inovactio.awakenawakennomi.init;

import com.inovactio.awakenawakennomi.util.AwakenRegistry;
import net.minecraftforge.eventbus.api.IEventBus;

public class ModEntities {
    public static void register(IEventBus eventBus) {
        AwakenRegistry.ENTITY_TYPES.register(eventBus);
    }
}
