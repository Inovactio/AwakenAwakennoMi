package com.inovactio.awakenawakennomi.effects.sliding;

import com.inovactio.awakenawakennomi.abilities.subesubenomi.SubeHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.potion.Effects;

import java.awt.*;

public class SmoothWorldSlidingEffect extends SlidingEffect {
    private static final double MAX_SPEED = 0.2D;
    private static final double SLIDE_POWER = 2D;

    public SmoothWorldSlidingEffect() {
        super(EffectType.HARMFUL, SubeHelper.SUBE_COLOR.getRGB(), SLIDE_POWER, MAX_SPEED);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        super.applyEffectTick(entity, amplifier);
    }
}
