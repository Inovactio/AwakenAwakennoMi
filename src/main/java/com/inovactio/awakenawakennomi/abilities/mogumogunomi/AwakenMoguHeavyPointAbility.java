package com.inovactio.awakenawakennomi.abilities.mogumogunomi;

import com.inovactio.awakenawakennomi.abilities.ushiushinomi.giraffe.AwakenGiraffeHeavyPointAbility;
import com.inovactio.awakenawakennomi.api.abilities.AwakenZoanAbility;
import com.inovactio.awakenawakennomi.init.ModMorphs;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.ForgeMod;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import xyz.pixelatedw.mineminenomi.api.abilities.*;
import xyz.pixelatedw.mineminenomi.api.abilities.components.ChangeStatsComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.components.ContinuousComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.components.CooldownComponent;
import xyz.pixelatedw.mineminenomi.api.helpers.AbilityHelper;
import xyz.pixelatedw.mineminenomi.api.helpers.AttributeHelper;
import xyz.pixelatedw.mineminenomi.api.morph.MorphInfo;
import xyz.pixelatedw.mineminenomi.data.entity.devilfruit.DevilFruitCapability;
import xyz.pixelatedw.mineminenomi.init.ModAbilities;
import xyz.pixelatedw.mineminenomi.init.ModAttributes;

import java.util.function.Predicate;

public class AwakenMoguHeavyPointAbility extends AwakenZoanAbility{
    private static final ITextComponent[] DESCRIPTION = AbilityHelper.registerDescriptionText("awakenawakennomi", "awaken_mogu_heavy_point", new Pair[]{ImmutablePair.of("Transforms the user into a mole, which focuses on strength and digging speed", (Object)null)});
    public static final AbilityCore<AwakenMoguHeavyPointAbility> INSTANCE;
    private static final AbilityAttributeModifier SPEED_MODIFIER;
    private static final AbilityAttributeModifier JUMP_BOOST_MODIFIER;
    private static final AbilityAttributeModifier STEP_HEIGHT_MODIFIER;
    private static final AbilityAttributeModifier ARMOR_MODIFIER;
    private static final AbilityAttributeModifier STRENGTH_MODIFIER;
    private static final AbilityAttributeModifier ATTACK_SPEED_MODIFIER;
    private static final AbilityAttributeModifier REACH_MODIFIER;
    private static final AbilityAttributeModifier TOUGHNESS_MODIFIER;
    private static final AbilityAttributeModifier MINING_SPEED_MODIFIER;

    public AwakenMoguHeavyPointAbility(AbilityCore core) {
        super(core);
        Predicate<LivingEntity> isContinuityActive = (entity) -> this.continuousComponent.isContinuous();
        this.statsComponent.addAttributeModifier(Attributes.MOVEMENT_SPEED, SPEED_MODIFIER, isContinuityActive);
        this.statsComponent.addAttributeModifier(ModAttributes.JUMP_HEIGHT, JUMP_BOOST_MODIFIER, isContinuityActive);
        this.statsComponent.addAttributeModifier(ModAttributes.STEP_HEIGHT, STEP_HEIGHT_MODIFIER, isContinuityActive);
        this.statsComponent.addAttributeModifier(Attributes.ARMOR, ARMOR_MODIFIER, isContinuityActive);
        this.statsComponent.addAttributeModifier(ModAttributes.PUNCH_DAMAGE, STRENGTH_MODIFIER, isContinuityActive);
        this.statsComponent.addAttributeModifier(Attributes.ATTACK_SPEED, ATTACK_SPEED_MODIFIER, isContinuityActive);
        this.statsComponent.addAttributeModifier(ForgeMod.REACH_DISTANCE, REACH_MODIFIER, isContinuityActive);
        this.statsComponent.addAttributeModifier(ModAttributes.ATTACK_RANGE, REACH_MODIFIER, isContinuityActive);
        this.statsComponent.addAttributeModifier(ModAttributes.TOUGHNESS, TOUGHNESS_MODIFIER, isContinuityActive);
        this.statsComponent.addAttributeModifier(ModAttributes.MINING_SPEED, MINING_SPEED_MODIFIER, isContinuityActive);
        this.continuousComponent.addEndEvent(100, this::onEndContinuityEvent);
    }

    public void duringContinuityEvent(PlayerEntity player, int time) {
        player.addEffect(new EffectInstance(Effects.DIG_SPEED, 5, 2, false, false));
        if (!player.hasEffect(Effects.NIGHT_VISION) || player.getEffect(Effects.NIGHT_VISION).getDuration() < 500) {
            player.addEffect(new EffectInstance(Effects.NIGHT_VISION, 500, 0, false, false));
        }

    }

    protected void onEndContinuityEvent(LivingEntity entity, IAbility ability) {
        entity.removeEffect(Effects.MOVEMENT_SPEED);
        entity.removeEffect(Effects.NIGHT_VISION);
    }

    public MorphInfo getTransformation() {
        return (MorphInfo)ModMorphs.AWAKEN_MOGU.get();
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
        INSTANCE = new AbilityCore.Builder<>("Awaken Mogu Heavy Point", AbilityCategory.DEVIL_FRUITS, AwakenMoguHeavyPointAbility::new)
                .setUnlockCheck(AwakenMoguHeavyPointAbility::canUnlock)
                .setIcon(new ResourceLocation("awakenawakennomi", "textures/abilities/mogu/awaken_mogu_heavy_point.png"))
                .addDescriptionLine(DESCRIPTION)
                .addAdvancedDescriptionLine(new AbilityDescriptionLine.IDescriptionLine[]{AbilityDescriptionLine.NEW_LINE, CooldownComponent.getTooltip(10.0F), ContinuousComponent.getTooltip(), ChangeStatsComponent.getTooltip()})
                .build();
        SPEED_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_MOVEMENT_SPEED_UUID, INSTANCE, "Awaken Giraffe Heavy Point Speed Modifier", (double)0.75F, AttributeModifier.Operation.MULTIPLY_TOTAL);
        JUMP_BOOST_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_JUMP_BOOST_UUID, INSTANCE, "Awaken Giraffe Heavy Point Jump Modifier", (double)3.5F, AttributeModifier.Operation.ADDITION);
        STEP_HEIGHT_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_STEP_HEIGHT_UUID, INSTANCE, "Awaken Giraffe Heavy Point Step Height Modifier", (double)1F, AttributeModifier.Operation.ADDITION);
        ARMOR_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_ARMOR_UUID, INSTANCE, "Mogu Heavy Point Modifier", (double)10.0F, AttributeModifier.Operation.ADDITION);
        STRENGTH_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_STRENGTH_UUID, INSTANCE, "Mogu Heavy Point Modifier", (double)12.0F, AttributeModifier.Operation.ADDITION);
        ATTACK_SPEED_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_ATTACK_SPEED_UUID, INSTANCE, "Mogu Heavy Point Modifier", (double)0.30F, AttributeModifier.Operation.ADDITION);
        REACH_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_ATTACK_REACH_UUID, INSTANCE, "Mogu Heavy Reach Modifier", (double)-0.5F, AttributeModifier.Operation.ADDITION);
        TOUGHNESS_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_TOUGHNESS_UUID, INSTANCE, "Mogu Heavy Point Toughness Modifier", (double)4.0F, AttributeModifier.Operation.ADDITION);
        MINING_SPEED_MODIFIER = new AbilityAttributeModifier(AttributeHelper.MORPH_MINING_SPEED_UUID, INSTANCE, "Mogu Heavy Point Mining Speed Modifier", (double)10.0F, AttributeModifier.Operation.ADDITION);
    }
}
