package repository;

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

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

public class FileRepository implements Repository {
    public List<String> readDirectoryContents(String path) {
        List<String> fileList = new ArrayList<String>();

        Path dir = Paths.get(path);
        String allowedFileTypes = "[!.DS_]*";
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, allowedFileTypes)) {
            for (Path file: stream) {
                if (file.toFile().isFile()) {
                    String name = file.toString();
                    fileList.add(name.substring(path.endsWith("/") ? path.length() : path.length() + 1));
                } else {
                    List<String> subContents = readDirectoryContents(file.toString(), path);
                    fileList.addAll(subContents);
                }
            }
        } catch (IOException | DirectoryIteratorException x) {
            System.err.println(x);
        }
        return fileList;
    }

    public static List<String> readDirectoryContents(String path, String dirPath) {
        List<String> fileList = new ArrayList<String>();

        Path dir = Paths.get(path);
        String allowedFileTypes = "[!.DS_]*";
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, allowedFileTypes)) {
            for (Path file: stream) {
                if (file.toFile().isFile()) {
                    String name = file.toString();
                    fileList.add(name.substring(dirPath.endsWith("/") ? dirPath.length() : dirPath.length() + 1));
                } else {
                    List<String> subContents = readDirectoryContents(file.toString(), dirPath);
                    fileList.addAll(subContents);
                }
            }
        } catch (IOException | DirectoryIteratorException x) {
            System.err.println(x);
        }
        Collections.sort(fileList);
        return fileList;
    }

    public byte[] readFile(String path) {
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
                String line;
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

    public String getFileType(String path) {
        Path file = Paths.get(path);
        try {
            return Files.probeContentType(file);
        } catch (IOException e) {
            System.err.println(e);
            return "";
        }
    }

    public void writeFile(String intendedFilePath, String fileType, byte[] fileContents) {
        streamWriter(intendedFilePath, fileType, fileContents, false);
    }

    public void writeAndAppendFile(String intendedFilePath, String fileType, byte[] fileContents) {
        streamWriter(intendedFilePath, fileType, fileContents, true);
    }

    private void streamWriter(String intendedFilePath, String fileType, byte[] fileContents, boolean appendFile)  {
        Path path = Paths.get(intendedFilePath + "." + fileType);
        createDirectories(Paths.get(trimLastResource(intendedFilePath)));
        try {
            OutputStream fileOptions = appendFile ? Files.newOutputStream(path,  CREATE, APPEND) : Files.newOutputStream(path);
            OutputStream out = new BufferedOutputStream(fileOptions);
            out.write(fileContents, 0, fileContents.length);
            out.flush();
            out.close();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public void createDirectories(Path directoryPaths) {
        try {
            Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxrwxrwx");
            FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(perms);
            Files.createDirectories(directoryPaths, attr);
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public void deleteDirectory(String directoryPath) {
        List<String> directoryContents = readDirectoryContents(directoryPath);
        for (int i = 0; i < directoryContents.size(); i++) {
            deleteFile(directoryPath + "/" + directoryContents.get(i));
        }
        deleteFile(directoryPath);
    }

    public void deleteFile(String filePath) {
        Path path = Paths.get(filePath);

        try {
            Files.delete(path);
            System.err.println("Deleted " + path);
        } catch (DirectoryNotEmptyException x) {
            System.err.format("%s not empty%n", path);
        } catch (IOException x) {
            System.err.println(x);
        }
    }

    public String trimLastResource(String path) {
        int lastSlashIndex = path.lastIndexOf("/");
        return path.substring(0, lastSlashIndex);
    }
}
