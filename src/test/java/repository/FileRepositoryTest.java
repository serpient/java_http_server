package repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileRepositoryTest {
    FileRepository fileHandler;

    @BeforeEach
    public void cleanRepository() {
        fileHandler = new FileRepository();
    }

    @AfterEach
    public void deleteRepository() {
        fileHandler.deleteDirectory("./public/test");
    }


    @Test
    public void static_files_in_public_directory_are_rendered_as_a_HTML_page_with_contents_listed() {
        String path = "./public";

        assertEquals(true, fileHandler.readDirectoryContents(path).contains("Home.html"));
        assertEquals(true, fileHandler.readDirectoryContents(path).contains("TurtleTab.txt"));
        assertEquals(true, fileHandler.readDirectoryContents(path).contains("japan.png"));
        assertEquals(true, fileHandler.readDirectoryContents(path).contains("water.png"));
    }

    @Test
    public void html_file_can_be_read_from_public_directory() {
        String path = "./public/Home.html";
        String homeHTML = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Home Page</title>\n" +
                "</head>\n" +
                "<body BGCOLOR=\"FFFFFF\">\n" +
                "\n" +
                "<h1>HELLO!!</h1>\n" +
                "<p>This is a very simple HTML document</p>\n" +
                "<p>It only has two paragraphs</p>\n" +
                "\n" +
                "</body>\n" +
                "</html>";
        assertEquals(homeHTML, new String(fileHandler.readFile(path)));
    }

    @Test
    public void html_type_can_be_determined() {
        String path = "./public/Home.html";
        assertEquals("text/html", fileHandler.getFileType(path));
    }

    @Test
    public void text_file_can_be_read_from_public_directory() {
        String path = "./public/TurtleTab.txt";
        String homeHTML = "TurtleTab is a Google Chrome Extension Built with React. It creates a new homepage which " +
                "features current Weather, Todo and Notes functionality. It also accesses your browser data to see Bookmarks, enable/disable Apps and Extensions, and see/clear your History. It is a collaborative effort by a remote team of aspiring developers who met on Chingu, an international community of coders.";
        assertEquals(homeHTML, new String(fileHandler.readFile(path)));
    }

    @Test
    public void text_type_can_be_determined() {
        String path = "./public/TurtleTab.txt";
        assertEquals("text/plain", fileHandler.getFileType(path));
    }

    @Test
    public void image_file_can_be_read_from_public_directory() {
        String path = "./public/water.png";
        byte[] image = fileHandler.readFile(path);
        int imageContentLength = 1448876;
        assertEquals(imageContentLength, image.length);
    }

    @Test
    public void image_type_can_be_determined() {
        String path = "./public/japan.png";
        assertEquals("image/png", fileHandler.getFileType(path));
    }

    @Test
    public void file_can_be_written_to_directory() {
        String path = "./public/test/1";
        String body = "Dog Breed: Corgi";
        fileHandler.writeFile(path, "txt", body.getBytes());
        assertEquals(body, new String(fileHandler.readFile("./public/test/1.txt")));
    }

    @Test
    public void path_can_trim_last_resource() {
        String path = "./public/test/1";
        assertEquals("./public/test", fileHandler.trimLastResource(path));
    }

    @Test
    public void file_can_be_deleted() {
        String path = "./public/test/1";
        String body = "Dog Breed: Corgi";
        fileHandler.writeFile(path, "txt", body.getBytes());


        String deleteThisPath = "./public/test/1.txt";
        fileHandler.deleteFile(deleteThisPath);
        assertEquals(null, fileHandler.readFile("./public/test/1.txt"));
    }


    @Test
    public void directory_can_be_deleted() {
        String path = "./public/test/1";
        String body = "Dog Breed: Corgi";
        fileHandler.writeFile(path, "txt", body.getBytes());

        String deleteThisPath = "./public/test";
        fileHandler.deleteDirectory(deleteThisPath);
        assertEquals(null, fileHandler.readFile("./public/test/1.txt"));
        assertEquals(null, fileHandler.readFile("./public/test"));
    }

    @Test
    public void directory_can_read_subfolders() {
        String path = "./public/test/1";
        String body = "Dog Breed: Corgi";
        fileHandler.writeFile(path, "txt", body.getBytes());

        String path2 = "./public/test/2";
        String body2 = "<h1>CAT Breed: Corgi</h1>";
        fileHandler.writeFile(path2, "html", body2.getBytes());


        String basePath = "./public/";

        assertEquals(true, fileHandler.readDirectoryContents(basePath).contains("Home.html"));
        assertEquals(true, fileHandler.readDirectoryContents(basePath).contains("TurtleTab.txt"));
        assertEquals(true, fileHandler.readDirectoryContents(basePath).contains("japan.png"));
        assertEquals(true, fileHandler.readDirectoryContents(basePath).contains("water.png"));
        assertEquals(true, fileHandler.readDirectoryContents(basePath).contains("test/2.html"));
        assertEquals(true, fileHandler.readDirectoryContents(basePath).contains("test/1.txt"));
    }
}
