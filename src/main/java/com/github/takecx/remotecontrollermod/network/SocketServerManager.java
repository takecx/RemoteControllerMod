package com.github.takecx.remotecontrollermod.network;

import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

@Mod.EventBusSubscriber
public class SocketServerManager {
    private static ServerSocket serverSocket;
    private static Thread serverThread;

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        serverThread = new Thread(() -> {
            try {
                serverSocket = new ServerSocket(5000); // 例：5000番ポート
                while (!serverSocket.isClosed()) {
                    Socket clientSocket = serverSocket.accept();
                    new Thread(() -> handleClient(clientSocket)).start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "RemoteController-ServerThread");
        serverThread.start();
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            if (serverThread != null && serverThread.isAlive()) {
                serverThread.interrupt();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket socket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String command;
            while ((command = reader.readLine()) != null) {
                String finalCommand = command.trim();
                // ★ここでMinecraft内にコマンドを送る
                NetworkHandler.INSTANCE.sendToServer(new CommandPacket(finalCommand));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
