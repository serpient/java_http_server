package http_server;

import directory_page_creator.DirectoryPageCreator;
import http_standards.MIMETypes;
import http_standards.Methods;
import repository.Repository;
import repository.FileRepository;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class Router {
    private HashMap<String, HashMap<String, Callback>> routes;
    private Set<String> methods;
    private Path basePath;
    private static Path fullStaticDirectoryPath;
    private Repository repository;
    private static String dirPath;

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

    public static Path getFullStaticDirectoryPath() {
        return fullStaticDirectoryPath;
    }

    public static String dirPath() {
        return dirPath;
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

    public void staticDirectory(String staticDirectoryRelativePath) {
        dirPath = staticDirectoryRelativePath;
        fullStaticDirectoryPath = Paths.get(basePath.toString(), staticDirectoryRelativePath);
        List<String> directoryContents = repository.readDirectoryContents(fullStaticDirectoryPath.toString());
        createResourceRoutes(directoryContents, staticDirectoryRelativePath);
        createStaticDirectoryRoute(staticDirectoryRelativePath);
    }

    private void createStaticDirectoryRoute(String staticDirectoryRelativePath) {
        get(staticDirectoryRelativePath, (Request request, Response response) -> {
            List<String> directoryContents = repository.readDirectoryContents(fullStaticDirectoryPath.toString());
            response.sendBody(new DirectoryPageCreator(directoryContents, staticDirectoryRelativePath).generateHTML().getBytes(), MIMETypes.html);
        });
    }

    private void createResourceRoutes(List<String> directoryContents, String staticDirectoryRelativePath) {
        for (int i = 0; i < directoryContents.size(); i++) {
            String fileName = directoryContents.get(i);
            String filePath = staticDirectoryRelativePath + "/" + fileName;

            get(filePath, (Request request, Response response) -> {
                response.sendFile("/" + fileName);
            });
        }
    }

    public String saveResource(String resourcePath, String fileType, byte[] content) {
        repository.writeFile(getFullStaticDirectoryPath() + resourcePath, fileType, content);
        createNewResourceRoutes(resourcePath, fileType);
        return resourcePath;
    }

    private void createNewResourceRoutes(String resourcePath, String fileType) {
        List<String> pathList = newResourcePaths(resourcePath, fileType);
        for (int i = 0; i < pathList.size(); i++) {
            get(pathList.get(i), (Request request, Response response) -> {
                response.sendFile(resourcePath + "." + fileType);
            });
            delete(pathList.get(i), (Request request, Response response) -> {
                deleteResource(resourcePath, fileType);
                response.successfulDelete();
            });
        }
    }

    private List<String> newResourcePaths(String resourcePath, String fileType) {
        return Arrays.asList(
                resourcePath,
                resourcePath + "." + fileType,
                dirPath() + resourcePath,
                dirPath() + resourcePath + "." + fileType
        );
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
        repository.deleteFile(getFullStaticDirectoryPath() + resourcePath + "." + fileType);
        deleteResourceRoute(resourcePath);
        deleteResourceRoute(resourcePath + "." + fileType);
        deleteResourceRoute(dirPath() + resourcePath);
        deleteResourceRoute(dirPath() + resourcePath + "." + fileType);
        System.err.println(routes.keySet());
    }

    private void deleteResourceRoute(String resourcePath) {
        routes.remove(resourcePath);
    }
}
