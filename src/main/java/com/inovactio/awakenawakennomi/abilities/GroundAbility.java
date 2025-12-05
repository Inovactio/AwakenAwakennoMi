package com.inovactio.awakenawakennomi.abilities;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.Ability;

/**
 * Super-classe d'aide pour les capacités qui ne peuvent être lancées que depuis le sol.
 * Fournit une méthode utilitaire pour vérifier que l'utilisateur est au sol et envoyer
 * un message côté serveur si ce n'est pas le cas.
 */
public abstract class GroundAbility extends Ability {

    protected GroundAbility(xyz.pixelatedw.mineminenomi.api.abilities.AbilityCore<? extends Ability> core) {
        super(core);
        // Marquer comme nouvelle capacité par défaut, comme le fait BlockUseAbility
        this.isNew = true;
    }

    /**
     * Vérifie que l'entité est sur le sol. Si non, envoie un message côté serveur au joueur
     * et renvoie false. Renvoie true si l'entité est au sol.
     */
    protected boolean ensureOnGroundOrNotify(LivingEntity user) {
        if (user == null) return false;
        if (user.isOnGround()) return true;
        if (!user.level.isClientSide && user instanceof PlayerEntity) {
            user.sendMessage(new TranslationTextComponent("awakenawakennomi.ability.ground_only"), user.getUUID());
        }
        return false;
    }
}
