package com.github.takecx.remotecontrollermod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {
    private static final Logger LOGGER = LogManager.getLogger();
    private ServerSocket serverSocket;
    private boolean isListening = true;

    public SocketServer(int port) throws IOException {
        this.serverSocket = new ServerSocket();
        this.serverSocket.bind(new InetSocketAddress("localhost",port));
        LOGGER.info("SocketServer listening on port" + port);
    }

    public void communicate() throws Exception {
        while(isListening){
            Socket socket = null;
            BufferedReader reader = null;
            PrintWriter writer = null;
            try{
                // 接続されるまでaccept()で待機
                socket = this.serverSocket.accept();

                reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));

                APIHandler apiHandler = new APIHandler();

                String clientSentence;
                while((clientSentence = reader.readLine()) != null) {
                    apiHandler.Process(clientSentence);
                }
            }catch (Exception e){
                LOGGER.error(e);
            }finally {
                if (socket != null){
                    try {
                        socket.close();
                    } catch (IOException e) {
                        LOGGER.error(e);
                    }
                }
                if (reader != null)
                    try {
                        reader.close();
                    } catch (IOException e) {
                        LOGGER.error(e);
                    }
                if (writer != null) {
                    writer.close();
                }
            }
        }
    }

    public void Close() {
        LOGGER.info("Closing socket");
        isListening = false;
        if (serverSocket != null){
            try {
                serverSocket.close();
            } catch (IOException e) {
                LOGGER.error(e);
            }
        }
    }

}
