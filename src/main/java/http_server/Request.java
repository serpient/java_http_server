package http_server;

import java.util.HashMap;

public class Request {
    private String method;
    private String route;
    private String body;
    private HashMap<String, String> headers;

    public Request(String method, String route, String body, HashMap<String, String> headers) {
        this.method = method;
        this.route = route;
        this.body = body;
        this.headers = headers;
    }

    public String getMethod() {
        return method;
    }

    public String getBody() {
        return body;
    }

    public String getRoute() {
        return route;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }
}
