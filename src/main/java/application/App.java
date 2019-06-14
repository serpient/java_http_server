package application;
import http_server.RequestParser;
import http_server.Response;
import http_server.Server;
import http_server.Router;

public class App {
    static Router app;
    static Server server;

    public static void main(String args[]) {
        app = new Router();
        createRoutes();

        server = new Server(setPortNumber(args), app);
        server.start();
    }

    private static void createRoutes() {
        app.get("/simple_get", (RequestParser req, Response res) -> {});

        app.head("/simple_get", (RequestParser req, Response res) -> {});

        app.get("/get_with_body", (RequestParser req, Response res) -> {
            res.body("Here are all my favorite movies:\n" + "- Harry " +
                    "Potter\n");
        });

        app.head("/get_with_body", (RequestParser req, Response res) -> {
            res.body("Here are all my favorite movies:\n" + "- Harry " +
                    "Potter\n");
        });

        app.post("/echo_body", (RequestParser req, Response res) -> {});
    }

    private static int setPortNumber(String[] terminal_args) {
        return terminal_args.length > 0
                ? Integer.parseInt(terminal_args[0])
                : 5000;
    }
}
