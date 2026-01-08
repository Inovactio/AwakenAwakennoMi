package com.inovactio.awakenawakennomi.abilities.hisohisonomi;

import com.inovactio.awakenawakennomi.abilities.bomubomunomi.PiercingBlastAbility;
import com.inovactio.awakenawakennomi.api.abilities.IAwakenable;
import com.inovactio.awakenawakennomi.entities.projectiles.bomu.PiercingBlastProjectile;
import com.inovactio.awakenawakennomi.entities.projectiles.hiso.EtherealWhisperProjectile;
import com.inovactio.awakenawakennomi.util.InoHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import org.apache.commons.lang3.tuple.ImmutablePair;
import xyz.pixelatedw.mineminenomi.api.abilities.*;
import xyz.pixelatedw.mineminenomi.api.abilities.components.AnimationComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.components.ChargeComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.components.CooldownComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.components.ProjectileComponent;
import xyz.pixelatedw.mineminenomi.api.damagesource.SourceElement;
import xyz.pixelatedw.mineminenomi.api.damagesource.SourceHakiNature;
import xyz.pixelatedw.mineminenomi.api.helpers.AbilityHelper;
import xyz.pixelatedw.mineminenomi.data.entity.devilfruit.DevilFruitCapability;
import xyz.pixelatedw.mineminenomi.init.ModAbilities;
import xyz.pixelatedw.mineminenomi.init.ModAnimations;
import xyz.pixelatedw.mineminenomi.init.ModSounds;

public class EtherealWhisperAbility extends Ability implements IAwakenable {

    public static final AbilityCore<EtherealWhisperAbility> INSTANCE;
    private static final ITextComponent[] DESCRIPTION = AbilityHelper.registerDescriptionText("awakenawakennomi", "ethereal_whisper",
            ImmutablePair.of("Ethereal Whisper (placeholder)", null));
    private final ChargeComponent chargeComponent = (new ChargeComponent(this)).addStartEvent(this::startChargeEvent).addEndEvent(this::stopChargeEvent);
    private final AnimationComponent animationComponent = new AnimationComponent(this);
    private final ProjectileComponent projectileComponent = new ProjectileComponent(this, this::createProjectile);
    private final static float COOLDOWN = 500.0F;
    private final static int CHARGE_TIME = 50;
    private static final float PROJECTILE_SPEED = 2.5F;
    private static final float PROJECTILE_INACCURACY = 0.0F;

    public EtherealWhisperAbility(AbilityCore<EtherealWhisperAbility> core) {
        super(core);
        this.isNew = true;
        this.addComponents(this.chargeComponent, this.animationComponent, this.projectileComponent);
        this.addUseEvent(this::useEvent);
    }

    private void useEvent(LivingEntity entity, IAbility ability) {
        if(this.chargeComponent.isCharging()){
            return;
        }
        this.chargeComponent.startCharging(entity, CHARGE_TIME);
    }

    private EtherealWhisperProjectile createProjectile(LivingEntity entity) {
        return new EtherealWhisperProjectile(entity.level, entity);
    }

    protected static boolean canUnlock(LivingEntity user) {
        return DevilFruitCapability.get(user).hasAwakenedFruit()
                && DevilFruitCapability.get(user).hasDevilFruit(ModAbilities.HISO_HISO_NO_MI);
    }

    @Override
    public boolean AwakenUnlock(LivingEntity user) {
        return canUnlock(user);
    }
    private void startChargeEvent(LivingEntity entity, IAbility ability) {
    }

    private void stopChargeEvent(LivingEntity entity, IAbility ability) {
        this.projectileComponent.shoot(entity, PROJECTILE_SPEED, PROJECTILE_INACCURACY);
        this.cooldownComponent.startCooldown(entity, COOLDOWN);
    }

    static {
        INSTANCE = new AbilityCore.Builder<>("Ethereal Whisper", AbilityCategory.DEVIL_FRUITS, EtherealWhisperAbility::new)
                .setUnlockCheck(EtherealWhisperAbility::canUnlock)
                .addDescriptionLine(DESCRIPTION)
                .addAdvancedDescriptionLine(AbilityDescriptionLine.NEW_LINE, CooldownComponent.getTooltip(COOLDOWN))
                .addAdvancedDescriptionLine(ChargeComponent.getTooltip(CHARGE_TIME))
                .addAdvancedDescriptionLine(ProjectileComponent.getProjectileTooltips())
                .setSourceHakiNature(SourceHakiNature.SPECIAL)
                .setSourceElement(SourceElement.SHOCKWAVE)
                .setIcon(new ResourceLocation("awakenawakennomi", "textures/abilities/hiso/ethereal_whisper.png"))
                .build();
    }
}