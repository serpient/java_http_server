package http_server;

import http_protocol.RequestCreator;
import http_protocol.Stringer;

public class Session implements Runnable {
    private final SocketWrapper client;
    private Router router;
    private Boolean isRunning;

    public Session(SocketWrapper client, Router router) {
        this.client = client;
        this.router = router;
        this.isRunning = true;
    }

    public void run() {
        while (!Thread.interrupted() && isRunning) {
            if (!client.ready()) {
                try {
                    Thread.sleep(1000);
                    continue;
                } catch (InterruptedException e) {
                    close();
                    return;
                }
            }

            String input = client.readData();

            Request request = RequestCreator.from(input);
            String response = new Response(request, router).generateResponse();

            serverPrint(response);

            client.sendData(response);

            close();
        }
    }

    public void close() {
        client.close();
        isRunning = false;
        System.out.println("Disconnecting client");
    }

    private void serverPrint(String message) {
        System.out.println(Stringer.crlf + message + Stringer.crlf);
    }
}
