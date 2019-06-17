package application;
import http_server.Request;
import http_server.Response;
import http_server.Server;
import http_server.Router;

public class App {
    static Router app;
    static Server server;

    public static void main(String args[]) {
        app = createRouter();

        server = new Server(setPortNumber(args), app);
        server.start();
    }

    private static Router createRouter() {
        Router app = new Router();

        app.get("/simple_get", (Request request, Response response) -> {});

        app.head("/simple_get", (Request request, Response response) -> {});

        app.get("/get_with_body", (Request request, Response response) -> {
            response.body("Here are all my favorite movies:\n" + "- Harry " +
                    "Potter\n");
        });

        app.head("/get_with_body", (Request request, Response response) -> {
            response.body("Here are all my favorite movies:\n" + "- Harry " +
                    "Potter\n");
        });

        app.post("/echo_body", (Request request, Response response) -> {});

        return app;
    }

    private static int setPortNumber(String[] terminal_args) {
        return terminal_args.length > 0
                ? Integer.parseInt(terminal_args[0])
                : 5000;
    }
}
