package http_server;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class ResponseBuilder {
    private Response response;
    private Request request;
    private Router router;
    private HashMap<String, Callback> methodCollection;
    private String crlf = "\r\n";

    public ResponseBuilder(Response response, Request request, Router router) {
        this.response = response;
        this.request = request;
        this.router = router;
        this.methodCollection = router.getMethodCollection(request.getRoute());
    }

    public String build() {
        if (methodCollection.isEmpty()) {
            response.setStatus(StatusCode.NOT_FOUND.get());
            return responseBuilder();
        }

        if (request.getMethod().equals("OPTIONS")) {
            response.setHeader("Allow", createOptionsHeader());
            return responseBuilder();
        }

        if (methodCollection.get(request.getMethod()) == null) {
            response.setStatus(StatusCode.METHOD_NOT_ALLOWED.get());
            response.setHeader("Allow", createOptionsHeader());
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
        return "HTTP/1.1 " + response.getStatus() + crlf;
    }

    public String buildHeader() {
        response.setHeader("Date", currentDateTime());
        response.setHeader("Server", "JavaServer/0.1");

        if (hasBody(response.getBody())) {
            response.setHeader("Content-Type", "text/plain");
            response.setHeader("Content-Length", Integer.toString(getContentLength(response.getBody())));
        }

        return createHeaderString();
    }

    public String buildBody() {
        if (!request.getMethod().equals("HEAD") && hasBody(response.getBody())) {
            return crlf + response.getBody();
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
            header += key + ": " + value + crlf;
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
        availableMethods.add("OPTIONS");
        for (String m : router.getMethods()) {
            if (methodCollection.containsKey(m)) {
                availableMethods.add(m);
            }
        }

        return availableMethods.toString().replaceAll("[\\[\\]]", "");
    }
}
