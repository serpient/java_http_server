package http_server;

import http_standards.StatusCode;
import json_handler.JSONPatcher;

import java.util.Arrays;
import java.util.List;

public class RouterResourceHandler {
    public static OperationResult save(Router router, String resourcePath, String fileType, byte[] content) {
        router.getRepository().writeFile(router.getFullDirectoryPath() + resourcePath, fileType, content);
        paths(router, resourcePath, fileType).forEach(path -> {
            router.get(path, (Request request, Response response) -> {
                response.setFile(resourcePath + "." + fileType);
            });
            router.delete(path, (Request request, Response response) -> {
                OperationResult result = delete(router, resourcePath, fileType);
                response.forDelete(result);
            });
        });
        return new OperationResult(true, StatusCode.created, content);
    }

    public static OperationResult delete(Router router, String resourcePath, String fileType) {
        try {
            String fullFilePath = router.getDirectoryPath() + resourcePath + "." + fileType;
            byte[] fileContent = router.getRepository().readFile(fullFilePath);
            router.getRepository().deleteFile(router.getFullDirectoryPath() + resourcePath);
            paths(router, resourcePath, fileType).forEach(pathToDelete -> router.getRoutes().remove(pathToDelete));
            return new OperationResult(true, StatusCode.noContent, fileContent);
        } catch (NullPointerException e) {
            System.err.println("Deletion of file action terminated. File path was not valid.");
            return new OperationResult(false, StatusCode.internalError);
        }
    }

    public static List<String> paths(Router router, String resourcePath, String fileType) {
        return Arrays.asList(
                resourcePath,
                resourcePath + "." + fileType,
                router.getDirectoryPath() + resourcePath,
                router.getDirectoryPath() + resourcePath + "." + fileType
        );
    }

    public static OperationResult updateJSON(Router router, String resourcePath, String patchDocument) {
        try {
            String jsonFile = new String(router.getRepository().readFile(router.getFullDirectoryPath() + resourcePath + ".json"));
            OperationResult patchResult = JSONPatcher.runPatchDocument(patchDocument, jsonFile);
            if (patchResult.valid()) {
                save(router, resourcePath, "json", patchResult.data());
            }
            return patchResult;
        } catch (NullPointerException e) {
            return new OperationResult(false, StatusCode.internalError);
        }
    }
}
