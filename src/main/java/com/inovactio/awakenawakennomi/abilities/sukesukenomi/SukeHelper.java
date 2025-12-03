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
        } else {
            // client-side: utiliser le cache client (signature différente)
            invisible = !InvisibleBlockManager.isInvisible(pos, player);
            InvisibleBlockManager.setInvisible(pos, player, invisible);
        }

        ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new ToggleInvisiblePacket(pos, player, invisible));
    }
}
