package http_server;

import http_protocol.Headers;
import http_protocol.ResponseCreator;
import http_protocol.StatusCode;
import java.util.LinkedHashMap;

public class Response {
    private ResponseCreator responseCreator;
    private LinkedHashMap<String, String> headerCollection = new LinkedHashMap<>();
    private String status;
    private String responseBody = "";

    public Response(Request request, Router router) {
        this.responseCreator = new ResponseCreator(this, request, router);
        this.status = StatusCode.ok;
    }

    public String generateResponse() {
        return responseCreator.create();
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
        setStatus(StatusCode.moved);
        setHeader(Headers.location, "http://127.0.0.1:5000" + redirectedRoute);
    }
}
