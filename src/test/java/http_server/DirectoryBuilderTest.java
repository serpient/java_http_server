package http_server;

import org.junit.Test;

import java.util.List;
import static org.junit.Assert.assertEquals;

public class DirectoryBuilderTest {
    @Test
    public void static_files_in_public_directory_are_rendered_as_a_HTML_page_with_contents_listed() {
        String path = "/Users/fsadikin/documents/java_http_server/public";
        List<String> directoryContents = new FileHandler().readDirectoryContents(path);
        DirectoryBuilder directoryBuilder = new DirectoryBuilder(directoryContents);
        String htmlOutput = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "<meta charset=\"UTF-8\">\n" +
                "<title>Home Page</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<ul>\n" +
                "<li>Home.html</li>\n" +
                "<li>TurtleTab.txt</li>\n" +
                "<li>japan.png</li>\n" +
                "<li>water.png</li>\n" +
                "</ul>\n" +
                "</body>\n" +
                "</html>";

        assertEquals(htmlOutput, directoryBuilder.generateHTML());
    }
}
