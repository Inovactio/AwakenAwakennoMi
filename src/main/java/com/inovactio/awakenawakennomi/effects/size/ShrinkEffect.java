package com.inovactio.awakenawakennomi.effects.size;

import com.inovactio.awakenawakennomi.abilities.minimininomi.MiniHelper;
import com.inovactio.awakenawakennomi.init.AwakenAttributes;
import net.minecraft.entity.ai.attributes.*;
import net.minecraft.potion.EffectType;
import xyz.pixelatedw.mineminenomi.api.effects.ModEffect;

public class ShrinkEffect extends ModEffect {
    private static final double SCALE = -0.5f;

    public ShrinkEffect() {
        super(EffectType.HARMFUL, MiniHelper.MINI_COLOR.getRGB());
        this.addAttributeModifier((Attribute) AwakenAttributes.SIZE.get(), "3f6b7a2e-1b6e-4b1d-9c6f-6d2e1a4c8f55", SCALE, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }
}
