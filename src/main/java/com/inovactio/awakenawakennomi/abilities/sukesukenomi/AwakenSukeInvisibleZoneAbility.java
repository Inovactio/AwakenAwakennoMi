package com.inovactio.awakenawakennomi.abilities.sukesukenomi;

import com.inovactio.awakenawakennomi.abilities.GroundAbility;
import com.inovactio.awakenawakennomi.api.abilities.IAwakenable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import org.apache.commons.lang3.tuple.ImmutablePair;
import xyz.pixelatedw.mineminenomi.api.abilities.*;
import xyz.pixelatedw.mineminenomi.api.abilities.components.ChargeComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.components.ContinuousComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.components.AnimationComponent;
import xyz.pixelatedw.mineminenomi.api.helpers.AbilityHelper;
import xyz.pixelatedw.mineminenomi.api.util.TargetsPredicate;
import xyz.pixelatedw.mineminenomi.data.entity.devilfruit.DevilFruitCapability;
import xyz.pixelatedw.mineminenomi.init.ModEffects;
import xyz.pixelatedw.mineminenomi.init.ModAbilities;
import com.inovactio.awakenawakennomi.util.PropagationHelper;
import com.inovactio.awakenawakennomi.init.ModAnimations;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class AwakenSukeInvisibleZoneAbility extends GroundAbility implements IAwakenable {

    private static final ITextComponent[] DESCRIPTION =
            AbilityHelper.registerDescriptionText("awakenawakennomi", "awaken_suke_invisible_zone",
                    ImmutablePair.of("Crée une zone sphérique où les blocs deviennent invisibles progressivement.", null));

    public static final AbilityCore<AwakenSukeInvisibleZoneAbility> INSTANCE;
    private static final TargetsPredicate TARGETS_CHECK = (new TargetsPredicate()).testFriendlyFaction();
    private static final int CHARGE_TIME = 600;
    private static final int COOLDOWN_BASE = 200;            // cooldown minimal
    private static final int COOLDOWN_PER_8_BLOCKS = 50;    // ajout par tranche de 8 blocs
    private static final int COOLDOWN_MAX = 1200;           // cap du cooldown
    private static final int RADIUS = 48;
    private static final double PROPAGATION_EASE = 0.35;

    private final ChargeComponent chargeComponent = (new ChargeComponent(this, comp -> comp.getChargePercentage() > 0.5F))
            .addStartEvent(100, this::onStartCharge)
            .addTickEvent(100, this::onTickCharge)
            .addEndEvent(100, this::onEndCharge);

    private final ContinuousComponent continuousComponent = (new ContinuousComponent(this, true))
            .addTickEvent(100, this::onContinuityTick)
            .addEndEvent(100, this::onEndContinuity);

    // Ajout de l'AnimationComponent
    private final AnimationComponent animationComponent = new AnimationComponent(this);

    private final List<PropagationHelper.PropagationEntry> affectedEntries = new ArrayList<>();
    private BlockPos zoneCenter = null;
    private int lastProgress = 0;

    // stocker UUIDs si besoin (non strictement nécessaire ici, utilisé si on souhaite tracking)
    private final Set<UUID> slowedEntities = new HashSet<>();

    // rayon effectif des blocs déjà rendus invisibles (en blocs)
    private double currentInvisibleRadius = 0.0;

    private boolean requestedStartContinuity = false;
    private boolean requestedKeepPartial = false;

    public AwakenSukeInvisibleZoneAbility(AbilityCore<AwakenSukeInvisibleZoneAbility> core) {
        super(core);
        this.isNew = true;
        // ajouter animationComponent aux composants
        this.addComponents(this.chargeComponent, this.continuousComponent, this.animationComponent);
        this.addUseEvent(this::onUseEvent);
    }

    private void onUseEvent(LivingEntity entity, IAbility ability) {
        if (this.chargeComponent.isCharging()) {
            this.requestedStartContinuity = true;
            this.requestedKeepPartial = true;
            this.chargeComponent.stopCharging(entity);
        } else if (this.continuousComponent.isContinuous()) {
            this.continuousComponent.stopContinuity(entity);
        } else {
            // vérifier via la super-classe GroundAbility
            if (!ensureOnGroundOrNotify(entity)) return;
            this.chargeComponent.startCharging(entity, CHARGE_TIME);
        }
    }

    private void onStartCharge(LivingEntity entity, IAbility ability) {
        affectedEntries.clear();
        lastProgress = 0;
        requestedStartContinuity = false;
        requestedKeepPartial = false;
        zoneCenter = entity.blockPosition();
        currentInvisibleRadius = 0.0;
        World world = entity.level;

        List<PropagationHelper.PropagationEntry> entries =
                PropagationHelper.computePropagationEntries(zoneCenter, RADIUS, CHARGE_TIME,
                        frac -> Math.pow(frac, PROPAGATION_EASE), true, null);

        affectedEntries.addAll(entries);

        if (!world.isClientSide) {
            entity.addEffect(new EffectInstance(ModEffects.MOVEMENT_BLOCKED.get(), CHARGE_TIME + 10, 1, false, false));
            entity.setDeltaMovement(Vector3d.ZERO);

            if (world instanceof ServerWorld) {
                ((ServerWorld) world).sendParticles(ParticleTypes.AMBIENT_ENTITY_EFFECT,
                        zoneCenter.getX() + 0.5, zoneCenter.getY() + 0.5, zoneCenter.getZ() + 0.5,
                        40, 2.5, 2.5, 2.5, 0.0);
            }
            world.playSound(null, zoneCenter, SoundEvents.ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);

            // Lancer l'animation KNEEL côté serveur (AnimationComponent gère la propagation)
            this.animationComponent.start(entity, ModAnimations.KNEEL_PUNCH_GROUND, CHARGE_TIME);
        }

        // appliquer ralentissement initial si besoin (sera régulièrement rafraîchi pendant le chargement)
        if (!entity.level.isClientSide) {
            updateSlowness(entity);
        }
    }

    private void onTickCharge(LivingEntity entity, IAbility ability) {
        if (entity.level.isClientSide) return;
        entity.setDeltaMovement(Vector3d.ZERO);

        float percent = this.chargeComponent.getChargePercentage();
        int elapsed = Math.min(CHARGE_TIME, Math.round(percent * CHARGE_TIME));

        if (lastProgress < affectedEntries.size()) {
            UUID owner = entity.getUUID();
            int i = lastProgress;
            while (i < affectedEntries.size() && affectedEntries.get(i).tickToApply <= elapsed) {
                BlockPos pos = affectedEntries.get(i).pos;
                SukeHelper.forceHideBlock(pos, entity.level, owner);

                if (i % 8 == 0 && entity.level instanceof ServerWorld) {
                    ((ServerWorld) entity.level).sendParticles(ParticleTypes.AMBIENT_ENTITY_EFFECT,
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            6, 0.6, 0.6, 0.6, 0.0);
                }
                i++;
            }
            lastProgress = i;

            // recalculer le rayon effectif en fonction des blocs déjà appliqués
            currentInvisibleRadius = computeCurrentInvisibleRadius(lastProgress);
        }

        // rafraîchir le malus de ralentissement pendant la charge
        updateSlowness(entity);
    }

    private void onEndCharge(LivingEntity entity, IAbility ability) {
        boolean isServer = !entity.level.isClientSide;
        boolean chargeCompleted = (lastProgress >= affectedEntries.size() && !affectedEntries.isEmpty());

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
            if (requestedKeepPartial && lastProgress < affectedEntries.size()) {
                List<PropagationHelper.PropagationEntry> kept = new ArrayList<>(affectedEntries.subList(0, lastProgress));
                affectedEntries.clear();
                affectedEntries.addAll(kept);
            }
        }

        // mettre à jour le rayon effectif final après la fin de l'incantation / trimming
        currentInvisibleRadius = computeCurrentInvisibleRadius(lastProgress > 0 ? lastProgress : affectedEntries.size());

        boolean wantContinuity = requestedStartContinuity || chargeCompleted;

        requestedKeepPartial = false;
        requestedStartContinuity = false;

        entity.removeEffect(ModEffects.MOVEMENT_BLOCKED.get());

        // Arrêter l'animation côté serveur dès la fin de la charge
        if (isServer) {
            this.animationComponent.stop(entity);
        }

        if (wantContinuity) {
            if (isServer) {
                entity.level.playSound(null, entity.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                this.continuousComponent.startContinuity(entity, -1.0F);
            }
        } else {
            if (isServer) {
                // calculer le cooldown AVANT de vider la liste d'entrées affectées
                int cdBase = computeCooldown(affectedEntries.size());
                // s'assurer d'un cooldown minimal correspondant au temps d'incantation
                int cd = Math.max(cdBase, CHARGE_TIME);
                affectedEntries.clear();
                zoneCenter = null;
                currentInvisibleRadius = 0.0;
                this.cooldownComponent.startCooldown(entity, cd);

                // on ne rafraîchira plus le slow -> il expirera naturellement (durée courte)
                slowedEntities.clear();
            }
        }
    }

    private void onContinuityTick(LivingEntity entity, IAbility ability) {
        if (zoneCenter == null) return;

        // rafraîchir le malus de ralentissement pendant la continuité
        updateSlowness(entity);

        // si aucun bloc n'est rendu invisible, on stoppe immédiatement la continuité
        if (currentInvisibleRadius <= 0.0) {
            if (!entity.level.isClientSide) {
                this.continuousComponent.stopContinuity(entity);
            }
            return;
        }

        double distSq = entity.blockPosition().distSqr(zoneCenter);
        if (distSq > (currentInvisibleRadius * currentInvisibleRadius)) {
            if (!entity.level.isClientSide) {
                this.continuousComponent.stopContinuity(entity);
            }
        }
    }

    private void onEndContinuity(LivingEntity entity, IAbility ability) {
        if (!entity.level.isClientSide) {
            // arrêter l'animation côté clients via AnimationComponent
            this.animationComponent.stop(entity);

            UUID owner = entity.getUUID();
            for (PropagationHelper.PropagationEntry e : affectedEntries) {
                SukeHelper.forceRevealBlock(e.pos, entity.level, owner);
            }
            entity.removeEffect(ModEffects.MOVEMENT_BLOCKED.get());
        }
        // calculer le cooldown AVANT de vider la liste
        int cdBase = computeCooldown(affectedEntries.size());
        // s'assurer d'un cooldown minimal correspondant au temps d'incantation
        int cd = Math.max(cdBase, CHARGE_TIME);
        affectedEntries.clear();
        zoneCenter = null;
        currentInvisibleRadius = 0.0;
        this.cooldownComponent.startCooldown(entity, cd);

        // laisser le malus expirer naturellement -> on stoppe le tracking
        slowedEntities.clear();
    }


    private void updateSlowness(LivingEntity owner) {
        if (zoneCenter == null) return;
        if (owner == null || owner.level == null || owner.level.isClientSide) return;

        World world = owner.level;

        // n'appliquer le slow que si le rayon visible > 0
        double r = currentInvisibleRadius;
        if (r <= 0.0) return;

        AxisAlignedBB box = new AxisAlignedBB(
                zoneCenter.getX() - r, zoneCenter.getY() - r, zoneCenter.getZ() - r,
                zoneCenter.getX() + r, zoneCenter.getY() + r, zoneCenter.getZ() + r
        );

        List<LivingEntity> list = world.getEntitiesOfClass(LivingEntity.class, box,
                e -> !e.equals(owner) && e.isAlive() && !TARGETS_CHECK.test(owner, e));

        for (LivingEntity target : list) {
            // vérifier que la cible est réellement à l'intérieur du rayon des blocs invisibles (distance euclidienne)
            double dx = target.getX() - (zoneCenter.getX() + 0.5);
            double dy = target.getY() - (zoneCenter.getY() + 0.5);
            double dz = target.getZ() - (zoneCenter.getZ() + 0.5);
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (dist > r) continue;

            // applique Slowness I (amplifier 0) pour 40 ticks et rafraîchit
            target.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 40, 0, true, false, true));

            // détecte si l'entité venait d'être ajoutée au tracking (nouvelle application)
            boolean isNew = slowedEntities.add(target.getUUID());

            if (world instanceof ServerWorld) {
                ServerWorld sw = (ServerWorld) world;
                // Effet principal : particules identiques à celles utilisées lors du punch pour cohérence
                if (isNew) {
                    // burst plus visible à la première application
                    sw.sendParticles(ParticleTypes.AMBIENT_ENTITY_EFFECT,
                            target.getX(), target.getY() + 0.5, target.getZ(),
                            18, 0.35, 0.6, 0.35, 0.0);
                    sw.sendParticles(ParticleTypes.CLOUD,
                            target.getX(), target.getY() + 0.3, target.getZ(),
                            6, 0.35, 0.25, 0.35, 0.01);
                } else {
                    // rafraîchissement discret pour éviter le spam visuel
                    sw.sendParticles(ParticleTypes.AMBIENT_ENTITY_EFFECT,
                            target.getX(), target.getY() + 0.5, target.getZ(),
                            6, 0.2, 0.35, 0.2, 0.0);
                }
            }
        }

        // Note : on ne retire pas immédiatement le malus aux entités quittant la zone,
        // l'effet expirera naturellement peu après (40 ticks) si non rafraîchi.
    }

    /**
     * Calcule le rayon effectif (en blocs) pour les premières "count" entrées d'affectedEntries.
     * Si count <= 0, renvoie 0.
     */
    private double computeCurrentInvisibleRadius(int count) {
        if (zoneCenter == null || affectedEntries.isEmpty() || count <= 0) return 0.0;
        int capped = Math.min(count, affectedEntries.size());
        double maxSq = 0.0;
        for (int i = 0; i < capped; i++) {
            BlockPos p = affectedEntries.get(i).pos;
            double dx = (p.getX() + 0.5) - (zoneCenter.getX() + 0.5);
            double dy = (p.getY() + 0.5) - (zoneCenter.getY() + 0.5);
            double dz = (p.getZ() + 0.5) - (zoneCenter.getZ() + 0.5);
            double dsq = dx * dx + dy * dy + dz * dz;
            if (dsq > maxSq) maxSq = dsq;
        }
        return Math.sqrt(maxSq);
    }


    protected static boolean canUnlock(LivingEntity user) {
        return DevilFruitCapability.get(user).hasAwakenedFruit()
                && DevilFruitCapability.get(user).hasDevilFruit(ModAbilities.SUKE_SUKE_NO_MI);
    }

    @Override
    public boolean AwakenUnlock(LivingEntity user) {
        return canUnlock(user);
    }

    /**
     * Calcule un cooldown dynamique basé sur le nombre de blocs affectés.
     * Permet d'équilibrer l'impact d'une grande zone tout en limitant le cooldown maximal.
     */
    private int computeCooldown(int blocks) {
        int b = Math.max(0, blocks);
        int extraChunks = b / 8;
        int cd = COOLDOWN_BASE + extraChunks * COOLDOWN_PER_8_BLOCKS;
        return Math.min(cd, COOLDOWN_MAX);
    }

    static {
        INSTANCE = new AbilityCore.Builder<>(
                "AwakenSukeInvisibleZone", AbilityCategory.DEVIL_FRUITS, AwakenSukeInvisibleZoneAbility::new)
                .setUnlockCheck(AwakenSukeInvisibleZoneAbility::canUnlock)
                .addDescriptionLine(DESCRIPTION)
                .setIcon(new net.minecraft.util.ResourceLocation("awakenawakennomi", "textures/abilities/awaken_suke_invisible_zone.png"))
                .build();
    }
}
