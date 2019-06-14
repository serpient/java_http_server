package application;
import http_server.HTTPProtocol;
import http_server.Router;

public class App {
    static Router app;
    static HTTPProtocol server;

    public static void main(String args[]) {
        app = new Router();
        createRoutes();

        server = new HTTPProtocol();

        server.start(setPortNumber(args), app);
    }

    private static void createRoutes() {
        app.get("/simple_get", (String req, String res) -> "");

        app.head("/simple_get", (String req, String res) -> "");

        app.get("/get_with_body", (String req, String res) -> {
            return "Here are all my favorite movies:\n" + "- Harry " +
                    "Potter\n";
        });

        app.head("/get_with_body", (String req, String res) -> {
            return "Here are all my favorite movies:\n" + "- Harry " +
                    "Potter\n";
        });

        app.post("/echo_body", (String req, String res) -> "");
    }

    private static int setPortNumber(String[] terminal_args) {
        return terminal_args.length > 0
                ? Integer.parseInt(terminal_args[0])
                : 5000;
    }
}
