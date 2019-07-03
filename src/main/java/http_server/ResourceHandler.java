package http_server;

import directory_page_creator.DirectoryPageCreator;
import http_standards.MIMETypes;
import repository.Repository;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class ResourceHandler {
    private static Path fullStaticDirectoryPath;
    private static String directoryPath;
    private static Router router;

    public ResourceHandler(Router router, String directoryPath) {
        this.router = router;
        this.fullStaticDirectoryPath = router.getFullStaticDirectoryPath();
        this.directoryPath = directoryPath;
    }

    public void createStaticDirectory(String staticDirectoryRelativePath) {
        List<String> directoryContents = router.getRepository().readDirectoryContents(fullStaticDirectoryPath.toString());
        createStaticResourceRoute(directoryContents, staticDirectoryRelativePath);
        createStaticDirectoryRoute(staticDirectoryRelativePath);
    }

    public void save(String resourcePath, String fileType, byte[] content) {
        router.getRepository().writeFile(fullStaticDirectoryPath + resourcePath, fileType, content);
    }

    public void delete(String resourcePath, String fileType) {
        router.getRepository().deleteFile(fullStaticDirectoryPath + resourcePath);
    }

    private void createStaticDirectoryRoute(String staticDirectoryRelativePath) {
        router.get(staticDirectoryRelativePath, (Request request, Response response) -> {
            List<String> directoryContents = router.getRepository().readDirectoryContents(fullStaticDirectoryPath.toString());
            response.sendBody(new DirectoryPageCreator(directoryContents, staticDirectoryRelativePath).generateHTML().getBytes(), MIMETypes.html);
        });
    }

    private void createStaticResourceRoute(List<String> directoryContents, String staticDirectoryRelativePath) {
        for (int i = 0; i < directoryContents.size(); i++) {
            String fileName = directoryContents.get(i);
            String filePath = staticDirectoryRelativePath + "/" + fileName;

            router.get(filePath, (Request request, Response response) -> {
                response.sendFile("/" + fileName);
            });
        }
    }

    public List<String> paths(String resourcePath, String fileType) {
        return Arrays.asList(
                resourcePath,
                resourcePath + "." + fileType,
                directoryPath + resourcePath,
                directoryPath + resourcePath + "." + fileType
        );
    }

}
