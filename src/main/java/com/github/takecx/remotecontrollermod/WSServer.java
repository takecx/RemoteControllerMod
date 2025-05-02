package com.github.takecx.remotecontrollermod;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class WSServer extends WebSocketServer {

    private APIHandler myAPIHandler = null;
    private static final Logger LOGGER = LogManager.getLogger();

    public WSServer(InetSocketAddress address) {
        super(address);

        this.myAPIHandler = new APIHandler();
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        conn.send("Welcome to the server!"); // This method sends a message to the new client
        broadcast("new connection: " + handshake.getResourceDescriptor()); // This method sends a message to all clients
                                                                           // connected
        LOGGER.debug("new connection to " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        LOGGER.debug(
                "closed " + conn.getRemoteSocketAddress() + " with exit code " + code + " additional info: " + reason);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        LOGGER.debug("received message from " + conn.getRemoteSocketAddress() + ": " + message);
        Object result = null;
        try {
            result = this.myAPIHandler.Process(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (result != null) {
            broadcast((String) result);
        }
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        LOGGER.debug("received ByteBuffer from " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        LOGGER.error("an error occurred on connection " + conn.getRemoteSocketAddress() + ":" + ex);
    }

    @Override
    public void onStart() {

        LOGGER.debug("server started successfully");
    }
}
