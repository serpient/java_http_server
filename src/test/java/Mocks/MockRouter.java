package Mocks;

import http_server.Request;
import http_server.Response;
import http_server.Router;

public class MockRouter {
    Router app;

    public MockRouter() {
        this.app = new Router();
    }

    public Router getApp() {
        initializeRoutes();
        return app;
    }

    private void initializeRoutes() {
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


        app.post("/echo_body", (Request request, Response response) -> {});

        app.get("/method_options", (Request request, Response response) -> {});

        app.head("/method_options", (Request request, Response response) -> {});

        app.all("/redirect", (Request request, Response response) -> {
            response.redirect("/simple_get");
        });
    }
}
