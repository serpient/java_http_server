package http_server;

public class RequestParser {
    String responseStatus;
    String responseBody;
    String requestMethod;
    String requestRoute;
    String requestBody;

    public RequestParser(String requestMethod, String requestRoute, String requestBody) {
        this.requestMethod = requestMethod;
        this.requestRoute = requestRoute;
        this.responseStatus = "";
        this.responseBody = "";
        this.requestBody = requestBody;

        parseRequest();
    }

    public String getResponseStatus() {
        return responseStatus;
    }

    public String getResponseBody() {
        return responseBody;
    }

    private void parseRequest() {
        if (requestRoute.equals("/simple_get")) {
            switch (requestMethod) {
                case "GET":
                case "HEAD":
                    updateResponseStatus("200 OK");
                    break;
            }
        } else if (requestRoute.equals("/get_with_body")) {
            switch (requestMethod) {
                case "GET":
                case "HEAD":
                    updateResponseBody("Here are all my favorite movies:\n" + "- Harry Potter\n");
                case "POST":
                    updateResponseStatus("200 OK");
                    break;
            }
        } else if (requestRoute.equals("/echo_body")) {
            switch (requestMethod) {
                case "POST":
                    updateResponseBody(requestBody); // echo back the body
                    updateResponseStatus("200 OK");
                    break;
            }
        } else {
                updateResponseStatus("404 Not Found");
        }
    }

    private void updateResponseStatus(String newStatus) {
        this.responseStatus = newStatus;
    }

    private void updateResponseBody(String newBody) {
        this.responseBody = newBody;
    }

}
