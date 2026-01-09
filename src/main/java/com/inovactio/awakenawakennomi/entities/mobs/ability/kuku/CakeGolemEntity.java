package com.inovactio.awakenawakennomi.entities.mobs.ability.kuku;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkHooks;
import xyz.pixelatedw.mineminenomi.abilities.CommandAbility;
import xyz.pixelatedw.mineminenomi.abilities.haki.BusoshokuHakiEmissionAbility;
import xyz.pixelatedw.mineminenomi.abilities.haki.BusoshokuHakiHardeningAbility;
import xyz.pixelatedw.mineminenomi.abilities.haki.BusoshokuHakiInternalDestructionAbility;
import xyz.pixelatedw.mineminenomi.api.entities.ICommandReceiver;
import xyz.pixelatedw.mineminenomi.api.enums.NPCCommand;
import xyz.pixelatedw.mineminenomi.data.entity.ability.AbilityDataCapability;
import xyz.pixelatedw.mineminenomi.data.entity.ability.IAbilityData;
import xyz.pixelatedw.mineminenomi.data.entity.entitystats.EntityStatsCapability;
import xyz.pixelatedw.mineminenomi.data.entity.entitystats.IEntityStats;
import xyz.pixelatedw.mineminenomi.data.entity.haki.HakiDataCapability;
import xyz.pixelatedw.mineminenomi.data.entity.haki.IHakiData;
import xyz.pixelatedw.mineminenomi.entities.mobs.OPEntity;
import xyz.pixelatedw.mineminenomi.entities.mobs.ability.NightmareSoldierEntity;
import xyz.pixelatedw.mineminenomi.entities.mobs.bandits.AbstractBanditEntity;
import xyz.pixelatedw.mineminenomi.entities.mobs.goals.DashDodgeProjectilesGoal;
import xyz.pixelatedw.mineminenomi.entities.mobs.goals.DashDodgeTargetGoal;
import xyz.pixelatedw.mineminenomi.entities.mobs.goals.FactionHurtByTargetGoal;
import xyz.pixelatedw.mineminenomi.entities.mobs.goals.abilities.haki.BusoshokuHakiEmissionWrapperGoal;
import xyz.pixelatedw.mineminenomi.entities.mobs.goals.abilities.haki.BusoshokuHakiHardeningWrapperGoal;
import xyz.pixelatedw.mineminenomi.entities.mobs.goals.abilities.haki.BusoshokuHakiInternalDestructionWrapperGoal;
import xyz.pixelatedw.mineminenomi.entities.mobs.marines.AbstractMarineEntity;
import xyz.pixelatedw.mineminenomi.entities.mobs.pirates.AbstractPirateEntity;
import xyz.pixelatedw.mineminenomi.init.ModAttributes;
import xyz.pixelatedw.mineminenomi.init.ModEntityPredicates;

import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Predicate;

public class CakeGolemEntity extends OPEntity implements ICommandReceiver, IEntityAdditionalSpawnData {
    @Nullable
    private UUID ownerId;
    @Nullable
    private LivingEntity owner;
    private long lastCommandTime;
    private LivingEntity lastCommandSender;
    private NPCCommand currentCommand;
    private int attackAnimationTick;

    public CakeGolemEntity(EntityType type, World world) {
        super(type, world);
        this.currentCommand = NPCCommand.IDLE;
    }

    public CakeGolemEntity(World world, LivingEntity owner) {
        super((EntityType) KukuMobs.CAKE_GOLEM.get(), world);
        this.attackAnimationTick = 10;
        this.currentCommand = NPCCommand.IDLE;
        if (world != null && !world.isClientSide) {
            this.setOwner(owner);
            this.setDetails();
            IEntityStats props = EntityStatsCapability.get(this);
            props.setHeart(false);
            props.setShadow(true);
            IHakiData ownerHakiProps = HakiDataCapability.get(owner);
            IHakiData hakiProps = HakiDataCapability.get(this);
            hakiProps.setBusoshokuHakiExp(ownerHakiProps.getBusoshokuHakiExp());
            this.getAttribute((Attribute) ModAttributes.TOUGHNESS.get()).setBaseValue((double)9.0F);
            this.getAttribute(Attributes.ARMOR).setBaseValue((double)24.0F);
            this.getAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue((double)13.0F);
            this.getAttribute((Attribute)ModAttributes.STEP_HEIGHT.get()).setBaseValue((double)8.0F);
            ((GroundPathNavigator)this.getNavigation()).setCanOpenDoors(false);
            if (EntityStatsCapability.get(owner).getDoriki() < (double)10000.0F) {
                this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((double)150.0F + EntityStatsCapability.get(owner).getDoriki() / (double)50.0F);
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue((double)12.0F + EntityStatsCapability.get(owner).getDoriki() / (double)1000.0F);
            } else {
                this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((double)350.0F);
                this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue((double)22.0F);
            }
        }
    }

    protected void registerGoals() {
        CommandAbility.addCommandGoals(this);
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(0, new OpenDoorGoal(this, false));
        this.goalSelector.addGoal(0, new DashDodgeProjectilesGoal(this, 250.0F, 2.5F));
        this.goalSelector.addGoal(0, new DashDodgeTargetGoal(this, 250.0F, 2.5F));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, (double)1.0F, true));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomWalkingGoal(this, 0.8));
        this.goalSelector.addGoal(5, new LookAtGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.addGoal(5, new LookAtGoal(this, AbstractMarineEntity.class, 8.0F));
        this.goalSelector.addGoal(5, new LookAtGoal(this, AbstractPirateEntity.class, 8.0F));
        this.goalSelector.addGoal(5, new LookAtGoal(this, AbstractBanditEntity.class, 8.0F));
        this.goalSelector.addGoal(5, new LookRandomlyGoal(this));
        this.targetSelector.addGoal(0, new HurtByTargetGoal(this, new Class[0]));
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return OPEntity.createAttributes().add(Attributes.FOLLOW_RANGE, (double)64.0F).add(Attributes.MOVEMENT_SPEED, 0.65).add(Attributes.ATTACK_DAMAGE, (double)22.0F).add(Attributes.MAX_HEALTH, (double)650.0F).add(Attributes.KNOCKBACK_RESISTANCE, (double)3.0F);
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    public void remove(boolean keepData) {
        super.remove(keepData);
    }

    public boolean hurt(DamageSource damageSource, float damageValue) {
        return damageSource.getEntity() != null && damageSource.getEntity() instanceof PlayerEntity && damageSource.getEntity() == this.getOwner() ? false : super.hurt(damageSource, damageValue);
    }

    public boolean doHurtTarget(Entity target) {
        float damage = (float)this.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
        int knockback = 0;
        if (target instanceof LivingEntity) {
            damage += EnchantmentHelper.getDamageBonus(this.getMainHandItem(), ((LivingEntity)target).getMobType());
            knockback = (int)((float)knockback + (float)EnchantmentHelper.getKnockbackBonus(this));
        }

        boolean flag = target.hurt(DamageSource.mobAttack(this), damage);
        if (flag && knockback > 0) {
            target.push((double)(-MathHelper.sin(this.xRot * (float)Math.PI / 180.0F) * (float)knockback * 0.5F), 0.1, (double)(MathHelper.cos(this.xRot * (float)Math.PI / 180.0F) * (float)knockback * 0.5F));
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, (double)1.0F, 0.6));
        }

        return flag;
    }

    private void setDetails() {

    }

    public void aiStep() {
        this.updateSwingTime();
        super.aiStep();
    }

    public void tick() {
        if (!this.level.isClientSide) {
            if (this.getOwner() == null || !this.getOwner().isAlive()) {
                this.remove();
                return;
            }
        }

        super.tick();
    }

    public EntitySize getDimensions(Pose pose) {
        return super.getDimensions(pose);
    }

    public void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
        if (this.ownerId != null) {
            nbt.putUUID("ownerId", this.ownerId);
        }

    }

    public void readAdditionalSaveData(CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("ownerId")) {
            this.ownerId = nbt.getUUID("ownerId");
        }

    }

    public void writeSpawnData(PacketBuffer buffer) {
        buffer.writeUUID(this.ownerId);
    }

    public void readSpawnData(PacketBuffer data) {
        this.ownerId = data.readUUID();
    }

    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }


    public void setOwner(LivingEntity owner) {
        this.owner = owner;
        this.ownerId = owner.getUUID();
        IEntityStats stats = EntityStatsCapability.get(this);
        stats.setFaction(EntityStatsCapability.get(owner).getFaction());
        Predicate<Entity> factionScope = ModEntityPredicates.getEnemyFactions(this);
        Predicate<Entity> notSame = (entity) -> !(entity instanceof NightmareSoldierEntity);
        if (factionScope != null) {
            this.targetSelector.addGoal(1, new FactionHurtByTargetGoal(this, factionScope, new Class[0]));
            this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, MobEntity.class, 10, true, true, factionScope.and(notSame)));
        }

        IAbilityData abilityProps = AbilityDataCapability.get(owner);
        if (abilityProps.hasUnlockedAbility(BusoshokuHakiInternalDestructionAbility.INSTANCE)) {
            this.goalSelector.addGoal(1, new BusoshokuHakiInternalDestructionWrapperGoal(this));
        } else if (abilityProps.hasUnlockedAbility(BusoshokuHakiEmissionAbility.INSTANCE)) {
            this.goalSelector.addGoal(1, new BusoshokuHakiEmissionWrapperGoal(this));
        } else if (abilityProps.hasUnlockedAbility(BusoshokuHakiHardeningAbility.INSTANCE)) {
            this.goalSelector.addGoal(1, new BusoshokuHakiHardeningWrapperGoal(this));
        }

    }

    @OnlyIn(Dist.CLIENT)
    public int getAttackAnimationTick() {
        return this.attackAnimationTick;
    }

    @Nullable
    public LivingEntity getOwner() {
        if (this.owner == null && this.ownerId != null) {
            this.owner = this.level.getPlayerByUUID(this.ownerId);
        }

        return this.owner;
    }

    public boolean canReceiveCommandFrom(LivingEntity commandSender) {
        return this.getOwner().equals(commandSender);
    }

    public void setCurrentCommand(@Nullable LivingEntity commandSender, NPCCommand command) {
        this.lastCommandTime = this.level.getGameTime();
        this.lastCommandSender = commandSender;
        this.currentCommand = command;
    }

    public NPCCommand getCurrentCommand() {
        return this.currentCommand;
    }

    @Nullable
    public LivingEntity getLastCommandSender() {
        return this.lastCommandSender;
    }

    public long getLastCommandTime() {
        return this.lastCommandTime;
    }
}
