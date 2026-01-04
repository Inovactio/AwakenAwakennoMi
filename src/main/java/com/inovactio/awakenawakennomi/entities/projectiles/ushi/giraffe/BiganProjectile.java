package com.inovactio.awakenawakennomi.entities.projectiles.ushi.giraffe;

import com.inovactio.awakenawakennomi.abilities.ushiushinomi.giraffe.KirimanjaroAbility;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import xyz.pixelatedw.mineminenomi.api.abilities.AbilityCore;
import xyz.pixelatedw.mineminenomi.api.abilities.IAbility;
import xyz.pixelatedw.mineminenomi.entities.projectiles.AbilityProjectileEntity;

import javax.annotation.Nullable;

public class BiganProjectile extends AbilityProjectileEntity {
    public static final double PROJECTILE_SIZE = 1;
    private final float DAMAGE = 20.0F;
    private final float ARMOR_PIERCING = 0.25F;
    private final int MAX_LIFE = 5;
    public BiganProjectile(EntityType type, World world) {
        super(type, world);
    }

    public BiganProjectile(World world, LivingEntity player) {
        super((EntityType) UshiGiraffeProjectiles.BIGAN.get(), world, player, KirimanjaroAbility.INSTANCE);
        this.setDamage(DAMAGE);
        this.setArmorPiercing(ARMOR_PIERCING);
        this.setAffectedByHardening();
        this.setPassThroughEntities();
        this.setFist();
        this.setMaxLife(MAX_LIFE);
        this.setEntityCollisionSize(PROJECTILE_SIZE, PROJECTILE_SIZE, PROJECTILE_SIZE);
    }

    public BiganProjectile(EntityType type, World world, LivingEntity thrower, @Nullable AbilityCore<? extends IAbility> parent) {
        super(type, world, thrower, parent, parent != null ? parent.getSourceElement() : null, parent != null ? parent.getSourceHakiNature() : null, parent != null ? parent.getSourceTypes() : null);
    }
}
