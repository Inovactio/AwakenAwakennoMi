package com.inovactio.awakenawakennomi.abilities.sukesukenomi;

import com.inovactio.awakenawakennomi.api.abilities.IAwakenable;
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

    private final ChargeComponent chargeComponent = (new ChargeComponent(this, comp -> comp.getChargePercentage() > 0.5F))
            .addStartEvent(100, this::onStartCharge)
            .addTickEvent(100, this::onTickCharge)
            .addEndEvent(100, this::onEndCharge);

    private final ContinuousComponent continuousComponent = (new ContinuousComponent(this, true))
            .addTickEvent(100, this::onContinuityTick)
            .addEndEvent(100, this::onEndContinuity);

    private final List<BlockPos> affectedBlocks = new ArrayList<>();
    private BlockPos zoneCenter = null;
    private int lastProgress = 0;

    // Flags :
    // vrai si l'utilisateur a expressément demandé de démarrer la continuité en réactivant pendant le chargement
    private boolean requestedStartContinuity = false;
    // vrai si on doit conserver uniquement la portion déjà masquée (réactivation) et ne pas masquer le reste
    private boolean requestedKeepPartial = false;

    public AwakenSukeInvisibleZoneAbility(AbilityCore<AwakenSukeInvisibleZoneAbility> core) {
        super(core);
        this.isNew = true;
        this.addComponents(new AbilityComponent[]{this.chargeComponent, this.continuousComponent});
        this.addUseEvent(this::onUseEvent);
    }

    private void onUseEvent(LivingEntity entity, IAbility ability) {
        if (this.chargeComponent.isCharging()) {
            // L'utilisateur appuie pour réactiver pendant le chargement :
            // - il souhaite démarrer la continuité
            // - conserver uniquement les blocs déjà masqués (ne pas forcer le masquage des restants)
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
        affectedBlocks.clear();
        lastProgress = 0;
        // reset flags au début d'un nouveau charge
        requestedStartContinuity = false;
        requestedKeepPartial = false;
        zoneCenter = entity.blockPosition();
        World world = entity.level;
        int r = RADIUS;
        int r2 = r * r;
        BlockPos.Mutable mut = new BlockPos.Mutable();
        List<BlockPos> temp = new ArrayList<>();
        for (int dy = -r; dy <= r; dy++) {
            for (int dx = -r; dx <= r; dx++) {
                for (int dz = -r; dz <= r; dz++) {
                    if (dx * dx + dy * dy + dz * dz <= r2) {
                        mut.set(zoneCenter.getX() + dx, zoneCenter.getY() + dy, zoneCenter.getZ() + dz);
                        temp.add(mut.immutable());
                    }
                }
            }
        }
        temp.sort((a, b) -> Double.compare(a.distSqr(zoneCenter), b.distSqr(zoneCenter)));
        affectedBlocks.addAll(temp);

        if (!world.isClientSide) {
            // appliquer l'effet custom d'immobilisation côté serveur
            entity.addEffect(new EffectInstance((Effect) ModEffects.MOVEMENT_BLOCKED.get(), CHARGE_TIME + 10, 1, false, false));
            entity.setDeltaMovement(Vector3d.ZERO);

            // petit effet visuel initial
            if (world instanceof ServerWorld) {
                ServerWorld sw = (ServerWorld) world;
                sw.sendParticles(ParticleTypes.AMBIENT_ENTITY_EFFECT,
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

        // progression : rendre invisible petit à petit en fonction du pourcentage de charge
        float percent = this.chargeComponent.getChargePercentage(); // 0.0 - 1.0

        // Courbe d'easing (ease-in) : commence lent puis accélère.
        final double exponent = 2.2;
        float eased = (float) Math.pow(Math.max(0.0f, Math.min(1.0f, percent)), exponent);

        int total = affectedBlocks.size();
        int toAffect = Math.min(total, Math.round(eased * total));

        if (toAffect > lastProgress) {
            UUID owner = entity.getUUID();
            for (int i = lastProgress; i < toAffect; i++) {
                BlockPos pos = affectedBlocks.get(i);
                SukeHelper.forceHideBlock(pos, entity.level, owner);
                if (i % 20 == 0 && entity.level instanceof ServerWorld) {
                    ((ServerWorld) entity.level).sendParticles(ParticleTypes.AMBIENT_ENTITY_EFFECT,
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            6, 0.2, 0.2, 0.2, 0.0);
                }
            }
            lastProgress = toAffect;
        }
    }

    private void onEndCharge(LivingEntity entity, IAbility ability) {
        boolean isServer = !entity.level.isClientSide;

        // considérer la charge complète uniquement si tous les blocs ont déjà été masqués
        boolean chargeCompleted = (lastProgress >= affectedBlocks.size() && affectedBlocks.size() > 0);

        // Serveur : masquer les restants ou tronquer la liste si demandé
        if (isServer) {
            if (!requestedKeepPartial) {
                if (lastProgress < affectedBlocks.size()) {
                    UUID owner = entity.getUUID();
                    for (int i = lastProgress; i < affectedBlocks.size(); i++) {
                        SukeHelper.forceHideBlock(affectedBlocks.get(i), entity.level, owner);
                    }
                    lastProgress = affectedBlocks.size();
                }
            } else {
                if (lastProgress < affectedBlocks.size()) {
                    List<BlockPos> kept = new ArrayList<>(affectedBlocks.subList(0, lastProgress));
                    affectedBlocks.clear();
                    affectedBlocks.addAll(kept);
                }
            }
        } else {
            // Client : si on a demandé de garder une portion, s'assurer de tronquer aussi localement
            if (requestedKeepPartial && lastProgress < affectedBlocks.size()) {
                List<BlockPos> kept = new ArrayList<>(affectedBlocks.subList(0, lastProgress));
                affectedBlocks.clear();
                affectedBlocks.addAll(kept);
            }
        }

        boolean wantContinuity = requestedStartContinuity || chargeCompleted;

        // reset flags
        requestedKeepPartial = false;
        requestedStartContinuity = false;

        // Toujours retirer l'effet d'immobilisation localement (client + serveur) pour permettre au joueur de bouger
        entity.removeEffect((Effect) ModEffects.MOVEMENT_BLOCKED.get());

        if (wantContinuity) {
            // Démarrage de la continuité uniquement côté serveur
            if (isServer) {
                entity.level.playSound(null, entity.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                this.continuousComponent.startContinuity(entity, -1.0F);
            }
        } else {
            // Nettoyage et cooldown côté serveur uniquement
            if (isServer) {
                affectedBlocks.clear();
                zoneCenter = null;
                this.cooldownComponent.startCooldown(entity, COOLDOWN);
            }
        }
    }

    private void onContinuityTick(LivingEntity entity, IAbility ability) {
        if (zoneCenter == null) return;
        double distSq = entity.blockPosition().distSqr(zoneCenter);
        if (distSq > (RADIUS * RADIUS)) {
            // Si le joueur sort de la zone côté serveur, on stoppe la continuité
            if (!entity.level.isClientSide) {
                this.continuousComponent.stopContinuity(entity);
            }
        }
    }

    private void onEndContinuity(LivingEntity entity, IAbility ability) {
        if (!entity.level.isClientSide) {
            UUID owner = entity.getUUID();
            for (BlockPos pos : affectedBlocks) {
                SukeHelper.forceRevealBlock(pos, entity.level, owner);
            }
            // supprimer l'effet custom d'immobilisation
            entity.removeEffect((Effect) ModEffects.MOVEMENT_BLOCKED.get());
            // NOTE : pas de SoundEvents.ENDERDRAGON_FLAP comme demandé
        }
        affectedBlocks.clear();
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
