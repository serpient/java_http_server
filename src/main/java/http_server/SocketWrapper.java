package http_server;

import java.io.OutputStream;

public interface SocketWrapper {
    String readData();
    void sendData(String data);
    void close();
    boolean ready();
    void setOutputStream(OutputStream outputStream);
    void sendBinary(byte[] binary);
}
