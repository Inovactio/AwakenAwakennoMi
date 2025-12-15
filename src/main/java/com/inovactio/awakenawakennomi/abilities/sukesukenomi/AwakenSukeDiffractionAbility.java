package com.inovactio.awakenawakennomi.abilities.sukesukenomi;

import com.inovactio.awakenawakennomi.entities.projectiles.suke.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import org.apache.commons.lang3.tuple.ImmutablePair;
import xyz.pixelatedw.mineminenomi.abilities.goro.ElThorAbility;
import xyz.pixelatedw.mineminenomi.api.abilities.*;
import com.inovactio.awakenawakennomi.api.abilities.IAwakenable;
import xyz.pixelatedw.mineminenomi.api.abilities.components.*;
import xyz.pixelatedw.mineminenomi.api.damagesource.SourceElement;
import xyz.pixelatedw.mineminenomi.api.damagesource.SourceHakiNature;
import xyz.pixelatedw.mineminenomi.api.helpers.AbilityHelper;
import xyz.pixelatedw.mineminenomi.api.math.VectorHelper;
import xyz.pixelatedw.mineminenomi.data.entity.devilfruit.DevilFruitCapability;
import xyz.pixelatedw.mineminenomi.entities.projectiles.goro.LightningEntity;
import xyz.pixelatedw.mineminenomi.init.ModAbilities;
import xyz.pixelatedw.mineminenomi.init.ModAnimations;
import xyz.pixelatedw.mineminenomi.init.ModParticleEffects;
import xyz.pixelatedw.mineminenomi.init.ModSounds;
import xyz.pixelatedw.mineminenomi.particles.effects.ParticleEffect;
import xyz.pixelatedw.mineminenomi.wypi.WyHelper;

/**
 * Minimal Awaken ability placeholder for Suke Suke no Mi (diffraction).
 * No runtime logic here — this class only provides the basic ability definition
 * so you can implement the behavior elsewhere or later.
 */
public class AwakenSukeDiffractionAbility extends Ability implements IAwakenable {

    private static final ITextComponent[] DESCRIPTION = AbilityHelper.registerDescriptionText("awakenawakennomi", "awaken_suke_diffraction",
            new ImmutablePair[]{ImmutablePair.of("Awaken Diffraction (placeholder)", (Object)null)});
    private static final float COOLDOWN = 200.0F;
    private static final float PROJECTILE_SPEED = 3.0F;
    private static final float PROJECTILE_INACCURACY = 0.25F;
    private static final int CHARGE_TIME = 25;
    public static final AbilityCore<AwakenSukeDiffractionAbility> INSTANCE;
    private final ChargeComponent chargeComponent = (new ChargeComponent(this)).addEndEvent(this::stopChargeEvent);
    private final ContinuousComponent continuousComponent = (new ContinuousComponent(this, true)).addStartEvent(this::onContinuityStart);
    private final RepeaterComponent repeaterComponent = (new RepeaterComponent(this)).addTriggerEvent(this::onRepeaterTrigger).addStopEvent(this::onRepeaterStop);
    private final ProjectileComponent projectileComponent = new ProjectileComponent(this, this::createProjectile);
    private int ProjectileCount = 0;

    public AwakenSukeDiffractionAbility(AbilityCore<AwakenSukeDiffractionAbility> core) {
        super(core);
        this.isNew = true;
        this.addComponents(new AbilityComponent[]{this.chargeComponent,this.continuousComponent, this.repeaterComponent, this.projectileComponent});
        this.addCanUseCheck(SukeHelper::canUseInvisibleAbility);
        this.addUseEvent(this::useEvent);
    }

    private void useEvent(LivingEntity entity, IAbility ability) {
        if(this.continuousComponent.isContinuous())
        {
            this.repeaterComponent.stop(entity);
        }
        this.chargeComponent.startCharging(entity, CHARGE_TIME);

    }

    private void stopChargeEvent(LivingEntity entity, IAbility ability) {
            this.continuousComponent.triggerContinuity(entity);
    }

    private void onContinuityStart(LivingEntity entity, IAbility ability) {
        if (!entity.level.isClientSide) {
            this.repeaterComponent.start(entity, 7, 10);
        }
    }

    private void onRepeaterTrigger(LivingEntity entity, IAbility ability) {
        if (super.canUse(entity).isFail()) {
            this.repeaterComponent.stop(entity);
        }
        entity.level.playSound((PlayerEntity)null, entity.blockPosition(), (SoundEvent) ModSounds.PIKA_SFX.get(), SoundCategory.PLAYERS, 2.0F, 1.0F);
        this.projectileComponent.shoot(entity, PROJECTILE_SPEED, PROJECTILE_INACCURACY);
    }

    private void onRepeaterStop(LivingEntity entity, IAbility ability) {
        ProjectileCount = 0;
        this.continuousComponent.stopContinuity(entity);
        super.cooldownComponent.startCooldown(entity, COOLDOWN);
    }


    private DiffractionProjectile createProjectile(LivingEntity entity) {
        switch (ProjectileCount)
        {
            case 0:
                ProjectileCount++;
                return new DiffractionProjectileBlue(entity.level, entity, this);
            case 1:
                ProjectileCount++;
                return new DiffractionProjectileIndigo(entity.level, entity, this);
            case 2:
                ProjectileCount++;
                return new DiffractionProjectileViolet(entity.level, entity, this);
            case 3:
                ProjectileCount++;
                return new DiffractionProjectileRed(entity.level, entity, this);
            case 4:
                ProjectileCount++;
                return new DiffractionProjectileOrange(entity.level, entity, this);
            case 5:
                ProjectileCount++;
                return new DiffractionProjectileYellow(entity.level, entity, this);
            case 6:
                ProjectileCount=0;
                return new DiffractionProjectileGreen(entity.level, entity, this);
            default:
                return new DiffractionProjectileBlue(entity.level, entity, this);
        }
    }

    protected static boolean canUnlock(LivingEntity user) {
        return DevilFruitCapability.get(user).hasAwakenedFruit()
                && DevilFruitCapability.get(user).hasDevilFruit(ModAbilities.SUKE_SUKE_NO_MI);
    }

    @Override
    public boolean AwakenUnlock(LivingEntity user) {
        return canUnlock(user);
    }

    static {
        INSTANCE = new AbilityCore.Builder<>("Awaken Suke Diffraction", AbilityCategory.DEVIL_FRUITS, AwakenSukeDiffractionAbility::new)
                .setUnlockCheck(AwakenSukePunchAbility::canUnlock)
                .addDescriptionLine(DESCRIPTION)
                .addAdvancedDescriptionLine(new AbilityDescriptionLine.IDescriptionLine[]{AbilityDescriptionLine.NEW_LINE, CooldownComponent.getTooltip(COOLDOWN)})
                .addAdvancedDescriptionLine(ChargeComponent.getTooltip(CHARGE_TIME))
                .addAdvancedDescriptionLine(ProjectileComponent.getProjectileTooltips())
                .setSourceHakiNature(SourceHakiNature.SPECIAL)
                .setSourceElement(SourceElement.LIGHT)
                .setIcon(new ResourceLocation("awakenawakennomi", "textures/abilities/awaken_suke_diffraction.png"))
                .build();
    }
}
