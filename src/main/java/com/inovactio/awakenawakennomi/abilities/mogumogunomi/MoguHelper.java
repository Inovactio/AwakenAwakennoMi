package com.inovactio.awakenawakennomi.abilities.mogumogunomi;

import com.inovactio.awakenawakennomi.init.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.util.text.TranslationTextComponent;
import xyz.pixelatedw.mineminenomi.abilities.sui.FreeSwimmingAbility;
import xyz.pixelatedw.mineminenomi.api.abilities.AbilityUseResult;
import xyz.pixelatedw.mineminenomi.api.abilities.IAbility;
import xyz.pixelatedw.mineminenomi.data.entity.ability.AbilityDataCapability;
import xyz.pixelatedw.mineminenomi.data.entity.ability.IAbilityData;
import xyz.pixelatedw.mineminenomi.init.ModI18n;

public class MoguHelper {
    public static boolean isDigging(LivingEntity entity)
    {
        return entity.hasEffect((Effect) ModEffects.GROUND_DIG.get()) ||entity.hasEffect((Effect) ModEffects.GROUND_DIG_BREAK.get());
    }

    public static AbilityUseResult isDigging(LivingEntity entity, IAbility ability) {
        IAbilityData props = AbilityDataCapability.get(entity);
        MoguDigAbility digging = (MoguDigAbility)props.getEquippedAbility(MoguDigAbility.INSTANCE);
        return digging != null && digging.isContinuous() && isDigging(entity) ? AbilityUseResult.success() : AbilityUseResult.fail(new TranslationTextComponent(ModI18n.ABILITY_MESSAGE_MISSING_DEPENDENCY_SINGLE, new Object[]{ability.getCore().getLocalizedName().getString(), MoguDigAbility.INSTANCE.getLocalizedName().getString()}));
    }
}
