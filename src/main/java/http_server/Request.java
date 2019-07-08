package http_server;

import http_standards.Headers;
import http_standards.MIMETypes;

import java.util.HashMap;

public class Request {
    private String method;
    private String route;
    private String body;
    private HashMap<String, String> headers;
    private HashMap<String, String> parameters;

    public Request(String method, String route, String body, HashMap<String, String> headers,
                   HashMap<String, String> parameters) {
        this.method = method;
        this.route = route;
        this.body = body;
        this.headers = headers;
        this.parameters = parameters;

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

    public HashMap<String, String> getParameters() {
        return parameters;
    }

    public String getContentFileType() {
        return MIMETypes.getFileType(getHeaders().get(Headers.contentType));
    }
}
