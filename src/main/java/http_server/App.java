package http_server;

import java.io.IOException;
import java.net.ServerSocket;

public class App {
    public static void main(String args[]) {
        try {
            ServerSocket serverSocket = new ServerSocket(setPortNumber(args));
            Thread serverThread = new Thread(new HTTPProtocol(serverSocket), "HTTPServer");
            serverThread.start();
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }

    private static int setPortNumber(String[] terminal_args) {
        return terminal_args.length > 0
            ? Integer.parseInt(terminal_args[0])
            : 5000;
    }
}