package http_server;

import http_protocol.Headers;
import http_protocol.Methods;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Router {
    private HashMap<String, HashMap<String, Callback>> routes;
    private Set<String> methods;
    private Path basePath;
    private static Path staticDirectoryPath;

    public Router() {
        this.routes = new HashMap<>();
        this.methods = new LinkedHashSet<>();
        methods.add(Methods.get);
        methods.add(Methods.head);
        methods.add(Methods.post);
        methods.add(Methods.put);
    }

    public HashMap<String, HashMap<String, Callback>> getRouter() {
        return routes;
    }

    public void basePath(Path path) {
        this.basePath = path;
    }

    public void staticDirectory(String newStaticDirectoryPath) {
        staticDirectoryPath = Paths.get(basePath.toString(), newStaticDirectoryPath);
        List<String> directoryContents = FileHandler.readDirectoryContents(staticDirectoryPath.toString());

        createContentRoutes(directoryContents, newStaticDirectoryPath);
        createStaticDirectoryRoute(directoryContents, newStaticDirectoryPath);
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

    public void all(String route, Callback handler) {
        for (String method : methods) {
            updateRoutes(method, route, handler);
        }
    }

    public Set<String> getMethods() {
        return methods;
    }

    public static Path getStaticDirectoryPath() {
        return staticDirectoryPath;
    }

    public HashMap<String, Callback> getMethodCollection(String route) {
        return routes.get(route) == null
                ? new HashMap<>()
                : routes.get(route);
    }

    public void runCallback(Request request, Response response) {
        getMethodCollection(request.getRoute()).get(request.getMethod()).run(request, response);
    }

    public void saveResource(String path, String fileType, byte[] content) {
        FileHandler.writeFile(getStaticDirectoryPath() + path, fileType, content);
        get(path, (Request request, Response response) -> {
            response.sendFile(path + "." + fileType);
        });
    }

    private void updateRoutes(String method, String route, Callback handler) {
        HashMap<String, Callback> methodCollection = getMethodCollection(route);
        methodCollection.put(method, handler);
        routes.put(route, methodCollection);
    }

    private void createStaticDirectoryRoute(List<String> directoryContents, String newStaticDirectoryPath) {
        String directoryHTML = new DirectoryCreator(directoryContents, newStaticDirectoryPath).generateHTML();

        get(newStaticDirectoryPath, (Request request, Response response) -> {
            response.setHeader(Headers.contentType, "text/html");
            response.setBody(directoryHTML);
        });
    }

    private void createContentRoutes(List<String> directoryContents, String newStaticDirectoryPath) {
        for (int i = 0; i < directoryContents.size(); i++) {

            String fileName = directoryContents.get(i);
            String filePath = newStaticDirectoryPath + "/" +fileName;

            get(filePath, (Request request, Response response) -> {
                if (FileHandler.getFileType(filePath).matches("image(.*)")) {
                    byte[] img = FileHandler.readFile(staticDirectoryPath + "/" + fileName);
                    response.saveBinary(img);
                } else {
                    response.setBody(FileHandler.getFileContents(staticDirectoryPath + "/" + fileName));
                }
                response.setHeader(Headers.contentType, FileHandler.getFileType(filePath));
            });
        }
    }
}
