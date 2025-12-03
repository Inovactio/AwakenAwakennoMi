package com.inovactio.awakenawakennomi.abilities.sukesukenomi;

import com.inovactio.awakenawakennomi.api.common.InvisibleBlockManager;
import com.inovactio.awakenawakennomi.network.ModNetwork;
import com.inovactio.awakenawakennomi.network.ToggleInvisiblePacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;

import java.awt.*;
import java.util.UUID;

public class SukeHelper {

    public static Color SUKE_COLOR = new Color(162, 229, 229, 152);

    public static void toggleBlockInvisibility(BlockPos pos, World world, UUID player) {
        boolean invisible;

        if (world instanceof ServerWorld) {
            // server-side: utiliser l'API serveur
            invisible = !InvisibleBlockManager.isInvisible(world, pos);
            InvisibleBlockManager.setInvisible(world, pos, invisible, player);
            // broadcast aux clients la nouvelle valeur
            ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new ToggleInvisiblePacket(pos, player, invisible));
        } else {
            // client-side: utiliser le cache client (signature différente)
            invisible = !InvisibleBlockManager.isInvisible(pos, player);
            InvisibleBlockManager.setInvisible(pos, player, invisible);
            // ne pas envoyer de broadcast "ALL" depuis le client (le client doit normalement envoyer une requête au serveur).
            // ici on envoie vers le serveur pour demander la modification (le serveur décidera / autorisera)
            ModNetwork.CHANNEL.sendToServer(new ToggleInvisiblePacket(pos, player, invisible));
        }
    }

    /**
     * Force l'état invisible côté serveur (authoritative) et notifie les clients.
     * Doit idéalement être appelé côté serveur ; côté client met à jour seulement le cache local.
     */
    public static void forceHideBlock(BlockPos pos, World world, UUID player) {
        forceSetInvisible(pos, world, player, true);
    }

    /**
     * Force la révélation côté serveur (authoritative) et notifie les clients.
     * Doit idéalement être appelé côté serveur ; côté client met à jour seulement le cache local.
     */
    public static void forceRevealBlock(BlockPos pos, World world, UUID player) {
        forceSetInvisible(pos, world, player, false);
    }

    private static void forceSetInvisible(BlockPos pos, World world, UUID player, boolean invisible) {
        if (world instanceof ServerWorld) {
            // Appliquer côté serveur et informer tous les clients du nouvel état
            InvisibleBlockManager.setInvisible(world, pos, invisible, player);
            ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new ToggleInvisiblePacket(pos, player, invisible));
        } else {
            // Côté client : mise à jour locale seulement (cache client)
            InvisibleBlockManager.setInvisible(pos, player, invisible);
            // Ne pas envoyer directement un broadcast ALL depuis le client :
            // si on veut que le serveur applique l'état de force, il faut envoyer un paquet dédié au serveur.
            // Ici on n'envoie rien pour éviter d'avoir le serveur qui *toggle* (comportement actuel du ToggleInvisiblePacket).
        }
    }
}
