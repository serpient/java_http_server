package http_server;

import java.io.*;
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
            router.setPort(port);
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

                OutputStream outputStream = clientSocket.getOutputStream();
                BufferedReader inputStream = new BufferedReader((new InputStreamReader(clientSocket.getInputStream())));
                ClientSocket clientSocketWrapper = new ClientSocket(clientSocket, inputStream,
                        new StreamWriter(outputStream));

                Session session = new Session(clientSocketWrapper, router);
                int sessionId = new Random().nextInt(5000);
                session.setId(sessionId);
                String threadName = "CLIENT_SESSION_" + sessionId;
                Thread t = new Thread(session, threadName);
                t.start();

                threadMessage("Started Thread: " + threadName);

            } catch (IOException e) {
                threadMessage(e.toString());
            }
        }
    }
}
