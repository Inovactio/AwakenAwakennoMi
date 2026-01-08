package com.inovactio.awakenawakennomi.effects.size;

import com.inovactio.awakenawakennomi.AwakenAwakenNoMiMod;
import com.inovactio.awakenawakennomi.abilities.minimininomi.MiniHelper;
import com.inovactio.awakenawakennomi.init.AwakenAttributes;
import com.inovactio.awakenawakennomi.util.CartAddonHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.*;
import net.minecraft.potion.EffectType;
import xyz.pixelatedw.mineminenomi.api.effects.ModEffect;

public abstract class ShrinkEffect extends ModEffect {
    protected final double Scale;

    public ShrinkEffect(EffectType effectType, int liquidColorIn, double scale, AttributeModifier.Operation operation) {
        super(effectType, liquidColorIn);
        this.Scale = scale;
        this.addAttributeModifier((Attribute) AwakenAttributes.SIZE.get(), "3f6b7a2e-1b6e-4b1d-9c6f-6d2e1a4c8f55", Scale, operation);
        if(AwakenAwakenNoMiMod.hasCartAddonInstalled())
        {
            this.addAttributeModifier(CartAddonHelper.GetCartSizeAttribute(), "3f6b7a2e-1b6e-4b1d-9c6f-6d2e1a4c8f55", Scale, operation);
        }
    }

    public ShrinkEffect(EffectType effectType, int liquidColorIn, double scale) {
        this(effectType, liquidColorIn, scale, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeModifierManager map, int amp) {
        super.addAttributeModifiers(entity, map, amp);
        entity.refreshDimensions();
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeModifierManager map, int amp) {
        super.removeAttributeModifiers(entity, map, amp);
        entity.refreshDimensions();
    }
}
