package com.inovactio.awakenawakennomi.abilities.bomubomunomi;

import com.inovactio.awakenawakennomi.api.abilities.IAwakenable;
import com.inovactio.awakenawakennomi.util.ToolTipHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.server.ServerWorld;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import xyz.pixelatedw.mineminenomi.abilities.rokushiki.GeppoAbility;
import xyz.pixelatedw.mineminenomi.api.abilities.*;
import xyz.pixelatedw.mineminenomi.api.abilities.components.AbilityComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.components.DamageTakenComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.components.PoolComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.components.StackComponent;
import xyz.pixelatedw.mineminenomi.api.damagesource.SourceElement;
import xyz.pixelatedw.mineminenomi.api.helpers.AbilityHelper;
import xyz.pixelatedw.mineminenomi.data.entity.devilfruit.DevilFruitCapability;
import xyz.pixelatedw.mineminenomi.data.entity.entitystats.EntityStatsCapability;
import xyz.pixelatedw.mineminenomi.data.entity.entitystats.IEntityStats;
import xyz.pixelatedw.mineminenomi.init.ModAbilities;
import xyz.pixelatedw.mineminenomi.init.ModAbilityPools;
import xyz.pixelatedw.mineminenomi.init.ModParticleEffects;
import xyz.pixelatedw.mineminenomi.init.ModSounds;
import xyz.pixelatedw.mineminenomi.particles.effects.CommonExplosionParticleEffect;
import xyz.pixelatedw.mineminenomi.particles.effects.ParticleEffect;
import xyz.pixelatedw.mineminenomi.wypi.WyHelper;

public class AwakenBlastJump extends Ability implements IAwakenable {
    private static final ITextComponent[] DESCRIPTION = AbilityHelper.registerDescriptionText("awakenawakennomi", "awaken_blast_jump", new Pair[]{ImmutablePair.of("The user kicks the air beneath them to launch themselves into the air", (Object)null)});
    private static final int MIN_JUMPS = 3;
    private static final int MAX_JUMPS = 6;
    private static final float SHORT_COOLDOWN_PER_STACK = 10.0F;
    private static final float LONG_COOLDOWN_PER_STACK = 50.0F;
    private static final float POWER = 2.0F; // explosion power
    private static final int EXPLOSION_SIZE = 10;
    private static final float STATIC_DAMAGE = 40.0F;
    private static final AbilityDescriptionLine.IDescriptionLine GEPPO_STACKS = (e, a) -> {
        if (a instanceof AwakenBlastJump) {
            AwakenBlastJump blastJump = (AwakenBlastJump)a;
            AbilityStat.Builder statBuilder = new AbilityStat.Builder(StackComponent.STACKS_STAT, blastJump.getMaxJumps(e), blastJump.getMaxJumps(e));
            return statBuilder.build().getStatDescription();
        } else {
            return null;
        }
    };
    public static final AbilityCore<AwakenBlastJump> INSTANCE;
    private final PoolComponent poolComponent;
    private final DamageTakenComponent damageTakenComponent;
    private final StackComponent stackComponent;
    private boolean hasFallDamage;

    public AwakenBlastJump(AbilityCore<AwakenBlastJump> core) {
        super(core);
        this.poolComponent = new PoolComponent(this, ModAbilityPools.GEPPO_LIKE, new AbilityPool2[0]);
        this.damageTakenComponent = (new DamageTakenComponent(this)).addOnAttackEvent(this::onDamageTaken);
        this.stackComponent = new StackComponent(this);
        this.hasFallDamage = true;
        this.isNew = true;
        this.addComponents(new AbilityComponent[]{this.poolComponent, this.damageTakenComponent, this.stackComponent});
        this.setOGCD();
        this.addCanUseCheck(AbilityHelper::canUseMomentumAbilities);
        this.addUseEvent(this::onUseEvent);
        this.addTickEvent(this::tickEvent);
    }

    private void onUseEvent(LivingEntity entity, IAbility ability) {
        this.stackComponent.setDefaultStacks(this.getMaxJumps(entity));
        int stacksUsed = 1;
        Vector3d speed;
        double ySpeed;
        if (entity.isInWater()) {
            speed = WyHelper.propulsion(entity, (double)2.0F, (double)2.0F, (double)2.0F);
            ySpeed = speed.y;
            stacksUsed = this.stackComponent.getStacks();
        } else {
            if (entity.isOnGround()) {
                speed = WyHelper.propulsion(entity, (double)1.0F, (double)1.0F);
                ySpeed = 1.86;
            } else {
                speed = WyHelper.propulsion(entity, (double)1.5F, (double)1.5F);
                ySpeed = (double)1.25F;
            }

            stacksUsed = 1;
        }

        AbilityHelper.setDeltaMovement(entity, speed.x, ySpeed, speed.z);
        this.stackComponent.addStacks(entity, this, -stacksUsed);
        this.hasFallDamage = false;
        GenerateExplosion(entity);
        if (this.stackComponent.getStacks() <= 0) {
            super.cooldownComponent.startCooldown(entity, this.getCooldownTicks());
            this.stackComponent.setStacks(entity, this, this.getMaxJumps(entity));
        } else {
            super.cooldownComponent.startCooldown(entity, 10.0F);
        }

    }

    private float onDamageTaken(LivingEntity entity, IAbility ability, DamageSource damageSource, float damage) {
        if (!this.hasFallDamage && damageSource == DamageSource.FALL) {
            this.resetStacks(entity);
            return 0.0F;
        } else {
            return damage;
        }
    }

    public void tickEvent(LivingEntity entity, IAbility ability) {
        if (this.getLastUseGametime() <= 0L && this.stackComponent.getDefaultStacks() <= 0) {
            this.stackComponent.setDefaultStacks(this.getMaxJumps(entity));
            this.stackComponent.revertStacksToDefault(entity, this);
        }

        if (!entity.level.isClientSide && !this.hasFallDamage && this.stackComponent.getStacks() < this.stackComponent.getDefaultStacks() && entity.isOnGround() && entity.level.getGameTime() > this.getLastUseGametime() + 10L) {
            this.resetStacks(entity);
        }

    }

    private void resetStacks(LivingEntity entity) {
        if (this.stackComponent.getStacks() != this.stackComponent.getDefaultStacks()) {
            this.cooldownComponent.stopCooldown(entity);
            this.cooldownComponent.startCooldown(entity, this.getCooldownTicks());
        }

        this.stackComponent.setStacks(entity, this, this.getMaxJumps(entity));
        this.stackComponent.setDefaultStacks(this.getMaxJumps(entity));
        this.hasFallDamage = true;
    }

    private int getMaxJumps(LivingEntity entity) {
        return 10;
    }

    private float getCooldownTicks() {
        return (float)(this.stackComponent.getDefaultStacks() - this.stackComponent.getStacks()) * 50.0F;
    }

    public CompoundNBT save(CompoundNBT nbt) {
        nbt = super.save(nbt);
        nbt.putBoolean("hasFallDamage", this.hasFallDamage);
        return nbt;
    }

    public void load(CompoundNBT nbt) {
        super.load(nbt);
        this.hasFallDamage = nbt.getBoolean("hasFallDamage");
    }

    private static boolean canUnlock(LivingEntity user) {
        return DevilFruitCapability.get(user).hasAwakenedFruit()
                && DevilFruitCapability.get(user).hasDevilFruit(ModAbilities.BOMU_BOMU_NO_MI);
    }

    private static void GenerateExplosion(LivingEntity entity)
    {
        ExplosionAbility ex = new ExplosionAbility(entity, entity.level, entity.getX(), entity.getY(), entity.getZ()-1, POWER);
        ex.setExplosionSize(EXPLOSION_SIZE);
        ex.setStaticDamage(STATIC_DAMAGE);
        ex.setDestroyBlocks(false);
        ex.setDropBlocksAfterExplosion(false);
        ex.setFireAfterExplosion(false);
        ex.setDamageOwner(false);
        ex.setSmokeParticles(new CommonExplosionParticleEffect(EXPLOSION_SIZE));
        ex.disableExplosionKnockback();
        ex.setExplosionSound(true);
        ex.doExplosion();
    }

    @Override
    public boolean AwakenUnlock(LivingEntity user) {
        return canUnlock(user);
    }



    static {
        INSTANCE = new AbilityCore.Builder<>("AwakenBlastJump", AbilityCategory.DEVIL_FRUITS, AwakenBlastJump::new)
                .addDescriptionLine(DESCRIPTION)
                .addAdvancedDescriptionLine(new AbilityDescriptionLine.IDescriptionLine[]{AbilityDescriptionLine.NEW_LINE, AbilityHelper.createShortLongCooldownStat(10.0F, 50.0F), GEPPO_STACKS})
                .addAdvancedDescriptionLine(ToolTipHelper.getExplosionTooltips((int)POWER, (int)EXPLOSION_SIZE, (int)STATIC_DAMAGE))
                .setSourceElement(SourceElement.EXPLOSION)
                .setUnlockCheck(AwakenBlastJump::canUnlock)
                .setIcon(new ResourceLocation("awakenawakennomi", "textures/abilities/awaken_blast_jump.png"))
                .build();
    }
}
