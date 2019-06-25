package mocks;

import http_protocol.StatusCode;
import http_server.Request;
import http_server.Response;
import http_server.Router;

import java.nio.file.Paths;

public class MockRouter {
    public Router getApp() {
        return createRouter();
    }

    private Router createRouter() {
        Router app = new Router();

        app.basePath(Paths.get(System.getProperty("user.dir")));

        app.staticDirectory("/public");

        app.get("/", (Request request, Response response) -> {
            response.redirect("/public");
        });

        app.get("/simple_get", (Request request, Response response) -> {});

        app.head("/simple_get", (Request request, Response response) -> {});

        app.head("/get_with_body", (Request request, Response response) -> {
            response.setBody("Here are all my favorite movies:\n" + "- Harry " +
                    "Potter\n");
        });

        app.get("/harry_potter", (Request request, Response response) -> {
            response.setBody("Here are all my favorite movies:\n" + "- Harry " +
                    "Potter\n");
        });

        app.post("/echo_body", (Request request, Response response) -> {
            response.setStatus(StatusCode.ok);
            response.setBody(request.getBody());
        });

        app.get("/method_options", (Request request, Response response) -> {});

        app.head("/method_options", (Request request, Response response) -> {});

        app.all("/redirect", (Request request, Response response) -> {
            response.redirect("/simple_get");
        });

        app.post("/dog/1", (Request request, Response response) -> {

        });

        app.get("/dog/1", (Request request, Response response) -> {
            response.sendFile(request.getRoute());
        });

        app.post("/dog/3", (Request request, Response response) -> {

        });

        app.get("/dog/3", (Request request, Response response) -> {
            response.sendFile(request.getRoute());
        });

        return app;
    }
}
