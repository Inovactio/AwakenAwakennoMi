package com.inovactio.awakenawakennomi.abilities.bomubomunomi;

import com.inovactio.awakenawakennomi.abilities.sukesukenomi.AwakenSukeDiffractionAbility;
import com.inovactio.awakenawakennomi.abilities.sukesukenomi.AwakenSukePunchAbility;
import com.inovactio.awakenawakennomi.api.abilities.IAwakenable;
import com.inovactio.awakenawakennomi.entities.projectiles.bomu.PiercingBlastProjectile;
import com.inovactio.awakenawakennomi.util.InoHelper;
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
import xyz.pixelatedw.mineminenomi.api.abilities.components.AnimationComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.components.ChargeComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.components.CooldownComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.components.ProjectileComponent;
import xyz.pixelatedw.mineminenomi.api.damagesource.SourceElement;
import xyz.pixelatedw.mineminenomi.api.damagesource.SourceHakiNature;
import xyz.pixelatedw.mineminenomi.api.helpers.AbilityHelper;
import xyz.pixelatedw.mineminenomi.api.math.VectorHelper;
import xyz.pixelatedw.mineminenomi.data.entity.devilfruit.DevilFruitCapability;
import xyz.pixelatedw.mineminenomi.entities.projectiles.goro.LightningEntity;
import xyz.pixelatedw.mineminenomi.entities.projectiles.pika.AmaterasuProjectile;
import xyz.pixelatedw.mineminenomi.init.ModAbilities;
import xyz.pixelatedw.mineminenomi.init.ModAnimations;
import xyz.pixelatedw.mineminenomi.init.ModParticleEffects;
import xyz.pixelatedw.mineminenomi.init.ModSounds;
import xyz.pixelatedw.mineminenomi.particles.effects.ParticleEffect;
import xyz.pixelatedw.mineminenomi.wypi.WyHelper;

public class AwakenPiercingBlast extends Ability implements IAwakenable {

    public static final AbilityCore<AwakenPiercingBlast> INSTANCE;
    private static final ITextComponent[] DESCRIPTION = AbilityHelper.registerDescriptionText("awakenawakennomi", "awaken_piercing_blast",
            ImmutablePair.of("Awaken Piercing Blast (placeholder)", null));
    private final ChargeComponent chargeComponent = (new ChargeComponent(this)).addStartEvent(this::startChargeEvent).addEndEvent(this::stopChargeEvent);
    private final AnimationComponent animationComponent = new AnimationComponent(this);
    private final ProjectileComponent projectileComponent = new ProjectileComponent(this, this::createProjectile);
    private final static float MIN_COOLDOWN = 100.0F;
    private final static float MAX_COOLDOWN = 1000.0F;
    private final static int CHARGE_TIME = 1000;
    private static final float PROJECTILE_MIN_SPEED = 2.5F;
    private static final float PROJECTILE_MAX_SPEED = 5.0F;
    private static final float PROJECTILE_INACCURACY = 0.0F;
    private static final float EXPLOSION_MIN_SIZE = 1.0F;
    private static final float EXPLOSION_MAX_SIZE = 50.0F;

    public AwakenPiercingBlast(AbilityCore<AwakenPiercingBlast> core) {
        super(core);
        this.isNew = true;
        this.addComponents(this.chargeComponent, this.animationComponent, this.projectileComponent);
        this.addUseEvent(this::useEvent);
    }

    private void useEvent(LivingEntity entity, IAbility ability) {
        if(this.chargeComponent.isCharging()){
            this.chargeComponent.stopCharging(entity);
            return;
        }
        this.chargeComponent.startCharging(entity, CHARGE_TIME);
    }

    private PiercingBlastProjectile createProjectile(LivingEntity entity) {
        PiercingBlastProjectile proj = new PiercingBlastProjectile(entity.level, entity);
        float multiplier = this.chargeComponent.getChargePercentage();
        proj.setDamage(proj.getDamage() * multiplier);
        proj.SetExplosionDamage(proj.getDamage() * 0.5f);
        float size = InoHelper.linearInterpollation(EXPLOSION_MIN_SIZE, EXPLOSION_MAX_SIZE, multiplier);
        proj.SetExplosionSize(size);
        return proj;
    }

    protected static boolean canUnlock(LivingEntity user) {
        return DevilFruitCapability.get(user).hasAwakenedFruit()
                && DevilFruitCapability.get(user).hasDevilFruit(ModAbilities.BOMU_BOMU_NO_MI);
    }

    @Override
    public boolean AwakenUnlock(LivingEntity user) {
        return canUnlock(user);
    }
    private void startChargeEvent(LivingEntity entity, IAbility ability) {
        entity.level.playSound((PlayerEntity)null, entity.blockPosition(), (SoundEvent)ModSounds.PIKA_CHARGE_SFX.get(), SoundCategory.PLAYERS, 2.0F, 1.0F);
        this.animationComponent.start(entity, ModAnimations.AIM_SNIPER);
    }

    private void stopChargeEvent(LivingEntity entity, IAbility ability) {
        entity.level.playSound((PlayerEntity)null, entity.blockPosition(), (SoundEvent) ModSounds.PIKA_SFX.get(), SoundCategory.PLAYERS, 2.0F, 1.0F);
        this.projectileComponent.shoot(entity, InoHelper.linearInterpollation(PROJECTILE_MIN_SPEED,PROJECTILE_MAX_SPEED,this.chargeComponent.getChargePercentage()), PROJECTILE_INACCURACY);
        this.cooldownComponent.startCooldown(entity, InoHelper.linearInterpollation(MIN_COOLDOWN, MAX_COOLDOWN, this.chargeComponent.getChargePercentage()));
        this.animationComponent.stop(entity);
    }

    static {
        INSTANCE = new AbilityCore.Builder<>("Awaken Piercing Blast", AbilityCategory.DEVIL_FRUITS, AwakenPiercingBlast::new)
                .setUnlockCheck(AwakenPiercingBlast::canUnlock)
                .addDescriptionLine(DESCRIPTION)
                .addAdvancedDescriptionLine(AbilityDescriptionLine.NEW_LINE, CooldownComponent.getTooltip(MIN_COOLDOWN, MAX_COOLDOWN))
                .addAdvancedDescriptionLine(ChargeComponent.getTooltip(0,CHARGE_TIME))
                .addAdvancedDescriptionLine(ProjectileComponent.getProjectileTooltips())
                .setSourceHakiNature(SourceHakiNature.SPECIAL)
                .setSourceElement(SourceElement.EXPLOSION)
                .setIcon(new ResourceLocation("awakenawakennomi", "textures/abilities/awaken_piercing_blast.png"))
                .build();
    }
}
