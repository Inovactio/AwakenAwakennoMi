package com.inovactio.awakenawakennomi.entities.projectiles.suke;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.pixelatedw.mineminenomi.api.abilities.Ability;
import xyz.pixelatedw.mineminenomi.api.abilities.ExplosionAbility;
import xyz.pixelatedw.mineminenomi.entities.projectiles.AbilityProjectileEntity;
import xyz.pixelatedw.mineminenomi.particles.effects.CommonExplosionParticleEffect;

public class DiffractionProjectileViolet extends DiffractionProjectile {
    public static final float DAMAGE = 25.0F;
    public DiffractionProjectileViolet(EntityType type, World world) {
        super(type, world);
    }

    public DiffractionProjectileViolet(World world, LivingEntity player, Ability ability) {
        super(SukeProjectiles.DIFFRACTION_VIOLET.get(), world, player, ability);
        this.setDamage(DAMAGE);
    }
}
