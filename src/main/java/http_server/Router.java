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
    private Set<String> methods;
    private Path basePath;
    private static Path fullDirectoryPath;
    private Repository repository;
    private static int port;
    private static ResourceHandler resource;

    public Router() {
        this.routes = new HashMap<>();
        this.methods = new LinkedHashSet<>();
        methods.add(Methods.get);
        methods.add(Methods.head);
        methods.add(Methods.post);
        methods.add(Methods.put);
        methods.add(Methods.options);
        methods.add(Methods.delete);
        repository = new FileRepository();
        port = 5000;
        basePath = Paths.get(System.getProperty("user.dir"));
    }

    public Router(String staticDirectoryRelativePath) {
        this();
        if (staticDirectoryRelativePath != "") {
            staticDirectory(staticDirectoryRelativePath);
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

    public Set<String> getMethods() {
        return methods;
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

    public void all(String route, Callback handler) {
        for (String method : methods) {
            updateRoutes(method, route, handler);
        }
    }

    private void updateRoutes(String method, String route, Callback handler) {
        HashMap<String, Callback> methodCollection = getMethodCollection(route);
        methodCollection.put(method, handler);
        setOptionsMethod(methodCollection);
        routes.put(route, methodCollection);
    }

    private void setOptionsMethod(HashMap<String, Callback> methodCollection) {
        if (!methodCollection.containsKey(Methods.options)) {
            methodCollection.put(Methods.options, (Request request, Response response) -> {
                response.options(createOptionsHeader(request.getRoute()));
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

    public void fillResponseForRequest(Request request, Response response) {
        getMethodCollection(request.getRoute()).get(request.getMethod()).run(request, response);
    }

    private String trimPath(String path) {
        int trimFrom = path.lastIndexOf(".");
        return path.substring(trimFrom + 1);
    }

    public void staticDirectory(String directoryPath) {
        this.fullDirectoryPath = Paths.get(basePath.toString(), directoryPath);
        String formattedDirectoryPath = directoryPath.startsWith(".") || directoryPath.startsWith("/") ? trimPath(directoryPath) : directoryPath;
        resource = new ResourceHandler(this, formattedDirectoryPath);
        resource.createStaticDirectory(formattedDirectoryPath);
    }

    public String saveResource(String resourcePath, String fileType, byte[] content) {
        resource.save(resourcePath + "." + fileType, fileType, content);
        resource.paths(resourcePath, fileType).forEach(path -> {
            get(path, (Request request, Response response) -> {
                response.sendFile(resourcePath + "." + fileType);
            });
            delete(path, (Request request, Response response) -> {
                resource.delete(resourcePath + "." + fileType, fileType);
                resource.paths(resourcePath, fileType).forEach(pathToDelete -> deleteRoutes(pathToDelete));
                response.successfulDelete();
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

    public void deleteResource(String resourcePath, String fileType) {
        resource.delete(resourcePath, fileType);
        resource.paths(resourcePath, fileType).forEach(path -> deleteRoutes(path));
    }

    public void deleteRoutes(String resourcePath) {
        routes.remove(resourcePath);
    }
}
