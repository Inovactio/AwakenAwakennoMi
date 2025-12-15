package com.inovactio.awakenawakennomi.entities.projectiles.suke;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import xyz.pixelatedw.mineminenomi.api.abilities.Ability;
import xyz.pixelatedw.mineminenomi.entities.projectiles.AbilityProjectileEntity;

import javax.annotation.Nullable;

public abstract class DiffractionProjectile extends AbilityProjectileEntity {
    protected static final float ARMOR_PIERCING = 0.5F;
    public DiffractionProjectile(EntityType type, World world) {
        super(type, world);
    }

    public DiffractionProjectile(EntityType type, World world, LivingEntity thrower, @Nullable Ability parent) {
        super(type, world, thrower, parent);
        this.setArmorPiercing(ARMOR_PIERCING);
    }
}
