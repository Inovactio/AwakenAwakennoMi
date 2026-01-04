package com.inovactio.awakenawakennomi.abilities.kamekamenomi;

import java.util.List;

import com.inovactio.awakenawakennomi.api.abilities.IAwakenable;
import com.inovactio.awakenawakennomi.api.animations.IHandAnimation;
import com.inovactio.awakenawakennomi.init.ModAnimations;
import com.inovactio.awakenawakennomi.init.ModMorphs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import xyz.pixelatedw.mineminenomi.api.abilities.*;
import xyz.pixelatedw.mineminenomi.api.abilities.components.*;
import xyz.pixelatedw.mineminenomi.api.helpers.AbilityHelper;
import xyz.pixelatedw.mineminenomi.api.helpers.AttributeHelper;
import xyz.pixelatedw.mineminenomi.api.morph.MorphInfo;
import xyz.pixelatedw.mineminenomi.data.entity.devilfruit.DevilFruitCapability;
import xyz.pixelatedw.mineminenomi.init.ModAbilities;
import xyz.pixelatedw.mineminenomi.init.ModAttributes;
import xyz.pixelatedw.mineminenomi.init.ModEffects;
import xyz.pixelatedw.mineminenomi.wypi.WyHelper;

public class SpinAbility extends Ability implements IAwakenable, IHandAnimation {
    private static final ITextComponent[] DESCRIPTION = AbilityHelper.registerDescriptionText("awakenawakennomi", "spin", new Pair[] { ImmutablePair.of("The user retreats into their shell and spins rapidly, knocking back nearby enemies and deflecting attacks.", null) });
    public static final AbilityCore<SpinAbility> INSTANCE;

    private final ContinuousComponent continuousComponent = new ContinuousComponent(this, true);
    private final CooldownComponent cooldownComponent = new CooldownComponent(this);
    private final AnimationComponent animationComponent = new AnimationComponent(this);
    private final DealDamageComponent dealDamageComponent = new DealDamageComponent(this);
    private final RangeComponent rangeComponent = new RangeComponent(this);
    private final ChangeStatsComponent changeStatsComponent = new ChangeStatsComponent(this);

    private static final AbilityAttributeModifier JUMP_MODIFIER;
    private static final AbilityAttributeModifier SPEED_MODIFIER;

    private static final float AREA = 2.5F;
    private static final float DAMAGE = 20.0F;
    private static final float DURATION_SECONDS = 100.0F;
    private static final float COOLDOWN_SECONDS = 600.0F;
    private static final int TICK_INTERVAL = 20; // how often to apply the expel (ticks)

    public SpinAbility(AbilityCore<SpinAbility> core) {
        super(core);
        this.isNew = true;
        RequireMorphComponent requireMorphComponent = new RequireMorphComponent(this, (MorphInfo) ModMorphs.AWAKEN_KAME.get(), xyz.pixelatedw.mineminenomi.init.ModMorphs.KAME_WALK.get());
        this.addComponents(this.cooldownComponent, this.continuousComponent, this.animationComponent, requireMorphComponent, this.dealDamageComponent, this.rangeComponent, this.changeStatsComponent);

        this.continuousComponent
                .addStartEvent(this::onStartContinuity)
                .addTickEvent(TICK_INTERVAL, this::onTickContinuity)
                .addEndEvent(this::onEndContinuity);

        this.changeStatsComponent.addAttributeModifier(Attributes.MOVEMENT_SPEED, SPEED_MODIFIER);
        this.changeStatsComponent.addAttributeModifier(ModAttributes.JUMP_HEIGHT, JUMP_MODIFIER);

        this.addUseEvent(this::onUse);
    }

    private void onUse(LivingEntity entity, xyz.pixelatedw.mineminenomi.api.abilities.IAbility ability) {
        if (this.continuousComponent.isContinuous()) {
            this.continuousComponent.stopContinuity(entity);
        } else {
            this.continuousComponent.triggerContinuity(entity, DURATION_SECONDS);
        }
    }

    private void onStartContinuity(LivingEntity entity, xyz.pixelatedw.mineminenomi.api.abilities.IAbility ability) {
        this.animationComponent.start(entity, ModAnimations.SPIN);
        this.changeStatsComponent.applyModifiers(entity);
    }

    private void onTickContinuity(LivingEntity user, xyz.pixelatedw.mineminenomi.api.abilities.IAbility ability) {
        List<LivingEntity> targets = this.rangeComponent.getTargetsInArea(user, user.blockPosition(), AREA);

        for(LivingEntity target : targets) {
            if (this.dealDamageComponent.hurtTarget(user, target, DAMAGE)) {
                Vector3d speed = WyHelper.propulsion(user, (double)2.0F, (double)2.0F);
                AbilityHelper.setDeltaMovement(target, speed.x, 1, speed.z);
            }
        }
    }

    private void onEndContinuity(LivingEntity entity, xyz.pixelatedw.mineminenomi.api.abilities.IAbility ability) {
        this.changeStatsComponent.removeModifiers(entity);
        this.animationComponent.stop(entity);
        this.cooldownComponent.startCooldown(entity, COOLDOWN_SECONDS);
    }

    @Override
    public boolean DisableHandOfPlayer() {
        return true;
    }

    protected static boolean canUnlock(LivingEntity user) {
        return DevilFruitCapability.get(user).hasAwakenedFruit()
                && DevilFruitCapability.get(user).hasDevilFruit(ModAbilities.KAME_KAME_NO_MI);
    }

    @Override
    public boolean AwakenUnlock(LivingEntity user) {
        return canUnlock(user);
    }

    static {
        INSTANCE = new AbilityCore.Builder<>("Spin", AbilityCategory.DEVIL_FRUITS,SpinAbility::new)
                .setUnlockCheck(SpinAbility::canUnlock)
                .addDescriptionLine(DESCRIPTION)
                .addAdvancedDescriptionLine(new AbilityDescriptionLine.IDescriptionLine[] { AbilityDescriptionLine.NEW_LINE, ContinuousComponent.getTooltip(DURATION_SECONDS), CooldownComponent.getTooltip(COOLDOWN_SECONDS), ChangeStatsComponent.getTooltip()})
                .setIcon(new ResourceLocation("awakenawakennomi", "textures/abilities/kame/spin.png"))
                .build();
        SPEED_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_MOVEMENT_SPEED_UUID, INSTANCE, "Spin Speed Modifier", (double)2F, AttributeModifier.Operation.MULTIPLY_TOTAL);
        JUMP_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_JUMP_BOOST_UUID, INSTANCE, "Spin Modifier", (double)2.0F, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }


}
