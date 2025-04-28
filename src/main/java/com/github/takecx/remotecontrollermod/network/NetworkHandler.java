package com.github.takecx.remotecontrollermod.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.SimpleChannel;
import net.minecraftforge.network.event.EventNetworkChannel;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = ChannelBuilder.named(new ResourceLocation("remotecontrollermod", "main"))
            .networkProtocolVersion(PROTOCOL_VERSION::toString)
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .simpleChannel();

    public static void register() {
        int id = 0;

        INSTANCE.messageBuilder(CommandPacket.class, id, NetworkDirection.PLAY_TO_SERVER)
                .encoder(CommandPacket::encode)
                .decoder(CommandPacket::decode)
                .consumerMainThread(CommandPacket::handle)
                .add();
    }
}
