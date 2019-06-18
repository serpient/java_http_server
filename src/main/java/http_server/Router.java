package http_server;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

public class Router {
    private HashMap<String, HashMap<String, Callback>> router;
    private Set<String> methods;

    public Router() {
        this.router = new HashMap<>();
        this.methods = new LinkedHashSet<>();
        methods.add("GET");
        methods.add("HEAD");
        methods.add("POST");
        methods.add("PUT");
    }

    public HashMap<String, HashMap<String, Callback>> getRouter() {
        return router;
    }

    public void get(String route, Callback handler) {
        updateRouter("GET", route, handler);
    }

    public void head(String route, Callback handler) {
        updateRouter("HEAD", route, handler);
    }

    public void post(String route, Callback handler) {
        updateRouter("POST", route, handler);
    }

    public void put(String route, Callback handler) {
        updateRouter("PUT", route, handler);
    }

    public void all(String route, Callback handler) {
        for (String m : methods) {
            updateRouter(m, route, handler);
        }
    }

    public HashMap<String, Callback> getMethodCollection(String route) {
        return router.get(route) == null
                ? new HashMap<>()
                : router.get(route);
    }

    public String createOptionsHeader(HashMap<String, Callback> methodCollection) {
        Set<String> availableMethods = new LinkedHashSet<>();
        availableMethods.add("OPTIONS");
        for (String m : methods) {
            if (methodCollection.containsKey(m)) {
                availableMethods.add(m);
            }
        }

        return availableMethods.toString().replaceAll("[\\[\\]]", "");
    }

    public void runCallback(Request request, Response response) {
        System.err.println(request.getRoute());
        System.err.println(request.getMethod());
        getMethodCollection(request.getRoute()).get(request.getMethod()).run(request, response);
    }

    private void updateRouter(String method, String route, Callback handler) {
        HashMap<String, Callback> methodCollection = getMethodCollection(route);
        methodCollection.put(method, handler);
        router.put(route, methodCollection);
    }
}
