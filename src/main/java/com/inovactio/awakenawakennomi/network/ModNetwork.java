package com.inovactio.awakenawakennomi.network;

import com.inovactio.awakenawakennomi.AwakenAwakenNoMiMod;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class ModNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(AwakenAwakenNoMiMod.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int id = 0;

    public static void register() {
        CHANNEL.registerMessage(id++, ToggleInvisiblePacket.class,
                ToggleInvisiblePacket::encode,
                ToggleInvisiblePacket::decode,
                ToggleInvisiblePacket::handle);

        CHANNEL.registerMessage(id++, RemovePlayerInvisibleBlocksPacket.class,
                RemovePlayerInvisibleBlocksPacket::encode,
                RemovePlayerInvisibleBlocksPacket::decode,
                RemovePlayerInvisibleBlocksPacket::handle);
    }
}
