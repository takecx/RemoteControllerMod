package com.github.takecx.remotecontrollermod.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("remotecontrollermod", "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;

        INSTANCE.messageBuilder(CommandPacket.class, id, NetworkDirection.PLAY_TO_SERVER)
                .encoder(CommandPacket::encode)
                .decoder(CommandPacket::decode)
                .consumerMainThread(CommandPacket::handle)
                .add();
    }
}
