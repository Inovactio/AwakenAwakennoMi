package com.inovactio.awakenawakennomi.api.abilities;

import com.google.common.base.Strings;
import net.minecraft.entity.LivingEntity;
import xyz.pixelatedw.mineminenomi.api.abilities.AbilityCore;
import xyz.pixelatedw.mineminenomi.api.abilities.IAbility;
import xyz.pixelatedw.mineminenomi.api.abilities.MorphAbility2;
import xyz.pixelatedw.mineminenomi.api.abilities.components.*;
import xyz.pixelatedw.mineminenomi.data.entity.ability.AbilityDataCapability;
import xyz.pixelatedw.mineminenomi.data.entity.devilfruit.DevilFruitCapability;
import xyz.pixelatedw.mineminenomi.init.ModAbilityKeys;

public abstract class AwakenZoanAbility extends MorphAbility2 implements IAwakenable{

    protected final ContinuousComponent continuousComponent = (new ContinuousComponent(this, true)).addStartEvent(110, this::startContinuityEvent).addTickEvent(110, this::tickContinuityEvent).addEndEvent(110, this::stopContinuityEvent);
    protected final ChangeStatsComponent statsComponent = new ChangeStatsComponent(this);
    protected final MorphComponent morphComponent = new MorphComponent(this);

    public AwakenZoanAbility(AbilityCore core) {
        super(core);
        this.isNew = true;
        this.addComponents(new AbilityComponent[]{this.continuousComponent, this.statsComponent, this.morphComponent});
        this.addUseEvent(this::onUseEvent);
    }

    protected void onUseEvent(LivingEntity entity, IAbility ability) {
        String currentMorph = DevilFruitCapability.get(entity).getZoanPoint();
        if (!Strings.isNullOrEmpty(currentMorph) && !currentMorph.equals(this.getTransformation().getForm())) {
            for(IAbility abl : AbilityDataCapability.get(entity).getEquippedAbilitiesWith(new AbilityComponentKey[]{ModAbilityKeys.MORPH, ModAbilityKeys.CONTINUOUS})) {
                if (abl != this) {
                    ((ContinuousComponent)abl.getComponent(ModAbilityKeys.CONTINUOUS).get()).stopContinuity(entity);
                }
            }
        }
        this.continuousComponent.triggerContinuity(entity, this.getContinuityHoldTime());
    }

    protected void startContinuityEvent(LivingEntity entity, IAbility ability) {
        float initialHealthPercentage = entity.getHealth() / entity.getMaxHealth();
        this.morphComponent.startMorph(entity, this.getTransformation());
        this.statsComponent.applyModifiers(entity);
        float newHealth = entity.getMaxHealth() * initialHealthPercentage;
        entity.setHealth(newHealth);
    }

    protected void tickContinuityEvent(LivingEntity entity, IAbility ability) {
        if (!this.morphComponent.isMorphed()) {
            this.continuousComponent.stopContinuity(entity);
        }

    }

    protected void stopContinuityEvent(LivingEntity entity, IAbility ability) {
        float initialHealthPercentage = entity.getHealth() / entity.getMaxHealth();
        this.morphComponent.stopMorph(entity);
        this.statsComponent.removeModifiers(entity);
        this.cooldownComponent.startCooldown(entity, this.getCooldownTicks());
        float newHealth = entity.getMaxHealth() * initialHealthPercentage;
        entity.setHealth(newHealth);
    }
}
