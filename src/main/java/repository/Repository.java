package repository;

import java.util.List;

public interface Repository {
    List<String> readDirectoryContents(String path);
    byte[] readFile(String path);
    String getFileType(String path);
    void writeFile(String intendedFilePath, String fileType, byte[] fileContents);
    void writeAndAppendFile(String intendedFilePath, String fileType, byte[] fileContents);
    void deleteFile(String filePath);
}
