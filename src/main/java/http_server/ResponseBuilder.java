package http_server;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class ResponseBuilder {
    private Response response;
    private Request request;
    private String crlf = "\r\n";

    public ResponseBuilder(Response response, Request request) {
        this.response = response;
        this.request = request;
    }

    public String build() {
        return responseBuilder(buildStatus(), buildHeader(), buildBody());
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

    private String responseBuilder(String status, String header, String body) {
        return status + header + body;
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
}
