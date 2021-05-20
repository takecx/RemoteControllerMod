package com.github.takecx.remotecontrollermod;

import com.github.takecx.remotecontrollermod.lists.BlockList;
import com.github.takecx.remotecontrollermod.lists.ItemList;
import com.github.takecx.remotecontrollermod.messages.MoveCameraMessageToClient;
import com.github.takecx.remotecontrollermod.messages.SpawnEntityMessageToClient;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.*;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// entity
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntityClassification;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.ai.attributes.Attributes;

// websocket
import java.net.InetSocketAddress;

import java.util.Optional;
import java.util.stream.Collectors;

import static net.minecraftforge.fml.network.NetworkDirection.PLAY_TO_CLIENT;


// The value here should match an entry in the META-INF/mods.toml file
@Mod("remotecontrollermod")
public class Remotecontrollermod {

    public static final String MODID = "remotecontrollermod";
    public static boolean isShowingMenu = false;

    // for network
    public static SimpleChannel simpleChannel;    // used to transmit your network messages
    public static final ResourceLocation simpleChannelRL = new ResourceLocation("remotecontrollmod", "rcmchannel");
    public static final String MESSAGE_PROTOCOL_VERSION = "1.0";  // a version number for the protocol you're using.  Can be used to maintain backward
    public static final byte MOVE_CAMERA_MESSAGE_ID = 63;
    public static final byte SPAWN_ENTITY_MESSAGE_ID = 64;

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    private WSServer wsServer = null;

    public static final EntityType AGENT =
            EntityType.Builder.create(AgentEntity::new, EntityClassification.CREATURE)
                    .build("agent");

    private static final Item AgentEgg = new SpawnEggItem(AGENT, 0xFFFFFF, 0xFF0000,
            new Item.Properties()
                    .group(ItemGroup.MISC));

    public Remotecontrollermod() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);

        RenderingRegistry.registerEntityRenderingHandler(AGENT, new IRenderFactory () {
            @Override
            public EntityRenderer<? super AgentEntity> createRenderFor(EntityRendererManager manager) {
                return new AgentRenderer(manager, new AgentModel(), 0.3f);
            }
        });
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
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m -> m.getMessageSupplier().get()).
                collect(Collectors.toList()));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
        if(!event.getServer().getWorld(World.OVERWORLD).isRemote){
            String host = "localhost";
//        int port = 53199;
            int port = 14711;

            wsServer = new WSServer(new InetSocketAddress(host, port));
            wsServer.start();
        }
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
            LOGGER.info("HELLO from Register Block");
            blockRegistryEvent.getRegistry().registerAll(
                    BlockList.StarBlock
            );
        }

        @SubscribeEvent
        public static void onRegisterItems(final RegistryEvent.Register<Item> itemRegistryEvent) {
            LOGGER.info("HELLO from Register Item");

            AgentEgg.setRegistryName(new ResourceLocation(MODID, "egg_remote_agent"));
            itemRegistryEvent.getRegistry().register(AgentEgg);

            itemRegistryEvent.getRegistry().registerAll(
                    ItemList.StarIngot = new Item(new Item.Properties().group(ItemGroup.MISC))
                            .setRegistryName(new ResourceLocation(MODID, "star_ingot")),
                    ItemList.StarBlock = new BlockItem(BlockList.StarBlock, new Item.Properties().group(ItemGroup.MISC))
                            .setRegistryName(BlockList.StarBlock.getRegistryName())
            );
        }

        @SubscribeEvent
        public static void onRegisterEntities(final RegistryEvent.Register<EntityType<?>> entityRegisterEvent) {
            LOGGER.info("HELLO from Register Entity");

            GlobalEntityTypeAttributes.put(AGENT,
                    AgentEntity.func_233666_p_()
                            .createMutableAttribute(Attributes.MAX_HEALTH, 999.0D)
                            .createMutableAttribute(Attributes.ATTACK_DAMAGE, 99.0D)
                            .createMutableAttribute(Attributes.ATTACK_SPEED, 99.0D)
                            .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.5D)
                            .create());

            AGENT.setRegistryName(MODID, "entity_remote_agent");
            entityRegisterEvent.getRegistry().register(AGENT);
        }

        // Register a channel for your packets.  You can send multiple types of packets on the same channel.  Most mods will only ever
        //  need one channel.
        @SubscribeEvent
        public static void onCommonSetupEvent(FMLCommonSetupEvent event) {

            simpleChannel = NetworkRegistry.newSimpleChannel(simpleChannelRL, () -> MESSAGE_PROTOCOL_VERSION,
                    MessageHandlerOnClient::isThisProtocolAcceptedByClient,
                    MessageHandlerOnServer::isThisProtocolAcceptedByServer);

            // Register the two different types of messages:
            //  AirStrike, which is sent from the client to the server to say "call an air strike on {this location} that I just clicked on"
            //  TargetEffect, which is sent from the server to all clients to say "someone called an air strike on {this location}, draw some particles there"

//            simpleChannel.registerMessage(AIRSTRIKE_MESSAGE_ID, AirstrikeMessageToServer.class,
//                    AirstrikeMessageToServer::encode, AirstrikeMessageToServer::decode,
//                    MessageHandlerOnServer::onMessageReceived,
//                    Optional.of(PLAY_TO_SERVER));

            simpleChannel.registerMessage(MOVE_CAMERA_MESSAGE_ID, MoveCameraMessageToClient.class,
                    MoveCameraMessageToClient::encode, MoveCameraMessageToClient::decode,
                    MessageHandlerOnClient::onMoveCameraMessageReceived,
                    Optional.of(PLAY_TO_CLIENT));
            simpleChannel.registerMessage(SPAWN_ENTITY_MESSAGE_ID, SpawnEntityMessageToClient.class,
                    SpawnEntityMessageToClient::encode, SpawnEntityMessageToClient::decode,
                    MessageHandlerOnClient::onSpawnEntityMessageReceived,
                    Optional.of(PLAY_TO_CLIENT));

            // it is possible to register the same message class and handler on both sides if you want, eg,
//    simpleChannel.registerMessage(AIRSTRIKE_MESSAGE_ID, AirstrikeMessageToServer.class,
//            AirstrikeMessageBothDirections::encode, AirstrikeMessageBothDirections::decode,
//            MessageHandlerOnBothSides::onMessage);
            // I recommend that you don't do this because it can lead to crashes (and in particular dedicated server problems) if you aren't
            //    very careful to keep the client-side and server-side code separate
        }

    }
}