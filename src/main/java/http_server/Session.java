package http_server;

import http_protocol.RequestCreator;

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
            Response response = new Response(router,request);

            if (response.requestIsValid()) {
                router.fillResponseForRequest(request, response);
            }

            byte[] httpResponse = response.getBytes();
            client.sendBinary(httpResponse);

            close();
        }
    }

    public void close() {
        client.close();
        isRunning = false;
        System.out.println("Disconnecting client");
    }
}
