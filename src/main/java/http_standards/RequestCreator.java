package http_standards;

import http_server.Request;

public class RequestCreator {
    public static Request from(String request) {
        return new Request(
                Parser.getMethod(request),
                Parser.getRoute(request),
                Parser.getBody(request),
                Parser.getHeaders(request)
        );
    }
}
