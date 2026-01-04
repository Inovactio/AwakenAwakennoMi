package com.inovactio.awakenawakennomi.entities.projectiles.bomu;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import xyz.pixelatedw.mineminenomi.abilities.pika.AmaterasuAbility;
import xyz.pixelatedw.mineminenomi.api.abilities.ExplosionAbility;
import xyz.pixelatedw.mineminenomi.entities.projectiles.AbilityProjectileEntity;
import xyz.pixelatedw.mineminenomi.entities.projectiles.pika.PikaProjectiles;
import xyz.pixelatedw.mineminenomi.init.ModParticleTypes;
import xyz.pixelatedw.mineminenomi.particles.data.SimpleParticleData;
import xyz.pixelatedw.mineminenomi.particles.effects.CommonExplosionParticleEffect;
import xyz.pixelatedw.mineminenomi.wypi.WyHelper;

public class PiercingBlastProjectile extends AbilityProjectileEntity {
    protected static final float ARMOR_PIERCING = 1.0F;
    private float ExplosionSize = 10.0F;
    private float ExplosionDamage = 50.0F;

    public PiercingBlastProjectile(EntityType type, World world) {
        super(type, world);
    }

    public PiercingBlastProjectile(World world, LivingEntity player) {
        super((EntityType) BomuProjectiles.PIERCING_BLAST.get(), world, player, AmaterasuAbility.INSTANCE);
        this.setDamage(ExplosionDamage);
        this.setArmorPiercing(ARMOR_PIERCING);
        this.onBlockImpactEvent = this::onBlockImpactEvent;
        this.onEntityImpactEvent = this::onEntityImpactEvent;
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

    private void onEntityImpactEvent(LivingEntity entity) {
        CreateExplosionAtPosition(entity.getX(), entity.getY(), entity.getZ());
    }

    private void CreateExplosionAtPosition(double x, double y, double z) {
        ExplosionAbility explosion = super.createExplosion(this.getThrower(), this.level, x, y, z, ExplosionSize);
        explosion.setStaticDamage(ExplosionDamage);
        explosion.setSmokeParticles(new CommonExplosionParticleEffect((int)(this.getDamage() / 6.0F)));
        explosion.setDestroyBlocks(true);
        explosion.setStaticBlockResistance(0);
        explosion.doExplosion();
    }
}
