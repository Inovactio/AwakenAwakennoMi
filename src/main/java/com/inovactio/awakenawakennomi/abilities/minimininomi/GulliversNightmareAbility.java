package com.inovactio.awakenawakennomi.abilities.minimininomi;

import com.inovactio.awakenawakennomi.abilities.subesubenomi.SmoothWorldAbility;
import com.inovactio.awakenawakennomi.abilities.subesubenomi.SubeHelper;
import com.inovactio.awakenawakennomi.api.abilities.IAwakenable;
import com.inovactio.awakenawakennomi.api.abilities.ZoneAbility;
import com.inovactio.awakenawakennomi.init.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import xyz.pixelatedw.mineminenomi.api.abilities.AbilityCategory;
import xyz.pixelatedw.mineminenomi.api.abilities.AbilityCore;
import xyz.pixelatedw.mineminenomi.api.abilities.AbilityDescriptionLine;
import xyz.pixelatedw.mineminenomi.api.abilities.components.ChargeComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.components.CooldownComponent;
import xyz.pixelatedw.mineminenomi.api.helpers.AbilityHelper;
import xyz.pixelatedw.mineminenomi.data.entity.devilfruit.DevilFruitCapability;
import xyz.pixelatedw.mineminenomi.init.ModAbilities;

import java.util.ArrayList;
import java.util.List;

public class GulliversNightmareAbility extends ZoneAbility implements IAwakenable {
    private static final ITextComponent[] DESCRIPTION = AbilityHelper.registerDescriptionText("awakenawakennomi", "gullivers_nightmare", new Pair[]{ImmutablePair.of("", (Object) null)});
    public static final AbilityCore<GulliversNightmareAbility> INSTANCE;

    public GulliversNightmareAbility(AbilityCore<GulliversNightmareAbility> core) {
        super(core);
        zoneColor = MiniHelper.MINI_COLOR;
        this.applyEffectOnUser = true;
    }

    protected static boolean canUnlock(LivingEntity user) {
        return DevilFruitCapability.get(user).hasAwakenedFruit()
                && DevilFruitCapability.get(user).hasDevilFruit(ModAbilities.MINI_MINI_NO_MI);
    }

    @Override
    protected void applyEffectToEntityInZone(LivingEntity owner, LivingEntity target) {
        if(!target.hasEffect(ModEffects.GULLIVERS_NIGHTMARE_SHRINK.get())){
            target.addEffect(new EffectInstance((Effect) ModEffects.GULLIVERS_NIGHTMARE_SHRINK.get(), Integer.MAX_VALUE, 0, false, true, true));
        }
    }

    @Override
    protected void onEntityLeavesZone(LivingEntity owner, LivingEntity target) {
        target.removeEffect(ModEffects.GULLIVERS_NIGHTMARE_SHRINK.get());
    }

    @Override
    protected List<Effect> getZoneEffectsToClearOnEnd() {
        List<Effect> effects = new ArrayList<>();
        effects.add(ModEffects.GULLIVERS_NIGHTMARE_SHRINK.get());
        return effects;
    }


    @Override
    public boolean AwakenUnlock(LivingEntity user) {
        return canUnlock(user);
    }

    static {
        INSTANCE = new AbilityCore.Builder<>("Gullivers Nightmare", AbilityCategory.DEVIL_FRUITS, GulliversNightmareAbility::new)
                .setUnlockCheck(GulliversNightmareAbility::canUnlock)
                .addDescriptionLine(DESCRIPTION)
                .setIcon(new ResourceLocation("awakenawakennomi", "textures/abilities/mini/gullivers_nightmare.png"))
                .addAdvancedDescriptionLine(new AbilityDescriptionLine.IDescriptionLine[]{AbilityDescriptionLine.NEW_LINE, CooldownComponent.getTooltip(COOLDOWN), ChargeComponent.getTooltip(MIN_CHARGE_TIME,CHARGE_TIME)})
                .build();
    }
}
