package http_server;

import http_standards.RequestCreator;
import server_logger.ServerLogger;

public class Session implements Runnable {
    private final SocketWrapper client;
    private Router router;
    private Boolean isRunning;
    private ServerLogger log;
    private int sessionId;

    public Session(SocketWrapper client, Router router) {
        this.client = client;
        this.router = router;
        this.isRunning = true;
        this.log = new ServerLogger(router.getRepository(), "./log");
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
            log.writeRequest(sessionId, input);

            Request request = RequestCreator.from(input);
            byte[] httpResponse = new Response(router,request).create();
            client.sendBinary(httpResponse);
            log.writeResponse(sessionId, httpResponse);

            close();
        }
    }

    public void close() {
        client.close();
        isRunning = false;
        System.out.println("Disconnecting client");
    }

    public void setId(int sessionId) {
        this.sessionId = sessionId;
    }
}
