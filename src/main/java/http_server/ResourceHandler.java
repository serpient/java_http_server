package http_server;

import directory_page_creator.DirectoryPageCreator;
import http_standards.MIMETypes;
import json_handler.JSONHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class ResourceHandler {
    private static Path fullDirectoryPath;
    private static String directoryPath;
    private static Router router;

    public ResourceHandler(Router router, String directoryPath) {
        this.router = router;
        this.fullDirectoryPath = router.getFullDirectoryPath();
        this.directoryPath = directoryPath;
    }

    public void createDirectory(String directoryPath) {
        List<String> directoryContents = router.getRepository().readDirectoryContents(fullDirectoryPath.toString());
        createContentRoutes(directoryContents, directoryPath);
        router.get(directoryPath, (Request request, Response response) -> {
            response.setBody(new DirectoryPageCreator(directoryContents, directoryPath).generateHTML().getBytes(), MIMETypes.html);
        });

        router.get("/", (Request request, Response response) -> {
            response.redirect(directoryPath);
        });
    }

    private void createContentRoutes(List<String> directoryContents, String directoryPath) {
        for (int i = 0; i < directoryContents.size(); i++) {
            String fileName = directoryContents.get(i);
            String filePath = directoryPath + "/" + fileName;

            router.get(filePath, (Request request, Response response) -> {
                response.setFile("/" + fileName);
            });
        }
    }

    public void save(String resourcePath, String fileType, byte[] content) {
        router.getRepository().writeFile(fullDirectoryPath + resourcePath, fileType, content);
    }

    public void delete(String resourcePath) {
        router.getRepository().deleteFile(fullDirectoryPath + resourcePath);
    }

    public List<String> paths(String resourcePath, String fileType) {
        return Arrays.asList(
                resourcePath,
                resourcePath + "." + fileType,
                directoryPath + resourcePath,
                directoryPath + resourcePath + "." + fileType
        );
    }

    public boolean updateJSON(String resourcePath, String patchDocument) {
        if (!patchDocument.startsWith("[")) {
            System.err.println("Invalid JSON patch document. Terminating update request.");
            return false;
        }

        String jsonFile = new String(router.getRepository().readFile(fullDirectoryPath + resourcePath + ".json"));
        JSONArray patchInstructions = JSONHandler.parseArray(patchDocument);
        JSONObject updatedJSONObject = JSONHandler.parse(jsonFile);

        for (int i = 0; i < patchInstructions.length(); i++) {
            JSONObject instructions = (JSONObject) patchInstructions.get(i);
            if (!instructions.keySet().contains("op") && !instructions.keySet().contains("path")) {
                System.err.println("Invalid JSON patch document. Terminating update request.");
                return false;
            }
            String operation = (String) instructions.get("op");
            String path = (String) instructions.get("path");
            String value = instructions.has("value") ? (String) instructions.get("value") : "";

            updatedJSONObject = JSONHandler.run(operation, path, value, updatedJSONObject);
        }

        save(resourcePath, "json", updatedJSONObject.toString().getBytes());
        return true;
    }
}
