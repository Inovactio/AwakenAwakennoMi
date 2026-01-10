package com.inovactio.awakenawakennomi.abilities.kukukukunomi;

import com.inovactio.awakenawakennomi.api.abilities.IAwakenable;
import com.inovactio.awakenawakennomi.entities.mobs.ability.kuku.CakeGolemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.FoodStats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import xyz.pixelatedw.mineminenomi.api.abilities.*;
import xyz.pixelatedw.mineminenomi.api.abilities.components.*;
import xyz.pixelatedw.mineminenomi.api.helpers.AbilityHelper;
import xyz.pixelatedw.mineminenomi.data.entity.devilfruit.DevilFruitCapability;
import xyz.pixelatedw.mineminenomi.init.ModAbilities;
import xyz.pixelatedw.mineminenomi.init.ModSounds;

import javax.annotation.Nullable;

public class LivingFeastAbility extends Ability implements IAwakenable {

    private static final int CHARGE_TIME = 100;
    private static final int MIN_COOLDOWN = 1000;
    private static final int MAX_COOLDOWN = 5000;
    private static final float MULTIPLIER_MIN = 0.0F;
    private static final float MULTIPLIER_MAX = 5.0F;
    private static final int MIN_FOOD_LEVEL = 6;
    private static final ITextComponent[] DESCRIPTION = AbilityHelper.registerDescriptionText("awakenawakennomi", "living_feast", new Pair[]{ImmutablePair.of("Sacrifice your own hunger and saturation to breathe life into a loyal Cake Golem. The more nourishment you surrender, the larger and more powerful your creation becomes.", (Object)null)});
    public static final AbilityCore<LivingFeastAbility> INSTANCE;
    private final ContinuousComponent continuousComponent = (new ContinuousComponent(this, true)).addStartEvent(100, this::startContinuityEvent).addTickEvent(100, this::onTickEvent).addEndEvent(100, this::stopContinuityEvent);
    private final StackComponent stackComponent = new StackComponent(this);
    private final ChargeComponent chargeComponent = (new ChargeComponent(this)).addStartEvent(this::startChargeEvent).addEndEvent(this::stopChargeEvent);
    private CakeGolemEntity cakeGolem = null;

    public LivingFeastAbility(AbilityCore<LivingFeastAbility> core) {
        super(core);
        this.isNew = true;
        this.addComponents(new AbilityComponent[]{this.continuousComponent, this.stackComponent, this.chargeComponent});
        this.addUseEvent(this::onUseEvent);
    }

    private void onUseEvent(LivingEntity entity, IAbility ability) {
        if(this.continuousComponent.isContinuous())
        {
            this.continuousComponent.stopContinuity(entity);
            return;
        }
        if(this.chargeComponent.isCharging()){
            return;
        }
        this.chargeComponent.startCharging(entity, CHARGE_TIME);
    }

    private void startChargeEvent(LivingEntity entity, IAbility ability){

    }

    private void stopChargeEvent(LivingEntity entity, IAbility ability) {
        this.continuousComponent.triggerContinuity(entity);
    }



    private void startContinuityEvent(LivingEntity entity, IAbility ability) {
        float multiplier = MULTIPLIER_MIN;

        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;

            multiplier = getFoodMultiplier(player);
            updateFoodLevel(player);
        }

        this.cakeGolem = new CakeGolemEntity(entity.level, entity, multiplier);
        this.cakeGolem.moveTo(entity.getX(), entity.getY(), entity.getZ(), entity.yRot, entity.xRot);
        entity.level.addFreshEntity(this.cakeGolem);
    }

    private void onTickEvent(LivingEntity entity, IAbility ability) {
        if (this.cakeGolem == null || !this.cakeGolem.isAlive()) {
            this.continuousComponent.stopContinuity(entity);
        }

    }

    private void stopContinuityEvent(LivingEntity entity, IAbility ability) {
        if (this.cakeGolem != null) {
            this.cakeGolem.kill();
        }

        float cooldown = MathHelper.clamp(this.continuousComponent.getContinueTime(), MIN_COOLDOWN, MAX_COOLDOWN);
        this.cooldownComponent.startCooldown(entity, cooldown);
    }

    @Nullable
    public CakeGolemEntity getCakeGolem() {
        return this.cakeGolem;
    }

    public CompoundNBT save(CompoundNBT nbt) {
        nbt = super.save(nbt);
        return nbt;
    }

    public void load(CompoundNBT nbt) {
        super.load(nbt);
    }

    private float getFoodMultiplier(PlayerEntity player) {
        if (player.abilities.instabuild) {
            return MULTIPLIER_MAX;
        }

        FoodStats food = player.getFoodData();
        int foodLevel = food.getFoodLevel();
        float saturation = food.getSaturationLevel();

        float foodNorm = foodLevel / 20.0F;
        float satNorm = MathHelper.clamp(saturation / 20.0F, 0.0F, 1.0F);
        float score = (foodNorm * 0.6F) + (satNorm * 0.4F); // 0..1

        return MathHelper.lerp(score, MULTIPLIER_MIN, MULTIPLIER_MAX);
    }

    private void updateFoodLevel(PlayerEntity player) {
        if (player.abilities.instabuild) {
            return;
        }

        FoodStats food = player.getFoodData();
        int foodLevel = food.getFoodLevel();

        if (foodLevel >= MIN_FOOD_LEVEL) {
            food.setSaturation(0.0F);
            food.setFoodLevel(MIN_FOOD_LEVEL);
        }
    }

    protected static boolean canUnlock(LivingEntity user) {
        return DevilFruitCapability.get(user).hasAwakenedFruit()
                && DevilFruitCapability.get(user).hasDevilFruit(ModAbilities.KUKU_KUKU_NO_MI);
    }

    @Override
    public boolean AwakenUnlock(LivingEntity user) {
        return canUnlock(user);
    }

    static {
        INSTANCE = new AbilityCore.Builder<>("Living Feast", AbilityCategory.DEVIL_FRUITS, LivingFeastAbility::new)
                .setUnlockCheck(LivingFeastAbility::canUnlock)
                .addDescriptionLine(DESCRIPTION)
                .setIcon(new ResourceLocation("awakenawakennomi", "textures/abilities/kuku/living_feast.png"))
                .addAdvancedDescriptionLine(new AbilityDescriptionLine.IDescriptionLine[]{AbilityDescriptionLine.NEW_LINE, ContinuousComponent.getTooltip(), CooldownComponent.getTooltip(MIN_COOLDOWN,MAX_COOLDOWN)})
                .build();
    }
}
