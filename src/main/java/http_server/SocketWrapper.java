package http_server;

public interface SocketWrapper {
    String readData();
    void close();
    boolean ready();
    void sendBinary(byte[] binary);
}
