package http_server;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class Response {
    private Request request;
    private Router router;
    private ResponseBuilder responseBuilder;
    private LinkedHashMap<String, String> headerCollection = new LinkedHashMap<>();
    private HashMap<String, Callback> methodCollection;
    private String status;
    private String responseBody = "";

    public Response(Request request, Router router) {
        this.request = request;
        this.router = router;
        this.responseBuilder = new ResponseBuilder(this, request);
        this.methodCollection = router.getMethodCollection(request.getRoute());
        this.status = "200 OK";
    }

    public String generateResponse() {
        if (methodCollection.isEmpty()) {
            setStatus("404 Not Found");
            return responseBuilder.build();
        }

        if (request.getMethod().equals("OPTIONS")) {
            setHeader("Allow", router.createOptionsHeader(methodCollection));
            return responseBuilder.build();
        }

        if (methodCollection.get(request.getMethod()) == null) {
            setStatus("405 Method Not Allowed");
            setHeader("Allow", router.createOptionsHeader(methodCollection));
            return responseBuilder.build();
        }

        if (hasBody(request.getBody())) {
            responseBody += request.getBody();
        }

        router.runCallback(request, this);

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
        setStatus("301 Moved Permanently");
        setHeader("Location", "http://localhost:5000" + redirectedRoute);
    }

    private boolean hasBody(String body) {
        return body == "" ? false : body.length() > 0;
    }
}
