package com.inovactio.awakenawakennomi.api.abilities;

import java.util.function.Predicate;

import com.inovactio.awakenawakennomi.api.abilities.components.BlockTriggerComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import xyz.pixelatedw.mineminenomi.api.abilities.Ability;
import xyz.pixelatedw.mineminenomi.api.abilities.IAbility;
import xyz.pixelatedw.mineminenomi.api.abilities.components.AbilityComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.AbilityCore;
import xyz.pixelatedw.mineminenomi.api.abilities.components.ChangeStatsComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.components.ContinuousComponent;

@Mod.EventBusSubscriber
public abstract class BlockUseAbility extends Ability {
    protected final ContinuousComponent continuousComponent =
            (new ContinuousComponent(this, this.isParallel()))
                    .addStartEvent(90, this::onStart)
                    .addTickEvent(90, this::onTick)
                    .addEndEvent(90, this::onEnd);

    protected final ChangeStatsComponent statsComponent = new ChangeStatsComponent(this);
    protected final BlockTriggerComponent blockTriggerComponent =
            new BlockTriggerComponent(this)
                    .addTryBlockHitEvent(200, this::tryBlockHitEvent)
                    .addOnBlockHitEvent(200, this::onBlockPunchedEvent);

    private int uses = 0;
    private boolean markForStopping;


    public BlockUseAbility(AbilityCore<? extends BlockUseAbility> core) {
        super(core);
        this.isNew = true;
        this.addComponents(new AbilityComponent[]{continuousComponent, statsComponent, blockTriggerComponent});
        this.addUseEvent(200, this::onUseEvent);
    }

    private void onUseEvent(LivingEntity entity, IAbility ability) {
        this.continuousComponent.triggerContinuity(entity, this.getPunchHoldTime());
    }

    private BlockTriggerComponent.HitResult tryBlockHitEvent(LivingEntity entity, BlockPos pos, World world, IAbility ability) {
        return this.canActivate().test(entity) ? BlockTriggerComponent.HitResult.HIT : BlockTriggerComponent.HitResult.PASS;
    }

    private boolean onBlockPunchedEvent(LivingEntity entity, BlockPos pos, World world, IAbility ability) {
        if (this.markForStopping) {
            return true;
        }
        this.increaseUses();
        return this.onBlockUsed(entity, pos, world);
    }

    private void onStart(LivingEntity entity, IAbility ability) {
        this.uses = 0;
        this.markForStopping = false;
        this.statsComponent.applyModifiers(entity);
    }

    private void onTick(LivingEntity entity, IAbility ability) {
        if (this.markForStopping) {
            this.continuousComponent.stopContinuity(entity);
            this.markForStopping = false;
        }
    }

    private void onEnd(LivingEntity entity, IAbility ability) {
        this.statsComponent.removeModifiers(entity);
        float cd = this.getPunchCooldown();
        if (cd > 0)
            this.cooldownComponent.startCooldown(entity, cd);
    }

    public void increaseUses() {
        ++this.uses;
        if (this.getUseLimit() > 0 && this.uses >= this.getUseLimit()) {
            this.markForStopping = true;
        }
    }

    public abstract boolean onBlockUsed(LivingEntity entity, BlockPos pos, World world);

    public abstract float getPunchCooldown();

    public boolean isParallel() {
        return false;
    }

    public float getPunchHoldTime() {
        return -1.0F;
    }

    public abstract Predicate<LivingEntity> canActivate();

    public abstract int getUseLimit();

    public abstract boolean GetAllowBlockActivation();

    public boolean isActive() {
        return this.isContinuous() || this.isCharging();
    }
}
