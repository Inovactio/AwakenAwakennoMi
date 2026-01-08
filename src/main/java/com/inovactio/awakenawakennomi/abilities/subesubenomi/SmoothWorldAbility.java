package com.inovactio.awakenawakennomi.abilities.subesubenomi;

import com.inovactio.awakenawakennomi.api.abilities.IAwakenable;
import com.inovactio.awakenawakennomi.api.abilities.ZoneAbility;
import com.inovactio.awakenawakennomi.init.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import xyz.pixelatedw.mineminenomi.api.abilities.*;
import xyz.pixelatedw.mineminenomi.api.abilities.components.ChargeComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.components.CooldownComponent;
import xyz.pixelatedw.mineminenomi.api.helpers.AbilityHelper;
import xyz.pixelatedw.mineminenomi.data.entity.devilfruit.DevilFruitCapability;
import xyz.pixelatedw.mineminenomi.init.ModAbilities;

public class SmoothWorldAbility extends ZoneAbility implements IAwakenable {
    private static final ITextComponent[] DESCRIPTION = AbilityHelper.registerDescriptionText("awakenawakennomi", "smooth_world", new Pair[]{ImmutablePair.of("Creates a smooth domain around the user. Creatures inside lose traction and slide uncontrollably, weakening their ability to fight effectively.", (Object) null)});
    public static final AbilityCore<SmoothWorldAbility> INSTANCE;

    public SmoothWorldAbility(AbilityCore<SmoothWorldAbility> core) {
        super(core);
        zoneColor = SubeHelper.SUBE_COLOR;
    }

    protected static boolean canUnlock(LivingEntity user) {
        return DevilFruitCapability.get(user).hasAwakenedFruit()
                && DevilFruitCapability.get(user).hasDevilFruit(ModAbilities.SUBE_SUBE_NO_MI);
    }

    @Override
    protected void applyEffectToEntityInZone(LivingEntity owner, LivingEntity target) {
        target.addEffect(new EffectInstance((Effect) ModEffects.SMOOTH_WORLD_SLIDING.get(), (int)this.zoneTime, 0, false, false, false));
    }

    @Override
    protected void onEntityLeavesZone(LivingEntity owner, LivingEntity target) {
        target.removeEffect(ModEffects.SMOOTH_WORLD_SLIDING.get());
    }


    @Override
    public boolean AwakenUnlock(LivingEntity user) {
        return canUnlock(user);
    }

    static {

        INSTANCE = new AbilityCore.Builder<>("Smooth World", AbilityCategory.DEVIL_FRUITS, SmoothWorldAbility::new)
                .setUnlockCheck(SmoothWorldAbility::canUnlock)
                .addDescriptionLine(DESCRIPTION)
                .setIcon(new ResourceLocation("awakenawakennomi", "textures/abilities/sube/smooth_world.png"))
                .addAdvancedDescriptionLine(new AbilityDescriptionLine.IDescriptionLine[]{AbilityDescriptionLine.NEW_LINE, CooldownComponent.getTooltip(COOLDOWN), ChargeComponent.getTooltip(MIN_CHARGE_TIME,CHARGE_TIME)})
                .build();
    }
}