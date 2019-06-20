package http_server;

import org.junit.Test;

import java.awt.image.BufferedImage;

import static org.junit.Assert.assertEquals;

public class FileHandlerTest {
    @Test
    public void static_files_in_public_directory_are_rendered_as_a_HTML_page_with_contents_listed() {
        FileHandler fileHandler = new FileHandler();
        String path = "/Users/fsadikin/documents/java_http_server/public";

        assertEquals(true, fileHandler.readDirectoryContents(path).contains("Home.html"));
        assertEquals(true, fileHandler.readDirectoryContents(path).contains("TurtleTab.txt"));
        assertEquals(true, fileHandler.readDirectoryContents(path).contains("japan.png"));
        assertEquals(true, fileHandler.readDirectoryContents(path).contains("water.png"));
    }

    @Test
    public void html_file_can_be_read_from_public_directory() {
        FileHandler fileHandler = new FileHandler();
        String path = "/Users/fsadikin/documents/java_http_server/public/Home.html";
        String homeHTML = "<!DOCTYPE html>" +
                "<html lang=\"en\">" +
                "<head>" +
                "    <meta charset=\"UTF-8\">" +
                "    <title>Home Page</title>" +
                "</head>" +
                "<body BGCOLOR=\"FFFFFF\">" +
                "<h1>HELLO!!</h1>" +
                "<p>This is a very simple HTML document</p>" +
                "<p>It only has two paragraphs</p>" +
                "</body>" +
                "</html>";
        assertEquals(homeHTML, fileHandler.readFile(path));
    }

    @Test
    public void html_type_can_be_determined() {
        FileHandler fileHandler = new FileHandler();
        String path = "/Users/fsadikin/documents/java_http_server/public/Home.html";
        assertEquals("text/html", fileHandler.getFileType(path));
    }

    @Test
    public void text_file_can_be_read_from_public_directory() {
        FileHandler fileHandler = new FileHandler();
        String path = "/Users/fsadikin/documents/java_http_server/public/TurtleTab.txt";
        String homeHTML = "TurtleTab is a Google Chrome Extension Built with React. It creates a new homepage which " +
                "features current Weather, Todo and Notes functionality. It also accesses your browser data to see Bookmarks, enable/disable Apps and Extensions, and see/clear your History. It is a collaborative effort by a remote team of aspiring developers who met on Chingu, an international community of coders.";
        assertEquals(homeHTML, fileHandler.readFile(path));
    }

    @Test
    public void text_type_can_be_determined() {
        FileHandler fileHandler = new FileHandler();
        String path = "/Users/fsadikin/documents/java_http_server/public/TurtleTab.txt";
        assertEquals("text/plain", fileHandler.getFileType(path));
    }

    @Test
    public void image_file_can_be_read_from_public_directory() {
        FileHandler fileHandler = new FileHandler();
        String path = "/Users/fsadikin/documents/java_http_server/public/japan.png";
        BufferedImage image = fileHandler.readImage(path);
        assertEquals("class java.awt.image.BufferedImage", image.getClass().toString());
    }

    @Test
    public void image_type_can_be_determined() {
        FileHandler fileHandler = new FileHandler();
        String path = "/Users/fsadikin/documents/java_http_server/public/japan.png";
        assertEquals("image/png", fileHandler.getFileType(path));
    }

}
