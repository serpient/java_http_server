package http_server;

import http_standards.Headers;
import http_standards.MIMETypes;
import http_standards.Methods;
import http_standards.StatusCode;
import http_standards.Timestamp;

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
            router.fillResponse(request, this);
        }

        return getBytes();
    }

    private boolean requestIsValid() {
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

    private byte[] getBytes() {
        initDefaultHeaders();
        return ResponseByteAssembler.generateBytes(status, headers, body);
    }

    private void initDefaultHeaders() {
        setHeader(Headers.date, Timestamp.GMT());
        setHeader(Headers.server, "JavaServer/0.1");
    }

    public String getStatus() {
        return status;
    }

    public LinkedHashMap<String, String> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setHeader(String headerName, String headerValue) {
        headers.put(headerName, headerValue);
    }

    public void setFile(String path) {
        String filePath = findFullFileName(path);
        try {
            byte[] file = router.getRepository().readFile(filePath);
            this.body = file;
            setHeader(Headers.contentType, router.getRepository().getFileType(filePath));
            setHeader(Headers.contentLength, file.length + "");
        } catch (NullPointerException e) {
            System.err.println("Setting file in Response terminated. File path was not valid.");
            setStatus(StatusCode.internalError);
        }
    }

    private String findFullFileName(String path) {
        String filePath = router.getFullDirectoryPath() + path;
        if (!path.contains(".")) {
            for (String route : router.getRoutes().keySet()) {
                if (route.contains(path + ".") && !route.contains(router.getDirectoryPath())) {
                    filePath = router.getFullDirectoryPath() + route;
                    break;
                }
            }
        }
        return filePath;
    }

    public void setBody(byte[] bodyContent, String contentType) {
        setHeader(Headers.contentLength, Integer.toString(bodyContent.length));
        setHeader(Headers.contentType, contentType);
        this.body = bodyContent;
    }

    public void setBody(String bodyContent, String contentType) {
        setBody(bodyContent.getBytes(), contentType);
    }

    public void redirect(String redirectedRoute) {
        setStatus(StatusCode.moved);
        setHeader(Headers.location, "http://127.0.0.1:" + router.getPort() + redirectedRoute);
    }

    public void forPut(OperationResult operationResult) {
        setStatus(operationResult.statusCode());
    }

    public void forPut(OperationResult operationResult, byte[] content, String contentType) {
        setStatus(operationResult.statusCode());
        setBody(content, contentType);
    }

    public void forPost(OperationResult postResult, String resourceLocation) {
        if (postResult.valid()) {
            setHeader(Headers.location, resourceLocation);
        } else {
            setBody(postResult.data(), MIMETypes.plain);
        }
        setStatus(postResult.statusCode());
    }

    public void forPost(OperationResult postResult, String resourceLocation, byte[] content, String contentType) {
        if (postResult.valid()) {
            setHeader(Headers.location, resourceLocation);
            setBody(content, contentType);
        } else {
            setBody(postResult.data(), MIMETypes.plain);
        }
        setStatus(postResult.statusCode());
    }

    public void forPost(OperationResult postResult, String resourceLocation, String content, String contentType) {
        forPost(postResult, resourceLocation, content.getBytes(), contentType);
    }

    public void forHead(byte[] bodyContent, String contentType) {
        setHeader(Headers.contentLength, Integer.toString(bodyContent.length));
        setHeader(Headers.contentType, contentType);
    }

    public void forHead(String bodyContent, String contentType) {
        forHead(bodyContent.getBytes(), contentType);
    }

    public void forOptions(String allowedHeaders) {
        setHeader(Headers.allowedHeaders, allowedHeaders);
    }

    public void forDelete(OperationResult deletionResult) {
        setStatus(deletionResult.statusCode());
    }

    public void forDelete(OperationResult deletionResult, byte[] bodyContent, String contentType) {
        handleResult(deletionResult, bodyContent, contentType);
    }

    public void forPatch(OperationResult patchResult) {
        setStatus(patchResult.statusCode());
    }

    public void forPatch(OperationResult patchResult, byte[] bodyContent, String contentType) {
        handleResult(patchResult, bodyContent, contentType);
    }

    private void handleResult(OperationResult result, byte[] bodyContent, String contentType) {
        if (result.valid()) {
            setStatus(StatusCode.ok);
            setBody(bodyContent, contentType);
        } else {
            setStatus(result.statusCode());
        }
    }
}
