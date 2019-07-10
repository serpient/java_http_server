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
    private static Path fullDirectoryPath;
    private Repository repository;
    private static int port;
    private static ResourceHandler resource;

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

    public HashMap<String, HashMap<String, Callback>> getRouter() {
        return routes;
    }

    public static Path getFullDirectoryPath() {
        return fullDirectoryPath;
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
        this.resource = new ResourceHandler(this, formattedDirectoryPath);
        resource.createDirectory(formattedDirectoryPath);
    }

    public void deleteResource(String resourcePath, String fileType) {
        resource.delete(resourcePath + "." + fileType);
        resource.paths(resourcePath, fileType).forEach(pathToDelete -> deleteRoutes(pathToDelete));
    }

    private void deleteRoutes(String resourcePath) {
        routes.remove(resourcePath);
    }

    public String saveResource(String resourcePath, String fileType, byte[] content) {
        resource.save(resourcePath, fileType, content);
        resource.paths(resourcePath, fileType).forEach(path -> {
            get(path, (Request request, Response response) -> {
                response.setFile(resourcePath + "." + fileType);
            });
            delete(path, (Request request, Response response) -> {
                deleteResource(resourcePath, fileType);
                response.forDelete();
            });
        });
        return resourcePath;
    }

    public String saveResource(String resourcePath, String fileType, String content) {
        return saveResource(resourcePath, fileType, content.getBytes());
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
                    return Integer.parseInt(s.substring(idx + 1));
                })
                .max(Comparator.comparing(Integer::valueOf)).get();
    }

    public boolean updateJSONResource(String filePath, String patchDocument) {
        return resource.updateJSON(filePath,  patchDocument);
    }
}
