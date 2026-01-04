package com.inovactio.awakenawakennomi.mixins.mineminenomi;

import com.inovactio.awakenawakennomi.abilities.bomubomunomi.BlastJumpAbility;
import com.inovactio.awakenawakennomi.abilities.bomubomunomi.PiercingBlastAbility;
import com.inovactio.awakenawakennomi.abilities.dekadekanomi.TitanAbility;
import com.inovactio.awakenawakennomi.abilities.dekadekanomi.TitanTrampleAbility;
import com.inovactio.awakenawakennomi.abilities.dekadekanomi.TitanSmashAbility;
import com.inovactio.awakenawakennomi.abilities.hitohitonomi.AwakenHumanFormAbility;
import com.inovactio.awakenawakennomi.abilities.kamekamenomi.AwakenKameWalkPointAbility;
import com.inovactio.awakenawakennomi.abilities.kamekamenomi.SpinAbility;
import com.inovactio.awakenawakennomi.abilities.mogumogunomi.AwakenMoguHeavyPointAbility;
import com.inovactio.awakenawakennomi.abilities.mogumogunomi.MoguDigAbility;
import com.inovactio.awakenawakennomi.abilities.mogumogunomi.SubterraneanDashAbility;
import com.inovactio.awakenawakennomi.abilities.subesubenomi.SlickDisarmAbility;
import com.inovactio.awakenawakennomi.abilities.subesubenomi.SmoothWorldAbility;
import com.inovactio.awakenawakennomi.abilities.sukesukenomi.DiffractionAbility;
import com.inovactio.awakenawakennomi.abilities.sukesukenomi.InvisibleZoneAbility;
import com.inovactio.awakenawakennomi.abilities.sukesukenomi.InvisibleTouchAbility;
import com.inovactio.awakenawakennomi.abilities.bomubomunomi.BomuAirburstAbility;
import com.inovactio.awakenawakennomi.abilities.ushiushinomi.giraffe.AwakenGiraffeHeavyPointAbility;
import com.inovactio.awakenawakennomi.abilities.ushiushinomi.giraffe.KirimanjaroAbility;
import com.inovactio.awakenawakennomi.util.FruitInjectionHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.pixelatedw.mineminenomi.abilities.ushigiraffe.BiganAbility;
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
                FruitInjectionHelper.appendAbilities(fruit, InvisibleTouchAbility.INSTANCE, InvisibleZoneAbility.INSTANCE, DiffractionAbility.INSTANCE);
                break;

            case "Bomu Bomu no Mi":
                FruitInjectionHelper.appendAbilities(fruit, BomuAirburstAbility.INSTANCE, BlastJumpAbility.INSTANCE, PiercingBlastAbility.INSTANCE);
                break;

            case "Deka Deka no Mi":
                FruitInjectionHelper.appendAbilities(fruit, TitanAbility.INSTANCE, TitanTrampleAbility.INSTANCE, TitanSmashAbility.INSTANCE);
                break;

            case "Hito Hito no Mi":
                FruitInjectionHelper.appendAbilities(fruit, AwakenHumanFormAbility.INSTANCE);
                break;

            case "Kame Kame no Mi":
                FruitInjectionHelper.appendAbilities(fruit, AwakenKameWalkPointAbility.INSTANCE, SpinAbility.INSTANCE);
                break;

            case "Ushi Ushi no Mi, Model: Giraffe":
                FruitInjectionHelper.removeAbilities(fruit, BiganAbility.INSTANCE);
                FruitInjectionHelper.appendAbilities(fruit, AwakenGiraffeHeavyPointAbility.INSTANCE, KirimanjaroAbility.INSTANCE);
                break;

            case "Mogu Mogu no Mi":
                FruitInjectionHelper.appendAbilities(fruit, AwakenMoguHeavyPointAbility.INSTANCE, MoguDigAbility.INSTANCE, SubterraneanDashAbility.INSTANCE);
                break;

            case "Sube Sube No Mi":
                FruitInjectionHelper.appendAbilities(fruit, SmoothWorldAbility.INSTANCE, SlickDisarmAbility.INSTANCE);
                break;
            default:
                // Aucun ajout particulier pour les autres fruits
                break;
        }
    }
}
