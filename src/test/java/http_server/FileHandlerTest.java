package http_server;

import org.junit.Test;

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
    public void file_can_be_ready_from_public_directory() {
        
    }

}
