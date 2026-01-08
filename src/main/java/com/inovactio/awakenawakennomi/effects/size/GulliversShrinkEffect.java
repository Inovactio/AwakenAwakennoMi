package com.inovactio.awakenawakennomi.effects.size;

import com.inovactio.awakenawakennomi.abilities.minimininomi.MiniHelper;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.potion.EffectType;
import xyz.pixelatedw.mineminenomi.init.ModAttributes;

public class GulliversShrinkEffect extends ShrinkEffect{

    public static final double SCALE = -0.5F;
    public static final double SPEED_REDUCE = -0.2F;
    public static final String UUID = "3f6b7a2e-1b6e-4b1d-9c6f-6d2e1a4c8f55";

    public GulliversShrinkEffect() {
        super(EffectType.HARMFUL, MiniHelper.MINI_COLOR.getRGB(), SCALE, UUID);
        this.addAttributeModifier((Attribute) ModAttributes.ATTACK_RANGE.get(), UUID, SCALE, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier((Attribute) ModAttributes.MINING_SPEED.get(), UUID, SCALE, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, UUID, SCALE, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier((Attribute) ModAttributes.DAMAGE_REDUCTION.get(), UUID, SCALE, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, UUID, SPEED_REDUCE, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }
}
