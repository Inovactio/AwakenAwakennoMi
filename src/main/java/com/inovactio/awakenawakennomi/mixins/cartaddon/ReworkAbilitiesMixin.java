package com.inovactio.awakenawakennomi.mixins.cartaddon;

import com.inovactio.awakenawakennomi.abilities.dekadekanomi.TitanAbility;
import com.inovactio.awakenawakennomi.abilities.dekadekanomi.TitanSmashAbility;
import com.inovactio.awakenawakennomi.abilities.dekadekanomi.TitanTrampleAbility;
import com.inovactio.awakenawakennomi.util.FruitInjectionHelper;
import net.MrMagicalCart.cartaddon.init.ReworkAbilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.pixelatedw.mineminenomi.items.AkumaNoMiItem;

@Mixin({ReworkAbilities.class})
public class ReworkAbilitiesMixin {
    @Inject(
            method = {"registerFruit"},
            at = {@At("HEAD")},
            remap = false,
            cancellable = true
    )
    private static <T extends AkumaNoMiItem> void addAbilities(T fruit, CallbackInfoReturnable<T> info) {
        switch (fruit.getDevilFruitName()) {
            case "Deka Deka no Mi":
                FruitInjectionHelper.appendAbilities(fruit, TitanAbility.INSTANCE, TitanTrampleAbility.INSTANCE, TitanSmashAbility.INSTANCE);
                break;

            default:
                // Aucun ajout particulier pour les autres fruits
                break;
        }
    }
}
