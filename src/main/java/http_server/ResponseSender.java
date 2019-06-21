package http_server;

import http_protocol.Headers;
import http_protocol.Methods;
import http_protocol.StatusCode;
import http_protocol.Stringer;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class ResponseSender {
    private SocketWrapper client;
    private Response response;
    private Request request;
    private Router router;
    private HashMap<String, Callback> methodCollection;

    public ResponseSender(SocketWrapper client, Response response, Request request, Router router) {
        this.client = client;
        this.response = response;
        this.request = request;
        this.router = router;
        this.methodCollection = router.getMethodCollection(request.getRoute());
    }

    public void sendBinary() {
        if (methodCollection.isEmpty()) {
            response.setStatus(StatusCode.notFound);
            client.sendBinary(buildFullHeader().getBytes());
            return;
        }

        if (request.getMethod().equals(Methods.options)) {
            response.setHeader(Headers.allowedHeaders, createOptionsHeader());
            client.sendBinary(buildFullHeader().getBytes());
            return;
        }

        if (methodCollection.get(request.getMethod()) == null) {
            response.setStatus(StatusCode.methodNotAllowed);
            response.setHeader(Headers.allowedHeaders, createOptionsHeader());
            client.sendBinary(buildFullHeader().getBytes());
            return;
        }

        router.runCallback(request, response);

        if (response.getBinaryFile() != null) {
            client.sendBinary(buildFullHeader().getBytes());
            client.sendBinary(Stringer.crlf.getBytes());
            client.sendBinary(response.getBinaryFile());
            return;
        }

        if (hasBody(response.getBody())) {
            client.sendBinary((buildFullHeader() + buildBody()).getBytes());
            return;
        }

        client.sendBinary(buildFullHeader().getBytes());
        return;
    }

    public String buildFullHeader() {
        return buildStatus() + buildHeader();
    }

    public String buildStatus() {
        return "HTTP/1.1 " + response.getStatus() + Stringer.crlf;
    }

    public String buildHeader() {
        response.setHeader(Headers.date, currentDateTime());
        response.setHeader(Headers.server, "JavaServer/0.1");

        if (hasBody(response.getBody())) {
            System.err.println(response.getHeaders());
            if (response.getHeaders().get(Headers.contentType) == null) {
                response.setHeader(Headers.contentType, "text/plain");
            }

            if (response.getHeaders().get(Headers.contentLength) == null) {
                response.setHeader(Headers.contentLength, Integer.toString(getContentLength(response.getBody())));
            }

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
