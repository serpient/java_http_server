package application;
import html_builder.HTMLBuilder;
import http_server.Settings;
import http_server.OperationResult;
import http_standards.Headers;
import http_standards.MIMETypes;
import http_standards.Parser;
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
            response.forHead(bodyContent, MIMETypes.plain);
        });

        app.post("/echo_body", (Request request, Response response) -> {
            response.setBody(request.getBody(), request.getContentFileType());
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
            OperationResult saveResult = app.saveResource(uniqueRoute, request.getContentFileType(),
                    request.getBody());
            response.forPost(saveResult, uniqueRoute);
        });

        app.get("/cat/:id", (Request request, Response response) -> {
            response.setFile(request.getRoute());
        });

        app.put("/cat/:id", (Request request, Response response) -> {
            OperationResult result = app.saveResource(request.getRoute(), request.getContentFileType(),
                    request.getBody());
            response.forPut(result);
        });

        app.get("/multiple_parameters", (Request request, Response response) -> {
            String body = "Parameters: \n" + request.getParameters().entrySet();
            response.setBody(body, MIMETypes.plain);
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

        app.get("/contacts/:id", (Request request, Response response) -> {
            response.setFile(request.getRoute() + ".json");
        });

        app.patch("/contacts/:id", (Request request, Response response) -> {
            OperationResult updateResult = app.updateJSONResource(request.getRoute(), request.getBody());
            response.forPatch(updateResult);
        });

        return app;
    }
}
