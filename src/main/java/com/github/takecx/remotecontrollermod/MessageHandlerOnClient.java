package com.github.takecx.remotecontrollermod;

import com.github.takecx.remotecontrollermod.messages.MessageBase;
import com.github.takecx.remotecontrollermod.messages.MoveCameraMessageToClient;
//import com.github.takecx.remotecontrollermod.messages.SpawnEntityMessageToClient;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;
import java.util.function.Supplier;


/**
 * The MessageHandlerOnClient is used to process the network message once it has arrived on the Client side.
 * WARNING!  The MessageHandler runs in its own thread.  This means that if your onMessage code
 * calls any vanilla objects, it may cause crashes or subtle problems that are hard to reproduce.
 * Your onMessage handler should create a task which is later executed by the client or server thread as
 * appropriate - see below.
 * User: The Grey Ghost
 * Date: 15/01/2015
 */
public class MessageHandlerOnClient {

  private static boolean checkMessage(final MessageBase message, Supplier<NetworkEvent.Context> ctxSupplier){
    NetworkEvent.Context ctx = ctxSupplier.get();
    LogicalSide sideReceived = ctx.getDirection().getReceptionSide();
    ctx.setPacketHandled(true);

    if (sideReceived != LogicalSide.CLIENT) {
      LOGGER.warn("TargetEffectMessageToClient received on wrong side:" + ctx.getDirection().getReceptionSide());
      return false;
    }
    if (!message.isMessageValid()) {
      LOGGER.warn("TargetEffectMessageToClient was invalid" + message.message2String());
      return false;
    }
    // we know for sure that this handler is only used on the client side, so it is ok to assume
    //  that the ctx handler is a client, and that Minecraft exists.
    // Packets received on the server side must be handled differently!  See MessageHandlerOnServer

    Optional<ClientWorld> clientWorld = LogicalSidedProvider.CLIENTWORLD.get(sideReceived);
    if (!clientWorld.isPresent()) {
      LOGGER.warn("TargetEffectMessageToClient context could not provide a ClientWorld.");
      return false;
    }
    return true;
  }

  /**
   * Called when a message is received of the appropriate type.
   * CALLED BY THE NETWORK THREAD, NOT THE CLIENT THREAD
   */
  public static void onMoveCameraMessageReceived(final MoveCameraMessageToClient message, Supplier<NetworkEvent.Context> ctxSupplier) {
    if(!checkMessage(message,ctxSupplier)) return;

    // This code creates a new task which will be executed by the client during the next tick
    //  In this case, the task is to call messageHandlerOnClient.processMessage(worldclient, message)
    NetworkEvent.Context ctx = ctxSupplier.get();
    LogicalSide sideReceived = ctx.getDirection().getReceptionSide();
    Optional<ClientWorld> clientWorld = LogicalSidedProvider.CLIENTWORLD.get(sideReceived);
    ctx.enqueueWork(() -> processMessage(clientWorld.get(), message));
  }

  // This message is called from the Client thread.
  //   It spawns a number of Particle particles at the target location within a short range around the target location
  private static void processMessage(ClientWorld worldClient, MoveCameraMessageToClient message)
  {
//                RenderSystem.clear(256,false);
//                RenderSystem.matrixMode(GL11.GL_MODELVIEW);
//                RenderSystem.loadIdentity();

//                RenderSystem.initRenderThread();
                RenderSystem.translatef(0.5F,0.5F,0.5F);
  }

//  public static void onSpawnEntityMessageReceived(final SpawnEntityMessageToClient message, Supplier<NetworkEvent.Context> ctxSupplier) {
//    if(!checkMessage(message,ctxSupplier)) return;
//
//    // This code creates a new task which will be executed by the client during the next tick
//    //  In this case, the task is to call messageHandlerOnClient.processMessage(worldclient, message)
//    NetworkEvent.Context ctx = ctxSupplier.get();
//    LogicalSide sideReceived = ctx.getDirection().getReceptionSide();
//    Optional<ClientWorld> clientWorld = LogicalSidedProvider.CLIENTWORLD.get(sideReceived);
//    ctx.enqueueWork(() -> processSpawnEntityMessage(clientWorld.get(), message));
//  }

  // This message is called from the Client thread.
  //   It spawns a number of Particle particles at the target location within a short range around the target location
//  private static void processSpawnEntityMessage(ClientWorld worldClient, SpawnEntityMessageToClient message)
//  {
//    Optional<Entity> entity_op = EntityType.loadEntityUnchecked(message.getTargetEntity(),worldClient);
//    entity_op.ifPresent(entity -> {
//      BlockPos entityPos = message.getTargetCoordinates();
//      entity.setPosition(entityPos.getX(),entityPos.getY(),entityPos.getZ());
//      worldClient.addEntity(entity);
//    });
//  }

  public static boolean isThisProtocolAcceptedByClient(String protocolVersion) {
    return Remotecontrollermod.MESSAGE_PROTOCOL_VERSION.equals(protocolVersion);
  }

  private static final Logger LOGGER = LogManager.getLogger();
}
