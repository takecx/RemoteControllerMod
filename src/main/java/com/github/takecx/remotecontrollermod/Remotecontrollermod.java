package com.github.takecx.remotecontrollermod;

import com.github.takecx.remotecontrollermod.network.NetworkHandler;
import com.github.takecx.remotecontrollermod.network.SocketServerManager;

import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// entity
import net.minecraft.resources.ResourceLocation;

// websocket
import java.io.IOException;
import java.net.InetSocketAddress;

import java.util.stream.Collectors;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraft.client.Minecraft;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("remotecontrollermod")
public class Remotecontrollermod {

    public static final String MODID = "remotecontrollermod";
    public static boolean isShowingMenu = false;

    // for network
    public static SimpleChannel simpleChannel; // used to transmit your network messages
    public static final ResourceLocation simpleChannelRL = new ResourceLocation("remotecontrollmod", "rcmchannel");
    public static final String MESSAGE_PROTOCOL_VERSION = "1.0"; // a version number for the protocol you're using. Can
                                                                 // be used to maintain backward
    public static final byte MOVE_CAMERA_MESSAGE_ID = 63;
    public static final byte SPAWN_ENTITY_MESSAGE_ID = 64;

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    private WSServer wsServer = null;
    private SocketServer sServer = null;

    public Remotecontrollermod() {
        LOGGER.info("RemoteControllerMod is starting up!");
        // // Register the setup method for modloading
        // FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // // Register the enqueueIMC method for modloading
        // FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // // Register the processIMC method for modloading
        // FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // // Register the doClientStuff method for modloading
        // FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        // イベントバス取得
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // ネットワーク登録
        NetworkHandler.register();

        // サーバーイベントリスナー登録
        modEventBus.register(new SocketServerManager());
        // modEventBus.register(SocketServerManager.class);
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        LOGGER.info("Got game settings {}", Minecraft.getInstance().options);

    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo(MODID, "helloworld", () -> {
            LOGGER.info("Hello world from the MDK");
            return "Hello world";
        });
    }

    private void processIMC(final InterModProcessEvent event) {
        // some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}",
                event.getIMCStream().map(m -> m.getMessageSupplier().get()).collect(Collectors.toList()));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) throws IOException {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
        if (!event.getServer().overworld().isClientSide) {
            String host = "localhost";

            // Start WebSocket Server
            // int port = 53199;
            int port = 14711;
            wsServer = new WSServer(new InetSocketAddress(host, port));
            wsServer.start();

            // Start Socket Server
            int socketPort = 14712;
            sServer = new SocketServer(socketPort);
            new Thread(() -> {
                try {
                    sServer.communicate();
                } catch (Exception e) {
                    LOGGER.error(e);
                } finally {
                    LOGGER.info("Closing RemoteControllerMod");
                    if (sServer != null)
                        sServer.Close();
                }
            }).start();
        }
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) throws InterruptedException {
        LOGGER.info("GOODBYE from server stopping");
        if (!event.getServer().getLevel(Level.OVERWORLD).isClientSide) {
            wsServer.stop();
        }
    }

    // You can use EventBusSubscriber to automatically subscribe events on the
    // contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {

        // Register a channel for your packets. You can send multiple types of packets
        // on the same channel. Most mods will only ever
        // need one channel.
        @SubscribeEvent
        public static void onCommonSetupEvent(FMLCommonSetupEvent event) {

            simpleChannel = NetworkRegistry.newSimpleChannel(simpleChannelRL, () -> MESSAGE_PROTOCOL_VERSION,
                    MESSAGE_PROTOCOL_VERSION::equals,
                    MESSAGE_PROTOCOL_VERSION::equals);

            // Register the two different types of messages:
            // AirStrike, which is sent from the client to the server to say "call an air
            // strike on {this location} that I just clicked on"
            // TargetEffect, which is sent from the server to all clients to say "someone
            // called an air strike on {this location}, draw some particles there"

            // simpleChannel.registerMessage(AIRSTRIKE_MESSAGE_ID,
            // AirstrikeMessageToServer.class,
            // AirstrikeMessageToServer::encode, AirstrikeMessageToServer::decode,
            // MessageHandlerOnServer::onMessageReceived,
            // Optional.of(PLAY_TO_SERVER));

            // simpleChannel.registerMessage(MOVE_CAMERA_MESSAGE_ID,
            // MoveCameraMessageToClient.class,
            // MoveCameraMessageToClient::encode, MoveCameraMessageToClient::decode,
            // MessageHandlerOnClient::onMoveCameraMessageReceived,
            // Optional.of(PLAY_TO_CLIENT));
            // simpleChannel.registerMessage(SPAWN_ENTITY_MESSAGE_ID,
            // SpawnEntityMessageToClient.class,
            // SpawnEntityMessageToClient::encode, SpawnEntityMessageToClient::decode,
            // MessageHandlerOnClient::onSpawnEntityMessageReceived,
            // Optional.of(PLAY_TO_CLIENT));

            // it is possible to register the same message class and handler on both sides
            // if you want, eg,
            // simpleChannel.registerMessage(AIRSTRIKE_MESSAGE_ID,
            // AirstrikeMessageToServer.class,
            // AirstrikeMessageBothDirections::encode,
            // AirstrikeMessageBothDirections::decode,
            // MessageHandlerOnBothSides::onMessage);
            // I recommend that you don't do this because it can lead to crashes (and in
            // particular dedicated server problems) if you aren't
            // very careful to keep the client-side and server-side code separate
        }

    }
}