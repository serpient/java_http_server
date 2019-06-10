package http_server;

public class HTTPSession implements Runnable {
    private final SocketWrapper client;
    private Boolean isRunning;

    public HTTPSession(SocketWrapper client) {
        this.client = client;
        this.isRunning = true;
    }

    public void run() {
        while (!Thread.interrupted() && isRunning) {
            String outputLine = "Echo Server: " + client.readData();
            System.err.println(outputLine);
            client.sendData(outputLine);
            close();
        }
    }

    public void close() {
        client.close();
        isRunning = false;
        System.out.println("Disconnecting client");
    }
}
