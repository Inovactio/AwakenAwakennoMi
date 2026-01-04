package com.inovactio.awakenawakennomi.abilities.subesubenomi;

import com.inovactio.awakenawakennomi.abilities.sukesukenomi.SukeHelper;
import com.inovactio.awakenawakennomi.api.abilities.IAwakenable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import xyz.pixelatedw.mineminenomi.abilities.sabi.RustTouchAbility;
import xyz.pixelatedw.mineminenomi.api.abilities.*;
import xyz.pixelatedw.mineminenomi.api.abilities.components.*;
import xyz.pixelatedw.mineminenomi.api.damagesource.SourceHakiNature;
import xyz.pixelatedw.mineminenomi.api.damagesource.SourceType;
import xyz.pixelatedw.mineminenomi.api.helpers.AbilityHelper;
import xyz.pixelatedw.mineminenomi.api.helpers.ItemsHelper;
import xyz.pixelatedw.mineminenomi.data.entity.devilfruit.DevilFruitCapability;
import xyz.pixelatedw.mineminenomi.init.*;
import xyz.pixelatedw.mineminenomi.particles.effects.ParticleEffect;
import xyz.pixelatedw.mineminenomi.wypi.WyHelper;

import java.util.function.Predicate;

public class SlickDisarmAbility extends PunchAbility2 implements IAwakenable {
    private static final ITextComponent[] DESCRIPTION = AbilityHelper.registerDescriptionText("awakenawakennomi", "slick_disarm", new Pair[]{ImmutablePair.of("Coat your fists in slickness and strike your target. Their hands become too slippery to keep a grip, forcing them to drop items from both hands.", (Object)null)});
    private static final float COOLDOWN = 300.0F;
    public static final AbilityCore<SlickDisarmAbility> INSTANCE;
    private static final AbilityOverlay OVERLAY;
    private final SkinOverlayComponent skinOverlayComponent;

    public SlickDisarmAbility(AbilityCore<SlickDisarmAbility> core) {
        super(core);
        this.skinOverlayComponent = new SkinOverlayComponent(this, OVERLAY, new AbilityOverlay[0]);
        this.addComponents(new AbilityComponent[]{this.skinOverlayComponent});
        this.continuousComponent.addStartEvent(100, this::startContinuityEvent);
        this.continuousComponent.addEndEvent(100, this::endContinuityEvent);
    }

    private void startContinuityEvent(LivingEntity entity, IAbility ability) {
        this.skinOverlayComponent.showAll(entity);
    }

    private void endContinuityEvent(LivingEntity entity, IAbility ability) {
        this.skinOverlayComponent.hideAll(entity);
    }

    public boolean onHitEffect(LivingEntity entity, LivingEntity target, ModDamageSource source) {
        if (entity == null || !entity.isAlive()) return false;
        if (target == null || !target.isAlive()) return false;
        if (target.level.isClientSide) return true;

        if (!entity.getMainHandItem().isEmpty() || !entity.getOffhandItem().isEmpty()) {
            return false;
        }

        ItemStack main = target.getMainHandItem();
        ItemStack off = target.getOffhandItem();

        boolean droppedSomething = false;

        if (!main.isEmpty()) {
            target.setItemSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
            if (target instanceof net.minecraft.entity.player.PlayerEntity) {
                ((net.minecraft.entity.player.PlayerEntity) target).drop(main, false);
            } else {
                target.spawnAtLocation(main);
            }
            droppedSomething = true;
        }

        if (!off.isEmpty()) {
            target.setItemSlot(EquipmentSlotType.OFFHAND, ItemStack.EMPTY);
            if (target instanceof net.minecraft.entity.player.PlayerEntity) {
                ((net.minecraft.entity.player.PlayerEntity) target).drop(off, false);
            } else {
                target.spawnAtLocation(off);
            }
            droppedSomething = true;
        }

        return droppedSomething;
    }

    public Predicate<LivingEntity> canActivate() {
        return (entity) -> this.continuousComponent.isContinuous();
    }

    public int getUseLimit() {
        return 1;
    }

    public float getPunchDamage() {
        return 1.0F;
    }

    public float getPunchCooldown() {
        return COOLDOWN;
    }

    protected static boolean canUnlock(LivingEntity user) {
        return DevilFruitCapability.get(user).hasAwakenedFruit()
                && DevilFruitCapability.get(user).hasDevilFruit(ModAbilities.SUBE_SUBE_NO_MI);
    }

    @Override
    public boolean AwakenUnlock(LivingEntity user) {
        return canUnlock(user);
    }

    static {
        INSTANCE = new AbilityCore.Builder<>("Slick Disarm", AbilityCategory.DEVIL_FRUITS, SlickDisarmAbility::new)
                .setUnlockCheck(SmoothWorldAbility::canUnlock)
                .addDescriptionLine(DESCRIPTION)
                .setIcon(new ResourceLocation("awakenawakennomi", "textures/abilities/sube/slick_disarm.png"))
                .addAdvancedDescriptionLine(new AbilityDescriptionLine.IDescriptionLine[]{AbilityDescriptionLine.NEW_LINE, CooldownComponent.getTooltip(COOLDOWN), ContinuousComponent.getTooltip(), ChangeStatsComponent.getTooltip()})
                .setSourceHakiNature(SourceHakiNature.HARDENING).setSourceType(new SourceType[]{SourceType.FIST})
                .build();
        OVERLAY =
                new AbilityOverlay.Builder()
                        .setOverlayPart(AbilityOverlay.OverlayPart.LIMB)
                        .setOverlayPart(AbilityOverlay.OverlayPart.ARM)
                        .setColor(SubeHelper.SUBE_COLOR)
                        .build();
    }
}
