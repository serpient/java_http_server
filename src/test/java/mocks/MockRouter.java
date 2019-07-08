package mocks;

import http_standards.MIMETypes;
import http_server.Request;
import http_server.Response;
import http_server.Router;
import repository.Repository;

public class MockRouter {
    Repository repository;

    public MockRouter(Repository repository) {
        this.repository = repository;
    }

    public Router getApp() {
        return createRouter();
    }

    private Router createRouter() {
        Router app = new Router();

        app.staticDirectory("/public");

        app.setRepository(repository);

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

        app.get("/harry_potter", (Request request, Response response) -> {
            String bodyContent = "Here are all my favorite movies:\n" + "- Harry " +
                    "Potter\n";
            response.sendBody(bodyContent.getBytes(), MIMETypes.plain);
        });

        app.post("/echo_body", (Request request, Response response) -> {
            response.successfulPost(request.getRoute());
        });

        app.get("/method_options", (Request request, Response response) -> {});

        app.head("/method_options", (Request request, Response response) -> {});

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

        app.get("/multiple_parameters", (Request request, Response response) -> {
            String body = "Parameters: \n" + request.getParameters().entrySet();
            response.sendBody(body.getBytes(), MIMETypes.plain);
        });

        return app;
    }
}
