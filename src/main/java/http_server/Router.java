package http_server;

import http_standards.Methods;
import repository.Repository;
import repository.FileRepository;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Stream;

public class Router {
    private HashMap<String, HashMap<String, Callback>> routes;
    private Path basePath;
    private Path fullDirectoryPath;
    private String directoryPath;
    private Repository repository;
    private int port;

    public Router() {
        this.routes = new HashMap<>();
        this.repository = new FileRepository();
        this.port = 5000;
        this.basePath = Paths.get(System.getProperty("user.dir"));
    }

    public Router(String directoryPath) {
        this();
        if (directoryPath != "") {
            directory(directoryPath);
        }
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public Repository getRepository() {
        return repository;
    }

    public HashMap<String, HashMap<String, Callback>> getRoutes() {
        return routes;
    }

    public Path getFullDirectoryPath() {
        return fullDirectoryPath;
    }

    public String getDirectoryPath() {
        return directoryPath;
    }

    public void basePath(Path path) {
        this.basePath = path;
    }

    public void get(String route, Callback handler) {
        updateRoutes(Methods.get, route, handler);
    }

    public void head(String route, Callback handler) {
        updateRoutes(Methods.head, route, handler);
    }

    public void post(String route, Callback handler) {
        updateRoutes(Methods.post, route, handler);
    }

    public void put(String route, Callback handler) {
        updateRoutes(Methods.put, route, handler);
    }

    public void delete(String route, Callback handler) {
        updateRoutes(Methods.delete, route, handler);
    }

    public void patch(String route, Callback handler) {
        updateRoutes(Methods.patch, route, handler);
    }

    public void all(String route, Callback handler) {
        for (String method : getMethods()) {
            updateRoutes(method, route, handler);
        }
    }

    private void updateRoutes(String method, String route, Callback handler) {
        HashMap<String, Callback> methodCollection = getMethodCollection(route);
        methodCollection.put(method, handler);
        setOptionsMethod(methodCollection);
        routes.put(route, methodCollection);
    }

    private Set<String> getMethods() {
        return Methods.allMethods();
    }

    private void setOptionsMethod(HashMap<String, Callback> methodCollection) {
        if (!methodCollection.containsKey(Methods.options)) {
            methodCollection.put(Methods.options, (Request request, Response response) -> {
                response.forOptions(createOptionsHeader(request.getRoute()));
            });
        }
    }

    public String createOptionsHeader(String route) {
        Set<String> availableMethods = new LinkedHashSet<>();
        availableMethods.add(Methods.options);
        for (String method : getMethods()) {
            if (getMethodCollection(route).containsKey(method)) {
                availableMethods.add(method);
            }
        }
        return availableMethods.toString().replaceAll("[\\[\\]]", "");
    }

    public boolean routeInvalid(String route) {
        return getMethodCollection(route).isEmpty();
    }

    public boolean methodInvalid(String route, String method) {
        return !getMethodCollection(route).containsKey(method);
    }

    public HashMap<String, Callback> getMethodCollection(String route) {
        for (String keyRoute: routes.keySet()) {
            String parentRoute = route.substring(0, route.lastIndexOf("/"));
            if (keyRoute.startsWith(parentRoute + "/:")) {
                return routes.get(keyRoute) == null ? new HashMap<>() : routes.get(keyRoute);
            }
        }

        return routes.get(route) == null ? new HashMap<>() : routes.get(route);
    }

    public void fillResponse(Request request, Response response) {
        getMethodCollection(request.getRoute()).get(request.getMethod()).run(request, response);
    }

    private String trimPath(String path) {
        if (path.startsWith(".") || path.startsWith("/")) {
            int trimFrom = path.lastIndexOf(".");
            return path.substring(trimFrom + 1);
        } else {
            return path;
        }
    }

    public void directory(String directoryPath) {
        this.fullDirectoryPath = Paths.get(basePath.toString(), directoryPath);
        String formattedDirectoryPath = trimPath(directoryPath);
        this.directoryPath = formattedDirectoryPath;
        RouterDirectoryHandler.createDirectory(this, formattedDirectoryPath);
    }

    public OperationResult deleteResource(String resourcePath, String fileType) {
        return RouterResourceHandler.delete(this, resourcePath, fileType);
    }

    public OperationResult saveResource(String resourcePath, String fileType, byte[] content) {
        return RouterResourceHandler.save(this, resourcePath, fileType, content);
    }

    public OperationResult saveResource(String resourcePath, String fileType, String content) {
        return RouterResourceHandler.save(this, resourcePath, fileType, content.getBytes());
    }

    public String getUniqueRoute(String path) {
        return path + "/" + getAvailableRouteId(path);
    }

    private int getAvailableRouteId(String route) {
        if (findMatchingRoutes(route).count() == 0) {
            return 1;
        } else {
            return findGreatestResourceId(route) + 1;
        }
    }

    private Stream<String> findMatchingRoutes(String route) {
        return routes.keySet()
                .stream()
                .filter(s -> s.startsWith(route + "/") && !s.endsWith(":id"));
    }

    private int findGreatestResourceId(String route) {
        return routes.keySet()
                .stream()
                .filter(s -> s.startsWith(route + "/") && !s.endsWith(":id"))
                .map(s -> {
                    int idx = s.lastIndexOf("/");
                    String resourceName = s.substring(idx + 1);
                    if (resourceName.contains(".")) {
                        int lastIdx = resourceName.lastIndexOf(".");
                        resourceName = resourceName.substring(0,lastIdx);
                    }

                    return Integer.parseInt(resourceName);
                })
                .max(Comparator.comparing(Integer::valueOf)).get();
    }

    public OperationResult updateJSONResource(String filePath, String patchDocument) {
        return RouterResourceHandler.updateJSON(this, filePath,  patchDocument);
    }
}
