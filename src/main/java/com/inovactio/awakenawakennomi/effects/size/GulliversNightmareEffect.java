package com.inovactio.awakenawakennomi.effects.size;

import com.inovactio.awakenawakennomi.abilities.minimininomi.MiniHelper;
import net.minecraft.potion.EffectType;

public class GulliversNightmareEffect extends ShrinkEffect{
    public GulliversNightmareEffect() {
        super(EffectType.HARMFUL, MiniHelper.MINI_COLOR.getRGB(), -0.5F);
    }
}
