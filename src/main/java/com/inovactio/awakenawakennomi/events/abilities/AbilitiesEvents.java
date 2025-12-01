package com.inovactio.awakenawakennomi.events.abilities;

import com.inovactio.awakenawakennomi.api.abilities.BlockUseAbility;
import com.inovactio.awakenawakennomi.api.abilities.components.BlockTriggerComponent;
import com.inovactio.awakenawakennomi.init.ModAbilityKeys;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import xyz.pixelatedw.mineminenomi.api.abilities.IAbility;
import xyz.pixelatedw.mineminenomi.data.entity.ability.AbilityDataCapability;
import xyz.pixelatedw.mineminenomi.data.entity.ability.IAbilityData;

@Mod.EventBusSubscriber(modid = "awakenawakennomi")
public class AbilitiesEvents {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() != Hand.MAIN_HAND) return;

        World world = event.getWorld();
        if (world.isClientSide) return;

        PlayerEntity player = event.getPlayer();
        BlockPos pos = event.getPos();

        IAbilityData props = AbilityDataCapability.get(player);
        if (props == null) return;

        for (IAbility ability : props.getEquippedAbilities()) {
            if (!(ability instanceof BlockUseAbility)) continue;

            BlockUseAbility blockAbility = (BlockUseAbility) ability;
            if (!blockAbility.isActive()) continue;

            ItemStack heldItem = player.getMainHandItem();
            if (!blockAbility.GetAllowBlockActivation() && heldItem.isEmpty()) {
                event.setCanceled(true);
            }

            ability.getComponent(ModAbilityKeys.BLOCK_TRIGGER).ifPresent(comp -> {
                BlockTriggerComponent.HitResult result = comp.tryHit(player, pos, world);
                if (result == BlockTriggerComponent.HitResult.HIT) {
                    comp.onHit(player, pos, world);
                }
            });
        }
    }
}
