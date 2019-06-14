package http_server;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Response {
    RequestParser requestParser;
    Router router;

    public Response(RequestParser requestParser, Router router) {
        System.err.println(router);
        this.requestParser = requestParser;
        this.router = router;
    }

    public String generateResponse() {
        String requestRoute = requestParser.route();
        String requestMethod = requestParser.method();
        String requestBody = requestParser.body();

        String crlf = "\r\n";

        String outputLine = starterHeader();

        String responseBody = router.response(requestMethod, requestRoute, "", outputLine);

        if (hasBody(requestBody)) {
            responseBody += requestBody;
        }

        System.err.println("request=" + requestRoute);

        if (hasBody(responseBody)) {
            String contentTypeHeader = "Content-Type: text/plain" + crlf;
            String contentLengthHeader = "Content-Length: " + getContentLength(responseBody) + crlf;

            outputLine += contentTypeHeader + contentLengthHeader + crlf;

            if (!requestMethod.equals("HEAD")) {
                outputLine += responseBody;
            }

        }

        return outputLine;
    }

    private String starterHeader() {
        String crlf = "\r\n";

        String responseLine = "HTTP/1.1 " + "200 OK" + crlf;
        String dateHeader = "Date: " + currentDateTime() + crlf;
        String serverHeader = "Server: JavaServer/0.1" + crlf;
        return responseLine + dateHeader + serverHeader;
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
