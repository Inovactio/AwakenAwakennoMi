package com.inovactio.awakenawakennomi.events.abilities;

import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.pixelatedw.mineminenomi.api.events.stats.DorikiEvent;

import static com.inovactio.awakenawakennomi.util.UnlockAwakenHelper.checkForAwakenUnlocks;

@Mod.EventBusSubscriber(
        modid = "awakenawakennomi"
)
public class AbilityProgressionEvents {
    @SubscribeEvent
    public static void onDorikiGained(DorikiEvent.Post event) {
        checkForAwakenUnlocks(event.getPlayer());
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        checkForAwakenUnlocks(event.getPlayer());
    }
}
