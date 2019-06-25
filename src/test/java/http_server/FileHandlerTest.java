package http_server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FileHandlerTest {
    @Test
    public void static_files_in_public_directory_are_rendered_as_a_HTML_page_with_contents_listed() {
        String path = "./public";

        assertEquals(true, FileHandler.readDirectoryContents(path).contains("Home.html"));
        assertEquals(true, FileHandler.readDirectoryContents(path).contains("TurtleTab.txt"));
        assertEquals(true, FileHandler.readDirectoryContents(path).contains("japan.png"));
        assertEquals(true, FileHandler.readDirectoryContents(path).contains("water.png"));
    }

    @Test
    public void html_file_can_be_read_from_public_directory() {
        String path = "./public/Home.html";
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
        assertEquals(homeHTML, FileHandler.getFileContents(path));
    }

    @Test
    public void html_type_can_be_determined() {
        String path = "./public/Home.html";
        assertEquals("text/html", FileHandler.getFileType(path));
    }

    @Test
    public void text_file_can_be_read_from_public_directory() {
        String path = "./public/TurtleTab.txt";
        String homeHTML = "TurtleTab is a Google Chrome Extension Built with React. It creates a new homepage which " +
                "features current Weather, Todo and Notes functionality. It also accesses your browser data to see Bookmarks, enable/disable Apps and Extensions, and see/clear your History. It is a collaborative effort by a remote team of aspiring developers who met on Chingu, an international community of coders.";
        assertEquals(homeHTML, FileHandler.getFileContents(path));
    }

    @Test
    public void text_type_can_be_determined() {
        String path = "./public/TurtleTab.txt";
        assertEquals("text/plain", FileHandler.getFileType(path));
    }

    @Test
    public void image_file_can_be_read_from_public_directory() {
        String path = "./public/water.png";
        byte[] image = FileHandler.readFile(path);
        int imageContentLength = 1448876;
        assertEquals(imageContentLength, image.length);
    }

    @Test
    public void image_type_can_be_determined() {
        String path = "./public/japan.png";
        assertEquals("image/png", FileHandler.getFileType(path));
    }

    @Test
    public void file_can_be_written_to_directory() {
        String path = "./public/dog/1";
        String body = "Dog Breed: Corgi";
        FileHandler.writeFile(path, "txt", body.getBytes());
        assertEquals(body, new String(FileHandler.readFile("./public/dog/1.txt")));
    }

    @Test
    public void path_can_trim_last_resource() {
        String path = "./public/dog/1";
        assertEquals("./public/dog", FileHandler.trimLastResource(path));
    }

    @Test
    public void file_can_be_deleted() {
        String path = "./public/dog/1";
        String body = "Dog Breed: Corgi";
        FileHandler.writeFile(path, "txt", body.getBytes());


        String deleteThisPath = "./public/dog/1.txt";
        FileHandler.deleteFile(deleteThisPath);
        assertEquals(null, FileHandler.readFile("./public/dog/1.txt"));
    }


    @Test
    public void directory_can_be_deleted() {
        String path = "./public/dog/1";
        String body = "Dog Breed: Corgi";
        FileHandler.writeFile(path, "txt", body.getBytes());

        String deleteThisPath = "./public/dog";
        FileHandler.deleteDirectory(deleteThisPath);
        assertEquals(null, FileHandler.readFile("./public/dog/1.txt"));
        assertEquals(null, FileHandler.readFile("./public/dog"));
    }
}
