package com.inovactio.awakenawakennomi.entities.projectiles.deka;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import xyz.pixelatedw.mineminenomi.abilities.pika.AmaterasuAbility;
import xyz.pixelatedw.mineminenomi.api.abilities.ExplosionAbility;
import xyz.pixelatedw.mineminenomi.entities.projectiles.AbilityProjectileEntity;
import xyz.pixelatedw.mineminenomi.particles.effects.ParticleEffect;

public class TitanSmashProjectile extends AbilityProjectileEntity {

    private final float DAMAGE = 150.0F;
    private float ExplosionSize = 30.0F;
    private float ExplosionDamage = 50.0F;
    public static float PROJECTILE_SIZE = 24.0F;

    public TitanSmashProjectile(EntityType type, World world) {
        super(type, world);
    }

    public TitanSmashProjectile(World world, LivingEntity player) {
        super((EntityType) DekaProjectiles.TIMAN_SMASH.get(), world, player, AmaterasuAbility.INSTANCE);
        this.setDamage(DAMAGE);
        this.setPassThroughEntities();
        super.setFist();
        this.setEntityCollisionSize(PROJECTILE_SIZE, PROJECTILE_SIZE, PROJECTILE_SIZE);
        this.onBlockImpactEvent = this::onBlockImpactEvent;
    }

    public void SetExplosionSize(float size) {
        this.ExplosionSize = size;
    }

    public void SetExplosionDamage(float damage) {
        this.ExplosionDamage = damage;
    }

    private void onBlockImpactEvent(BlockPos hit) {
        CreateExplosionAtPosition(hit.getX(), hit.getY(), hit.getZ());
    }

    private void CreateExplosionAtPosition(double x, double y, double z) {
        ExplosionAbility explosion = super.createExplosion(this.getThrower(), this.level, x, y, z, ExplosionSize);
        explosion.setStaticDamage(ExplosionDamage);
        explosion.setExplosionSound(true);
        explosion.setDamageOwner(false);
        explosion.setDestroyBlocks(true);
        explosion.setFireAfterExplosion(false);
        explosion.setSmokeParticles((ParticleEffect)null);
        explosion.setDamageEntities(false);
        explosion.doExplosion();
    }
}
