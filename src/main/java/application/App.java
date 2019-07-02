package application;
import http_standards.MIMETypes;
import http_standards.StatusCode;
import http_server.Request;
import http_server.Response;
import http_server.Server;
import http_server.Router;

import java.nio.file.Path;
import java.nio.file.Paths;

public class App {
    public static void main(String args[]) {
        Router app = createRouter();

        Server server = new Server(setPortNumber(args), app);
        server.start();
    }

    private static Router createRouter() {
        Router app = new Router();

        app.basePath(getProjectBasePath());

        app.staticDirectory("/public");

        app.get("/", (Request request, Response response) -> {
            response.redirect("/public");
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

    private static int setPortNumber(String[] terminal_args) {
        return terminal_args.length > 0
                ? Integer.parseInt(terminal_args[0])
                : 5000;
    }

    private static Path getProjectBasePath() {
        return Paths.get(System.getProperty("user.dir"));
    }
}
