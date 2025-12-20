package com.inovactio.awakenawakennomi.abilities.sukesukenomi;

import com.inovactio.awakenawakennomi.entities.projectiles.suke.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import org.apache.commons.lang3.tuple.ImmutablePair;
import xyz.pixelatedw.mineminenomi.api.abilities.*;
import com.inovactio.awakenawakennomi.api.abilities.IAwakenable;
import xyz.pixelatedw.mineminenomi.api.abilities.components.*;
import xyz.pixelatedw.mineminenomi.api.damagesource.SourceElement;
import xyz.pixelatedw.mineminenomi.api.damagesource.SourceHakiNature;
import xyz.pixelatedw.mineminenomi.api.helpers.AbilityHelper;
import xyz.pixelatedw.mineminenomi.data.entity.devilfruit.DevilFruitCapability;
import xyz.pixelatedw.mineminenomi.init.ModAbilities;
import xyz.pixelatedw.mineminenomi.init.ModSounds;

public class DiffractionAbility extends Ability implements IAwakenable {

    private static final ITextComponent[] DESCRIPTION = AbilityHelper.registerDescriptionText("awakenawakennomi", "diffraction",
            ImmutablePair.of("Diffraction (placeholder)", null));
    private static final float COOLDOWN = 200.0F;
    private static final float PROJECTILE_SPEED = 3.0F;
    private static final float PROJECTILE_INACCURACY = 0.25F;
    private static final int CHARGE_TIME = 25;
    public static final AbilityCore<DiffractionAbility> INSTANCE;
    private final ChargeComponent chargeComponent = (new ChargeComponent(this)).addEndEvent(this::stopChargeEvent);
    private final ContinuousComponent continuousComponent = (new ContinuousComponent(this, true)).addStartEvent(this::onContinuityStart);
    private final RepeaterComponent repeaterComponent = (new RepeaterComponent(this)).addTriggerEvent(this::onRepeaterTrigger).addStopEvent(this::onRepeaterStop);
    private final ProjectileComponent projectileComponent = new ProjectileComponent(this, this::createProjectile);

    // index of next projectile color to spawn
    private int projectileIndex = 0;

    // factories producing the different colored projectiles; initialized in constructor
    private final ProjectileFactory[] projectileFactories;

    // functional interface to create projectiles while capturing 'this'
    private interface ProjectileFactory {
        DiffractionProjectile create(LivingEntity entity, DiffractionAbility ability);
    }

    @SuppressWarnings("deprecation")
    public DiffractionAbility(AbilityCore<DiffractionAbility> core) {
        super(core);
        this.isNew = true;
        this.addComponents(this.chargeComponent, this.continuousComponent, this.repeaterComponent, this.projectileComponent);
        this.addCanUseCheck(SukeHelper::canUseInvisibleAbility);
        this.addUseEvent(this::useEvent);

        // initialize the factories array to avoid repetitive switch logic
        this.projectileFactories = new ProjectileFactory[] {
                (e, a) -> new DiffractionProjectileBlue(e.level, e, a),
                (e, a) -> new DiffractionProjectileIndigo(e.level, e, a),
                (e, a) -> new DiffractionProjectileViolet(e.level, e, a),
                (e, a) -> new DiffractionProjectileRed(e.level, e, a),
                (e, a) -> new DiffractionProjectileOrange(e.level, e, a),
                (e, a) -> new DiffractionProjectileYellow(e.level, e, a),
                (e, a) -> new DiffractionProjectileGreen(e.level, e, a)
        };
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
        entity.level.playSound(null, entity.blockPosition(), ModSounds.PIKA_SFX.get(), SoundCategory.PLAYERS, 2.0F, 1.0F);
        this.projectileComponent.shoot(entity, PROJECTILE_SPEED, PROJECTILE_INACCURACY);
    }

    private void onRepeaterStop(LivingEntity entity, IAbility ability) {
        projectileIndex = 0;
        this.continuousComponent.stopContinuity(entity);
        super.cooldownComponent.startCooldown(entity, COOLDOWN);
    }


    private DiffractionProjectile createProjectile(LivingEntity entity) {
        if (projectileFactories == null || projectileFactories.length == 0) {
            // fallback to blue if something went wrong
            return new DiffractionProjectileBlue(entity.level, entity, this);
        }
        // create the projectile for the current index and increment cyclically
        DiffractionProjectile proj = projectileFactories[projectileIndex].create(entity, this);
        projectileIndex = (projectileIndex + 1) % projectileFactories.length;
        return proj;
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
        INSTANCE = new AbilityCore.Builder<>("Diffraction", AbilityCategory.DEVIL_FRUITS, DiffractionAbility::new)
                .setUnlockCheck(InvisibleTouchAbility::canUnlock)
                .addDescriptionLine(DESCRIPTION)
                .addAdvancedDescriptionLine(AbilityDescriptionLine.NEW_LINE, CooldownComponent.getTooltip(COOLDOWN))
                .addAdvancedDescriptionLine(ChargeComponent.getTooltip(CHARGE_TIME))
                .addAdvancedDescriptionLine(ProjectileComponent.getProjectileTooltips())
                .setSourceHakiNature(SourceHakiNature.SPECIAL)
                .setSourceElement(SourceElement.LIGHT)
                .setIcon(new ResourceLocation("awakenawakennomi", "textures/abilities/suke/diffraction.png"))
                .build();
    }
}
