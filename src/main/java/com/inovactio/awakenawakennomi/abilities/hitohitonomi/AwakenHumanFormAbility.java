package com.inovactio.awakenawakennomi.abilities.hitohitonomi;

import com.inovactio.awakenawakennomi.api.abilities.AwakenZoanAbility;
import com.inovactio.awakenawakennomi.api.abilities.IAwakenable;
import com.inovactio.awakenawakennomi.init.ModMorphs;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.ForgeMod;
import org.apache.commons.lang3.tuple.ImmutablePair;
import xyz.pixelatedw.mineminenomi.api.abilities.*;
import xyz.pixelatedw.mineminenomi.api.abilities.components.*;
import xyz.pixelatedw.mineminenomi.api.damagesource.SourceElement;
import xyz.pixelatedw.mineminenomi.api.damagesource.SourceHakiNature;
import xyz.pixelatedw.mineminenomi.api.helpers.AbilityHelper;
import xyz.pixelatedw.mineminenomi.api.helpers.AttributeHelper;
import xyz.pixelatedw.mineminenomi.api.morph.MorphInfo;
import xyz.pixelatedw.mineminenomi.data.entity.devilfruit.DevilFruitCapability;
import xyz.pixelatedw.mineminenomi.init.ModAbilities;
import xyz.pixelatedw.mineminenomi.init.ModAttributes;

import java.util.function.Predicate;

public class AwakenHumanFormAbility extends AwakenZoanAbility implements IAwakenable {

    private static final ITextComponent[] DESCRIPTION = AbilityHelper.registerDescriptionText("awakenawakennomi", "awaken_human_form",
            ImmutablePair.of("Awaken Human Form (placeholder)", null));
    public static final AbilityCore<AwakenHumanFormAbility> INSTANCE;
    private static final AbilityAttributeModifier SPEED_MODIFIER;
    private static final AbilityAttributeModifier JUMP_BOOST_MODIFIER;
    private static final AbilityAttributeModifier STRENGTH_MODIFIER;
    private static final AbilityAttributeModifier ATTACK_SPEED_MODIFIER;
    private static final AbilityAttributeModifier ARMOR_MODIFIER;
    private static final AbilityAttributeModifier ARMOR_THOUGNESS_MODIFIER;
    private static final AbilityAttributeModifier KNOCKBACK_RESISTANCE_MODIFIER;
    private static final AbilityAttributeModifier FALL_RESISTENCE_MODIFIER;
    private static final AbilityAttributeModifier HEALTH_BOOST_MODIFIER;
    private static final AbilityAttributeModifier STEP_HEIGHT_MODIFIER;
    private static final AbilityAttributeModifier FALL_RESISTANCE_MODIFIER;
    private static final AbilityAttributeModifier TOUGHNESS_MODIFIER;
    private static final AbilityAttributeModifier REACH_MODIFIER;

    public AwakenHumanFormAbility(AbilityCore<AwakenHumanFormAbility> core){
        super(core);
        this.statsComponent.addAttributeModifier(Attributes.MOVEMENT_SPEED, SPEED_MODIFIER);
        this.statsComponent.addAttributeModifier(ModAttributes.PUNCH_DAMAGE, STRENGTH_MODIFIER);
        this.statsComponent.addAttributeModifier(Attributes.ATTACK_SPEED, ATTACK_SPEED_MODIFIER);
        this.statsComponent.addAttributeModifier(Attributes.ARMOR, ARMOR_MODIFIER);
        this.statsComponent.addAttributeModifier(Attributes.ARMOR_TOUGHNESS, ARMOR_THOUGNESS_MODIFIER);
        this.statsComponent.addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, KNOCKBACK_RESISTANCE_MODIFIER);
        this.statsComponent.addAttributeModifier(ModAttributes.JUMP_HEIGHT, JUMP_BOOST_MODIFIER);
        this.statsComponent.addAttributeModifier(ModAttributes.FALL_RESISTANCE, FALL_RESISTENCE_MODIFIER);
        this.statsComponent.addAttributeModifier(Attributes.MAX_HEALTH, HEALTH_BOOST_MODIFIER);
        this.statsComponent.addAttributeModifier(ModAttributes.STEP_HEIGHT, STEP_HEIGHT_MODIFIER);
        this.statsComponent.addAttributeModifier(ModAttributes.FALL_RESISTANCE, FALL_RESISTANCE_MODIFIER);
        this.statsComponent.addAttributeModifier(ModAttributes.TOUGHNESS, TOUGHNESS_MODIFIER);
        this.statsComponent.addAttributeModifier(ForgeMod.REACH_DISTANCE, REACH_MODIFIER);
        this.statsComponent.addAttributeModifier(ModAttributes.ATTACK_RANGE, REACH_MODIFIER);
    }

    @Override
    public MorphInfo getTransformation() {
        return ModMorphs.AWAKEN_HUMAN.get();
    }

    protected static boolean canUnlock(LivingEntity user) {
        return DevilFruitCapability.get(user).hasAwakenedFruit()
                && DevilFruitCapability.get(user).hasDevilFruit(ModAbilities.HITO_HITO_NO_MI);
    }

    @Override
    public boolean AwakenUnlock(LivingEntity user) {
        return canUnlock(user);
    }

    static {
        INSTANCE = new AbilityCore.Builder<AwakenHumanFormAbility>("Awaken Human Form", AbilityCategory.DEVIL_FRUITS, AwakenHumanFormAbility::new)
                .setUnlockCheck(AwakenHumanFormAbility::canUnlock)
                .addDescriptionLine(DESCRIPTION)
                .addAdvancedDescriptionLine(new AbilityDescriptionLine.IDescriptionLine[]{AbilityDescriptionLine.NEW_LINE, ChangeStatsComponent.getTooltip()})
                .setIcon(new ResourceLocation("awakenawakennomi", "textures/abilities/hito/awaken_human_form.png"))
                .build();
        SPEED_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_MOVEMENT_SPEED_UUID, INSTANCE, "Awaken Human Point Speed Modifier", (double)2F, AttributeModifier.Operation.MULTIPLY_TOTAL);
        JUMP_BOOST_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_JUMP_BOOST_UUID, INSTANCE, "Awaken Human Jump Modifier", (double)6.0F, AttributeModifier.Operation.MULTIPLY_TOTAL);
        STRENGTH_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_STRENGTH_UUID, INSTANCE, "Awaken Human Strength Modifier", (double)10.0F, AttributeModifier.Operation.ADDITION);
        ATTACK_SPEED_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_ATTACK_SPEED_UUID, INSTANCE, "Awaken Human Attack Speed Modifier", (double)0.5F, AttributeModifier.Operation.MULTIPLY_BASE);
        ARMOR_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_ARMOR_UUID, INSTANCE, "Awaken Human Armor Modifier", (double)20.0F, AttributeModifier.Operation.ADDITION);
        ARMOR_THOUGNESS_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_ARMOR_TOUGHNESS_UUID, INSTANCE, "Awaken Human Armor Thougness Modifier", (double)20.0F, AttributeModifier.Operation.ADDITION);
        KNOCKBACK_RESISTANCE_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_KNOCKBACK_RESISTANCE_UUID, INSTANCE, "Awaken Human Knockback Resistance Modifier", (double)2.0F, AttributeModifier.Operation.ADDITION);
        FALL_RESISTENCE_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_FALL_RESISTANCE_UUID, INSTANCE, "Awaken Human Jump Resitance Modifier", (double)30.0F, AttributeModifier.Operation.ADDITION);
        HEALTH_BOOST_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_HEALTH_UUID, INSTANCE, "Awaken Human Health Modifier", (double)0.25F, AttributeModifier.Operation.MULTIPLY_TOTAL);
        STEP_HEIGHT_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_STEP_HEIGHT_UUID, INSTANCE, "Awaken Human Modifier", (double)1F, AttributeModifier.Operation.ADDITION);
        FALL_RESISTANCE_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_FALL_RESISTANCE_UUID, INSTANCE, "Awaken Human Fall Resistance Modifier", (double)14.0F, AttributeModifier.Operation.ADDITION);
        TOUGHNESS_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_TOUGHNESS_UUID, INSTANCE, "Awaken Human Toughness Modifier", (double)6.0F, AttributeModifier.Operation.ADDITION);
        REACH_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_ATTACK_REACH_UUID, INSTANCE, "Awaken Human Reach Modifier", (double)0.25F, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }
}
