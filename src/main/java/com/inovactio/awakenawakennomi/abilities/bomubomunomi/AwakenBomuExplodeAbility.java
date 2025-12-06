package com.inovactio.awakenawakennomi.abilities.bomubomunomi;

import com.inovactio.awakenawakennomi.api.abilities.IAwakenable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.particles.ParticleTypes;
import org.apache.commons.lang3.tuple.ImmutablePair;
import xyz.pixelatedw.mineminenomi.api.abilities.*;
import xyz.pixelatedw.mineminenomi.api.helpers.AbilityHelper;
import xyz.pixelatedw.mineminenomi.api.abilities.ExplosionAbility;
import net.minecraft.util.math.BlockPos;
import xyz.pixelatedw.mineminenomi.api.abilities.components.ContinuousComponent;
import xyz.pixelatedw.mineminenomi.data.entity.devilfruit.DevilFruitCapability;
import xyz.pixelatedw.mineminenomi.api.abilities.components.CooldownComponent;
import xyz.pixelatedw.mineminenomi.init.ModAbilities;

import java.util.Random;

@Deprecated
public class AwakenBomuExplodeAbility extends Ability implements IAwakenable {

    private static final ITextComponent[] DESCRIPTION =
            AbilityHelper.registerDescriptionText("awakenawakennomi", "awaken_bomu_explode",
                    ImmutablePair.of("Periodically creates non-destructive explosions around the user.", null));

    // Deprecated wrapper. Use AwakenBomuAirburstAbility instead.

    // Parameters
    private static final int TICK_INTERVAL = 20; // ticks between checks
    private static final double CHANCE_PER_TICK = 0.25; // chance to spawn an explosion each interval
    // portée doublée (~au moins 2x) comme demandé
    // portée de spawn (distance autour du joueur où l'explosion peut apparaître)
    private static final double RADIUS = 24.0; // radius around the player (doublé encore)
    private static final float POWER = 2.0F; // explosion power
    // Paramètres de classe exposés
    // ExplosionSize agrandie (doublée à nouveau)
    private static final float EXPLOSION_SIZE = 32.0F; // taille effective passée à ExplosionAbility
    private static final float STATIC_DAMAGE = 30.0F; // dégâts fixes infligés par l'explosion
    // plage verticale autorisée pour les explosions autour du joueur (en blocs)
    private static final double VERTICAL_RANGE = 6.0; // permet explosions au-dessus/en-dessous

    // Continuous component pour exécuter onTick régulièrement pendant la durée de la compétence
    private final ContinuousComponent continuousComponent = (new ContinuousComponent(this, true))
            .addTickEvent(TICK_INTERVAL, this::onTick)
            .addEndEvent(90, this::onEndContinuity);

    private final Random random = new Random();

    // aucun cast time : la compétence démarre immédiatement

    public AwakenBomuExplodeAbility(AbilityCore<AwakenBomuExplodeAbility> core) {
        super(core);
        // marquer comme "nouvelle" pour la compatibilité des composants
        this.isNew = true;
        // enregistrer notre continuousComponent
        this.addComponents(this.continuousComponent);
        // quand l'ability est utilisée :
        // - si la continuité est active -> l'arrêter (ce qui appelle onEndContinuity et déclenche le cooldown)
        // - sinon -> démarrer la continuité immédiatement pour 600 ticks (30s)
        this.addUseEvent(100, (ent, abl) -> {
            if (this.continuousComponent.isContinuous()) {
                this.continuousComponent.stopContinuity(ent);
            } else {
                this.continuousComponent.startContinuity(ent, 600);
            }
        });
    }

    private void onTick(LivingEntity entity, IAbility ability) {
        World world = entity.level;
        if (world instanceof ServerWorld) {

            if (random.nextDouble() <= CHANCE_PER_TICK) {
                double ox = (random.nextDouble() * 2.0 - 1.0) * RADIUS;
                double oz = (random.nextDouble() * 2.0 - 1.0) * RADIUS;
                double x = entity.getX() + ox;
                // Permettre des explosions à une hauteur supérieure ou inférieure au joueur
                double yOffset = (random.nextDouble() * 2.0 - 1.0) * VERTICAL_RANGE;
                double y = entity.getY() + yOffset;
                double z = entity.getZ() + oz;
                ServerWorld sw = (ServerWorld) world;
                // clamp Y dans les limites valides du monde (entre 1 et maxHeight-1)
                int maxH = sw.getMaxBuildHeight();
                if (y < 1.0) y = 1.0;
                if (y > maxH - 1) y = maxH - 1;
                BlockPos targetPos = new BlockPos((int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z));

                // vérifier que l'emplacement ciblé est vide (ne pas exploser à l'intérieur d'un bloc)
                if (!sw.isEmptyBlock(targetPos)) {
                    // position occupée, ignorer cette tentative
                    return;
                }

                // Utiliser ExplosionAbility pour un contrôle fin : pas de destruction de blocs,
                // pas de drops, pas de feu, et possibilité d'ajouter des entités immunisées.
                ExplosionAbility ex = new ExplosionAbility(entity, sw, x, y, z, POWER);

                // Ajuster la taille effective de l'explosion et forcer un dommage fixe
                ex.setExplosionSize(EXPLOSION_SIZE);
                ex.setStaticDamage(STATIC_DAMAGE);


                // Configurer l'explosion pour qu'elle soit non-destructive et non-griefing
                ex.setDestroyBlocks(false);
                ex.setDropBlocksAfterExplosion(false);
                ex.setFireAfterExplosion(false);
                ex.setDamageOwner(false); // ne pas blesser l'utilisateur
                ex.disableExplosionKnockback(); // éviter de gérer/restaurer la vélocité manuellement
                ex.setExplosionSound(true); // laisser le son (optionnel)


                // Lancer l'explosion
                ex.doExplosion();

                // ExplosionAbility peut utiliser un ParticleEffect (setSmokeParticles) si la classe est disponible
                // mais pour la robustesse on envoie côté serveur des particules visibles comme fallback.
                // effet d'explosion principal
                sw.sendParticles(ParticleTypes.EXPLOSION_EMITTER, x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
                // nuage/fumée
                sw.sendParticles(ParticleTypes.CLOUD, x, y, z, 24, 2.0, 1.0, 2.0, 0.05);
             }
         }
     }

    private void onEndContinuity(LivingEntity entity, IAbility ability) {
        if (!entity.level.isClientSide) {
            // Démarre le cooldown de 600 ticks (30s) à la fin de la continuité
            this.cooldownComponent.startCooldown(entity, 600);
        }
    }

     private static boolean canUnlock(LivingEntity user) {
         return DevilFruitCapability.get(user).hasAwakenedFruit()
                 && DevilFruitCapability.get(user).hasDevilFruit(ModAbilities.BOMU_BOMU_NO_MI);
     }

    @Override
    public boolean AwakenUnlock(LivingEntity user) {
        return canUnlock(user);
    }

    // Note: getUseLimit/onBlockUsed/GetAllowBlockActivation/canActivate sont propres à BlockUseAbility
    // et ne s'appliquent plus ici puisque nous héritons directement d'Ability. Le contrôle d'usage
    // est géré via les composants (ContinuousComponent, CooldownComponent, etc.).

    // No static registration - this class is deprecated. Use AwakenBomuAirburstAbility.
 }
