package com.inovactio.awakenawakennomi.abilities.kamekamenomi;

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
import xyz.pixelatedw.mineminenomi.abilities.kame.KameWalkPointAbility;
import xyz.pixelatedw.mineminenomi.api.abilities.AbilityAttributeModifier;
import xyz.pixelatedw.mineminenomi.api.abilities.AbilityCategory;
import xyz.pixelatedw.mineminenomi.api.abilities.AbilityCore;
import xyz.pixelatedw.mineminenomi.api.abilities.AbilityDescriptionLine;
import xyz.pixelatedw.mineminenomi.api.abilities.components.ChangeStatsComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.components.ContinuousComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.components.CooldownComponent;
import xyz.pixelatedw.mineminenomi.api.helpers.AbilityHelper;
import xyz.pixelatedw.mineminenomi.api.helpers.AttributeHelper;
import xyz.pixelatedw.mineminenomi.api.morph.MorphInfo;
import xyz.pixelatedw.mineminenomi.data.entity.devilfruit.DevilFruitCapability;
import xyz.pixelatedw.mineminenomi.init.ModAbilities;
import xyz.pixelatedw.mineminenomi.init.ModAttributes;

public class AwakenKameWalkPointAbility extends AwakenZoanAbility implements IAwakenable {
    private static final ITextComponent[] DESCRIPTION = AbilityHelper.registerDescriptionText("awakenawakennomi", "awaken_kame_walk_point",
            ImmutablePair.of("Awaken Human Form (placeholder)", null));
    public static final AbilityCore<AwakenKameWalkPointAbility> INSTANCE;
    private static final AbilityAttributeModifier SPEED_MODIFIER;
    private static final AbilityAttributeModifier ARMOR_MODIFIER;
    private static final AbilityAttributeModifier KNOCKBACK_RESISTANCE_MODIFIER;
    private static final AbilityAttributeModifier JUMP_BOOST_MODIFIER;
    private static final AbilityAttributeModifier WATER_SPEED_MODIFIER;
    private static final AbilityAttributeModifier TOUGHNESS_MODIFIER;
    private static final AbilityAttributeModifier STRENGTH_MODIFIER;


    public AwakenKameWalkPointAbility(AbilityCore<AwakenKameWalkPointAbility> core){
        super(core);
        this.statsComponent.addAttributeModifier(Attributes.MOVEMENT_SPEED, SPEED_MODIFIER);
        this.statsComponent.addAttributeModifier(Attributes.ARMOR, ARMOR_MODIFIER);
        this.statsComponent.addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, KNOCKBACK_RESISTANCE_MODIFIER);
        this.statsComponent.addAttributeModifier(ModAttributes.JUMP_HEIGHT, JUMP_BOOST_MODIFIER);
        this.statsComponent.addAttributeModifier(ForgeMod.SWIM_SPEED, WATER_SPEED_MODIFIER);
        this.statsComponent.addAttributeModifier(ModAttributes.TOUGHNESS, TOUGHNESS_MODIFIER);
        this.statsComponent.addAttributeModifier(ModAttributes.PUNCH_DAMAGE, STRENGTH_MODIFIER);
    }

    @Override
    public MorphInfo getTransformation() {
        return ModMorphs.AWAKEN_KAME.get();
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
        INSTANCE = new AbilityCore.Builder<AwakenKameWalkPointAbility>("Awaken Kame Walk Point", AbilityCategory.DEVIL_FRUITS, AwakenKameWalkPointAbility::new)
                .setUnlockCheck(AwakenKameWalkPointAbility::canUnlock)
                .addDescriptionLine(DESCRIPTION)
                .addAdvancedDescriptionLine(new AbilityDescriptionLine.IDescriptionLine[]{AbilityDescriptionLine.NEW_LINE, ContinuousComponent.getTooltip(), ChangeStatsComponent.getTooltip()})
                .setIcon(new ResourceLocation("awakenawakennomi", "textures/abilities/kame/awaken_kame_walk_point.png"))
                .build();
        SPEED_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_MOVEMENT_SPEED_UUID, INSTANCE, "Awaken Kame Walk Point Modifier", (double)0.5F, AttributeModifier.Operation.MULTIPLY_TOTAL);
        ARMOR_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_ARMOR_UUID, INSTANCE, "Awaken Kame Walk Point Modifier", (double)30.0F, AttributeModifier.Operation.ADDITION);
        KNOCKBACK_RESISTANCE_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_KNOCKBACK_RESISTANCE_UUID, INSTANCE, "Awaken Kame Walk Point Knockback Resistance Modifier", (double)3.0F, AttributeModifier.Operation.ADDITION);
        JUMP_BOOST_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_JUMP_BOOST_UUID, INSTANCE, "Awaken Kame Walk Point Jump Modifier", (double)2F, AttributeModifier.Operation.ADDITION);
        WATER_SPEED_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_SWIM_SPEED_UUID, INSTANCE, "Awaken Kame Walk Point Water Speed Modifier", (double)2.5F, AttributeModifier.Operation.ADDITION);
        TOUGHNESS_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_TOUGHNESS_UUID, INSTANCE, "Awaken Kame Walk Point Toughness Modifier", (double)20.0F, AttributeModifier.Operation.ADDITION);
        STRENGTH_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_STRENGTH_UUID, INSTANCE, "Awaken Kame Walk Point Strength Modifier", (double)6.0F, AttributeModifier.Operation.ADDITION);

    }
}
