package com.inovactio.awakenawakennomi.abilities.dekadekanomi;

import com.inovactio.awakenawakennomi.api.abilities.IAwakenable;
import com.inovactio.awakenawakennomi.entities.projectiles.deka.TitanSmashProjectile;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import xyz.pixelatedw.mineminenomi.api.abilities.*;

import xyz.pixelatedw.mineminenomi.api.abilities.components.AbilityComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.components.ChargeComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.components.CooldownComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.components.ProjectileComponent;
import xyz.pixelatedw.mineminenomi.api.helpers.AbilityHelper;
import xyz.pixelatedw.mineminenomi.data.entity.devilfruit.DevilFruitCapability;
import xyz.pixelatedw.mineminenomi.entities.projectiles.pika.AmaterasuProjectile;
import xyz.pixelatedw.mineminenomi.init.ModAbilities;

public class TitanSmash extends Ability implements IAwakenable {
    public static final AbilityCore<TitanSmash> INSTANCE;
    private static final ITextComponent[] DESCRIPTION = AbilityHelper.registerDescriptionText("awakenawakennomi", "titan_smash", new Pair[]{ImmutablePair.of("Allows the user to smash as a Titan.", (Object)null)});
    private static final int COOLDOWN = 400;
    private static final int CHARGE_TIME = 80;
    private static final float PROJECTILE_SPEED = 1.5F;
    private static final int PROJECTILE_INACCURACY = 0;
    private final ChargeComponent chargeComponent = (new ChargeComponent(this)).addEndEvent(this::stopChargeEvent);
    private final ProjectileComponent projectileComponent = new ProjectileComponent(this, this::createProjectile);
    public TitanSmash(AbilityCore<TitanSmash> core){
        super(core);
        this.isNew = true;
        this.addComponents(new AbilityComponent[]{this.chargeComponent, this.projectileComponent});
        this.addUseEvent(this::useEvent);
    }

    private void useEvent(LivingEntity entity, IAbility ability) {
        if(this.chargeComponent.isCharging()) return;
        this.chargeComponent.startCharging(entity, CHARGE_TIME);
    }

    private TitanSmashProjectile createProjectile(LivingEntity entity) {
        return new TitanSmashProjectile(entity.level, entity);
    }

    private void stopChargeEvent(LivingEntity entity, IAbility ability) {
        this.projectileComponent.shoot(entity, PROJECTILE_SPEED, PROJECTILE_INACCURACY);
        this.cooldownComponent.startCooldown(entity, COOLDOWN);
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
        INSTANCE = new AbilityCore.Builder<>("Titan Smash", AbilityCategory.DEVIL_FRUITS, TitanSmash::new)
                .addDescriptionLine(DESCRIPTION)
                .addAdvancedDescriptionLine(new AbilityDescriptionLine.IDescriptionLine[]{CooldownComponent.getTooltip(COOLDOWN)})
                .addAdvancedDescriptionLine(ChargeComponent.getTooltip(CHARGE_TIME))
                .addAdvancedDescriptionLine(ProjectileComponent.getProjectileTooltips())
                .setUnlockCheck(TitanSmash::canUnlock)
                .setIcon(new ResourceLocation("awakenawakennomi", "textures/abilities/titan_smash.png"))
                .build();
    }
}
