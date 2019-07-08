package application;
import http_server.Settings;
import http_standards.MIMETypes;
import http_standards.StatusCode;
import http_server.Request;
import http_server.Response;
import http_server.Server;
import http_server.Router;

public class App {
    public static void main(String args[]) {
        Settings.validateSettings(args);
        int port = Settings.getPort();
        String directory = Settings.getDirectory();

        Router app = createRouter(directory);
        Server server = new Server(port, app);
        server.start();

    }

    private static Router createRouter(String directory) {
        Router app = new Router(directory);

        app.get("/simple_get", (Request request, Response response) -> {
        });

        app.head("/simple_get", (Request request, Response response) -> {
        });

        app.head("/get_with_body", (Request request, Response response) -> {
            String bodyContent = "Here are all my favorite movies:\n" + "- Harry " +
                    "Potter\n";
            response.head(bodyContent, MIMETypes.plain);
        });

        app.post("/echo_body", (Request request, Response response) -> {
            response.sendBody(request.getBody(), request.getContentFileType());
            response.setStatus(StatusCode.ok);
        });

        app.get("/method_options", (Request request, Response response) -> {
        });

        app.head("/method_options", (Request request, Response response) -> {
        });

        app.get("/method_options2", (Request request, Response response) -> {
        });

        app.head("/method_options2", (Request request, Response response) -> {
        });

        app.put("/method_options2", (Request request, Response response) -> {
        });

        app.post("/method_options2", (Request request, Response response) -> {
        });

        app.all("/redirect", (Request request, Response response) -> {
            response.redirect("/simple_get");
        });

        app.post("/dog", (Request request, Response response) -> {
            String uniqueRoute = app.getUniqueRoute(request.getRoute());
            String resourceRoute = app.saveResource(uniqueRoute, request.getContentFileType(),
                    request.getBody());
            response.successfulPost(resourceRoute);
        });

        app.put("/cat/1", (Request request, Response response) -> {
            app.saveResource(request.getRoute(), request.getContentFileType(), request.getBody());
            response.successfulPut();
        });

        app.get("/multiple_parameters", (Request request, Response response) -> {
            String body = "Parameters: \n" + request.getParameters().entrySet();
            response.sendBody(body, MIMETypes.plain);
        });

        return app;
    }
}
