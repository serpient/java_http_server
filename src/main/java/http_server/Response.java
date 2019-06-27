package http_server;

import file_handler.FileHandler;
import http_protocol.Headers;
import http_protocol.StatusCode;

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

    public boolean requestIsValid() {
        if (router.routeInvalid(request.getRoute())) {
            initFromNotFound();
            return false;
        }

        if (router.methodInvalid(request.getRoute(), request.getMethod())) {
            initFromMethodNotAllowed();
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

    public void initFromNotFound() {
        this.status = StatusCode.notFound;
    }

    public void initFromMethodNotAllowed() {
        this.status = StatusCode.methodNotAllowed;
        setHeader(Headers.allowedHeaders, router.createOptionsHeader(request.getRoute()));
    }

    public void initFromFile(String path) {
        String filePath = Router.getFullStaticDirectoryPath() + path;
        byte[] file = FileHandler.readFile(filePath);
        this.body = file;
        setHeader(Headers.contentType, FileHandler.getFileType(filePath));
        setHeader(Headers.contentLength, file.length + "");
    }

    public void initFromRedirect(String redirectedRoute) {
        status = StatusCode.moved;
        setHeader(Headers.location, "http://127.0.0.1:5000" + redirectedRoute);
    }

    public void initFromBody(byte[] bodyContent, String contentType) {
        setHeader(Headers.contentLength, Integer.toString(bodyContent.length));
        setHeader(Headers.contentType, contentType);
        this.body = bodyContent;
    }

    public void initFromPutData(byte[] bodyContent) {
        this.status = bodyContent != null ? StatusCode.created : StatusCode.noContent;
    }

    public void initFromPostData(byte[] bodyContent, String resourceLocation) {
        if (bodyContent != null) {
            setHeader(Headers.location, resourceLocation);
            this.status = StatusCode.created;
        } else {
            this.status = StatusCode.noContent;
        }
    }

    public void initFromEmptyData() {
        this.status = StatusCode.noContent;
    }

    public void initFromHeadResponse(byte[] bodyContent, String contentType) {
        setHeader(Headers.contentLength, Integer.toString(bodyContent.length));
        setHeader(Headers.contentType, contentType);
    }

    public void initFromOptions(String allowedHeaders) {
        setHeader(Headers.allowedHeaders, allowedHeaders);
    }

    public void initFromHTTPResponse(String status, LinkedHashMap<String, String> headers, byte[] body) {
        this.status = status;
        this.headers = headers;
        this.body = body;
    }

    private void initDefaultHeaders() {
        ZonedDateTime date = LocalDateTime.now().atZone(ZoneId.of("GMT+00"));
        DateTimeFormatter byPattern = DateTimeFormatter.ofPattern("E, MMM dd yyyy HH:mm:ss z");
        String currentDateTime = date.format(byPattern);

        setHeader(Headers.date, currentDateTime);
        setHeader(Headers.server, "JavaServer/0.1");
    }

    private void setHeader(String headerName, String headerValue) {
        headers.put(headerName, headerValue);
    }
}
