package http_server;

import http_standards.Headers;
import http_standards.Methods;
import http_standards.StatusCode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;

public class Response {
    private LinkedHashMap<String, String> headers = new LinkedHashMap<>();
    private String status;
    private byte[] body;
    private Router router;
    private Request request;

    public Response(Router router, Request request) {
        this.status = StatusCode.ok;
        this.router = router;
        this.request = request;
    }

    public byte[] create() {
        if (requestIsValid()) {
            router.fillResponseForRequest(request, this);
        }

        return getBytes();
    }

    public boolean requestIsValid() {
        if (router.routeInvalid(request.getRoute())) {
            notFound();
            return false;
        }

        if (router.methodInvalid(request.getRoute(), request.getMethod())) {
            methodNotAllowed();
            return false;
        }

        if (Methods.creationMethods().contains(request.getMethod()) && request.getBody() == null) {
            noContent();
            return false;
        }

        return true;
    }

    public byte[] getBytes() {
        initDefaultHeaders();
        return ResponseByteAssembler.generateBytes(status, headers, body);
    }

    public String getStatus() {
        return status;
    }

    public byte[] getBody() {
        return body;
    }

    public LinkedHashMap<String, String> getHeaders() {
        return headers;
    }

    private void notFound() {
        setStatus(StatusCode.notFound);
    }

    private void methodNotAllowed() {
        setStatus(StatusCode.methodNotAllowed);
        setHeader(Headers.allowedHeaders, router.createOptionsHeader(request.getRoute()));
    }

    private void noContent() {
        setStatus(StatusCode.noContent);
    }

    public void sendFile(String path) {
        String filePath = router.getFullStaticDirectoryPath() + path;
        byte[] file = router.getRepository().readFile(filePath);
        this.body = file;
        setHeader(Headers.contentType, router.getRepository().getFileType(filePath));
        setHeader(Headers.contentLength, file.length + "");
    }

    public void redirect(String redirectedRoute) {
        setStatus(StatusCode.moved);
        setHeader(Headers.location, "http://127.0.0.1:5000" + redirectedRoute);
    }

    public void sendBody(byte[] bodyContent, String contentType) {
        setHeader(Headers.contentLength, Integer.toString(bodyContent.length));
        setHeader(Headers.contentType, contentType);
        this.body = bodyContent;
    }

    public void sendBody(String bodyContent, String contentType) {
        sendBody(bodyContent.getBytes(), contentType);
    }

    public void successfulPut() {
        this.status = StatusCode.created;
    }

    public void successfulPost(String resourceLocation) {
        setHeader(Headers.location, resourceLocation);
        setStatus(StatusCode.created);
    }

    public void head(byte[] bodyContent, String contentType) {
        setHeader(Headers.contentLength, Integer.toString(bodyContent.length));
        setHeader(Headers.contentType, contentType);
    }

    public void head(String bodyContent, String contentType) {
        head(bodyContent.getBytes(), contentType);
    }

    public void successfulDelete() {
        this.status = StatusCode.noContent;
    }

    public void options(String allowedHeaders) {
        setHeader(Headers.allowedHeaders, allowedHeaders);
    }

    private void initDefaultHeaders() {
        ZonedDateTime date = LocalDateTime.now().atZone(ZoneId.of("GMT+00"));
        DateTimeFormatter byPattern = DateTimeFormatter.ofPattern("E, MMM dd yyyy HH:mm:ss z");
        String currentDateTime = date.format(byPattern);

        setHeader(Headers.date, currentDateTime);
        setHeader(Headers.server, "JavaServer/0.1");
    }

    public void setHeader(String headerName, String headerValue) {
        headers.put(headerName, headerValue);
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
