package http_server;

public interface SocketWrapper {
    String readData();
    void sendData(String data);
    void close();
}
