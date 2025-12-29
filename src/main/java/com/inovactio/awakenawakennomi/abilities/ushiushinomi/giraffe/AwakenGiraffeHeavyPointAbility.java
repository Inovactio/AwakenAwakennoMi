package com.inovactio.awakenawakennomi.abilities.ushiushinomi.giraffe;

import com.inovactio.awakenawakennomi.abilities.hitohitonomi.AwakenHumanFormAbility;
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
import org.apache.commons.lang3.tuple.Pair;
import xyz.pixelatedw.mineminenomi.abilities.gomu.GomuGomuNoPistolAbility;
import xyz.pixelatedw.mineminenomi.api.abilities.*;
import xyz.pixelatedw.mineminenomi.api.abilities.components.ChangeStatsComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.components.ContinuousComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.components.CooldownComponent;
import xyz.pixelatedw.mineminenomi.api.helpers.AbilityHelper;
import xyz.pixelatedw.mineminenomi.api.helpers.AttributeHelper;
import xyz.pixelatedw.mineminenomi.api.morph.MorphInfo;
import xyz.pixelatedw.mineminenomi.data.entity.ability.AbilityDataCapability;
import xyz.pixelatedw.mineminenomi.data.entity.ability.IAbilityData;
import xyz.pixelatedw.mineminenomi.data.entity.devilfruit.DevilFruitCapability;
import xyz.pixelatedw.mineminenomi.init.ModAbilities;
import xyz.pixelatedw.mineminenomi.init.ModAttributes;

public class AwakenGiraffeHeavyPointAbility extends AwakenZoanAbility implements IAwakenable {
    private static final ITextComponent[] DESCRIPTION = AbilityHelper.registerDescriptionText("awakenawakennomi", "awaken_giraffe_heavy_point", new Pair[]{ImmutablePair.of("Transforms the user into a half-giraffe hybrid, which focuses on strength.", (Object)null)});
    public static final AbilityCore<AwakenGiraffeHeavyPointAbility> INSTANCE;
    private static final AbilityAttributeModifier SPEED_MODIFIER;
    private static final AbilityAttributeModifier JUMP_BOOST_MODIFIER;
    private static final AbilityAttributeModifier STRENGTH_MODIFIER;
    private static final AbilityAttributeModifier ATTACK_SPEED_MODIFIER;
    private static final AbilityAttributeModifier REACH_MODIFIER;
    private static final AbilityAttributeModifier ARMOR_MODIFIER;
    private static final AbilityAttributeModifier TOUGHNESS_MODIFIER;
    private static final AbilityAttributeModifier FALL_RESISTANCE_MODIFIER;
    private static final AbilityAttributeModifier STEP_HEIGHT_MODIFIER;

    public AwakenGiraffeHeavyPointAbility(AbilityCore core) {
        super(core);
        this.statsComponent.addAttributeModifier(Attributes.MOVEMENT_SPEED, SPEED_MODIFIER);
        this.statsComponent.addAttributeModifier(ModAttributes.PUNCH_DAMAGE, STRENGTH_MODIFIER);
        this.statsComponent.addAttributeModifier(Attributes.ATTACK_SPEED, ATTACK_SPEED_MODIFIER);
        this.statsComponent.addAttributeModifier(ModAttributes.JUMP_HEIGHT, JUMP_BOOST_MODIFIER);
        this.statsComponent.addAttributeModifier(ModAttributes.FALL_RESISTANCE, JUMP_BOOST_MODIFIER);
        this.statsComponent.addAttributeModifier(ForgeMod.REACH_DISTANCE, REACH_MODIFIER);
        this.statsComponent.addAttributeModifier(ModAttributes.ATTACK_RANGE, REACH_MODIFIER);
        this.statsComponent.addAttributeModifier(Attributes.ARMOR, ARMOR_MODIFIER);
        this.statsComponent.addAttributeModifier(ModAttributes.TOUGHNESS, TOUGHNESS_MODIFIER);
        this.statsComponent.addAttributeModifier(ModAttributes.FALL_RESISTANCE, FALL_RESISTANCE_MODIFIER);
        this.statsComponent.addAttributeModifier(ModAttributes.STEP_HEIGHT, STEP_HEIGHT_MODIFIER);
    }

    public MorphInfo getTransformation() {
        return (MorphInfo) ModMorphs.AWAKEN_USHI.get();
    }

    protected static boolean canUnlock(LivingEntity user) {
        return DevilFruitCapability.get(user).hasAwakenedFruit()
                && DevilFruitCapability.get(user).hasDevilFruit(ModAbilities.USHI_USHI_NO_MI_GIRAFFE);
    }

    @Override
    protected void startContinuityEvent(LivingEntity entity, IAbility ability) {
        super.startContinuityEvent(entity, ability);
        IAbilityData props = AbilityDataCapability.get(entity);
        ReworkedBiganAbility bigan = props.getEquippedAbility(ReworkedBiganAbility.INSTANCE);
        if (bigan != null) bigan.switchAwakenHeavyPoint(entity);

    }

    @Override
    protected void stopContinuityEvent(LivingEntity entity, IAbility ability) {
        super.stopContinuityEvent(entity, ability);
        IAbilityData props = AbilityDataCapability.get(entity);
        ReworkedBiganAbility bigan = props.getEquippedAbility(ReworkedBiganAbility.INSTANCE);
        if (bigan != null) bigan.switchNoPoint(entity);
    }

    @Override
    public boolean AwakenUnlock(LivingEntity user) {
        return canUnlock(user);
    }

    static {
        INSTANCE = new AbilityCore.Builder<>("Awaken Giraffe Heavy Point", AbilityCategory.DEVIL_FRUITS, AwakenGiraffeHeavyPointAbility::new)
                .setUnlockCheck(AwakenGiraffeHeavyPointAbility::canUnlock)
                .setIcon(new ResourceLocation("awakenawakennomi", "textures/abilities/ushi/giraffe/awaken_giraffe_heavy_point.png"))
                .addDescriptionLine(DESCRIPTION)
                .addAdvancedDescriptionLine(new AbilityDescriptionLine.IDescriptionLine[]{AbilityDescriptionLine.NEW_LINE, CooldownComponent.getTooltip(10.0F), ContinuousComponent.getTooltip(), ChangeStatsComponent.getTooltip()})
                .build();
        SPEED_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_MOVEMENT_SPEED_UUID, INSTANCE, "Awaken Giraffe Heavy Point Speed Modifier", (double)1F, AttributeModifier.Operation.MULTIPLY_TOTAL);
        JUMP_BOOST_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_JUMP_BOOST_UUID, INSTANCE, "Awaken Giraffe Heavy Point Jump Modifier", (double)5.0F, AttributeModifier.Operation.ADDITION);
        STRENGTH_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_STRENGTH_UUID, INSTANCE, "Awaken Giraffe Heavy Point Modifier", (double)8.0F, AttributeModifier.Operation.ADDITION);
        ATTACK_SPEED_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_ATTACK_SPEED_UUID, INSTANCE, "Awaken Giraffe Heavy Point Modifier", (double)0.30F, AttributeModifier.Operation.ADDITION);
        REACH_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_ATTACK_REACH_UUID, INSTANCE, "Awaken Giraffe Heavy Point Reach Modifier", (double)2.5F, AttributeModifier.Operation.ADDITION);
        ARMOR_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_ARMOR_UUID, INSTANCE, "Awaken Giraffe Heavy Point Modifier", (double)15.0F, AttributeModifier.Operation.ADDITION);
        TOUGHNESS_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_TOUGHNESS_UUID, INSTANCE, "Awaken Giraffe Heavy Point Toughness Modifier", (double)7.5F, AttributeModifier.Operation.ADDITION);
        FALL_RESISTANCE_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_FALL_RESISTANCE_UUID, INSTANCE, "Awaken Giraffe Heavy Point Fall Resistance Modifier", (double)4F, AttributeModifier.Operation.ADDITION);
        STEP_HEIGHT_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_STEP_HEIGHT_UUID, INSTANCE, "Awaken Giraffe Heavy Point Step Height Modifier", (double)1F, AttributeModifier.Operation.ADDITION);
    }
}
