package http_server;

import java.io.IOException;
import java.net.ServerSocket;

public class HTTPProtocol {
    public static void start(int port, Router router) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            Thread serverThread = new Thread(new Server(serverSocket, router), "HTTPServer");
            serverThread.start();
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }
}