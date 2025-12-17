package com.inovactio.awakenawakennomi.abilities.bomubomunomi;

import com.inovactio.awakenawakennomi.api.abilities.IAwakenable;
import com.inovactio.awakenawakennomi.util.ToolTipHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.particles.ParticleTypes;
import org.apache.commons.lang3.tuple.ImmutablePair;
import xyz.pixelatedw.mineminenomi.api.abilities.*;
import xyz.pixelatedw.mineminenomi.api.damagesource.SourceElement;
import xyz.pixelatedw.mineminenomi.api.helpers.AbilityHelper;
import xyz.pixelatedw.mineminenomi.api.abilities.ExplosionAbility;
import net.minecraft.util.math.BlockPos;
import xyz.pixelatedw.mineminenomi.api.abilities.components.ContinuousComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.components.CooldownComponent;
import xyz.pixelatedw.mineminenomi.data.entity.devilfruit.DevilFruitCapability;
import xyz.pixelatedw.mineminenomi.init.ModAbilities;

import java.util.Random;

public class AwakenBomuAirburstAbility extends Ability implements IAwakenable {

    private static final ITextComponent[] DESCRIPTION =
            AbilityHelper.registerDescriptionText("awakenawakennomi", "awaken_bomu_airburst",
                    ImmutablePair.of("Creates explosive bursts of air around the user.", null));

    public static final AbilityCore<AwakenBomuAirburstAbility> INSTANCE;

    // Parameters
    private static final int TICK_INTERVAL = 20; // ticks between checks
    private static final double CHANCE_PER_TICK = 0.25; // chance to spawn an explosion each interval
    private static final double RADIUS = 24.0; // spawn radius
    private static final float POWER = 2; // explosion power
    private static final float EXPLOSION_SIZE = 32;
    private static final float STATIC_DAMAGE = 30;
    private static final double VERTICAL_RANGE = 6.0;

    private final ContinuousComponent continuousComponent = (new ContinuousComponent(this, true))
            .addTickEvent(TICK_INTERVAL, this::onTick)
            .addEndEvent(90, this::onEndContinuity);

    private final Random random = new Random();

    public AwakenBomuAirburstAbility(AbilityCore<AwakenBomuAirburstAbility> core) {
        super(core);
        this.isNew = true;
        this.addComponents(this.continuousComponent);
        this.addUseEvent(100, (ent, abl) -> {
            if (this.continuousComponent.isContinuous()) {
                this.continuousComponent.stopContinuity(ent);
            } else {
                this.continuousComponent.startContinuity(ent, 600);
            }
        });
    }

    private void onTick(LivingEntity entity, IAbility ability) {
        World world = entity.level;
        if (world instanceof ServerWorld) {
            if (random.nextDouble() <= CHANCE_PER_TICK) {
                double ox = (random.nextDouble() * 2.0 - 1.0) * RADIUS;
                double oz = (random.nextDouble() * 2.0 - 1.0) * RADIUS;
                double x = entity.getX() + ox;
                double yOffset = (random.nextDouble() * 2.0 - 1.0) * VERTICAL_RANGE;
                double y = entity.getY() + yOffset;
                double z = entity.getZ() + oz;
                ServerWorld sw = (ServerWorld) world;
                int maxH = sw.getMaxBuildHeight();
                if (y < 1.0) y = 1.0;
                if (y > maxH - 1) y = maxH - 1;
                BlockPos targetPos = new BlockPos((int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z));
                if (!sw.isEmptyBlock(targetPos)) return;

                ExplosionAbility ex = new ExplosionAbility(entity, sw, x, y, z, POWER);
                ex.setExplosionSize(EXPLOSION_SIZE);
                ex.setStaticDamage(STATIC_DAMAGE);
                ex.setDestroyBlocks(false);
                ex.setDropBlocksAfterExplosion(false);
                ex.setFireAfterExplosion(false);
                ex.setDamageOwner(false);
                ex.disableExplosionKnockback();
                ex.setExplosionSound(true);

                ex.doExplosion();

                sw.sendParticles(ParticleTypes.EXPLOSION_EMITTER, x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
                sw.sendParticles(ParticleTypes.CLOUD, x, y, z, 24, 2.0, 1.0, 2.0, 0.05);
            }
        }
    }

    private void onEndContinuity(LivingEntity entity, IAbility ability) {
        if (!entity.level.isClientSide) {
            this.cooldownComponent.startCooldown(entity, 600);
        }
    }

    private static boolean canUnlock(LivingEntity user) {
        return DevilFruitCapability.get(user).hasAwakenedFruit()
                && DevilFruitCapability.get(user).hasDevilFruit(ModAbilities.BOMU_BOMU_NO_MI);
    }

    @Override
    public boolean AwakenUnlock(LivingEntity user) {
        return canUnlock(user);
    }

    static {
        INSTANCE = new AbilityCore.Builder<>("AwakenBomuAirburst", AbilityCategory.DEVIL_FRUITS, AwakenBomuAirburstAbility::new)
                .setUnlockCheck(AwakenBomuAirburstAbility::canUnlock)
                .addDescriptionLine(DESCRIPTION)
                .addAdvancedDescriptionLine(new AbilityDescriptionLine.IDescriptionLine[]{AbilityDescriptionLine.NEW_LINE, CooldownComponent.getTooltip(600.0F)})
                .addAdvancedDescriptionLine(ToolTipHelper.getExplosionTooltips((int)POWER, (int)EXPLOSION_SIZE, (int)STATIC_DAMAGE))
                .setSourceElement(SourceElement.EXPLOSION)
                .setIcon(new ResourceLocation("awakenawakennomi", "textures/abilities/awaken_bomu_airburst.png"))
                .build();
    }
}

