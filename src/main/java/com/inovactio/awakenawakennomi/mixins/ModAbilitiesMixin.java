package com.inovactio.awakenawakennomi.mixins;

import com.inovactio.awakenawakennomi.abilities.sukesukenomi.AwakenSukePunchAbility;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.pixelatedw.mineminenomi.api.abilities.AbilityCore;
import xyz.pixelatedw.mineminenomi.init.ModAbilities;
import xyz.pixelatedw.mineminenomi.items.AkumaNoMiItem;

import java.util.Arrays;

@Mixin({ModAbilities.class})
public class ModAbilitiesMixin {


    @Inject(
            method = {"registerFruit"},
            at = {@At("HEAD")},
            remap = false,
            cancellable = true
    )
    private static <T extends AkumaNoMiItem> void addAbilities(T fruit, CallbackInfoReturnable<T> info) {
        System.out.println("[AwakenAwakenNoMi] Injection appelée pour le fruit : " + fruit.getDevilFruitName());
        if (fruit.getDevilFruitName().equals("Suke Suke no Mi"))
        {
            // Récupérer les abilities existantes
            AbilityCore<?>[] original = fruit.getAbilities();

            // Étendre le tableau
            AbilityCore<?>[] extended = Arrays.copyOf(original, original.length + 1);
            extended[original.length] = AwakenSukePunchAbility.INSTANCE;

            // Réinjecter
            fruit.setAbilities(extended);
            System.out.println("[AwakenAwakenNoMi] Injection de l'ability");
        }
    }
}
