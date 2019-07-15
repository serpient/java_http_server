package mocks;

import html_builder.HTMLBuilder;
import http_server.OperationResult;
import http_standards.Headers;
import http_standards.MIMETypes;
import http_server.Request;
import http_server.Response;
import http_server.Router;
import http_standards.Parser;
import http_standards.StatusCode;
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

        app.directory("/public");

        app.setRepository(repository);

        app.get("/", (Request request, Response response) -> {
            response.redirect("/public");
        });

        app.get("/simple_get", (Request request, Response response) -> {});

        app.head("/simple_get", (Request request, Response response) -> {});

        app.head("/get_with_body", (Request request, Response response) -> {
            String bodyContent = "Here are all my favorite movies:\n" + "- Harry " +
                    "Potter\n";
            response.forHead(bodyContent.getBytes(), MIMETypes.plain);
        });

        app.get("/harry_potter", (Request request, Response response) -> {
            String bodyContent = "Here are all my favorite movies:\n" + "- Harry " +
                    "Potter\n";
            response.setBody(bodyContent.getBytes(), MIMETypes.plain);
        });

        app.post("/echo_body", (Request request, Response response) -> {
            response.forPost(new OperationResult(true, StatusCode.created), request.getRoute());
        });

        app.get("/method_options", (Request request, Response response) -> {});

        app.head("/method_options", (Request request, Response response) -> {});

        app.all("/redirect", (Request request, Response response) -> {
            response.redirect("/simple_get");
        });

        app.post("/dog", (Request request, Response response) -> {
            String uniqueRoute = app.getUniqueRoute(request.getRoute());
            OperationResult result = app.saveResource(uniqueRoute, request.getContentFileType(),
                    request.getBody().getBytes());
            response.forPost(result, uniqueRoute);
        });

        app.put("/cat/1", (Request request, Response response) -> {
            OperationResult result = app.saveResource(request.getRoute(), request.getContentFileType(),
                    request.getBody().getBytes());
            response.forPut(result);
        });

        app.get("/multiple_parameters", (Request request, Response response) -> {
            String body = "Parameters: \n" + request.getParameters().entrySet();
            response.setBody(body.getBytes(), MIMETypes.plain);
        });

        app.get("/form", (Request request, Response response) -> {
            HTMLBuilder htmlBuilder = new HTMLBuilder();
            htmlBuilder.append("<h2>Registration</h2>\n" +
                    "\n" +
                    "<form method=\"get\" action=\"/form_action\">\n" +
                    "  First name:<br>\n" +
                    "  <input type=\"text\" name=\"firstname\">\n" +
                    "  <br>\n" +
                    "  Last name:<br>\n" +
                    "  <input type=\"text\" name=\"lastname\">\n" +
                    "  <br><br>\n" +
                    "  <input type=\"submit\" value=\"Submit\">\n" +
                    "</form> ");
            response.setBody(htmlBuilder.generate().getBytes(), MIMETypes.html);
        });

        app.get("/form_action", (Request request, Response response) -> {
            String uniqueRoute = app.getUniqueRoute(request.getRoute());
            OperationResult result = app.saveResource(uniqueRoute, MIMETypes.getFileType(MIMETypes.plain),
                    (request.getParameters().entrySet() + "").getBytes());
            response.forPost(result, uniqueRoute);
            String body = "Parameters: \n" + request.getParameters().entrySet();
            response.setBody(body.getBytes(), MIMETypes.plain);
        });

        app.get("/post_form", (Request request, Response response) -> {
            HTMLBuilder htmlBuilder = new HTMLBuilder();
            htmlBuilder.append("<h2>Registration</h2>\n" +
                    "\n" +
                    "<form enctype=\"application/x-www-form-urlencoded\" method=\"post\" action=\"/post_form\">\n" +
                    "  First name:<br>\n" +
                    "  <input type=\"text\" name=\"firstname\">\n" +
                    "  <br>\n" +
                    "  Last name:<br>\n" +
                    "  <input type=\"text\" name=\"lastname\">\n" +
                    "  <br><br>\n" +
                    "  <input type=\"submit\" value=\"Submit\">\n" +
                    "</form> ");
            response.setBody(htmlBuilder.generate().getBytes(), MIMETypes.html);
        });

        app.post("/post_form", (Request request, Response response) -> {
            String uniqueRoute = app.getUniqueRoute(request.getRoute());
            String content;
            if (request.getHeaders().get(Headers.contentType).equals(MIMETypes.formUrlEncoded)) {
                content = Parser.decodeData(request.getBody()).entrySet() + "";
            } else {
                content = request.getBody();
            }
            OperationResult result = app.saveResource(uniqueRoute, "txt", content);
            response.forPost(result, uniqueRoute, "Parameters: \n" + content, MIMETypes.plain);
        });

        app.patch("/contacts/:id", (Request request, Response response) -> {
            OperationResult updateResult = app.updateJSONResource(request.getRoute(), request.getBody());
            response.forPatch(updateResult);
        });

        return app;
    }
}
