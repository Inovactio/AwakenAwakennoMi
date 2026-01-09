package com.inovactio.awakenawakennomi.entities.mobs.ability.kuku;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.potion.Effects;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.GameRules;
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
import xyz.pixelatedw.mineminenomi.entities.mobs.ability.DoppelmanEntity;
import xyz.pixelatedw.mineminenomi.entities.mobs.ability.NightmareSoldierEntity;
import xyz.pixelatedw.mineminenomi.entities.mobs.bandits.AbstractBanditEntity;
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
    private static final DataParameter<Float> SCALE_SIZE = EntityDataManager.defineId(CakeGolemEntity.class, DataSerializers.FLOAT);
    @Nullable
    private UUID ownerId;
    @Nullable
    private LivingEntity owner;
    private long lastCommandTime;
    private LivingEntity lastCommandSender;
    private NPCCommand currentCommand;
    private int attackAnimationTick;
    protected float scaleSize;
    protected final float baseHealth = 100.0F;
    protected final float baseAttackDamage = 5.0F;
    protected final float baseToughness = 0.5F;
    protected final float baseArmor = 2.0F;
    protected final float baseArmorToughness = 1.0F;
    protected final float baseStepHeight = 1.0F;
    private float playerJumpPendingScale = 0.0F;


    public CakeGolemEntity(EntityType type, World world) {
        super(type, world);
        this.currentCommand = NPCCommand.IDLE;
        this.scaleSize = 1.0F;
    }

    public CakeGolemEntity(World world, LivingEntity owner) {
        this(world, owner, 1.0F);
    }

    public CakeGolemEntity(World world, LivingEntity owner, float size) {
        super((EntityType) KukuMobs.CAKE_GOLEM.get(), world);
        this.attackAnimationTick = 10;
        this.currentCommand = NPCCommand.IDLE;
        this.setScaleSize(size);
        if (world != null && !world.isClientSide) {
            this.setOwner(owner);
            this.setDetails();
            IEntityStats props = EntityStatsCapability.get(this);
            props.setHeart(false);
            props.setShadow(true);
            IHakiData ownerHakiProps = HakiDataCapability.get(owner);
            IHakiData hakiProps = HakiDataCapability.get(this);
            hakiProps.setBusoshokuHakiExp(ownerHakiProps.getBusoshokuHakiExp());

            float effectiveSize = this.getScaleSize();
            this.getAttribute((Attribute) ModAttributes.TOUGHNESS.get()).setBaseValue((double) baseToughness * effectiveSize);
            this.getAttribute(Attributes.ARMOR).setBaseValue((double) baseArmor * effectiveSize);
            this.getAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue((double) baseArmorToughness * effectiveSize);
            this.getAttribute((Attribute) ModAttributes.STEP_HEIGHT.get()).setBaseValue((double) baseStepHeight * effectiveSize);
            this.maxUpStep = baseStepHeight * effectiveSize;

            ((GroundPathNavigator) this.getNavigation()).setCanOpenDoors(false);

            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((double) baseHealth * effectiveSize);
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue((double) baseAttackDamage * effectiveSize);
            this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue((double) 64);
        }
    }

    protected void registerGoals() {
        CommandAbility.addCommandGoals(this);
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(0, new OpenDoorGoal(this, false));
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
        return OPEntity.createAttributes().add(Attributes.FOLLOW_RANGE, (double)64.0F).add(Attributes.MOVEMENT_SPEED, 0.30).add(Attributes.ATTACK_DAMAGE, (double)22.0F).add(Attributes.MAX_HEALTH, (double)650.0F).add(Attributes.KNOCKBACK_RESISTANCE, (double)3.0F).add((Attribute)ModAttributes.JUMP_HEIGHT.get(), 4F);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SCALE_SIZE, 1.0F);
    }

    public float getScaleSize() {
        return this.entityData.get(SCALE_SIZE);
    }

    private void refreshStepHeightFromScale() {
        // Recalcule à partir de tes constantes et du scale actuel
        float effectiveSize = this.getScaleSize();
        float step = this.baseStepHeight * effectiveSize;

        // maxUpStep est le step réellement utilisé par la collision/move
        this.maxUpStep = step;

        // Si ton mod s’appuie aussi sur l’attribut, on le maintient cohérent
        if (this.getAttribute((net.minecraft.entity.ai.attributes.Attribute) ModAttributes.STEP_HEIGHT.get()) != null) {
            this.getAttribute((net.minecraft.entity.ai.attributes.Attribute) ModAttributes.STEP_HEIGHT.get())
                    .setBaseValue((double) step);
        }
    }

    public void setScaleSize(float size) {
        float clamped = MathHelper.clamp(size, 0.1F, 20.0F);
        this.scaleSize = clamped;
        this.entityData.set(SCALE_SIZE, clamped);
        this.refreshDimensions();

        // \=>\> important: garder maxUpStep cohérent après changement de taille
        this.refreshStepHeightFromScale();
    }

    public void remove(boolean keepData) {
        super.remove(keepData);
    }

    public boolean hurt(DamageSource damageSource, float damageValue) {
        return damageSource.getEntity() != null && damageSource.getEntity() instanceof PlayerEntity && damageSource.getEntity() == this.getOwner() ? false : super.hurt(damageSource, damageValue);
    }

    public boolean doHurtTarget(Entity target) {
        this.attackAnimationTick = 10;
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

        this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
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

    @Override
    public EntitySize getDimensions(Pose pose) {
        return super.getDimensions(pose).scale(this.getScaleSize());
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        buffer.writeUUID(this.ownerId);
        buffer.writeFloat(this.getScaleSize());
    }


    @Override
    public void readSpawnData(PacketBuffer data) {
        this.ownerId = data.readUUID();
        this.setScaleSize(data.readFloat());
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

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);

        if (this.level.isClientSide) {
            return;
        }
        if (!this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            return;
        }

        int count = Math.max(1, MathHelper.floor(this.getScaleSize())); // 1 * scaleSize

        for (int i = 0; i < count; i++) {
            this.spawnAtLocation(new ItemStack(Items.CAKE, 1), 0.0F);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public int getAttackAnimationTick() {
        return this.attackAnimationTick;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.IRON_GOLEM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.IRON_GOLEM_DEATH;
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

    @Override
    public ActionResultType mobInteract(PlayerEntity player, Hand hand) {
        // Côté client: on renvoie SUCCESS si c’est le owner pour l’animation
        if (this.level.isClientSide) {
            LivingEntity owner = this.getOwner();
            return (owner != null && owner.getUUID().equals(player.getUUID()))
                    ? ActionResultType.SUCCESS
                    : ActionResultType.PASS;
        }

        // Côté serveur: seul le owner peut monter
        LivingEntity owner = this.getOwner();
        if (owner != null && owner.getUUID().equals(player.getUUID())) {
            if (!player.isPassenger()) {
                player.startRiding(this, true);
            }
            return ActionResultType.CONSUME;
        }

        return super.mobInteract(player, hand);
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        if (!(passenger instanceof PlayerEntity)) return false;

        LivingEntity owner = this.getOwner();
        if (owner == null) return false;

        return this.getPassengers().isEmpty()
                && owner.getUUID().equals(((PlayerEntity) passenger).getUUID());
    }

    @Override
    @Nullable
    public LivingEntity getControllingPassenger() {
        if (this.getPassengers().isEmpty()) return null;
        Entity first = this.getPassengers().get(0);
        return first instanceof LivingEntity ? (LivingEntity) first : null;
    }

    @Override
    public boolean canBeControlledByRider() {
        return true;
    }

    private boolean isOwnerRiding() {
        LivingEntity rider = this.getControllingPassenger();
        LivingEntity owner = this.getOwner();
        return rider != null && owner != null && rider.getUUID().equals(owner.getUUID());
    }

    @Override
    public void positionRider(Entity passenger) {
        if (this.hasPassenger(passenger)) {
            double y = this.getY() + (this.getBbHeight() * 0.75D);
            passenger.setPos(this.getX(), y, this.getZ());
            return;
        }
        super.positionRider(passenger);
    }

    public void handleStartJump(int jumpPower) {
        int clamped = MathHelper.clamp(jumpPower, 0, 90);
        this.playerJumpPendingScale = clamped / 90.0F;
    }

    private double getCustomJump() {
        // Utilise ton attribut (dans ton createAttributes tu ajoutes ModAttributes.JUMP_HEIGHT)
        // Ajuste si chez toi cet attribut n'existe pas ou porte un autre nom.
        if (this.getAttribute((Attribute) ModAttributes.JUMP_HEIGHT.get()) != null) {
            return this.getAttribute((Attribute) ModAttributes.JUMP_HEIGHT.get()).getValue();
        }
        return this.getJumpPower(); // fallback LivingEntity
    }

    @Override
    public void travel(Vector3d travelVector) {
        if (!this.isAlive()) return;

        if (this.isVehicle() && this.canBeControlledByRider() && this.isOwnerRiding()) {
            this.refreshStepHeightFromScale();

            LivingEntity rider = this.getControllingPassenger();
            if (rider == null) {
                super.travel(travelVector);
                return;
            }

            // Orientation "monture"
            this.yRot = rider.yRot;
            this.yRotO = this.yRot;
            this.xRot = rider.xRot * 0.5F;
            this.setRot(this.yRot, this.xRot);
            this.yBodyRot = this.yRot;
            this.yHeadRot = this.yBodyRot;

            // Inputs façon cheval
            float f = rider.xxa * 0.5F; // strafe
            float f1 = rider.zza;       // forward
            if (f1 <= 0.0F) {
                f1 *= 0.25F; // nerf marche arrière
            }

            // Saut (si "pending" > 0 et onGround)
            if (this.playerJumpPendingScale > 0.0F && !this.jumping && this.onGround) {
                double d0 = this.getCustomJump() * (double) this.playerJumpPendingScale * (double) this.getBlockJumpFactor();
                double d1 = d0;

                if (this.hasEffect(Effects.JUMP)) {
                    d1 = d0 + (double) ((float) (this.getEffect(Effects.JUMP).getAmplifier() + 1) * 0.1F);
                }

                Vector3d motion = this.getDeltaMovement();
                this.setDeltaMovement(motion.x, d1, motion.z);

                this.jumping = true;
                this.hasImpulse = true;
                net.minecraftforge.common.ForgeHooks.onLivingJump(this);

                // Petit boost avant pendant le saut si on avance
                if (f1 > 0.0F) {
                    float s = MathHelper.sin(this.yRot * ((float) Math.PI / 180F));
                    float c = MathHelper.cos(this.yRot * ((float) Math.PI / 180F));
                    this.setDeltaMovement(this.getDeltaMovement().add(
                            (double) (-0.4F * s * this.playerJumpPendingScale),
                            0.0D,
                            (double) (0.4F * c * this.playerJumpPendingScale)
                    ));
                }

                this.playerJumpPendingScale = 0.0F;
            }

            this.flyingSpeed = this.getSpeed() * 0.1F;

            if (this.isControlledByLocalInstance()) {
                this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED));
                super.travel(new Vector3d((double) f, travelVector.y, (double) f1));
            } else if (rider instanceof PlayerEntity) {
                this.setDeltaMovement(Vector3d.ZERO);
            }

            if (this.onGround) {
                this.playerJumpPendingScale = 0.0F;
                this.jumping = false;
            }

            this.calculateEntityAnimation(this, false);
            return;
        }

        this.flyingSpeed = 0.02F;
        super.travel(travelVector);
    }
}
