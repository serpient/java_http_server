package http_server;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
            // IOException can never be thrown by the iteration.
            // In this snippet, it can only be thrown by newDirectoryStream.
            System.err.println(x);
        }
        Collections.sort(fileList);
        return fileList;
    }

    public static BufferedImage readImage(String path) {
        Path image = Paths.get(path);
        try {
            BufferedImage img = ImageIO.read(image.toFile());
            System.err.println(img);
            return img;
        } catch (IOException e) {
            System.err.println(e);
            return null;
        }
    }

    public static String readFile(String path) {
        String fileContents = "";
        Path file = Paths.get(path);

        boolean isRegularExecutableFile = Files.isRegularFile(file) & Files.isReadable(file);

        Charset charset = Charset.forName("US-ASCII");
        if (isRegularExecutableFile) {
            try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
                String line = null;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
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

    public static String getFileName(String path) {
        Path file = Paths.get(path);
        return file.getFileName().toString();
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

}
