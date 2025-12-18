package com.inovactio.awakenawakennomi.abilities.dekadekanomi;

import com.inovactio.awakenawakennomi.api.abilities.IAwakenable;
import com.inovactio.awakenawakennomi.init.ModMorphs;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import xyz.pixelatedw.mineminenomi.api.abilities.*;
import xyz.pixelatedw.mineminenomi.api.abilities.components.AbilityComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.components.DealDamageComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.components.RangeComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.components.RequireMorphComponent;
import xyz.pixelatedw.mineminenomi.api.damagesource.SourceHakiNature;
import xyz.pixelatedw.mineminenomi.api.damagesource.SourceType;
import xyz.pixelatedw.mineminenomi.api.helpers.AbilityHelper;
import xyz.pixelatedw.mineminenomi.api.helpers.MorphHelper;
import xyz.pixelatedw.mineminenomi.api.morph.MorphInfo;
import xyz.pixelatedw.mineminenomi.api.protection.block.FoliageBlockProtectionRule;
import xyz.pixelatedw.mineminenomi.data.entity.devilfruit.DevilFruitCapability;
import xyz.pixelatedw.mineminenomi.init.ModAbilities;
import xyz.pixelatedw.mineminenomi.init.ModParticleEffects;
import xyz.pixelatedw.mineminenomi.particles.effects.BreakingBlocksParticleEffect;
import xyz.pixelatedw.mineminenomi.particles.effects.ParticleEffect;
import xyz.pixelatedw.mineminenomi.wypi.WyHelper;

import java.util.ArrayList;
import java.util.List;

public class TitanTrampleAbility extends PassiveAbility2 implements IAwakenable {
    private static final ITextComponent[] DESCRIPTION = AbilityHelper.registerDescriptionText("awakenawakennomi", "titan_trample", new Pair[]{ImmutablePair.of("Running speed increases with acceleration trampling any nearby entity.", (Object)null)});
    private static final float TRAMPLE_AREA = 10.0F;
    private static final int BLOCK_BREAKING_AREA = 7;
    private static final float MAX_SPEED = 0.90F;
    private static final float DAMAGE = 24.0F;
    public static final AbilityCore<TitanTrampleAbility> INSTANCE;
    private final RangeComponent rangeComponent = new RangeComponent(this);
    private final DealDamageComponent dealDamageComponent = new DealDamageComponent(this);
    private final RequireMorphComponent requireMorphComponent;
    private BreakingBlocksParticleEffect.Details details;
    public float speed;

    public TitanTrampleAbility(AbilityCore<TitanTrampleAbility> ability) {
        super(ability);
        this.requireMorphComponent = new RequireMorphComponent(this, (MorphInfo) ModMorphs.AWAKEN_DEKA.get(), new MorphInfo[0]);
        this.details = new BreakingBlocksParticleEffect.Details(100);
        this.speed = 0.0F;
        this.addComponents(new AbilityComponent[]{this.rangeComponent, this.dealDamageComponent, this.requireMorphComponent});
        this.addDuringPassiveEvent(this::duringPassiveEvent);
    }

    public void duringPassiveEvent(LivingEntity entity) {
        if (entity.isOnGround()) {
            if (MorphHelper.getZoanInfo(entity) == com.inovactio.awakenawakennomi.init.ModMorphs.AWAKEN_DEKA.get()) {
                if (!entity.isSprinting()) {
                    this.speed = 0.0F;
                } else {
                    List<LivingEntity> targets = this.rangeComponent.getTargetsInArea(entity, entity.blockPosition(), TRAMPLE_AREA);
                    float acceleration = 0.004F;
                    acceleration *= this.speed > 0.0F ? 1.0F - this.speed / MAX_SPEED : 1.0F;
                    if (!(entity.zza > 0.0F) || entity.horizontalCollision) {
                        acceleration = -0.044999998F;
                    }

                    this.speed = MathHelper.clamp(this.speed + acceleration, 0.2F, MAX_SPEED);
                    int d2 = entity.zza > 0.0F ? 1 : 0;
                    Vector3d vec = entity.getLookAngle();
                    double x = vec.x * (double)this.speed * (double)d2;
                    double z = vec.z * (double)this.speed * (double)d2;
                    AbilityHelper.setDeltaMovement(entity, x, entity.getDeltaMovement().y, z);
                    if (!entity.level.isClientSide) {
                        List<BlockPos> blocks = WyHelper.getNearbyBlocks(entity.blockPosition(), entity.level, BLOCK_BREAKING_AREA, BLOCK_BREAKING_AREA, BLOCK_BREAKING_AREA, (state) -> !state.getMaterial().equals(Material.AIR) && FoliageBlockProtectionRule.INSTANCE.isApproved(state));
                        List<BlockPos> positions = new ArrayList();

                        for(BlockPos pos : blocks) {
                            if (AbilityHelper.placeBlockIfAllowed(entity, pos, Blocks.AIR.defaultBlockState(), FoliageBlockProtectionRule.INSTANCE)) {
                                positions.add(pos);
                            }
                        }

                        if (positions.size() > 0) {
                            this.details.setPositions(positions);
                            WyHelper.spawnParticleEffect((ParticleEffect) ModParticleEffects.BREAKING_BLOCKS.get(), entity, (double)0.0F, (double)0.0F, (double)0.0F, this.details);
                        }

                        for(LivingEntity target : targets) {
                            if (this.dealDamageComponent.hurtTarget(entity, target, DAMAGE)) {
                                Vector3d speed = WyHelper.propulsion(entity, (double)2.0F, (double)2.0F);
                                AbilityHelper.setDeltaMovement(target, speed.x, 1, speed.z);
                            }
                        }
                    }
                }

            }
        }
    }

    private static boolean canUnlock(LivingEntity user) {
        return DevilFruitCapability.get(user).hasAwakenedFruit()
                && DevilFruitCapability.get(user).hasDevilFruit(ModAbilities.DEKA_DEKA_NO_MI);
    }

    @Override
    public boolean AwakenUnlock(LivingEntity user) {
        return canUnlock(user);
    }

    static {
        INSTANCE = new AbilityCore.Builder<TitanTrampleAbility>("Titan Trample", AbilityCategory.DEVIL_FRUITS, AbilityType.PASSIVE, TitanTrampleAbility::new)
                .addDescriptionLine(DESCRIPTION)
                .addDescriptionLine(new AbilityDescriptionLine.IDescriptionLine[]{AbilityDescriptionLine.NEW_LINE, RequireMorphComponent.getTooltip()})
                .addAdvancedDescriptionLine(new AbilityDescriptionLine.IDescriptionLine[]{AbilityDescriptionLine.NEW_LINE, RangeComponent.getTooltip(5.0F, RangeComponent.RangeType.AOE), DealDamageComponent.getTooltip(8.0F)})
                .setSourceHakiNature(SourceHakiNature.HARDENING).setSourceType(new SourceType[]{SourceType.FIST})
                .setUnlockCheck(TitanTrampleAbility::canUnlock)
                .setIcon(new ResourceLocation("awakenawakennomi", "textures/abilities/deka/titan_trample.png"))
                .build();
    }
}
