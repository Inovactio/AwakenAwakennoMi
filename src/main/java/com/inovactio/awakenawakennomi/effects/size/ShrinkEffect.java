package com.inovactio.awakenawakennomi.effects.size;

import com.inovactio.awakenawakennomi.AwakenAwakenNoMiMod;
import com.inovactio.awakenawakennomi.init.AwakenAttributes;
import com.inovactio.awakenawakennomi.util.CartAddonHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.*;
import net.minecraft.potion.EffectType;
import xyz.pixelatedw.mineminenomi.api.effects.ModEffect;

public abstract class ShrinkEffect extends ModEffect {
    protected final double Scale;
    protected final String Uuid;
    public ShrinkEffect(EffectType effectType, int liquidColorIn, double scale, String uuid, AttributeModifier.Operation operation) {
        super(effectType, liquidColorIn);
        this.Scale = scale;
        this.Uuid = uuid;
        this.addAttributeModifier((Attribute) AwakenAttributes.SIZE.get(), Uuid, Scale, operation);
        if(AwakenAwakenNoMiMod.hasCartAddonInstalled())
        {
            this.addAttributeModifier(CartAddonHelper.GetCartSizeAttribute(), Uuid, Scale, operation);
        }
    }

    public ShrinkEffect(EffectType effectType, int liquidColorIn, double scale, String uuid) {
        this(effectType, liquidColorIn, scale, uuid, AttributeModifier.Operation.MULTIPLY_TOTAL);
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
