package com.inovactio.awakenawakennomi.effects;

import com.inovactio.awakenawakennomi.abilities.mogumogunomi.SubterraneanDashAbility;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.ForgeMod;
import xyz.pixelatedw.mineminenomi.abilities.sui.NekomimiPunchAbility;
import xyz.pixelatedw.mineminenomi.abilities.sui.NyanNyanSuplexAbility;
import xyz.pixelatedw.mineminenomi.api.abilities.IAbility;
import xyz.pixelatedw.mineminenomi.api.effects.ModEffect;
import xyz.pixelatedw.mineminenomi.api.helpers.AbilityHelper;
import xyz.pixelatedw.mineminenomi.api.helpers.DevilFruitHelper;
import xyz.pixelatedw.mineminenomi.api.protection.block.RestrictedBlockProtectionRule;
import xyz.pixelatedw.mineminenomi.data.entity.ability.AbilityDataCapability;
import xyz.pixelatedw.mineminenomi.data.entity.ability.IAbilityData;

public class GroundDigEffect extends ModEffect {

    public GroundDigEffect() {
        super(EffectType.NEUTRAL, 8954814);
    }

    public boolean disableParticles() {
        return false;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {

        entity.noPhysics = true;
        IAbilityData props = AbilityDataCapability.get(entity);

        boolean flying = false;
        if (entity instanceof PlayerEntity) {
            flying = ((PlayerEntity) entity).abilities.flying;
        }

        BlockPos pos = entity.blockPosition();
        boolean isMidAir = entity.level.getBlockState(pos.above()).isAir() && entity.level.getBlockState(pos.below()).isAir();
        boolean groundCheck = entity.position().y - DevilFruitHelper.getFloorLevel(entity).y() > 0.0D && entity.level.getBlockState(pos.below()).isAir();

        SubterraneanDashAbility subterreanDash = null;
        if (props != null) {
            IAbility a = props.getEquippedAbility(SubterraneanDashAbility.INSTANCE);
            if (a instanceof SubterraneanDashAbility) subterreanDash = (SubterraneanDashAbility) a;
        }
        boolean isSubterreanDashActive = subterreanDash != null && subterreanDash.isContinuous();
        boolean isSubterreanDashFresh = subterreanDash != null && entity.level.getGameTime() - subterreanDash.getLastUseGametime() < 100L;
        boolean canMove = AbilityHelper.canUseMomentumAbilities(entity);

        boolean isOutsideGround = false;
        if (groundCheck && !isSubterreanDashActive && !isSubterreanDashFresh && !isEntityInsideOpaqueBlock(entity)) {
            isOutsideGround = true;
        }

        if (isOutsideGround) {
            entity.noPhysics = false;
            return;
        }

        boolean isInsideBlock = isEntityInsideOpaqueBlock(entity);
        if ((isInsideBlock || entity.isSprinting()) && !flying) {
            AbilityHelper.setPose(entity, Pose.SWIMMING);
            entity.fallDistance = 0.0F;
        }

        if (isMidAir && !isSubterreanDashActive) {
            entity.noPhysics = true;
            return;
        }
        entity.setSwimming(true);
        boolean swimming = entity.getPose() == Pose.SWIMMING || isSubterreanDashActive;

        if (swimming && (isInsideBlock || (isMidAir && isSubterreanDashActive))) {
            double x = 0.0D, y = 0.0D, z = 0.0D;
            double swimSpeed = entity.getAttribute(ForgeMod.SWIM_SPEED.get()).getValue() / 2.0D;
            Vector3d lookVector = entity.getLookAngle();

            if (entity.zza != 0.0F && canMove) {
                double speed = Math.max(1.3D, Math.min(swimSpeed, 6.0D));
                x = lookVector.x * speed * entity.zza;
                y = lookVector.y * speed * entity.zza;
                z = lookVector.z * speed * entity.zza;
            }

            if (isSubterreanDashActive) {
                double speed = 1.6D;
                x = lookVector.x * speed;
                y = lookVector.y * speed;
                z = lookVector.z * speed;
            }

            if (entity.isShiftKeyDown()) {
                y = -0.2D;
            } else if (AbilityHelper.isJumping(entity) && !entity.level.getBlockState(pos.below()).isAir()) {
                y = 0.2D;
                if (entity.level.getBlockState(pos.above()).isAir()) y = 0.6D;
            }

            BlockPos frontPos = new BlockPos(entity.position().add(x, y + entity.getEyeHeight(), z));
            BlockState frontBlock = entity.level.getBlockState(frontPos);
            if (RestrictedBlockProtectionRule.INSTANCE.isBanned(frontBlock)) {
                Vector3d reversedLook = entity.zza < 0.0F ? lookVector : lookVector.reverse();
                x = reversedLook.x;
                y = reversedLook.y;
                z = reversedLook.z;
            }

            if (entity.getY() < 5.0D) y = 0.0D;

            AbilityHelper.setDeltaMovement(entity, x, y, z);
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    private boolean isEntityInsideOpaqueBlock(LivingEntity entity) {
        BlockPos.Mutable blockPos = new BlockPos.Mutable();

        for (int i = 0; i < 8; ++i) {
            int j = MathHelper.floor(entity.getY() + (((float) ((i >> 0) % 2) - 0.5F) * 0.1F) + entity.getEyeHeight());
            int k = MathHelper.floor(entity.getX() + (((float) ((i >> 1) % 2) - 0.5F) * entity.getBbHeight() * 0.8F));
            int l = MathHelper.floor(entity.getZ() + (((float) ((i >> 2) % 2) - 0.5F) * entity.getBbWidth() * 0.8F));
            if (blockPos.getX() != k || blockPos.getY() != j || blockPos.getZ() != l) {
                blockPos.set(k, j, l);
                boolean isSolid = entity.level.getBlockState(blockPos).isFaceSturdy(entity.level, blockPos, Direction.UP);
                boolean isLiquid = entity.level.getFluidState(blockPos).isSource();
                if (isSolid || isLiquid) {
                    return true;
                }
            }
        }

        return false;
    }
}
