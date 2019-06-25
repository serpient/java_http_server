package http_server;

import http_protocol.Headers;
import http_protocol.MIMETypes;
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
    private static Response response;
    private static Request request;
    private static Router router;
    private static HashMap<String, Callback> methodCollection;

    public ResponseSender(SocketWrapper client, Response response, Request request, Router router) {
        this.client = client;
        this.response = response;
        this.request = request;
        this.router = router;
        this.methodCollection = router.getMethodCollection(request.getRoute());
    }

    public void sendBinary() {

        boolean requestIsValid = handleInvalidRequests();

        if (requestIsValid) {
            router.runCallback(request, response);

            handleMethods(request.getMethod());

            if (response.getBinaryFile() != null) {
                handleBinaryResponse();
                return;
            }

            if (responseHasBody()) {
                client.sendData(buildFullHeader() + buildBody());
                return;
            } else {
                client.sendData(buildFullHeader());
            }
        }

        return;
    }

    private boolean handleInvalidRequests() {
        if (methodCollection.isEmpty()) {
            response.setStatus(StatusCode.notFound);
            client.sendData(buildFullHeader());
            return false;
        }

        if (!methodCollection.containsKey(request.getMethod())) {
            response.setStatus(StatusCode.methodNotAllowed);
            response.setHeader(Headers.allowedHeaders, createOptionsHeader());
            client.sendData(buildFullHeader());
            return false;
        }

        return true;
    }

    private void handleMethods(String method) {
        switch (method) {
            case Methods.options:
                break;
            case Methods.post:
                boolean hasResource = responseHasBody() && requestHasBody();
                response.setStatus(!hasResource ? StatusCode.noContent : StatusCode.created);

                if (requestHasBody()) {
                    String fileType = MIMETypes.getFileType(request.getHeaders().get(Headers.contentType));
                    response.setHeader(Headers.location, request.getRoute());
                    router.saveResource(request.getRoute(), fileType, request.getBody().getBytes());
                }
                break;
        }
    }

    private boolean responseHasBody() {
        return response.getBinaryFile() != null || response.getBody() != null;
    }

    private boolean requestHasBody() {
        return request.getBody() != null;
    }

    private void handleBinaryResponse() {
        client.sendBinary(buildFullHeader().getBytes());
        client.sendBinary(Stringer.crlf.getBytes());
        client.sendBinary(response.getBinaryFile());
    }

    public static String buildFullHeader() {
        return buildStatus() + buildHeader();
    }

    public static String buildStatus() {
        return "HTTP/1.1 " + response.getStatus() + Stringer.crlf;
    }

    public static String buildHeader() {
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

    public static String buildBody() {
        if (!request.getMethod().equals(Methods.head) && hasBody(response.getBody())) {
            return Stringer.crlf + response.getBody();
        } else {
            return "";
        }
    }

    private static String createHeaderString() {
        String header = "";

        for (Map.Entry<String, String> entry : response.getHeaders().entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            header += key + ": " + value + Stringer.crlf;
        }

        return header;
    }

    private static String currentDateTime() {
        ZonedDateTime date = LocalDateTime.now().atZone(ZoneId.of("GMT+00"));
        DateTimeFormatter byPattern = DateTimeFormatter.ofPattern("E, MMM dd yyyy HH:mm:ss z");
        return date.format(byPattern);
    }

    private static int getContentLength(String body) {
        return body.getBytes().length;
    }

    private static boolean hasBody(String body) {
        return body == "" ? false : body.length() > 0;
    }

    public static String createOptionsHeader() {
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
