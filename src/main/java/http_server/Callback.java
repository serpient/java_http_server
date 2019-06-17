package http_server;

public interface Callback {
    void run(RequestParser req, Response res);
}
