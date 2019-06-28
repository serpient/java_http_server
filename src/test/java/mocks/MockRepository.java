package mocks;

import http_standards.MIMETypes;
import repository.MockFile;
import repository.Repository;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class MockRepository implements Repository  {
    String directory;
    private HashMap<String, MockFile> memoryRepository = new HashMap<>();

    public MockRepository(String directory) {
        this.directory = directory;

        memoryRepository.put(filePath("/Home.html"), new MockFile("Home.html", MIMETypes.html,
                "<!DOCTYPE html>\n".getBytes()));

        memoryRepository.put(filePath("/TurtleTab.txt"), new MockFile("TurtleTab.txt", MIMETypes.plain, (
                "TurtleTabs a Google").getBytes()));

        memoryRepository.put(filePath("/water.png"), new MockFile("water.png", MIMETypes.png,
                "water image".getBytes()));

        memoryRepository.put(filePath("/japan.png"), new MockFile("japan.png", MIMETypes.png,
                "japan image".getBytes()));
    }

    private String filePath(String path) {
        return directory + path;
    }

    public List<String> readDirectoryContents(String path) {
        List<String> directory = memoryRepository
                .keySet()
                .stream()
                .filter(x -> x.startsWith(validatedPath(path)))
                .map(x -> x.substring(validatedPath(path).length() + 1))
                .collect(Collectors.toList());
        return directory;
    }

    public byte[] readFile(String path) {
        path = validatedPath(path);
        if (memoryRepository.containsKey(path)) {
            return memoryRepository.get(path).content();
        } else if (memoryRepository.containsKey(trimmedPath(path)))  {
            return memoryRepository.get(trimmedPath(path)).content();
        } else {
            throw new NullPointerException();
        }
    }

    public String getFileType(String path) {
        path = validatedPath(path);
        if (memoryRepository.containsKey(path)) {
            return memoryRepository.get(path).type();
        } else if (memoryRepository.containsKey(trimmedPath(path)))  {
            System.err.println(trimmedPath(path));
            return memoryRepository.get(trimmedPath(path)).type();
        } else {
            return "";
        }
    }
    public void writeFile(String intendedFilePath, String fileType, byte[] fileContents) {
        intendedFilePath = validatedPath(intendedFilePath);
        String fileName = intendedFilePath.substring(intendedFilePath.lastIndexOf("/"));
        memoryRepository.put(intendedFilePath, new MockFile(fileName, MIMETypes.getMIMEType(fileType), fileContents));
    }
    public void createDirectories(Path directoryPaths) {
        return;
    }
    public void deleteDirectory(String directoryPath) {
        return;
    }
    public void deleteFile(String filePath) {
        memoryRepository.remove(validatedPath(filePath));
    }

    public String validatedPath(String path) {
        return path.startsWith(directory) ? path : path.substring(path.indexOf(directory));
    }

    public String trimmedPath(String path) {
        return path.substring(0, path.lastIndexOf("."));
    }
}
