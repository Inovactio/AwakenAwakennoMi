package com.inovactio.awakenawakennomi.abilities.sukesukenomi;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import org.apache.commons.lang3.tuple.ImmutablePair;
import xyz.pixelatedw.mineminenomi.api.abilities.Ability;
import xyz.pixelatedw.mineminenomi.api.abilities.AbilityCategory;
import xyz.pixelatedw.mineminenomi.api.abilities.AbilityCore;
import xyz.pixelatedw.mineminenomi.api.abilities.AbilityDescriptionLine;
import com.inovactio.awakenawakennomi.api.abilities.IAwakenable;
import xyz.pixelatedw.mineminenomi.api.helpers.AbilityHelper;

/**
 * Minimal Awaken ability placeholder for Suke Suke no Mi (diffraction).
 * No runtime logic here — this class only provides the basic ability definition
 * so you can implement the behavior elsewhere or later.
 */
public class AwakenSukeDiffractionAbility extends Ability implements IAwakenable {

    private static final ITextComponent[] DESCRIPTION = AbilityHelper.registerDescriptionText("awakenawakennomi", "awaken_suke_diffraction",
            new ImmutablePair[]{ImmutablePair.of("Awaken Diffraction (placeholder)", (Object)null)});

    public static final AbilityCore<AwakenSukeDiffractionAbility> INSTANCE;

    public AwakenSukeDiffractionAbility(AbilityCore<AwakenSukeDiffractionAbility> core) {
        super(core);
        // no runtime components or logic here — keep class minimal
    }

    protected static boolean canUnlock(LivingEntity user) {
        // stub: return false by default (no automatic unlock)
        return false;
    }

    @Override
    public boolean AwakenUnlock(LivingEntity user) {
        return canUnlock(user);
    }

    static {
        INSTANCE = new AbilityCore.Builder<>("Awaken Suke Diffraction", AbilityCategory.DEVIL_FRUITS, AwakenSukeDiffractionAbility::new)
                .addDescriptionLine(DESCRIPTION)
                .addAdvancedDescriptionLine(new AbilityDescriptionLine.IDescriptionLine[]{AbilityDescriptionLine.NEW_LINE})
                .setIcon(new ResourceLocation("awakenawakennomi", "textures/abilities/awaken_suke_diffraction.png"))
                .build();
    }
}
