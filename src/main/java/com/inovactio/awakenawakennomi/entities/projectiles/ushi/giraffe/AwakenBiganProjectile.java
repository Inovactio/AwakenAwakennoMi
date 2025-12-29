package com.inovactio.awakenawakennomi.entities.projectiles.ushi.giraffe;

import com.inovactio.awakenawakennomi.abilities.ushiushinomi.giraffe.ReworkedBiganAbility;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import xyz.pixelatedw.mineminenomi.entities.projectiles.AbilityProjectileEntity;

public class AwakenBiganProjectile extends BiganProjectile {
    public static final double PROJECTILE_SIZE = 1.25;
    private final float DAMAGE = 80.0F;
    private final float ARMOR_PIERCING = 0.5F;
    private final int MAX_LIFE = 6;
    public AwakenBiganProjectile(EntityType type, World world) {
        super(type, world);
    }

    public AwakenBiganProjectile(World world, LivingEntity player) {
        super((EntityType) UshiGiraffeProjectiles.AWAKEN_BIGAN.get(), world, player, ReworkedBiganAbility.INSTANCE);
        this.setDamage(DAMAGE);
        this.setArmorPiercing(ARMOR_PIERCING);
        this.setAffectedByHardening();
        this.setPassThroughEntities();
        this.setFist();
        this.setMaxLife(MAX_LIFE);
        this.setEntityCollisionSize(PROJECTILE_SIZE, PROJECTILE_SIZE, PROJECTILE_SIZE);
    }
}
