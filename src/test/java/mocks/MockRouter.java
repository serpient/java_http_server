package mocks;

import http_protocol.MIMETypes;
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
            response.initFromRedirect("/public");
        });

        app.get("/simple_get", (Request request, Response response) -> {});

        app.head("/simple_get", (Request request, Response response) -> {});

        app.head("/get_with_body", (Request request, Response response) -> {
            String bodyContent = "Here are all my favorite movies:\n" + "- Harry " +
                    "Potter\n";
            response.initFromHeadResponse(bodyContent.getBytes(), MIMETypes.plain);
        });

        app.get("/harry_potter", (Request request, Response response) -> {
            String bodyContent = "Here are all my favorite movies:\n" + "- Harry " +
                    "Potter\n";
            response.initFromBody(bodyContent.getBytes(), MIMETypes.plain);
        });

        app.post("/echo_body", (Request request, Response response) -> {
            if (request.getBody() == null) {
                response.initFromEmptyData();
                return;
            }
            response.initFromPostData(
                    request.getBody().getBytes(),
                    request.getRoute()
            );
        });

        app.get("/method_options", (Request request, Response response) -> {});

        app.head("/method_options", (Request request, Response response) -> {});

        app.all("/initFromRedirect", (Request request, Response response) -> {
            response.initFromRedirect("/simple_get");
        });

        app.post("/dog", (Request request, Response response) -> {
            if (request.getBody() == null) {
                response.initFromEmptyData();
                return;
            }
            String uniqueRoute = app.getUniqueRoute(request.getRoute());
            app.saveResource(uniqueRoute, request.getContentFileType(), request.getBody().getBytes());
            response.initFromPostData(
                    request.getBody().getBytes(),
                    uniqueRoute
            );
        });

        app.put("/cat/1", (Request request, Response response) -> {
            if (request.getBody() == null) {
                response.initFromEmptyData();
                return;
            }
            app.saveResource(request.getRoute(), request.getContentFileType(), request.getBody().getBytes());
            response.initFromPutData(request.getBody().getBytes());
        });

        return app;
    }
}
