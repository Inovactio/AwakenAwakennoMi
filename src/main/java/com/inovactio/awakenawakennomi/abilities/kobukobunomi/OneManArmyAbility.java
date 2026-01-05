package com.inovactio.awakenawakennomi.abilities.kobukobunomi;

import com.inovactio.awakenawakennomi.api.abilities.IAwakenable;
import com.inovactio.awakenawakennomi.util.InoHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.ForgeMod;
import org.apache.commons.lang3.tuple.ImmutablePair;
import xyz.pixelatedw.mineminenomi.api.abilities.*;
import xyz.pixelatedw.mineminenomi.api.abilities.components.*;
import xyz.pixelatedw.mineminenomi.api.helpers.AbilityHelper;
import xyz.pixelatedw.mineminenomi.api.helpers.AttributeHelper;
import xyz.pixelatedw.mineminenomi.data.entity.devilfruit.DevilFruitCapability;
import xyz.pixelatedw.mineminenomi.init.*;
import xyz.pixelatedw.mineminenomi.particles.effects.ParticleEffect;
import xyz.pixelatedw.mineminenomi.wypi.WyHelper;

public class OneManArmyAbility extends Ability implements IAwakenable {
    public static final AbilityCore<OneManArmyAbility> INSTANCE;
    private static final ITextComponent[] DESCRIPTION = AbilityHelper.registerDescriptionText("awakenawakennomi", "one_man_army",
            ImmutablePair.of("One Man Army (placeholder)", null));
    public static final int HOLD_TIME = 1200;
    private static final int MIN_COOLDOWN = 1200;
    private static final float MAX_COOLDOWN = 3600;
    private static final int UNCONSCIOUS_MAX_TIME = 300;
    private static final int CHARGE_TIME = 50;
    private static final AbilityOverlay OVERLAY;
    private final ChargeComponent chargeComponent = (new ChargeComponent(this)).addStartEvent(this::startChargeEvent).addEndEvent(this::stopChargeEvent);
    private final ContinuousComponent continuousComponent = (new ContinuousComponent(this, true)).addStartEvent(this::startContinuityEvent).addTickEvent(this::duringContinuityEvent).addEndEvent(this::endContinuityEvent);
    private final ChangeStatsComponent changeStatsComponent = new ChangeStatsComponent(this);
    private final SkinOverlayComponent skinOverlayComponent;
    private final AnimationComponent animationComponent = new AnimationComponent(this);
    private static final AbilityAttributeModifier SPEED_MODIFIER;
    private static final AbilityAttributeModifier JUMP_BOOST_MODIFIER;
    private static final AbilityAttributeModifier STRENGTH_MODIFIER;
    private static final AbilityAttributeModifier ATTACK_SPEED_MODIFIER;
    private static final AbilityAttributeModifier ARMOR_MODIFIER;
    private static final AbilityAttributeModifier ARMOR_THOUGNESS_MODIFIER;
    private static final AbilityAttributeModifier KNOCKBACK_RESISTANCE_MODIFIER;
    private static final AbilityAttributeModifier STEP_HEIGHT_MODIFIER;
    private static final AbilityAttributeModifier FALL_RESISTANCE_MODIFIER;
    private static final AbilityAttributeModifier TOUGHNESS_MODIFIER;

    public OneManArmyAbility(AbilityCore<OneManArmyAbility> core) {
        super(core);
        this.isNew = true;
        this.changeStatsComponent.addAttributeModifier(Attributes.MOVEMENT_SPEED, SPEED_MODIFIER);
        this.changeStatsComponent.addAttributeModifier(ModAttributes.PUNCH_DAMAGE, STRENGTH_MODIFIER);
        this.changeStatsComponent.addAttributeModifier(Attributes.ATTACK_SPEED, ATTACK_SPEED_MODIFIER);
        this.changeStatsComponent.addAttributeModifier(Attributes.ARMOR, ARMOR_MODIFIER);
        this.changeStatsComponent.addAttributeModifier(Attributes.ARMOR_TOUGHNESS, ARMOR_THOUGNESS_MODIFIER);
        this.changeStatsComponent.addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, KNOCKBACK_RESISTANCE_MODIFIER);
        this.changeStatsComponent.addAttributeModifier(ModAttributes.JUMP_HEIGHT, JUMP_BOOST_MODIFIER);
        this.changeStatsComponent.addAttributeModifier(ModAttributes.STEP_HEIGHT, STEP_HEIGHT_MODIFIER);
        this.changeStatsComponent.addAttributeModifier(ModAttributes.FALL_RESISTANCE, FALL_RESISTANCE_MODIFIER);
        this.changeStatsComponent.addAttributeModifier(ModAttributes.TOUGHNESS, TOUGHNESS_MODIFIER);
        this.skinOverlayComponent = new SkinOverlayComponent(this, OVERLAY, new AbilityOverlay[0]);
        this.addComponents(new AbilityComponent[]{this.chargeComponent, this.continuousComponent, this.changeStatsComponent, this.skinOverlayComponent, this.animationComponent});
        this.addUseEvent(this::useEvent);
    }

    private void useEvent(LivingEntity entity, IAbility ability) {
        if(this.chargeComponent.isCharging()){
            return;
        }
        if (this.continuousComponent.isContinuous()) {
            this.continuousComponent.stopContinuity(entity);
        }
        this.chargeComponent.startCharging(entity, CHARGE_TIME);
    }

    private void startChargeEvent(LivingEntity entity, IAbility ability) {
        this.animationComponent.start(entity, ModAnimations.AIM_SNIPER);
        entity.addEffect(new EffectInstance(ModEffects.MOVEMENT_BLOCKED.get(), CHARGE_TIME + 10, 1, false, false));
    }

    private void stopChargeEvent(LivingEntity entity, IAbility ability) {
        this.animationComponent.stop(entity);
        this.continuousComponent.triggerContinuity(entity, HOLD_TIME);
    }

    private void startContinuityEvent(LivingEntity entity, IAbility ability) {
        this.changeStatsComponent.applyModifiers(entity);
        this.skinOverlayComponent.showAll(entity);
    }

    private void duringContinuityEvent(LivingEntity entity, IAbility ability) {
        WyHelper.spawnParticleEffect((ParticleEffect) ModParticleEffects.SHOUREI.get(), entity, entity.getX(), entity.getY(), entity.getZ());
    }

    private void endContinuityEvent(LivingEntity entity, IAbility ability) {
        this.changeStatsComponent.removeModifiers(entity);
        this.skinOverlayComponent.hideAll(entity);
        float continueTime = this.continuousComponent.getContinueTime();
        float percent = continueTime / (float) HOLD_TIME;
        percent = Math.max(0f, Math.min(1f, percent));
        float cooldown = MIN_COOLDOWN + percent * (MAX_COOLDOWN - MIN_COOLDOWN);
        this.cooldownComponent.startCooldown(entity, cooldown);
        int unconsciousTime = (int) (this.continuousComponent.getContinueTime() / HOLD_TIME * UNCONSCIOUS_MAX_TIME);
        entity.addEffect(new EffectInstance((Effect) ModEffects.UNCONSCIOUS.get(), unconsciousTime, 0, false, false, true));
    }

    protected static boolean canUnlock(LivingEntity user) {
        return DevilFruitCapability.get(user).hasAwakenedFruit()
                && DevilFruitCapability.get(user).hasDevilFruit(ModAbilities.KOBU_KOBU_NO_MI);
    }

    @Override
    public boolean AwakenUnlock(LivingEntity user) {
        return canUnlock(user);
    }

    static {
        INSTANCE = new AbilityCore.Builder<OneManArmyAbility>("One Man Army", AbilityCategory.DEVIL_FRUITS, OneManArmyAbility::new)
                .setUnlockCheck(OneManArmyAbility::canUnlock)
                .addDescriptionLine(DESCRIPTION)
                .addAdvancedDescriptionLine(new AbilityDescriptionLine.IDescriptionLine[]{AbilityDescriptionLine.NEW_LINE, ChangeStatsComponent.getTooltip()})
                .setIcon(new ResourceLocation("awakenawakennomi", "textures/abilities/kobu/one_man_army.png"))
                .build();
        OVERLAY = (new AbilityOverlay.Builder()).setColor(KobuHelper.COLOR).setRenderType(AbilityOverlay.RenderType.ENERGY).build();
        SPEED_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_MOVEMENT_SPEED_UUID, INSTANCE, "Awaken Human Point Speed Modifier", (double) 2.5F, AttributeModifier.Operation.MULTIPLY_TOTAL);
        JUMP_BOOST_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_JUMP_BOOST_UUID, INSTANCE, "Awaken Human Jump Modifier", (double) 15.0F, AttributeModifier.Operation.ADDITION);
        STRENGTH_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_STRENGTH_UUID, INSTANCE, "Awaken Human Strength Modifier", (double) 20.0F, AttributeModifier.Operation.ADDITION);
        ATTACK_SPEED_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_ATTACK_SPEED_UUID, INSTANCE, "Awaken Human Attack Speed Modifier", (double) 0.5F, AttributeModifier.Operation.MULTIPLY_BASE);
        ARMOR_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_ARMOR_UUID, INSTANCE, "Awaken Human Armor Modifier", (double) 10.0F, AttributeModifier.Operation.ADDITION);
        ARMOR_THOUGNESS_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_ARMOR_TOUGHNESS_UUID, INSTANCE, "Awaken Human Armor Thougness Modifier", (double) 5.0F, AttributeModifier.Operation.ADDITION);
        KNOCKBACK_RESISTANCE_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_KNOCKBACK_RESISTANCE_UUID, INSTANCE, "Awaken Human Knockback Resistance Modifier", (double) 6.0F, AttributeModifier.Operation.ADDITION);
        STEP_HEIGHT_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_STEP_HEIGHT_UUID, INSTANCE, "Awaken Human Modifier", (double) 1F, AttributeModifier.Operation.ADDITION);
        FALL_RESISTANCE_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_FALL_RESISTANCE_UUID, INSTANCE, "Awaken Human Fall Resistance Modifier", (double) 30.0F, AttributeModifier.Operation.ADDITION);
        TOUGHNESS_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_TOUGHNESS_UUID, INSTANCE, "Awaken Human Toughness Modifier", (double) 5.0F, AttributeModifier.Operation.ADDITION);
    }
}
