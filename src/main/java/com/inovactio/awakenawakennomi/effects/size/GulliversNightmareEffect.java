package com.inovactio.awakenawakennomi.effects.size;

import com.inovactio.awakenawakennomi.abilities.minimininomi.MiniHelper;
import com.inovactio.awakenawakennomi.init.AwakenAttributes;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.potion.EffectType;
import xyz.pixelatedw.mineminenomi.init.ModAttributes;

public class GulliversNightmareEffect extends ShrinkEffect{

    public static final double Scale = -0.5F;
    public static final String Uuid = "3f6b7a2e-1b6e-4b1d-9c6f-6d2e1a4c8f55";

    public GulliversNightmareEffect() {
        super(EffectType.HARMFUL, MiniHelper.MINI_COLOR.getRGB(), Scale, Uuid);
        this.addAttributeModifier((Attribute) ModAttributes.ATTACK_RANGE.get(), Uuid, Scale, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier((Attribute) ModAttributes.MINING_SPEED.get(), Uuid, Scale, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, Uuid, Scale, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier((Attribute) ModAttributes.DAMAGE_REDUCTION.get(), Uuid, Scale, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, Uuid, Scale, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }
}
