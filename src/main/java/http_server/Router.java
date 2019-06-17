package http_server;

import java.util.HashMap;

public class Router {
    private HashMap<String, HashMap<String, Callback>> collection;

    public Router() {
        this.collection = new HashMap<>();
    }

    public HashMap<String, HashMap<String, Callback>> collection() {
        return collection;
    }

    public void get(String route, Callback handler) {
        updateCollection("GET", route, handler);
    }

    public void head(String route, Callback handler) {
        updateCollection("HEAD", route, handler);
    }

    public void post(String route, Callback handler) {
        updateCollection("POST", route, handler);
    }

    public void runCallback(String method, String route, Request request, Response response) {
        HashMap<String, Callback> methodCollection = getMethodCollection(route);
        if (methodCollection.isEmpty() || methodCollection.get(method) == null) {
            response.status("404 Not Found");
        } else {
            methodCollection.get(method).run(request, response);
        }
    }

    private void updateCollection(String method, String route, Callback handler) {
        HashMap<String, Callback> methodCollection = getMethodCollection(route);
        methodCollection.put(method, handler);
        collection.put(route, methodCollection);
    }

    private HashMap<String, Callback> getMethodCollection(String route) {
        return collection.get(route) == null
            ? new HashMap<>()
            : collection.get(route);
    }
}
