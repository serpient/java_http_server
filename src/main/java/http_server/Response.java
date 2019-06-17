package http_server;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

public class Response {
    Request request;
    Router router;
    LinkedHashMap<String, String> headerCollection = new LinkedHashMap<>();
    String status;
    String responseBody = "";
    String crlf = "\r\n";

    public Response(Request request, Router router) {
        System.err.println(router);
        this.request = request;
        this.router = router;
        this.status = "200 OK";
    }

    public String generateResponse() {
        String requestRoute = request.getRoute();
        String requestMethod = request.getMethod();
        String requestBody = request.getBody();

        header("Date", currentDateTime());
        header("Server", "JavaServer/0.1");
        router.runCallback(requestMethod, requestRoute, request, this);

        if (hasBody(requestBody)) {
            responseBody += requestBody;
        }

        if (hasBody(responseBody)) {
            header("Content-Type", "text/plain");
            header("Content-Length", Integer.toString(getContentLength(responseBody)));
        }

        String response = getHeader();

        if (!requestMethod.equals("HEAD") && hasBody(responseBody)) {
            response += crlf + responseBody;
        }

        return response;
    }

    public String getHeader() {
        String header = responseLine() + crlf;

        for (Map.Entry<String, String> entry : headerCollection.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            header += key + ": " + value + crlf;
        }

        return header;
    }

    public void header(String headerName, String headerValue) {
        headerCollection.put(headerName, headerValue);
    }

    public void status(String newStatus) {
        this.status = newStatus;
    }

    public void body(String newBody) {
        this.responseBody = newBody;
    }

    public String getResponseBody() {
        return responseBody;
    }

    private String responseLine() {
        return "HTTP/1.1 " + status;
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
}
