package application;
import http_standards.MIMETypes;
import http_standards.StatusCode;
import http_server.Request;
import http_server.Response;
import http_server.Server;
import http_server.Router;

import java.util.HashMap;

public class App {
    public static void main(String args[]) {
        HashMap<String, String> settings = parseServerSettings(args);
        int port = Integer.parseInt(settings.get("port"));
        String directory = settings.get("directory");

        Router app = createRouter(directory);
        Server server = new Server(port, app);
        server.start();
    }

    private static Router createRouter(String directory) {
        Router app = new Router(directory);

        app.staticDirectory("/images");

        app.get("/", (Request request, Response response) -> {
            response.redirect("/images");
        });

        app.get("/simple_get", (Request request, Response response) -> {});

        app.head("/simple_get", (Request request, Response response) -> {});

        app.head("/get_with_body", (Request request, Response response) -> {
            String bodyContent = "Here are all my favorite movies:\n" + "- Harry " +
                    "Potter\n";
            response.head(bodyContent.getBytes(), MIMETypes.plain);
        });

        app.post("/echo_body", (Request request, Response response) -> {
            response.sendBody(request.getBody().getBytes(), request.getContentFileType());
            response.setStatus(StatusCode.ok);
        });

        app.get("/method_options", (Request request, Response response) -> {});

        app.head("/method_options", (Request request, Response response) -> {});

        app.get("/method_options2", (Request request, Response response) -> {});

        app.head("/method_options2", (Request request, Response response) -> {});

        app.put("/method_options2", (Request request, Response response) -> {});

        app.post("/method_options2", (Request request, Response response) -> {});

        app.all("/redirect", (Request request, Response response) -> {
            response.redirect("/simple_get");
        });

        app.post("/dog", (Request request, Response response) -> {
            String uniqueRoute = app.getUniqueRoute(request.getRoute());
            String resourceRoute = app.saveResource(uniqueRoute, request.getContentFileType(),
                    request.getBody().getBytes());
            response.successfulPost(resourceRoute);
        });

        app.put("/cat/1", (Request request, Response response) -> {
            app.saveResource(request.getRoute(), request.getContentFileType(), request.getBody().getBytes());
            response.successfulPut();
        });

        return app;
    }

    private static HashMap<String, String> parseServerSettings(String[] args) {
        HashMap<String, String> customSettings = new HashMap<>();
        customSettings.put("port", "5000");
        customSettings.put("directory", "");

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-p")) {
                customSettings.put("port", args[i + 1]);
            }

            if (args[i].equals("-d")) {
                customSettings.put("directory", args[i + 1]);
            }
        }

        return customSettings;
    }
}
