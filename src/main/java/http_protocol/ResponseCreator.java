package http_protocol;

import http_server.Callback;
import http_server.Request;
import http_server.Response;
import http_server.Router;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class ResponseCreator {
    private Response response;
    private Request request;
    private Router router;
    private HashMap<String, Callback> methodCollection;

    public ResponseCreator(Response response, Request request, Router router) {
        this.response = response;
        this.request = request;
        this.router = router;
        this.methodCollection = router.getMethodCollection(request.getRoute());
    }

    public String create() {
        if (methodCollection.isEmpty()) {
            response.setStatus(StatusCode.notFound);
            return responseBuilder();
        }

        if (request.getMethod().equals(Methods.options)) {
            response.setHeader(Headers.allowedHeaders, createOptionsHeader());
            return responseBuilder();
        }

        if (methodCollection.get(request.getMethod()) == null) {
            response.setStatus(StatusCode.methodNotAllowed);
            response.setHeader(Headers.allowedHeaders, createOptionsHeader());
            return responseBuilder();
        }

        if (hasBody(request.getBody())) {
            String newBody = response.getBody();
            newBody += request.getBody();
            response.setBody(newBody);
        }

        router.runCallback(request, response);

        return responseBuilder();
    }

    public String buildStatus() {
        return "HTTP/1.1 " + response.getStatus() + Stringer.crlf;
    }

    public String buildHeader() {
        response.setHeader(Headers.date, currentDateTime());
        response.setHeader(Headers.server, "JavaServer/0.1");

        if (hasBody(response.getBody())) {
            response.setHeader(Headers.contentType, "text/plain");
            response.setHeader(Headers.contentLength, Integer.toString(getContentLength(response.getBody())));
        }

        return createHeaderString();
    }

    public String buildBody() {
        if (!request.getMethod().equals(Methods.head) && hasBody(response.getBody())) {
            return Stringer.crlf + response.getBody();
        } else {
            return "";
        }
    }

    private String responseBuilder() {
        return buildStatus() + buildHeader() + buildBody();
    }

    private String createHeaderString() {
        String header = "";

        for (Map.Entry<String, String> entry : response.getHeaders().entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            header += key + ": " + value + Stringer.crlf;
        }

        return header;
    }

    private String currentDateTime() {
        ZonedDateTime date = LocalDateTime.now().atZone(ZoneId.of("GMT+00"));
        DateTimeFormatter byPattern = DateTimeFormatter.ofPattern("E, MMM dd yyyy HH:mm:ss z");
        return date.format(byPattern);
    }

    private int getContentLength(String body) {
        return body.getBytes().length;
    }

    private boolean hasBody(String body) {
        return body == "" ? false : body.length() > 0;
    }

    private String createOptionsHeader() {
        Set<String> availableMethods = new LinkedHashSet<>();
        availableMethods.add(Methods.options);
        for (String method : router.getMethods()) {
            if (methodCollection.containsKey(method)) {
                availableMethods.add(method);
            }
        }

        return availableMethods.toString().replaceAll("[\\[\\]]", "");
    }
}
