package http_server;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class FileHandler {
    public static List<String> readDirectoryContents(String path) {
        List<String> fileList = new ArrayList<String>();

        Path dir = Paths.get(path);
        String allowedFileTypes = "[!.DS_]*";
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, allowedFileTypes)) {
            for (Path file: stream) {
                fileList.add(file.getFileName().toString());
            }
        } catch (IOException | DirectoryIteratorException x) {
            System.err.println(x);
        }
        Collections.sort(fileList);
        return fileList;
    }

    public static byte[] readFile(String path) {
        Path file = Paths.get(path);
        try {
            byte[] fileBytes = Files.readAllBytes(file);
            return fileBytes;
        } catch (NoSuchFileException e) {
            System.err.format("%s: no such" + " file or directory%n", path);
            return null;
        } catch (IOException e) {
            System.err.println(e);
            return null;
        }
    }

    public static String getFileContents(String path) {
        String fileContents = "";
        Path file = Paths.get(path);

        boolean isRegularExecutableFile = Files.isRegularFile(file) & Files.isReadable(file);

        Charset charset = Charset.forName("US-ASCII");
        if (isRegularExecutableFile) {
            try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
                String line = null;
                while ((line = reader.readLine()) != null) {
                    fileContents += line;
                }
            } catch (IOException x) {
                System.err.format("IOException: %s%n", x);
            }
        } else {
            System.err.println("[ " + path + " ] is not a readable file");
        }

        return fileContents;
    }

    public static String getFileType(String path) {
        Path file = Paths.get(path);
        try {
            return Files.probeContentType(file);
        } catch (IOException e) {
            System.err.println(e);
            return "";
        }
    }

    public static void writeFile(String intendedFilePath, String fileType, byte[] fileContents) {
        Path path = Paths.get(intendedFilePath + "." + fileType);

        createDirectories(Paths.get(trimLastResource(intendedFilePath)));

        try (OutputStream out = new BufferedOutputStream(
            Files.newOutputStream(path))
        ) {
            out.write(fileContents, 0, fileContents.length);
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public static void createDirectories(Path directoryPaths) {
        try {
            Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxrwxrwx");
            FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(perms);
            Files.createDirectories(directoryPaths, attr);
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public static void deleteDirectory(String directoryPath) {
        List<String> directoryContents = readDirectoryContents(directoryPath);
        for (int i = 0; i < directoryContents.size(); i++) {
            deleteFile(directoryPath + "/" + directoryContents.get(i));
        }
        deleteFile(directoryPath);
    }

    public static void deleteFile(String filePath) {
        Path path = Paths.get(filePath);

        try {
            Files.deleteIfExists(path);
        } catch (DirectoryNotEmptyException x) {
            System.err.format("%s not empty%n", path);
        } catch (IOException x) {
            // File permission problems are caught here.
            System.err.println(x);
        }
    }

    public static String trimLastResource(String path) {
        int lastSlashIndex = path.lastIndexOf("/");
        return path.substring(0, lastSlashIndex);
    }

}
