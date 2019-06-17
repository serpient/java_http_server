package http_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Server {
    private ServerSocket server;
    private Router router;

    public Server(int port, Router router) {
        try {
            this.server = new ServerSocket(port);
            this.router = router;
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }

    static void threadMessage(String message) {
        String threadName = Thread.currentThread().getName();
        System.out.format("%s: %s%n", threadName, message);
    }

    public void start() {
        while (true) {
            threadMessage("Waiting for connection");
            Socket clientSocket;
            try {
                clientSocket = server.accept();

                Boolean autoFlushWriter = true;
                PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream(), autoFlushWriter);
                WriterWrapper outputStream = new PrintWriterWrapper(printWriter);
                BufferedReader inputStream = new BufferedReader((new InputStreamReader(clientSocket.getInputStream())));

                ClientSocket clientSocketWrapper = new ClientSocket(clientSocket, inputStream, outputStream);

                Session session = new Session(clientSocketWrapper, router);

                String threadName = "CLIENT_SESSION_" + new Random().nextInt(5000);
                Thread t = new Thread(session, threadName);
                t.start();

                threadMessage("Started Thread: " + threadName);

            } catch (IOException e) {
                threadMessage(e.toString());
            }
        }
    }
}
