package com.inovactio.awakenawakennomi.entities.projectiles.suke;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.pixelatedw.mineminenomi.api.abilities.Ability;
import xyz.pixelatedw.mineminenomi.api.abilities.ExplosionAbility;
import xyz.pixelatedw.mineminenomi.entities.projectiles.AbilityProjectileEntity;
import xyz.pixelatedw.mineminenomi.particles.effects.CommonExplosionParticleEffect;

public class DiffractionProjectileIndigo extends DiffractionProjectile {
    public static final float DAMAGE = 15.0F;
    public DiffractionProjectileIndigo(EntityType type, World world) {
        super(type, world);
    }

    public DiffractionProjectileIndigo(World world, LivingEntity player, Ability ability) {
        super(SukeProjectiles.DIFFRACTION_INDIGO.get(), world, player, ability);
        this.setDamage(DAMAGE);
    }
}
