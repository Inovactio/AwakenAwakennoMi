package com.inovactio.awakenawakennomi.entities.projectiles.hiso;

import com.inovactio.awakenawakennomi.entities.projectiles.bomu.BomuProjectiles;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import xyz.pixelatedw.mineminenomi.abilities.pika.AmaterasuAbility;
import xyz.pixelatedw.mineminenomi.entities.projectiles.AbilityProjectileEntity;

public class EtherealWhisperProjectile extends AbilityProjectileEntity {
    protected static final float ARMOR_PIERCING = 1.0F;
    private final float Damage = 80.0F;

    public EtherealWhisperProjectile(EntityType type, World world) {
        super(type, world);
    }

    public EtherealWhisperProjectile(World world, LivingEntity player) {
        super((EntityType) HisoProjectiles.ETHEREAL_WHISPERS.get(), world, player, AmaterasuAbility.INSTANCE);
        this.setMaxLife(32);
        this.setCollideWithBlocks(false);
        this.setDamage(Damage);
        this.setArmorPiercing(ARMOR_PIERCING);
        this.onEntityImpactEvent = this::onEntityImpactEvent;
        this.setPassThroughEntities();
    }

    private void onEntityImpactEvent(LivingEntity entity) {
        //Add Effect here
    }
}
