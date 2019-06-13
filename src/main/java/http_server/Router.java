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

    public String response(String method, String route, String req, String res) {
        HashMap<String, Callback> methodCollection = getMethodCollection(route);
        return methodCollection.get(method).run(req, res);
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
