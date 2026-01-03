package com.inovactio.awakenawakennomi.abilities.mogumogunomi;

import com.inovactio.awakenawakennomi.api.abilities.IAwakenable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import xyz.pixelatedw.mineminenomi.abilities.sui.NekomimiPunchAbility;
import xyz.pixelatedw.mineminenomi.abilities.sui.SuiHelper;
import xyz.pixelatedw.mineminenomi.api.abilities.AbilityCategory;
import xyz.pixelatedw.mineminenomi.api.abilities.AbilityCore;
import xyz.pixelatedw.mineminenomi.api.abilities.AbilityDescriptionLine;
import xyz.pixelatedw.mineminenomi.api.abilities.DashAbility;
import xyz.pixelatedw.mineminenomi.api.abilities.components.CooldownComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.components.DealDamageComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.components.RangeComponent;
import xyz.pixelatedw.mineminenomi.api.damagesource.SourceHakiNature;
import xyz.pixelatedw.mineminenomi.api.damagesource.SourceType;
import xyz.pixelatedw.mineminenomi.api.helpers.AbilityHelper;
import xyz.pixelatedw.mineminenomi.data.entity.devilfruit.DevilFruitCapability;
import xyz.pixelatedw.mineminenomi.init.ModAbilities;

public class SubterraneanDashAbility extends DashAbility implements IAwakenable {
    private static final ITextComponent[] DESCRIPTION = AbilityHelper.registerDescriptionText("awakenawakennomi", "subterranean_dash", new Pair[]{ImmutablePair.of("Dash forward quickly while burrowing.", (Object)null)});
    private static final int COOLDOWN = 120;
    private static final float RANGE = 1.6F;
    private static final int DAMAGE = 20;
    public static final AbilityCore<SubterraneanDashAbility> INSTANCE;

    public SubterraneanDashAbility(AbilityCore<SubterraneanDashAbility> core) {
        super(core);
        this.addCanUseCheck(MoguHelper::isDigging);
    }

    public void onTargetHit(LivingEntity entity, LivingEntity target, float damage, DamageSource source) {
    }

    public boolean isParallel() {
        return true;
    }

    public float getDashCooldown() {
        return COOLDOWN;
    }

    public float getDamage() {
        return DAMAGE;
    }

    public float getRange() {
        return RANGE;
    }

    public double getSpeed() {
        return (double)3.0F;
    }

    public int getHoldTime() {
        return 20;
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
        INSTANCE = new AbilityCore.Builder<>("Subterranean Dash", AbilityCategory.DEVIL_FRUITS, SubterraneanDashAbility::new)
                .addDescriptionLine(DESCRIPTION)
                .setUnlockCheck(MoguDigAbility::canUnlock)
                .setIcon(new ResourceLocation("awakenawakennomi", "textures/abilities/mogu/subterranean_dash.png"))
                .addAdvancedDescriptionLine(new AbilityDescriptionLine.IDescriptionLine[]{AbilityDescriptionLine.NEW_LINE, CooldownComponent.getTooltip(120.0F), RangeComponent.getTooltip(1.6F, RangeComponent.RangeType.AOE), DealDamageComponent.getTooltip(20.0F)}).setSourceHakiNature(SourceHakiNature.HARDENING)
                .setSourceType(new SourceType[]{SourceType.FIST})
                .build();
    }
}
