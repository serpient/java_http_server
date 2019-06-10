package http_server;

public interface WriterWrapper {
    void send(String data);
    void close();
}
