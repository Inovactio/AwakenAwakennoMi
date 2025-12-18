package com.inovactio.awakenawakennomi.mixins;

import com.inovactio.awakenawakennomi.abilities.bomubomunomi.AwakenBlastJump;
import com.inovactio.awakenawakennomi.abilities.bomubomunomi.AwakenPiercingBlast;
import com.inovactio.awakenawakennomi.abilities.dekadekanomi.TitanAbility;
import com.inovactio.awakenawakennomi.abilities.dekadekanomi.TitanTrampleAbility;
import com.inovactio.awakenawakennomi.abilities.dekadekanomi.TitanSmashAbility;
import com.inovactio.awakenawakennomi.abilities.sukesukenomi.AwakenSukeDiffractionAbility;
import com.inovactio.awakenawakennomi.abilities.sukesukenomi.AwakenSukeInvisibleZoneAbility;
import com.inovactio.awakenawakennomi.abilities.sukesukenomi.AwakenSukePunchAbility;
import com.inovactio.awakenawakennomi.abilities.bomubomunomi.AwakenBomuAirburstAbility;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.pixelatedw.mineminenomi.api.abilities.AbilityCore;
import xyz.pixelatedw.mineminenomi.init.ModAbilities;
import xyz.pixelatedw.mineminenomi.items.AkumaNoMiItem;

import java.util.Arrays;
import java.util.ArrayList;

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

        switch (fruit.getDevilFruitName()) {
            case "Suke Suke no Mi":
                appendAbilities(fruit, AwakenSukePunchAbility.INSTANCE, AwakenSukeInvisibleZoneAbility.INSTANCE, AwakenSukeDiffractionAbility.INSTANCE);
                break;

            case "Bomu Bomu no Mi":
                appendAbilities(fruit, AwakenBomuAirburstAbility.INSTANCE, AwakenBlastJump.INSTANCE, AwakenPiercingBlast.INSTANCE);
                break;

            case "Deka Deka no Mi":
                appendAbilities(fruit, TitanAbility.INSTANCE, TitanTrampleAbility.INSTANCE, TitanSmashAbility.INSTANCE);
                break;

            default:
                // Aucun ajout particulier pour les autres fruits
                break;
        }
    }

    // Méthode utilitaire : ajoute des AbilityCore à un AkumaNoMiItem en évitant les doublons et en gérant les null
    private static void appendAbilities(AkumaNoMiItem fruit, AbilityCore<?>... abilities) {
        if (fruit == null || abilities == null || abilities.length == 0) return;

        AbilityCore<?>[] original = fruit.getAbilities();
        if (original == null) original = new AbilityCore<?>[0];

        ArrayList<AbilityCore<?>> toAdd = new ArrayList<>();
        for (AbilityCore<?> a : abilities) {
            if (a == null) continue;
            boolean present = false;
            for (AbilityCore<?> o : original) {
                if (o == a) { present = true; break; }
            }
            if (!present) toAdd.add(a);
        }

        if (toAdd.isEmpty()) return;

        AbilityCore<?>[] extended = Arrays.copyOf(original, original.length + toAdd.size());
        for (int i = 0; i < toAdd.size(); i++) {
            extended[original.length + i] = toAdd.get(i);
        }

        fruit.setAbilities(extended);

        StringBuilder sb = new StringBuilder();
        sb.append("[AwakenAwakenNoMi] Abilities ajoutées pour ").append(fruit.getDevilFruitName()).append(" : ");
        for (int i = 0; i < toAdd.size(); i++) {
            AbilityCore<?> a = toAdd.get(i);
            String name = a.getClass().getSimpleName();
            sb.append(name);
            if (i < toAdd.size() - 1) sb.append(", ");
        }
        System.out.println(sb.toString());
    }

    private static void removeAbilities(AkumaNoMiItem fruit, AbilityCore<?>... abilities) {
        if (fruit == null || abilities == null || abilities.length == 0) return;

        AbilityCore<?>[] original = fruit.getAbilities();
        if (original == null || original.length == 0) return;

        ArrayList<AbilityCore<?>> toRemove = new ArrayList<>();
        for (AbilityCore<?> a : abilities) {
            if (a != null) toRemove.add(a);
        }
        if (toRemove.isEmpty()) return;

        ArrayList<AbilityCore<?>> updatedList = new ArrayList<>();
        for (AbilityCore<?> o : original) {
            if (!toRemove.contains(o)) {
                updatedList.add(o);
            }
        }

        AbilityCore<?>[] updatedArray = updatedList.toArray(new AbilityCore<?>[0]);
        fruit.setAbilities(updatedArray);

        StringBuilder sb = new StringBuilder();
        sb.append("[AwakenAwakenNoMi] Abilities supprimées pour ").append(fruit.getDevilFruitName()).append(" : ");
        for (int i = 0; i < toRemove.size(); i++) {
            AbilityCore<?> a = toRemove.get(i);
            String name = a.getClass().getSimpleName();
            sb.append(name);
            if (i < toRemove.size() - 1) sb.append(", ");
        }
        System.out.println(sb.toString());
    }
}
