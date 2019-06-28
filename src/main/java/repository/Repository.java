package repository;

import java.nio.file.Path;
import java.util.List;

public interface Repository {
    List<String> readDirectoryContents(String path);
    byte[] readFile(String path);
    String getFileType(String path);
    void writeFile(String intendedFilePath, String fileType, byte[] fileContents);
    void createDirectories(Path directoryPaths);
    void deleteDirectory(String directoryPath);
    void deleteFile(String filePath);
    static String trimLastResource(String path) {
        int lastSlashIndex = path.lastIndexOf("/");
        return path.substring(0, lastSlashIndex);
    }
}
