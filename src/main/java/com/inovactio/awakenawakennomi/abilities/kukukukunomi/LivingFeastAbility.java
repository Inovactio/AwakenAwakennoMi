package com.inovactio.awakenawakennomi.abilities.kukukukunomi;

import com.inovactio.awakenawakennomi.api.abilities.IAwakenable;
import com.inovactio.awakenawakennomi.entities.mobs.ability.kuku.CakeGolemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import xyz.pixelatedw.mineminenomi.api.abilities.*;
import xyz.pixelatedw.mineminenomi.api.abilities.components.AbilityComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.components.ContinuousComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.components.CooldownComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.components.StackComponent;
import xyz.pixelatedw.mineminenomi.api.helpers.AbilityHelper;
import xyz.pixelatedw.mineminenomi.data.entity.devilfruit.DevilFruitCapability;
import xyz.pixelatedw.mineminenomi.init.ModAbilities;

import javax.annotation.Nullable;

public class LivingFeastAbility extends Ability implements IAwakenable {
    private static final int HOLD_TIME = 12000;
    private static final int MIN_COOLDOWN = 40;
    private static final int MAX_COOLDOWN = 2000;
    private static final ITextComponent[] DESCRIPTION = AbilityHelper.registerDescriptionText("awakenawakennomi", "living_feast", new Pair[]{ImmutablePair.of("The user summons a genie that fights for them.", (Object)null)});
    public static final AbilityCore<LivingFeastAbility> INSTANCE;
    private final ContinuousComponent continuousComponent = (new ContinuousComponent(this, true)).addStartEvent(100, this::startContinuityEvent).addTickEvent(100, this::onTickEvent).addEndEvent(100, this::stopContinuityEvent);
    private final StackComponent stackComponent = new StackComponent(this);
    private CakeGolemEntity cakeGolem = null;

    public LivingFeastAbility(AbilityCore<LivingFeastAbility> core) {
        super(core);
        this.isNew = true;
        this.addComponents(new AbilityComponent[]{this.continuousComponent, this.stackComponent});
        this.addUseEvent(this::onUseEvent);
    }

    private void onUseEvent(LivingEntity entity, IAbility ability) {
        this.continuousComponent.triggerContinuity(entity);
    }

    private void startContinuityEvent(LivingEntity entity, IAbility ability) {
        this.cakeGolem = new CakeGolemEntity(entity.level, entity);
        this.cakeGolem.moveTo(entity.getX(), entity.getY(), entity.getZ(), entity.yRot, entity.xRot);
        entity.level.addFreshEntity(this.cakeGolem);
    }

    private void onTickEvent(LivingEntity entity, IAbility ability) {
        if (this.cakeGolem == null || !this.cakeGolem.isAlive()) {
            this.continuousComponent.stopContinuity(entity);
        }

    }

    private void stopContinuityEvent(LivingEntity entity, IAbility ability) {
        if (this.cakeGolem != null) {
            this.cakeGolem.remove();
        }

        float cooldown = MathHelper.clamp(this.continuousComponent.getContinueTime(), MIN_COOLDOWN, MAX_COOLDOWN);
        this.cooldownComponent.startCooldown(entity, cooldown);
    }

    @Nullable
    public CakeGolemEntity getCakeGolem() {
        return this.cakeGolem;
    }

    public CompoundNBT save(CompoundNBT nbt) {
        nbt = super.save(nbt);
        return nbt;
    }

    public void load(CompoundNBT nbt) {
        super.load(nbt);
    }

    protected static boolean canUnlock(LivingEntity user) {
        return DevilFruitCapability.get(user).hasAwakenedFruit()
                && DevilFruitCapability.get(user).hasDevilFruit(ModAbilities.KUKU_KUKU_NO_MI);
    }

    @Override
    public boolean AwakenUnlock(LivingEntity user) {
        return canUnlock(user);
    }

    static {
        INSTANCE = new AbilityCore.Builder<>("Living Feast", AbilityCategory.DEVIL_FRUITS, LivingFeastAbility::new)
                .setUnlockCheck(LivingFeastAbility::canUnlock)
                .addDescriptionLine(DESCRIPTION)
                .addAdvancedDescriptionLine(new AbilityDescriptionLine.IDescriptionLine[]{AbilityDescriptionLine.NEW_LINE, ContinuousComponent.getTooltip(), CooldownComponent.getTooltip(40.0F, 2000.0F)})
                .build();
    }
}
