package com.inovactio.awakenawakennomi.mixins.mineminenomi;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.pixelatedw.mineminenomi.abilities.gomu.GomuGomuNoGigantAbility;
import xyz.pixelatedw.mineminenomi.data.entity.devilfruit.DevilFruitCapability;
import xyz.pixelatedw.mineminenomi.init.ModAbilities;

@Mixin(GomuGomuNoGigantAbility.class)
public class GomuGomuNoGiantAbilityMixin {

    @Inject(
            method = {"canUnlock"},
            at = {@At("HEAD")},
            remap = false,
            cancellable = true
    )
    private static void onCanUnlock(LivingEntity user, CallbackInfoReturnable<Boolean> ci) {
        ci.setReturnValue(DevilFruitCapability.get(user).hasAwakenedFruit()
                && DevilFruitCapability.get(user).hasDevilFruit(ModAbilities.GOMU_GOMU_NO_MI));
        ci.cancel();
    }
}
