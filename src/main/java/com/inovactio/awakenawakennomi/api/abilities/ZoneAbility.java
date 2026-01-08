package com.inovactio.awakenawakennomi.api.abilities;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.RegistryObject;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import xyz.pixelatedw.mineminenomi.api.abilities.Ability;
import xyz.pixelatedw.mineminenomi.api.abilities.AbilityCore;
import xyz.pixelatedw.mineminenomi.api.abilities.IAbility;
import xyz.pixelatedw.mineminenomi.api.abilities.components.AbilityComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.components.AnimationComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.components.ChargeComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.components.ContinuousComponent;
import xyz.pixelatedw.mineminenomi.api.helpers.AbilityHelper;
import xyz.pixelatedw.mineminenomi.api.math.EasingFunctionHelper;
import xyz.pixelatedw.mineminenomi.api.protection.BlockProtectionRule;
import xyz.pixelatedw.mineminenomi.api.protection.block.AirBlockProtectionRule;
import xyz.pixelatedw.mineminenomi.api.protection.block.FoliageBlockProtectionRule;
import xyz.pixelatedw.mineminenomi.api.protection.block.LiquidBlockProtectionRule;
import xyz.pixelatedw.mineminenomi.api.util.Interval;
import xyz.pixelatedw.mineminenomi.config.CommonConfig;
import xyz.pixelatedw.mineminenomi.entities.SphereEntity;
import xyz.pixelatedw.mineminenomi.init.ModAnimations;
import xyz.pixelatedw.mineminenomi.init.ModBlocks;
import xyz.pixelatedw.mineminenomi.init.ModEffects;
import xyz.pixelatedw.mineminenomi.init.ModSounds;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

public abstract class ZoneAbility extends Ability {
    private static final ITextComponent[] DESCRIPTION = AbilityHelper.registerDescriptionText("awakenawakennomi", "awaken_smooth_world", new Pair[]{ImmutablePair.of("Creates a spherical space around the user in which they can manipulate anything or use other skills", (Object)null)});
    private static final BlockProtectionRule GRIEF_RULE;
    protected int minZoneSize = 0;
    protected int maxZoneSize = 128;
    protected float minChargeTime = MIN_CHARGE_TIME;
    protected int chargeTime = CHARGE_TIME;
    protected float minCooldown = COOLDOWN;
    protected float maxCooldown = COOLDOWN;
    protected float cancelCooldown = 10.0F;
    protected float zoneTime = 1200.0F;
    protected Color zoneColor = new Color(255, 255, 255, 50);
    protected boolean allowAnimation = true;
    protected boolean onGroundOnly = true;
    protected boolean applyEffectOnUser = false;
    private final ChargeComponent chargeComponent = (new ChargeComponent(this)).addStartEvent(this::onStartCharge).addTickEvent(this::onChargeTick).addEndEvent(this::onChargeEnd);
    private final ContinuousComponent continuousComponent = (new ContinuousComponent(this, true)).addTickEvent(this::onContinuityTick).addEndEvent(this::onContinuityEnd);
    private final AnimationComponent animationComponent = new AnimationComponent(this);
    private SphereEntity zoneSphereEntity;
    private List<BlockPos> blockList = new ArrayList();
    private List<BlockPos> placedBlockList = new ArrayList();
    private float zoneSize = 0;
    private BlockPos centerPos;
    protected boolean isShrinking = false;
    private Interval checkPositionInterval = new Interval(chargeTime);
    private Interval playSoundInterval = new Interval(18);
    private final Set<UUID> affectedEntities = new HashSet<>();

    protected static final int MIN_CHARGE_TIME = 60;
    protected static final int CHARGE_TIME = 240;
    protected static final float COOLDOWN = 2400.0F;

    protected ZoneAbility(AbilityCore<? extends IAbility> core) {
        super(core);
        super.isNew = true;
        super.addComponents(new AbilityComponent[]{this.chargeComponent, this.continuousComponent, this.animationComponent});
        super.addUseEvent(this::onUseEvent);
    }

    private void onUseEvent(LivingEntity entity, IAbility ability) {
        if (this.chargeComponent.isCharging()) {
            if (this.chargeComponent.getChargeTime() < this.minChargeTime) {
                return;
            }

            this.chargeComponent.stopCharging(entity);
        } else if (this.continuousComponent.isContinuous()) {
            if (CommonConfig.INSTANCE.isExperiementalSpheresEnabled()) {
                this.isShrinking = true;
                this.chargeComponent.startCharging(entity, cancelCooldown);
                return;
            }

            this.continuousComponent.stopContinuity(entity);
        } else {
            if (onGroundOnly && !ensureOnGroundOrNotify(entity)) return;
            this.checkPositionInterval.restartIntervalToZero();
            entity.level.playSound((PlayerEntity)null, entity.blockPosition(), (SoundEvent)ModSounds.ROOM_CREATE_SFX.get(), SoundCategory.PLAYERS, 5.0F, 1.0F);
            this.isShrinking = false;
            this.setupSphere(entity);
            this.chargeComponent.startCharging(entity, chargeTime);
        }
    }

    protected void onStartCharge(LivingEntity entity, IAbility ability) {
        if (!entity.level.isClientSide && !isShrinking) {
            entity.addEffect(new EffectInstance(ModEffects.MOVEMENT_BLOCKED.get(), chargeTime, 1, false, false));
            if (allowAnimation) {
                this.animationComponent.start(entity, ModAnimations.RYU_NO_IBUKI, chargeTime);
            }
        }
    }

    protected void onChargeTick(LivingEntity entity, IAbility ability) {
        this.chargeTickSphere(entity);
        if (this.playSoundInterval.canTick()) {
            entity.level.playSound((PlayerEntity)null, entity.blockPosition(), (SoundEvent)ModSounds.ROOM_CHARGE_SFX.get(), SoundCategory.PLAYERS, 5.0F, 1.0F);
        }
    }

    protected void onChargeEnd(LivingEntity entity, IAbility ability) {
        if (!entity.level.isClientSide) {
            this.endChargeSphere(entity);
            this.endChargeBlocks(entity);
            animationComponent.stop(entity);
            entity.removeEffect(ModEffects.MOVEMENT_BLOCKED.get());
            entity.level.playSound((PlayerEntity)null, entity.blockPosition(), (SoundEvent)ModSounds.ROOM_EXPAND_SFX.get(), SoundCategory.PLAYERS, 5.0F, 1.0F);
            this.continuousComponent.startContinuity(entity, zoneTime);
        }
    }

    protected void onContinuityTick(LivingEntity entity, IAbility ability) {
        if (!entity.level.isClientSide) {
            if (this.continuityTickSphere(entity)) {
                if (this.continuityTickBlocks(entity)) {
                    this.applyEffectsInZone(entity);
                }
            }
        }
    }

    protected void onContinuityEnd(LivingEntity entity, IAbility ability) {
        // Retire les effets sur toutes les entités encore marquées comme affectées
        this.clearEffectsWhenZoneEnds(entity);

        this.continuityEndBlocks(entity);
        this.continuityEndSphere(entity);
        this.centerPos = null;
        entity.level.playSound((PlayerEntity)null, entity.blockPosition(), (SoundEvent)ModSounds.ROOM_CLOSE_SFX.get(), SoundCategory.PLAYERS, 5.0F, 1.0F);
        float roomSizeDebuff = (float)(this.zoneSize / maxZoneSize);
        float cooldown = minCooldown * roomSizeDebuff;
        cooldown = MathHelper.clamp(cooldown, minCooldown, maxCooldown);
        super.cooldownComponent.startCooldown(entity, cooldown);
    }

    protected void setupSphere(LivingEntity entity) {
        if (CommonConfig.INSTANCE.isExperiementalSpheresEnabled()) {
            this.zoneSphereEntity = new SphereEntity(entity.level, entity);
            this.zoneSphereEntity.setColor(zoneColor);
            this.zoneSphereEntity.setRadius(0.0F);
            this.zoneSphereEntity.setDetailLevel(32);
            this.zoneSphereEntity.setAnimationSpeed(1);
            entity.level.addFreshEntity(this.zoneSphereEntity);
            this.centerPos = this.zoneSphereEntity.blockPosition();
        }
    }

    protected void chargeTickSphere(LivingEntity entity) {
        if (!CommonConfig.INSTANCE.isExperiementalSpheresEnabled()) return;
        if (this.zoneSphereEntity == null) return;

        float pct = MathHelper.clamp(this.chargeComponent.getChargePercentage(), 0.0F, 1.0F);

        if (this.isShrinking) {
            // Shrink: démarrage rapide puis ralentit vers 0
            float easedPct = EasingFunctionHelper.easeOutCubic(1.0F - pct);
            float radius = MathHelper.clamp(easedPct * this.maxZoneSize, 0.0F, (float) this.maxZoneSize);
            this.zoneSphereEntity.setRadius(radius);
            this.zoneSize = radius;
            return;
        }

        // Expand: démarrage rapide (au lieu de très lent avec easeInCubic)
        float easedPct = EasingFunctionHelper.easeOutCubic(pct);
        float radius = MathHelper.clamp(easedPct * this.maxZoneSize, 0.0F, (float) this.maxZoneSize);

        this.zoneSphereEntity.setRadius(radius);
        this.zoneSize = radius;
    }

    protected void endChargeSphere(LivingEntity entity) {
        if (!CommonConfig.INSTANCE.isExperiementalSpheresEnabled()) return;
        if (entity.level.isClientSide) return;

        if (this.isShrinking) {
            if (this.zoneSphereEntity != null) this.zoneSphereEntity.remove();
            this.zoneSphereEntity = null;
            return;
        }

        if (this.zoneSphereEntity == null) return;

        float pct = MathHelper.clamp(this.chargeComponent.getChargePercentage(), 0.0F, 1.0F);

        // Même easing qu’en tick pour éviter tout “saut” en fin de charge
        float easedPct = EasingFunctionHelper.easeOutCubic(pct);

        float radius = MathHelper.clamp(easedPct * this.maxZoneSize, (float) this.minZoneSize, (float) this.maxZoneSize);

        this.zoneSphereEntity.setRadius(radius);
        this.zoneSize = radius;
    }

    protected boolean continuityTickSphere(LivingEntity entity) {
        if (!CommonConfig.INSTANCE.isExperiementalSpheresEnabled()) {
            return true;
        } else if (this.checkPositionInterval.canTick() && !this.isEntityInZone(entity)) {
            this.continuousComponent.stopContinuity(entity);
            return false;
        } else if (this.zoneSphereEntity != null && this.zoneSphereEntity.isAlive()) {
            return true;
        } else {
            this.continuousComponent.stopContinuity(entity);
            return false;
        }
    }

    protected void continuityEndSphere(LivingEntity entity) {
        if (CommonConfig.INSTANCE.isExperiementalSpheresEnabled()) {
            if (this.zoneSphereEntity != null) {
                this.zoneSphereEntity.remove();
            }

            this.zoneSphereEntity = null;
        }
    }

    protected void endChargeBlocks(LivingEntity entity) {
        if (!CommonConfig.INSTANCE.isExperiementalSpheresEnabled()) {
            if (this.blockList.isEmpty()) {
                this.zoneSize = Math.max(minZoneSize, (int)(maxZoneSize * this.chargeComponent.getChargeTime() / this.chargeComponent.getMaxChargeTime()));
                this.centerPos = new BlockPos(entity.getX(), entity.getY(), entity.getZ());
                this.blockList.addAll(AbilityHelper.createSphere(entity.level, entity.blockPosition(), (int)this.zoneSize, true, (Block)ModBlocks.OPE.get(), 0, GRIEF_RULE));
                this.placedBlockList.addAll(this.blockList);
            }
        }
    }

    protected boolean continuityTickBlocks(LivingEntity entity) {
        if (CommonConfig.INSTANCE.isExperiementalSpheresEnabled()) {
            return true;
        } else if (this.centerPos != null && this.checkPositionInterval.canTick() && !this.isPositionInZone(entity.blockPosition())) {
            this.continuousComponent.stopContinuity(entity);
            return false;
        } else {
            int placedBlocks = 0;
            Iterator<BlockPos> iter = this.placedBlockList.iterator();

            while (iter.hasNext()) {
                BlockPos pos = (BlockPos)iter.next();
                entity.level.sendBlockUpdated(pos, Blocks.AIR.defaultBlockState(), ((Block)ModBlocks.OPE.get()).defaultBlockState(), 0);
                iter.remove();
                ++placedBlocks;
                if (placedBlocks > 512) {
                    return false;
                }
            }

            return true;
        }
    }

    protected void continuityEndBlocks(LivingEntity entity) {
        if (!CommonConfig.INSTANCE.isExperiementalSpheresEnabled()) {
            for (BlockPos pos : this.blockList) {
                Block currentBlock = entity.level.getBlockState(pos).getBlock();
                if (currentBlock == ModBlocks.OPE.get()) {
                    entity.level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                }
            }

            this.blockList.clear();
            this.placedBlockList.clear();
        }
    }

    public float getZoneSize() {
        return this.zoneSize;
    }

    public BlockPos getCenterBlock() {
        return this.centerPos;
    }

    public boolean isEntityInZone(Entity entity) {
        if (this.isPositionInZone(entity.blockPosition())) {
            return true;
        } else if (this.zoneSphereEntity == null) {
            return false;
        } else {
            return entity.distanceToSqr(this.zoneSphereEntity) <= (double)(this.zoneSize * this.zoneSize);
        }
    }

    public boolean isPositionInZone(BlockPos pos) {
        return this.centerPos != null && pos.closerThan(this.centerPos, (double)this.zoneSize);
    }


    protected void applyEffectsInZone(LivingEntity owner) {
        if (owner == null || owner.level == null || owner.level.isClientSide) return;
        if (this.centerPos == null || this.zoneSize <= 0) return;

        double r = this.zoneSize;

        AxisAlignedBB box = new AxisAlignedBB(
                this.centerPos.getX() - r, this.centerPos.getY() - r, this.centerPos.getZ() - r,
                this.centerPos.getX() + r, this.centerPos.getY() + r, this.centerPos.getZ() + r
        );

        Predicate<LivingEntity> filter = e -> e != null
                && e.isAlive()
                && (this.applyEffectOnUser || !e.equals(owner))
                && this.isEntityInZone(e);

        List<LivingEntity> targets = owner.level.getEntitiesOfClass(LivingEntity.class, box, filter);

        if (this.applyEffectOnUser && owner.isAlive() && this.isEntityInZone(owner) && !targets.contains(owner)) {
            targets.add(owner);
        }

        Set<UUID> stillInside = new HashSet<>();
        for (LivingEntity target : targets) {
            stillInside.add(target.getUUID());
        }

        Iterator<UUID> it = this.affectedEntities.iterator();
        while (it.hasNext()) {
            UUID id = it.next();
            if (!stillInside.contains(id)) {
                LivingEntity left = findLivingEntityByUuid(owner, id);
                if (left != null) {
                    this.onEntityLeavesZone(owner, left);
                }
                it.remove();
            }
        }

        for (LivingEntity target : targets) {
            this.affectedEntities.add(target.getUUID());
            this.applyEffectToEntityInZone(owner, target);
        }
    }

    protected void applyEffectToEntityInZone(LivingEntity owner, LivingEntity target) {
        // Ex:
        // this.applyOrRefresh(target, ModEffects.MOVEMENT_BLOCKED.get(), 15, 0, false, false);
    }

    protected void onEntityLeavesZone(LivingEntity owner, LivingEntity target) {
        // Ex:
        // target.removeEffect(ModEffects.MOVEMENT_BLOCKED.get());
    }

    protected List<Effect> getZoneEffectsToClearOnEnd() {
        return new ArrayList<>();
    }

    protected void clearEffectsWhenZoneEnds(LivingEntity owner) {
        if (owner == null || owner.level == null || owner.level.isClientSide) return;

        for (UUID id : this.affectedEntities) {
            LivingEntity e = findLivingEntityByUuid(owner, id);
            if (e != null) {
                this.onEntityLeavesZone(owner, e);
                for (Effect effect : this.getZoneEffectsToClearOnEnd()) {
                    if (effect != null) e.removeEffect(effect);
                }
            }
        }

        this.affectedEntities.clear();
    }

    private LivingEntity findLivingEntityByUuid(LivingEntity owner, UUID id) {
        if (owner == null || owner.level == null || id == null) return null;

        // Côté Serveur (Là où la logique des capacités doit s'exécuter)
        if (owner.level instanceof ServerWorld) {
            Entity entity = ((ServerWorld) owner.level).getEntity(id);
            if (entity instanceof LivingEntity && entity.isAlive()) {
                return (LivingEntity) entity;
            }
        }
        return null;
    }

    protected void applyOrRefresh(LivingEntity target, Effect effect, int durationTicks, int amplifier, boolean ambient, boolean showParticles) {
        if (target == null || effect == null) return;
        // showIcon n’est pas dispo en 1\.16\.5, donc on gère juste particules + ambient
        target.addEffect(new EffectInstance(effect, durationTicks, amplifier, ambient, showParticles));
    }

    protected boolean ensureOnGroundOrNotify(LivingEntity user) {
        if (user == null) return false;
        if (user.isOnGround()) return true;
        if (!user.level.isClientSide && user instanceof PlayerEntity) {
            user.sendMessage(new TranslationTextComponent("awakenawakennomi.ability.ground_only"), user.getUUID());
        }
        return false;
    }

    static {
        GRIEF_RULE = (new BlockProtectionRule.Builder(new BlockProtectionRule[]{AirBlockProtectionRule.INSTANCE, FoliageBlockProtectionRule.INSTANCE, LiquidBlockProtectionRule.INSTANCE}))
                .addApprovedBlocks(new RegistryObject[]{ModBlocks.OPE})
                .setBypassGriefRule()
                .build();
    }
}