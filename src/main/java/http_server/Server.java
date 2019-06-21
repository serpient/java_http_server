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
                OutputStream outputStream = clientSocket.getOutputStream();
                PrintWriter printWriter = new PrintWriter(outputStream, autoFlushWriter);
                WriterWrapper writer = new PrintWriterWrapper(printWriter);
                BufferedReader inputStream = new BufferedReader((new InputStreamReader(clientSocket.getInputStream())));

                ClientSocket clientSocketWrapper = new ClientSocket(clientSocket, inputStream, writer);
                clientSocketWrapper.setOutputStream(outputStream);

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
