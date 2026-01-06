package com.inovactio.awakenawakennomi.init;

import com.inovactio.awakenawakennomi.util.AwakenRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import xyz.pixelatedw.mineminenomi.init.ModAttributes;
import xyz.pixelatedw.mineminenomi.init.ModEntities;
import xyz.pixelatedw.mineminenomi.wypi.WyRegistry;

public class AwakenAttributes {
    public static final RegistryObject<Attribute> SIZE =
            AwakenRegistry.registerAttribute(
                    "Size",
                    () -> (new RangedAttribute(
                            "attribute.name.generic.awakenawakennomi.size",
                            1.0D,
                            0.0D,
                            256.0D
                    )).setSyncable(true)
            );

    public static void register(IEventBus eventBus) {
        AwakenRegistry.ATTRIBUTES.register(eventBus);
    }

    @Mod.EventBusSubscriber(
            modid = "awakenawakennomi",
            bus = Mod.EventBusSubscriber.Bus.MOD
    )
    public static class Setup {
        @SubscribeEvent
        public static void onEntityConstruct(EntityAttributeModificationEvent event) {
            for(EntityType<? extends LivingEntity> type : event.getTypes()) {
                event.add(type, (Attribute)SIZE.get());
            }
        }
    }
}
