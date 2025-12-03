package com.inovactio.awakenawakennomi.abilities.sukesukenomi;

import com.inovactio.awakenawakennomi.api.abilities.IAwakenable;
import com.inovactio.awakenawakennomi.util.PropagationHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.vector.Vector3d;
import org.apache.commons.lang3.tuple.ImmutablePair;
import xyz.pixelatedw.mineminenomi.api.abilities.*;
import xyz.pixelatedw.mineminenomi.api.abilities.components.AbilityComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.components.ChargeComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.components.ContinuousComponent;
import xyz.pixelatedw.mineminenomi.api.helpers.AbilityHelper;
import xyz.pixelatedw.mineminenomi.data.entity.devilfruit.DevilFruitCapability;
import xyz.pixelatedw.mineminenomi.init.ModAbilities;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import xyz.pixelatedw.mineminenomi.init.ModEffects;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class AwakenSukeInvisibleZoneAbility extends Ability implements IAwakenable {

    private static final ITextComponent[] DESCRIPTION =
            AbilityHelper.registerDescriptionText("awakenawakennomi", "awaken_suke_invisible_zone",
                    ImmutablePair.of("Crée une zone sphérique où les blocs deviennent invisibles progressivement.", null));

    public static final AbilityCore<AwakenSukeInvisibleZoneAbility> INSTANCE;

    // 30 secondes = 600 ticks
    private static final int CHARGE_TIME = 600;
    private static final int COOLDOWN = 200;
    private static final int RADIUS = 48;

    // courbe d'easing (utilisée comme exposant dans Math.pow)
    private static final double PROPAGATION_EASE = 0.35;

    private final ChargeComponent chargeComponent = (new ChargeComponent(this, comp -> comp.getChargePercentage() > 0.5F))
            .addStartEvent(100, this::onStartCharge)
            .addTickEvent(100, this::onTickCharge)
            .addEndEvent(100, this::onEndCharge);

    private final ContinuousComponent continuousComponent = (new ContinuousComponent(this, true))
            .addTickEvent(100, this::onContinuityTick)
            .addEndEvent(100, this::onEndContinuity);

    // Utilisation du helper générique
    private final List<PropagationHelper.PropagationEntry> affectedEntries = new ArrayList<>();
    private BlockPos zoneCenter = null;
    private int lastProgress = 0;

    // Flags :
    private boolean requestedStartContinuity = false;
    private boolean requestedKeepPartial = false;

    public AwakenSukeInvisibleZoneAbility(AbilityCore<AwakenSukeInvisibleZoneAbility> core) {
        super(core);
        this.isNew = true;
        this.addComponents(new AbilityComponent[]{this.chargeComponent, this.continuousComponent});
        this.addUseEvent(this::onUseEvent);
    }

    private void onUseEvent(LivingEntity entity, IAbility ability) {
        if (this.chargeComponent.isCharging()) {
            // réactivation pendant chargement -> démarrer continuité plus tard et garder la portion déjà masquée
            this.requestedStartContinuity = true;
            this.requestedKeepPartial = true;
            this.chargeComponent.stopCharging(entity);
        } else if (this.continuousComponent.isContinuous()) {
            this.continuousComponent.stopContinuity(entity);
        } else {
            this.chargeComponent.startCharging(entity, CHARGE_TIME);
        }
    }

    private void onStartCharge(LivingEntity entity, IAbility ability) {
        affectedEntries.clear();
        lastProgress = 0;
        requestedStartContinuity = false;
        requestedKeepPartial = false;
        zoneCenter = entity.blockPosition();
        World world = entity.level;

        // calcul des entrées via le helper générique (sphère, voisinage 6-directions)
        List<PropagationHelper.PropagationEntry> entries =
                PropagationHelper.computePropagationEntries(zoneCenter, RADIUS, CHARGE_TIME,
                        frac -> Math.pow(frac, PROPAGATION_EASE), true, null);

        // Optionnel : si on veut stabiliser l'ordre avec un seed (comme avant), on peut shuffle de façon déterministe
        // ici on conserve l'ordre fourni par le helper
        affectedEntries.addAll(entries);

        if (!world.isClientSide) {
            // appliquer l'effet custom d'immobilisation côté serveur
            entity.addEffect(new EffectInstance((Effect) ModEffects.MOVEMENT_BLOCKED.get(), CHARGE_TIME + 10, 1, false, false));
            entity.setDeltaMovement(Vector3d.ZERO);

            if (world instanceof ServerWorld) {
                ((ServerWorld) world).sendParticles(ParticleTypes.AMBIENT_ENTITY_EFFECT,
                        zoneCenter.getX() + 0.5, zoneCenter.getY() + 0.5, zoneCenter.getZ() + 0.5,
                        40, 2.5, 2.5, 2.5, 0.0);
            }
            world.playSound(null, zoneCenter, SoundEvents.ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
        }
    }

    private void onTickCharge(LivingEntity entity, IAbility ability) {
        if (entity.level.isClientSide) return;
        // maintenir immobilisation en réinitialisant la vélocité
        entity.setDeltaMovement(Vector3d.ZERO);

        float percent = this.chargeComponent.getChargePercentage(); // 0.0 - 1.0
        int elapsed = Math.min(CHARGE_TIME, Math.round(percent * CHARGE_TIME));

        // cacher tous les blocs dont tickToApply <= elapsed
        if (lastProgress < affectedEntries.size()) {
            UUID owner = entity.getUUID();
            Random rng = new Random(); // petites particules aléatoires
            int i = lastProgress;
            while (i < affectedEntries.size() && affectedEntries.get(i).tickToApply <= elapsed) {
                BlockPos pos = affectedEntries.get(i).pos;
                SukeHelper.forceHideBlock(pos, entity.level, owner);

                // petits effets visuels par groupe pour lisibilité
                if (i % 8 == 0 && entity.level instanceof ServerWorld) {
                    ((ServerWorld) entity.level).sendParticles(ParticleTypes.AMBIENT_ENTITY_EFFECT,
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            6, 0.6, 0.6, 0.6, 0.0);
                }
                i++;
            }
            lastProgress = i;
        }
    }

    private void onEndCharge(LivingEntity entity, IAbility ability) {
        boolean isServer = !entity.level.isClientSide;

        // considérer la charge complète uniquement si tous les blocs ont déjà été masqués
        boolean chargeCompleted = (lastProgress >= affectedEntries.size() && !affectedEntries.isEmpty());

        // Serveur : masquer les restants ou tronquer la liste si demandé
        if (isServer) {
            if (!requestedKeepPartial) {
                if (lastProgress < affectedEntries.size()) {
                    UUID owner = entity.getUUID();
                    for (int i = lastProgress; i < affectedEntries.size(); i++) {
                        SukeHelper.forceHideBlock(affectedEntries.get(i).pos, entity.level, owner);
                    }
                    lastProgress = affectedEntries.size();
                }
            } else {
                if (lastProgress < affectedEntries.size()) {
                    List<PropagationHelper.PropagationEntry> kept = new ArrayList<>(affectedEntries.subList(0, lastProgress));
                    affectedEntries.clear();
                    affectedEntries.addAll(kept);
                }
            }
        } else {
            // Client : tronquer localement si nécessaire
            if (requestedKeepPartial && lastProgress < affectedEntries.size()) {
                List<PropagationHelper.PropagationEntry> kept = new ArrayList<>(affectedEntries.subList(0, lastProgress));
                affectedEntries.clear();
                affectedEntries.addAll(kept);
            }
        }

        boolean wantContinuity = requestedStartContinuity || chargeCompleted;

        // reset flags
        requestedKeepPartial = false;
        requestedStartContinuity = false;

        // Toujours retirer l'effet d'immobilisation localement (client + serveur) pour permettre au joueur de bouger
        entity.removeEffect((Effect) ModEffects.MOVEMENT_BLOCKED.get());

        if (wantContinuity) {
            if (isServer) {
                entity.level.playSound(null, entity.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                this.continuousComponent.startContinuity(entity, -1.0F);
            }
        } else {
            if (isServer) {
                affectedEntries.clear();
                zoneCenter = null;
                this.cooldownComponent.startCooldown(entity, COOLDOWN);
            }
        }
    }

    private void onContinuityTick(LivingEntity entity, IAbility ability) {
        if (zoneCenter == null) return;
        double distSq = entity.blockPosition().distSqr(zoneCenter);
        if (distSq > (RADIUS * RADIUS)) {
            if (!entity.level.isClientSide) {
                this.continuousComponent.stopContinuity(entity);
            }
        }
    }

    private void onEndContinuity(LivingEntity entity, IAbility ability) {
        if (!entity.level.isClientSide) {
            UUID owner = entity.getUUID();
            for (PropagationHelper.PropagationEntry e : affectedEntries) {
                SukeHelper.forceRevealBlock(e.pos, entity.level, owner);
            }
            entity.removeEffect((Effect) ModEffects.MOVEMENT_BLOCKED.get());
        }
        affectedEntries.clear();
        zoneCenter = null;
        this.cooldownComponent.startCooldown(entity, COOLDOWN);
    }

    protected static boolean canUnlock(LivingEntity user) {
        return DevilFruitCapability.get(user).hasAwakenedFruit()
                && DevilFruitCapability.get(user).hasDevilFruit(ModAbilities.SUKE_SUKE_NO_MI);
    }

    @Override
    public boolean AwakenUnlock(LivingEntity user) {
        return canUnlock(user);
    }

    static {
        INSTANCE = new AbilityCore.Builder<AwakenSukeInvisibleZoneAbility>(
                "AwakenSukeInvisibleZone", AbilityCategory.DEVIL_FRUITS, AwakenSukeInvisibleZoneAbility::new)
                .setUnlockCheck(AwakenSukeInvisibleZoneAbility::canUnlock)
                .addDescriptionLine(DESCRIPTION)
                .setIcon(new ResourceLocation("awakenawakennomi", "textures/abilities/awaken_suke_invisible_zone.png"))
                .build();
    }
}