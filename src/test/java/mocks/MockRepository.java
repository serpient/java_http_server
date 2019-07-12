package mocks;

import http_standards.MIMETypes;
import repository.MockFile;
import repository.Repository;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class MockRepository implements Repository  {
    String directory;
    private HashMap<String, MockFile> memoryRepository = new HashMap<>();

    public MockRepository(String directory) {
        this.directory = directory;
    }

    public List<String> readDirectoryContents(String path) {
        List<String> directory = memoryRepository
                .keySet()
                .stream()
                .filter(filePath -> filePath.startsWith(validatedPath(path)))
                .map(filePath -> filePath.substring(validatedPath(path).length() + 1))
                .collect(Collectors.toList());
        return directory;
    }

    public byte[] readFile(String path) {
        return getFile(path).content();
    }

    public String getFileType(String path) {
        return getFile(path).type();
    }

    public void writeFile(String intendedFilePath, String fileType, byte[] fileContents) {
        intendedFilePath = validatedPath(intendedFilePath);
        String fileName = intendedFilePath.substring(intendedFilePath.lastIndexOf("/"));
        String validatedFileType = MIMETypes.isMIMEType(fileType) ? fileType : MIMETypes.getMIMEType(fileType);

        memoryRepository.put(intendedFilePath, new MockFile(fileName, validatedFileType, fileContents));
    }

    public void writeAndAppendFile(String path, String fileType, byte[] fileContents) {
        String validatedPath = validatedPath(path);
        String fileName = validatedPath.substring(validatedPath.lastIndexOf("/"));
        String validatedFileType = MIMETypes.isMIMEType(fileType) ? fileType : MIMETypes.getMIMEType(fileType);

        byte[] appendedContents = appendFileContents(validatedPath, fileContents);
        memoryRepository.put(validatedPath, new MockFile(fileName, validatedFileType, appendedContents));
    }

    private byte[] appendFileContents(String path, byte[] fileContents) {
        String content = "";
        if (memoryRepository.containsKey(path)) {
            content = new String(readFile(path));
        }
        String appendedContent = content + new String(fileContents);
        return appendedContent.getBytes();
    }

    public void deleteFile(String filePath) {
        memoryRepository.remove(validatedPath(filePath));
    }

    public String validatedPath(String path) {
        if (path.contains(directory)) {
            return path.startsWith(directory) ? path : path.substring(path.indexOf(directory));
        } else {
            return path;
        }

    }

    private String trimmedPath(String path) {
        return path.substring(0, path.lastIndexOf("."));
    }

    private MockFile getFile(String path) {
        path = validatedPath(path);
        if (memoryRepository.containsKey(path)) {
            return memoryRepository.get(path);
        } else if (memoryRepository.containsKey(trimmedPath(path)))  {
            return memoryRepository.get(trimmedPath(path));
        } else {
            System.err.println("File path not valid");
            throw new NullPointerException();
        }
    }
}
