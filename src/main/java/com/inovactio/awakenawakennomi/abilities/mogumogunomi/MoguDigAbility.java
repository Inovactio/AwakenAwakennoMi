package com.inovactio.awakenawakennomi.abilities.mogumogunomi;

import com.inovactio.awakenawakennomi.api.abilities.IAwakenable;
import com.inovactio.awakenawakennomi.init.ModEffects;
import com.inovactio.awakenawakennomi.init.ModMorphs;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import xyz.pixelatedw.mineminenomi.api.abilities.*;
import xyz.pixelatedw.mineminenomi.api.abilities.components.AbilityComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.components.ContinuousComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.components.CooldownComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.components.RequireMorphComponent;
import xyz.pixelatedw.mineminenomi.api.helpers.AbilityHelper;
import xyz.pixelatedw.mineminenomi.api.morph.MorphInfo;
import xyz.pixelatedw.mineminenomi.data.entity.devilfruit.DevilFruitCapability;
import xyz.pixelatedw.mineminenomi.init.ModAbilities;

public class MoguDigAbility extends Ability implements IAwakenable {
    private static final ITextComponent[] DESCRIPTION = AbilityHelper.registerDescriptionText("awakenawakennomi", "mogu_dig", new Pair[]{ImmutablePair.of("Lets the user swim trough blocks. Swimming is activated by running into a ground block.", (Object)null)});
    private static final int COOLDOWN = 20;
    public static final AbilityCore<MoguDigAbility> INSTANCE;
    private final ContinuousComponent continuousComponent = (new ContinuousComponent(this, true)).addTickEvent(this::tickContinuityEvent).addEndEvent(this::endContinuityEvent);
    protected RequireMorphComponent requireMorphComponent = new RequireMorphComponent(this, (MorphInfo) ModMorphs.AWAKEN_MOGU.get(), xyz.pixelatedw.mineminenomi.init.ModMorphs.MOGU_HEAVY.get());

    public MoguDigAbility(AbilityCore<MoguDigAbility> core) {
        super(core);
        this.isNew = true;
        this.addComponents(new AbilityComponent[]{this.continuousComponent, requireMorphComponent});
        this.addCanUseCheck(AbilityHelper::canUseMomentumAbilities);
        this.addUseEvent(this::useEvent);
    }

    private void useEvent(LivingEntity entity, IAbility ability) {
        this.continuousComponent.triggerContinuity(entity);
    }

    private void tickContinuityEvent(LivingEntity entity, IAbility ability) {
        if (!MoguHelper.isDigging(entity)) {
            entity.addEffect(new EffectInstance((Effect) ModEffects.GROUND_DIG.get(), Integer.MAX_VALUE, 0, false, false, true));
        }
    }

    private void endContinuityEvent(LivingEntity entity, IAbility ability) {
        entity.removeEffect((Effect) ModEffects.GROUND_DIG.get());
        this.cooldownComponent.startCooldown(entity, 20.0F);
    }

    protected static boolean canUnlock(LivingEntity user) {
        return DevilFruitCapability.get(user).hasAwakenedFruit()
                && DevilFruitCapability.get(user).hasDevilFruit(ModAbilities.MOGU_MOGU_NO_MI);
    }

    @Override
    public boolean AwakenUnlock(LivingEntity user) {
        return canUnlock(user);
    }

    static {
        INSTANCE = new AbilityCore.Builder<>("Mogu Dig", AbilityCategory.DEVIL_FRUITS, MoguDigAbility::new)
                .setUnlockCheck(MoguDigAbility::canUnlock)
                .addDescriptionLine(DESCRIPTION)
                .setIcon(new ResourceLocation("awakenawakennomi", "textures/abilities/mogu/mogu_dig.png"))
                .addAdvancedDescriptionLine(new AbilityDescriptionLine.IDescriptionLine[]{AbilityDescriptionLine.NEW_LINE, CooldownComponent.getTooltip(20.0F), ContinuousComponent.getTooltip()})
                .build();
    }
}
