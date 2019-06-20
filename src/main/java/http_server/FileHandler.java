package http_server;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileHandler {
    public List<String> readDirectoryContents(String path) {
        List<String> fileList = new ArrayList<String>();

        Path dir = Paths.get(path);
        String allowedFileTypes = "[!.DS_]*";
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, allowedFileTypes)) {
            for (Path file: stream) {
                fileList.add(file.getFileName().toString());
            }
        } catch (IOException | DirectoryIteratorException x) {
            // IOException can never be thrown by the iteration.
            // In this snippet, it can only be thrown by newDirectoryStream.
            System.err.println(x);
        }
        Collections.sort(fileList);
        return fileList;
    }

}
