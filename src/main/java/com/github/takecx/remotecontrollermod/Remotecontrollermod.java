package com.github.takecx.remotecontrollermod;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// entity
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntityClassification;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.entity.ai.attributes.Attributes;

// websocket
import java.net.InetSocketAddress;
import org.java_websocket.server.WebSocketServer;

import java.util.stream.Collectors;


// The value here should match an entry in the META-INF/mods.toml file
@Mod("remotecontrollermod")
public class Remotecontrollermod {

    public static final String MODID = "remotecontrollermod";

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
        String host = "localhost";
        int port = 53199;

        WebSocketServer server = new WSServer(new InetSocketAddress(host, port));
        server.start();
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
            LOGGER.info("HELLO from Register Block");
        }

        @SubscribeEvent
        public static void onRegisterItems(final RegistryEvent.Register<Item> itemRegistryEvent) {
            LOGGER.info("HELLO from Register Item");

            AgentEgg.setRegistryName(new ResourceLocation(MODID, "egg_remote_agent"));
            itemRegistryEvent.getRegistry().register(AgentEgg);
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
    }
}
