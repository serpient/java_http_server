package http_server;

import java.util.List;
import directory_page_creator.DirectoryPageCreator;
import http_standards.MIMETypes;

public class RouterDirectoryHandler {
    public static void createDirectory(Router router, String directoryPath) {
        initializeDirectory(router);

        router.get(directoryPath, (Request request, Response response) -> {
            response.setBody(initializeDirectory(router).getBytes(), MIMETypes.html);
        });

        router.get("/", (Request request, Response response) -> {
            response.redirect(directoryPath);
        });
    }

    private static String initializeDirectory(Router router) {
        List<String> directoryContents = router.getRepository().readDirectoryContents(router.getFullDirectoryPath().toString());
        createContentRoutes(router, directoryContents);
        return DirectoryPageCreator.generateHTML(directoryContents, router.getDirectoryPath());
    }

    private static void createContentRoutes(Router router, List<String> directoryContents) {
        for (int i = 0; i < directoryContents.size(); i++) {
            String fileName = directoryContents.get(i);
            String fileNameWithoutFileType = fileName;
            if (fileName.contains(".")) {
                fileNameWithoutFileType = fileName.substring(0, fileName.lastIndexOf("."));
            }
            String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);

            RouterResourceHandler.paths(router, "/" + fileNameWithoutFileType, fileType).forEach(path -> {
                router.get(path, (Request request, Response response) -> {
                    response.setFile("/" + fileName);
                });
                router.delete(path, (Request request, Response response) -> {
                    response.setFile("/" + fileName);
                });
            });
        }
    }

}
