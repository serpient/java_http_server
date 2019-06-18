package http_server;

import java.util.LinkedHashMap;

public class Response {
    private ResponseBuilder responseBuilder;
    private LinkedHashMap<String, String> headerCollection = new LinkedHashMap<>();
    private String status;
    private String responseBody = "";

    public Response(Request request, Router router) {
        this.responseBuilder = new ResponseBuilder(this, request, router);
        this.status = StatusCode.OK.get();
    }

    public String generateResponse() {
        return responseBuilder.build();
    }

    public void setHeader(String headerName, String headerValue) {
        headerCollection.put(headerName, headerValue);
    }

    public LinkedHashMap<String, String> getHeaders() {
        return headerCollection;
    }

    public void setStatus(String newStatus) {
        this.status = newStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setBody(String newBody) {
        this.responseBody = newBody;
    }

    public String getBody() {
        return responseBody;
    }

    public void redirect(String redirectedRoute) {
        setStatus(StatusCode.MOVED.get());
        setHeader("Location", "http://localhost:5000" + redirectedRoute);
    }
}
