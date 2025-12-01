package com.inovactio.awakenawakennomi.abilities.sukesukenomi;

import com.inovactio.awakenawakennomi.api.common.InvisibleBlockManager;
import com.inovactio.awakenawakennomi.network.ModNetwork;
import com.inovactio.awakenawakennomi.network.ToggleInvisiblePacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

import java.awt.*;

public class SukeHelper {

    public static Color SUKE_COLOR = new Color(162, 229, 229, 152);

    public static void toggleBlockInvisibility(BlockPos pos, World world) {
        boolean invisible = !InvisibleBlockManager.isInvisible(world, pos);
        InvisibleBlockManager.setInvisible(world, pos, invisible);
        ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new ToggleInvisiblePacket(pos, invisible));
    }
}
