package http_server;

public interface Callback {
    void run(Request request, Response response);
}
