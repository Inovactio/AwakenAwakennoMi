package com.inovactio.awakenawakennomi.effects.sliding;

import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import xyz.pixelatedw.mineminenomi.api.effects.ModEffect;
import xyz.pixelatedw.mineminenomi.api.helpers.AbilityHelper;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class SlidingEffect extends ModEffect {

    private final double slidingPower; // > 1.0 pour "pousser" la vitesse
    private final double maxSpeed;

    private final boolean stepHeightEnabled;
    private final boolean preventJumpEnabled;

    private final Map<UUID, Float> originalStepHeights = new ConcurrentHashMap<>();

    protected SlidingEffect(EffectType typeIn, int liquidColorIn, double slidingPower, double maxSpeed) {
        this(typeIn, liquidColorIn, slidingPower, maxSpeed, true, true);
    }

    protected SlidingEffect(EffectType typeIn, int liquidColorIn, double slidingPower, double maxSpeed,
                            boolean stepHeightEnabled, boolean preventJumpEnabled) {
        super(typeIn, liquidColorIn);
        this.slidingPower = slidingPower;
        this.maxSpeed = maxSpeed;
        this.stepHeightEnabled = stepHeightEnabled;
        this.preventJumpEnabled = preventJumpEnabled;
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity == null || !entity.isAlive()) return;

        if (this.stepHeightEnabled) {
            UUID id = entity.getUUID();
            this.originalStepHeights.putIfAbsent(id, entity.maxUpStep);
            entity.maxUpStep = 1.0F;
        }

        if (this.preventJumpEnabled) {
            Vector3d v0 = entity.getDeltaMovement();
            if (v0.y > 0.0D) {
                AbilityHelper.setDeltaMovement(entity, v0.x, 0.0D, v0.z);
            }
            entity.setJumping(false);
        }

        if (!entity.isOnGround()) return;

        Vector3d v = entity.getDeltaMovement();
        double vx = v.x();
        double vz = v.z();

        if (Math.abs(vx) < 0.2D || Math.abs(vz) < 0.2D) {
            double x = MathHelper.clamp(vx * this.slidingPower, -this.maxSpeed, this.maxSpeed);
            double z = MathHelper.clamp(vz * this.slidingPower, -this.maxSpeed, this.maxSpeed);
            AbilityHelper.setDeltaMovement(entity, x, v.y(), z);
        }
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, net.minecraft.entity.ai.attributes.AttributeModifierManager manager, int amplifier) {
        if (entity != null && this.stepHeightEnabled) {
            UUID id = entity.getUUID();
            Float original = this.originalStepHeights.remove(id);
            if (original != null) {
                entity.maxUpStep = original;
            }
        }
        super.removeAttributeModifiers(entity, manager, amplifier);
    }
}
