package com.inovactio.awakenawakennomi.abilities.sukesukenomi;

import com.inovactio.awakenawakennomi.entities.projectiles.suke.DiffractionProjectile;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import org.apache.commons.lang3.tuple.ImmutablePair;
import xyz.pixelatedw.mineminenomi.api.abilities.*;
import com.inovactio.awakenawakennomi.api.abilities.IAwakenable;
import xyz.pixelatedw.mineminenomi.api.abilities.components.AbilityComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.components.AnimationComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.components.ProjectileComponent;
import xyz.pixelatedw.mineminenomi.api.helpers.AbilityHelper;
import xyz.pixelatedw.mineminenomi.data.entity.devilfruit.DevilFruitCapability;
import xyz.pixelatedw.mineminenomi.entities.projectiles.suke.ShishaNoTeProjectile;
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
    private final AnimationComponent animationComponent = new AnimationComponent(this);
    private final ProjectileComponent projectileComponent = new ProjectileComponent(this, this::createProjectile);

    public AwakenSukeDiffractionAbility(AbilityCore<AwakenSukeDiffractionAbility> core) {
        super(core);
        this.isNew = true;
        this.addComponents(new AbilityComponent[]{this.animationComponent, this.projectileComponent});
        this.addUseEvent(this::useEvent);
    }

    private void useEvent(LivingEntity entity, IAbility ability) {
        this.animationComponent.start(entity, ModAnimations.AIM_SNIPER, 6);
        this.projectileComponent.shoot(entity, PROJECTILE_SPEED, PROJECTILE_INACCURACY);
        this.cooldownComponent.startCooldown(entity, COOLDOWN);
    }

    private DiffractionProjectile createProjectile(LivingEntity entity) {
        return new DiffractionProjectile(entity.level, entity, this);
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
                .addDescriptionLine(DESCRIPTION)
                .addAdvancedDescriptionLine(new AbilityDescriptionLine.IDescriptionLine[]{AbilityDescriptionLine.NEW_LINE})
                .setIcon(new ResourceLocation("awakenawakennomi", "textures/abilities/awaken_suke_diffraction.png"))
                .build();
    }
}
