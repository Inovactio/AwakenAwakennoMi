package com.inovactio.awakenawakennomi.abilities.awaawanomi;

import com.inovactio.awakenawakennomi.api.abilities.IAwakenable;
import com.inovactio.awakenawakennomi.api.abilities.ZoneAbility;
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
import xyz.pixelatedw.mineminenomi.api.abilities.IAbility;
import xyz.pixelatedw.mineminenomi.api.abilities.components.ChargeComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.components.CooldownComponent;
import xyz.pixelatedw.mineminenomi.api.helpers.AbilityHelper;
import xyz.pixelatedw.mineminenomi.data.entity.devilfruit.DevilFruitCapability;
import xyz.pixelatedw.mineminenomi.init.ModAbilities;
import xyz.pixelatedw.mineminenomi.init.ModEffects;

import java.util.ArrayList;
import java.util.List;

public class WorldWashAbility extends ZoneAbility implements IAwakenable {
    private static final ITextComponent[] DESCRIPTION = AbilityHelper.registerDescriptionText("awakenawakennomi", "world_wash",
            new Pair[]{ImmutablePair.of("Creates a large soap domain around the user. All entities inside are instantly \"washed,\" purifying the target and stripping them of their combat readiness.", (Object) null)});
    public static final AbilityCore<WorldWashAbility> INSTANCE;
    public static final int COUNTER_EFFECT_DURATION = 200; // 10 seconds

    public WorldWashAbility(AbilityCore<WorldWashAbility> core) {
        super(core);
        zoneColor = AwaHelper.AWA_COLOR;
    }

    protected static boolean canUnlock(LivingEntity user) {
        return DevilFruitCapability.get(user).hasAwakenedFruit()
                && DevilFruitCapability.get(user).hasDevilFruit(ModAbilities.AWA_AWA_NO_MI);
    }

    @Override
    protected void applyEffectToEntityInZone(LivingEntity owner, LivingEntity target) {
        if(target.isInWater()){
            if(target.hasEffect(ModEffects.WASHED.get())){
                target.removeEffect(ModEffects.WASHED.get());
            }
            return;
        }
        target.addEffect(new EffectInstance((Effect) ModEffects.WASHED.get(), (int)this.zoneTime + 10, 0, false, false, true));
    }

    @Override
    protected void onEntityLeavesZone(LivingEntity owner, LivingEntity target) {
        target.removeEffect(ModEffects.WASHED.get());
    }

    @Override
    protected List<Effect> getZoneEffectsToClearOnEnd() {
        List<Effect> effects = new ArrayList<>();
        effects.add(ModEffects.WASHED.get());
        return effects;
    }

    @Override
    protected void onContinuityEnd(LivingEntity entity, IAbility ability){
        super.onContinuityEnd(entity, ability);
        entity.addEffect(new EffectInstance((Effect) ModEffects.WASHED.get(), COUNTER_EFFECT_DURATION, 0, false, false, true));
    }


    @Override
    public boolean AwakenUnlock(LivingEntity user) {
        return canUnlock(user);
    }

    static {

        INSTANCE = new AbilityCore.Builder<>("World Wash", AbilityCategory.DEVIL_FRUITS, WorldWashAbility::new)
                .setUnlockCheck(WorldWashAbility::canUnlock)
                .addDescriptionLine(DESCRIPTION)
                .setIcon(new ResourceLocation("awakenawakennomi", "textures/abilities/awa/world_wash.png"))
                .addAdvancedDescriptionLine(new AbilityDescriptionLine.IDescriptionLine[]{AbilityDescriptionLine.NEW_LINE, CooldownComponent.getTooltip(COOLDOWN), ChargeComponent.getTooltip(MIN_CHARGE_TIME,CHARGE_TIME)})
                .build();
    }
}
