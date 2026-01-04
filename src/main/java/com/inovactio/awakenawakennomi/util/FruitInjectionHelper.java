package com.inovactio.awakenawakennomi.util;

import xyz.pixelatedw.mineminenomi.api.abilities.AbilityCore;
import xyz.pixelatedw.mineminenomi.items.AkumaNoMiItem;

import java.util.ArrayList;
import java.util.Arrays;

public class FruitInjectionHelper {
    // Méthode utilitaire : ajoute des AbilityCore à un AkumaNoMiItem en évitant les doublons et en gérant les null
    public static void appendAbilities(AkumaNoMiItem fruit, AbilityCore<?>... abilities) {
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

    public static void removeAbilities(AkumaNoMiItem fruit, AbilityCore<?>... abilities) {
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
