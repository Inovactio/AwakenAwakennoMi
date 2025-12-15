package com.inovactio.awakenawakennomi.abilities.sukesukenomi;

import com.inovactio.awakenawakennomi.entities.projectiles.suke.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import org.apache.commons.lang3.tuple.ImmutablePair;
import xyz.pixelatedw.mineminenomi.api.abilities.*;
import com.inovactio.awakenawakennomi.api.abilities.IAwakenable;
import xyz.pixelatedw.mineminenomi.api.abilities.components.*;
import xyz.pixelatedw.mineminenomi.api.helpers.AbilityHelper;
import xyz.pixelatedw.mineminenomi.data.entity.devilfruit.DevilFruitCapability;
import xyz.pixelatedw.mineminenomi.init.ModAbilities;
import xyz.pixelatedw.mineminenomi.init.ModAnimations;

/**
 * Minimal Awaken ability placeholder for Suke Suke no Mi (diffraction).
 * No runtime logic here — this class only provides the basic ability definition
 * so you can implement the behavior elsewhere or later.
 */
public class AwakenSukeDiffractionAbility extends Ability implements IAwakenable {

    private static final ITextComponent[] DESCRIPTION = AbilityHelper.registerDescriptionText("awakenawakennomi", "awaken_suke_diffraction",
            new ImmutablePair[]{ImmutablePair.of("Awaken Diffraction (placeholder)", (Object)null)});
    private static final float COOLDOWN = 160.0F;
    private static final float PROJECTILE_SPEED = 2.0F;
    private static final float PROJECTILE_INACCURACY = 1.0F;
    public static final AbilityCore<AwakenSukeDiffractionAbility> INSTANCE;
    private final ContinuousComponent continuousComponent = (new ContinuousComponent(this, true)).addStartEvent(this::onContinuityStart);
    private final AnimationComponent animationComponent = new AnimationComponent(this);
    private final RepeaterComponent repeaterComponent = (new RepeaterComponent(this)).addTriggerEvent(this::onRepeaterTrigger).addStopEvent(this::onRepeaterStop);
    private final ProjectileComponent projectileComponent = new ProjectileComponent(this, this::createProjectile);
    private int ProjectileCount = 0;

    public AwakenSukeDiffractionAbility(AbilityCore<AwakenSukeDiffractionAbility> core) {
        super(core);
        this.isNew = true;
        this.addComponents(new AbilityComponent[]{this.continuousComponent, this.repeaterComponent,this.animationComponent, this.projectileComponent});
        this.addUseEvent(this::useEvent);
    }

    private void useEvent(LivingEntity entity, IAbility ability) {
        this.animationComponent.start(entity, ModAnimations.AIM_SNIPER, 6);
        if(this.continuousComponent.isContinuous())
        {
            this.repeaterComponent.stop(entity);
        }else{
            this.continuousComponent.triggerContinuity(entity);
        }
    }

    private void onContinuityStart(LivingEntity entity, IAbility ability) {
        if (!entity.level.isClientSide) {
            this.animationComponent.start(entity, ModAnimations.POINT_RIGHT_ARM);
            this.repeaterComponent.start(entity, 7, 10);
        }
    }

    private void onRepeaterTrigger(LivingEntity entity, IAbility ability) {
        if (super.canUse(entity).isFail()) {
            this.repeaterComponent.stop(entity);
        }

        this.projectileComponent.shoot(entity, PROJECTILE_SPEED, PROJECTILE_INACCURACY);
    }

    private void onRepeaterStop(LivingEntity entity, IAbility ability) {
        ProjectileCount = 0;
        this.continuousComponent.stopContinuity(entity);
        this.animationComponent.stop(entity);
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
                .addAdvancedDescriptionLine(new AbilityDescriptionLine.IDescriptionLine[]{AbilityDescriptionLine.NEW_LINE})
                .setIcon(new ResourceLocation("awakenawakennomi", "textures/abilities/awaken_suke_diffraction.png"))
                .build();
    }
}
