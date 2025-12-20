package com.inovactio.awakenawakennomi.mixins;

import com.inovactio.awakenawakennomi.abilities.bomubomunomi.AwakenBlastJump;
import com.inovactio.awakenawakennomi.abilities.bomubomunomi.AwakenPiercingBlast;
import com.inovactio.awakenawakennomi.abilities.dekadekanomi.TitanAbility;
import com.inovactio.awakenawakennomi.abilities.dekadekanomi.TitanTrampleAbility;
import com.inovactio.awakenawakennomi.abilities.dekadekanomi.TitanSmashAbility;
import com.inovactio.awakenawakennomi.abilities.sukesukenomi.DiffractionAbility;
import com.inovactio.awakenawakennomi.abilities.sukesukenomi.AwakenSukeInvisibleZoneAbility;
import com.inovactio.awakenawakennomi.abilities.sukesukenomi.AwakenSukePunchAbility;
import com.inovactio.awakenawakennomi.abilities.bomubomunomi.AwakenBomuAirburstAbility;
import com.inovactio.awakenawakennomi.util.FruitInjectionHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.pixelatedw.mineminenomi.init.ModAbilities;
import xyz.pixelatedw.mineminenomi.items.AkumaNoMiItem;

@Mixin({ModAbilities.class})
public class ModAbilitiesMixin {


    @Inject(
            method = {"registerFruit"},
            at = {@At("HEAD")},
            remap = false,
            cancellable = true
    )
    private static <T extends AkumaNoMiItem> void addAbilities(T fruit, CallbackInfoReturnable<T> info) {
        switch (fruit.getDevilFruitName()) {
            case "Suke Suke no Mi":
                FruitInjectionHelper.appendAbilities(fruit, AwakenSukePunchAbility.INSTANCE, AwakenSukeInvisibleZoneAbility.INSTANCE, DiffractionAbility.INSTANCE);
                break;

            case "Bomu Bomu no Mi":
                FruitInjectionHelper.appendAbilities(fruit, AwakenBomuAirburstAbility.INSTANCE, AwakenBlastJump.INSTANCE, AwakenPiercingBlast.INSTANCE);
                break;

            case "Deka Deka no Mi":
                FruitInjectionHelper.appendAbilities(fruit, TitanAbility.INSTANCE, TitanTrampleAbility.INSTANCE, TitanSmashAbility.INSTANCE);
                break;

            default:
                // Aucun ajout particulier pour les autres fruits
                break;
        }
    }


}
