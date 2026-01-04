package com.inovactio.awakenawakennomi.client;


import com.inovactio.awakenawakennomi.api.animations.IHandAnimation;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xyz.pixelatedw.mineminenomi.api.abilities.Ability;
import xyz.pixelatedw.mineminenomi.api.abilities.IAbility;
import xyz.pixelatedw.mineminenomi.data.entity.ability.AbilityDataCapability;
import xyz.pixelatedw.mineminenomi.data.entity.ability.IAbilityData;

import java.util.Set;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = "awakenawakennomi")
public final class ClientHandRenderHandler {

    private ClientHandRenderHandler() {}

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        Minecraft mc = Minecraft.getInstance();
        PlayerEntity player = mc.player;
        if (player == null) return;
        if (checkAnimationHandActive(player)) {
            event.setCanceled(true);
        }
    }

    private static boolean checkAnimationHandActive(PlayerEntity player) {

        IAbilityData abilityData = AbilityDataCapability.get(player);
        if (abilityData == null) return false;
        Set<IAbility> abilities =
                abilityData.getEquippedAbilities(a -> {
                    return a instanceof IHandAnimation && ((Ability) a).isContinuous() && ((IHandAnimation) a).DisableHandOfPlayer();
                });
        return !abilities.isEmpty();
    }
}
