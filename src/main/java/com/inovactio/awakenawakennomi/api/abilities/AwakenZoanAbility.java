package com.inovactio.awakenawakennomi.api.abilities;

import net.minecraft.entity.LivingEntity;
import xyz.pixelatedw.mineminenomi.api.abilities.Ability;
import xyz.pixelatedw.mineminenomi.api.abilities.AbilityCore;

public class AwakenZoanAbility extends Ability implements IAwakenable{
    public AwakenZoanAbility(AbilityCore core){
        super(core);
    }

    @Override
    public boolean AwakenUnlock(LivingEntity user) {
        return false;
    }
}
