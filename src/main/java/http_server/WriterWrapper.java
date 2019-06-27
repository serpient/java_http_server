package http_server;

public interface WriterWrapper {
    void send(byte[] data);
    void close();
}
