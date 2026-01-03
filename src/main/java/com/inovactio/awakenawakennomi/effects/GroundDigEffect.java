package com.inovactio.awakenawakennomi.effects;

import com.inovactio.awakenawakennomi.abilities.mogumogunomi.SubterraneanDashAbility;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectType;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.ToolType;
import xyz.pixelatedw.mineminenomi.abilities.sui.NekomimiPunchAbility;
import xyz.pixelatedw.mineminenomi.abilities.sui.NyanNyanSuplexAbility;
import xyz.pixelatedw.mineminenomi.api.abilities.IAbility;
import xyz.pixelatedw.mineminenomi.api.effects.ModEffect;
import xyz.pixelatedw.mineminenomi.api.helpers.AbilityHelper;
import xyz.pixelatedw.mineminenomi.api.helpers.DevilFruitHelper;
import xyz.pixelatedw.mineminenomi.api.protection.block.RestrictedBlockProtectionRule;
import xyz.pixelatedw.mineminenomi.data.entity.ability.AbilityDataCapability;
import xyz.pixelatedw.mineminenomi.data.entity.ability.IAbilityData;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public class GroundDigEffect extends ModEffect {

    protected static final Map<LivingEntity, BlockPos> lastBlockMap = Collections.synchronizedMap(new WeakHashMap<>());

    protected final boolean dig;

    protected GroundDigEffect(boolean dig) {
        super(EffectType.BENEFICIAL, 8954814);
        this.dig = dig;
    }

    public GroundDigEffect() {
        this(false);
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
        boolean groundCheck = entity.position().y - DevilFruitHelper.getFloorLevel(entity).y() > 0.0D
                && entity.level.getBlockState(pos.below()).isAir();

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

        if (this.dig) {
            BlockPos currentBlock = entity.blockPosition();
            BlockPos prevBlock = lastBlockMap.get(entity);

            if (prevBlock != null && !prevBlock.equals(currentBlock)) {
                digAlongPath(entity, prevBlock, currentBlock);
            }

            lastBlockMap.put(entity, currentBlock);
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
            double digSpeed = entity.getAttribute(Attributes.MOVEMENT_SPEED).getValue() / 5.0D;
            Vector3d lookVector = entity.getLookAngle();

            if (entity.zza != 0.0F && canMove) {
                double speed = Math.max(0.65D, Math.min(digSpeed, 3.0D));
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
    public void stopEffect(LivingEntity entity) {
        lastBlockMap.remove(entity);
        super.stopEffect(entity);
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

    private void digAlongPath(LivingEntity entity, BlockPos from, BlockPos to) {
        final int maxSteps = 24;

        int x1 = from.getX();
        int y1 = from.getY();
        int z1 = from.getZ();
        int x2 = to.getX();
        int y2 = to.getY();
        int z2 = to.getZ();

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int dz = Math.abs(z2 - z1);

        int xs = x2 > x1 ? 1 : -1;
        int ys = y2 > y1 ? 1 : -1;
        int zs = z2 > z1 ? 1 : -1;

        int x = x1, y = y1, z = z1;

        BlockPos.Mutable cursor = new BlockPos.Mutable();
        int steps = 0;

        // Petite sécurité : ne jamais casser le bloc "to" (bloc actuel du joueur).
        // On va donc itérer, mais sans le "tryDigBlock" final sur (x2, y2, z2).

        if (dx >= dy && dx >= dz) {
            int p1 = 2 * dy - dx;
            int p2 = 2 * dz - dx;

            while (x != x2 && steps++ < maxSteps) {
                cursor.set(x, y, z);
                // Ne casse pas si c'est déjà la destination (par sécurité)
                if (!cursor.equals(to)) {
                    tryDigBlock(entity, cursor);
                }

                x += xs;

                if (p1 >= 0) {
                    y += ys;
                    p1 -= 2 * dx;
                }
                if (p2 >= 0) {
                    z += zs;
                    p2 -= 2 * dx;
                }

                p1 += 2 * dy;
                p2 += 2 * dz;
            }
        } else if (dy >= dx && dy >= dz) {
            int p1 = 2 * dx - dy;
            int p2 = 2 * dz - dy;

            while (y != y2 && steps++ < maxSteps) {
                cursor.set(x, y, z);
                if (!cursor.equals(to)) {
                    tryDigBlock(entity, cursor);
                }

                y += ys;

                if (p1 >= 0) {
                    x += xs;
                    p1 -= 2 * dy;
                }
                if (p2 >= 0) {
                    z += zs;
                    p2 -= 2 * dy;
                }

                p1 += 2 * dx;
                p2 += 2 * dz;
            }
        } else {
            int p1 = 2 * dy - dz;
            int p2 = 2 * dx - dz;

            while (z != z2 && steps++ < maxSteps) {
                cursor.set(x, y, z);
                if (!cursor.equals(to)) {
                    tryDigBlock(entity, cursor);
                }

                z += zs;

                if (p1 >= 0) {
                    y += ys;
                    p1 -= 2 * dz;
                }
                if (p2 >= 0) {
                    x += xs;
                    p2 -= 2 * dz;
                }

                p1 += 2 * dy;
                p2 += 2 * dx;
            }
        }
    }

    private void tryDigBlock(LivingEntity entity, BlockPos pos) {
        if (entity.level == null || entity.level.isClientSide) return;

        BlockState state = entity.level.getBlockState(pos);
        if (state.isAir()) return;
        if (RestrictedBlockProtectionRule.INSTANCE.isBanned(state)) return;

        ToolType tool = state.getHarvestTool();
        if (tool != ToolType.PICKAXE && tool != ToolType.SHOVEL) {
            return;
        }

        float hardness = state.getDestroySpeed(entity.level, pos);
        if (hardness < 0.0F) return; // indestructible

        if (entity.level instanceof ServerWorld) {
            ServerWorld server = (ServerWorld) entity.level;

            server.levelEvent(2001, pos, net.minecraft.block.Block.getId(state));

            server.destroyBlock(pos, true);
        }
    }

    @Override
    public boolean isRemoveable() {
        return false;
    }

    @Override
    public boolean shouldRender(net.minecraft.potion.EffectInstance effect) {
        return false; // masque l'icône (inventaire + HUD)
    }

    @Override
    public boolean shouldRenderInvText(net.minecraft.potion.EffectInstance effect) {
        return false; // masque le texte dans l'inventaire
    }

    @Override
    public boolean shouldRenderHUD(net.minecraft.potion.EffectInstance effect) {
        return false; // masque l'affichage HUD (en haut à droite)
    }
}
