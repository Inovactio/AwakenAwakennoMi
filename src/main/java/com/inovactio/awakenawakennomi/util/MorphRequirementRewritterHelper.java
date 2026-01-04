package com.inovactio.awakenawakennomi.util;

import xyz.pixelatedw.mineminenomi.api.abilities.Ability;
import xyz.pixelatedw.mineminenomi.api.abilities.components.AbilityComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.components.RequireMorphComponent;
import xyz.pixelatedw.mineminenomi.api.morph.MorphInfo;

public class MorphRequirementRewritterHelper {
    public static boolean replaceRequireMorphIfInstance(Object instance,
                                                        AbilityComponent[] components,
                                                        Class<?> targetAbilityClass,
                                                        MorphInfo mainMorph,
                                                        MorphInfo... fallbacks) {
        try {
            if (components == null || components.length == 0) return false;
            if (mainMorph == null) return false;
            if (!targetAbilityClass.isInstance(instance)) return false;

            Ability self = (Ability) instance;
            RequireMorphComponent newReq = new RequireMorphComponent(self, mainMorph, fallbacks);
            boolean replacedAny = false;

            for (int i = 0; i < components.length; i++) {
                if (components[i] instanceof RequireMorphComponent) {
                    components[i] = newReq;
                    replacedAny = true;
                }
            }

            return replacedAny;
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
    }
}
