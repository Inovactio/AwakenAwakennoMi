package com.inovactio.awakenawakennomi.events.abilities;

import com.inovactio.awakenawakennomi.AwakenAwakenNoMiMod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import com.inovactio.awakenawakennomi.api.abilities.components.BlockTriggerComponent;
import xyz.pixelatedw.mineminenomi.data.entity.ability.AbilityDataCapability;
import xyz.pixelatedw.mineminenomi.api.abilities.IAbility;
import xyz.pixelatedw.mineminenomi.data.entity.ability.IAbilityData;
import com.inovactio.awakenawakennomi.init.ModAbilityKeys;

@Mod.EventBusSubscriber(modid = "awakenawakennomi")
public class AbilitiesEvents {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if(event.getHand() != Hand.MAIN_HAND) return;
        World world = event.getWorld();
        if (world.isClientSide) return; // ignore côté client

        PlayerEntity player = event.getPlayer();
        BlockPos pos = event.getPos();

        IAbilityData props = AbilityDataCapability.get(player);
        if (props == null) return;

        for (IAbility ability : props.getEquippedAbilities()) {
            ability.getComponent(ModAbilityKeys.BLOCK_TRIGGER).ifPresent(comp -> {
                BlockTriggerComponent blockComp = (BlockTriggerComponent) comp;
                BlockTriggerComponent.HitResult result = blockComp.tryHit(player, pos, world);
                if (result == BlockTriggerComponent.HitResult.HIT) {
                    boolean ok = blockComp.onHit(player, pos, world);
                    if (!ok) {
                        event.setCanceled(true);
                    }
                }
            });
        }
    }
}