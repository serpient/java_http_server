package application;
import http_server.Request;
import http_server.Response;
import http_server.Server;
import http_server.Router;

public class App {
    public static void main(String args[]) {
        Router app = createRouter();

        Server server = new Server(setPortNumber(args), app);
        server.start();
    }

    private static Router createRouter() {
        Router app = new Router();

        app.get("/simple_get", (Request request, Response response) -> {});

        app.head("/simple_get", (Request request, Response response) -> {});

        app.head("/get_with_body", (Request request, Response response) -> {
            response.setBody("Here are all my favorite movies:\n" + "- Harry " +
                    "Potter\n");
        });

        app.post("/echo_body", (Request request, Response response) -> {});

        app.get("/method_options", (Request request, Response response) -> {});

        app.head("/method_options", (Request request, Response response) -> {});

        app.get("/method_options2", (Request request, Response response) -> {});

        app.head("/method_options2", (Request request, Response response) -> {});

        app.put("/method_options2", (Request request, Response response) -> {});

        app.post("/method_options2", (Request request, Response response) -> {});

        app.all("/redirect", (Request request, Response response) -> {
            response.redirect("/simple_get");
        });

        return app;
    }

    private static int setPortNumber(String[] terminal_args) {
        return terminal_args.length > 0
                ? Integer.parseInt(terminal_args[0])
                : 5000;
    }
}
