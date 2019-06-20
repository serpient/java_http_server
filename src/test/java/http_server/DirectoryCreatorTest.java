package http_server;

import org.junit.Test;

import java.util.List;
import static org.junit.Assert.assertEquals;

public class DirectoryCreatorTest {
    @Test
    public void static_files_in_public_directory_are_rendered_as_a_HTML_page_with_contents_listed() {
        String path = "/Users/fsadikin/documents/java_http_server/public";
        List<String> directoryContents = new FileHandler().readDirectoryContents(path);
        DirectoryCreator directoryCreator = new DirectoryCreator(directoryContents, "/public");

        String directoryBody = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "<meta charset=\"UTF-8\">\n" +
                "<title>Home Page</title>\n" +
                "<style>.directory-page {    padding: 20px;    font-family: -apple-system, BlinkMacSystemFont, \"Segoe UI\", \"Roboto\", \"Oxygen\", \"Ubuntu\", \"Cantarell\", \"Fira Sans\",    \"Droid Sans\", \"Helvetica Neue\", sans-serif;    font-size: 20px;}.bullets {    color: grey;    margin: 20px 0px;}h1 {    text-align: center;    color: dark-grey;    font-weight: 600;    font-size: 42px;}hr {    color: grey;    border-weight: 2px;}</style></head>\n" +
                "<body>\n" +
                "<div class='directory-page'><h1>Directory for /public</h1><hr /><ul>\n" +
                "<li class='bullets'><a href='/public/Home.html'>Home.html</a></li>\n" +
                "<li class='bullets'><a href='/public/TurtleTab.txt'>TurtleTab.txt</a></li>\n" +
                "<li class='bullets'><a href='/public/japan.png'>japan.png</a></li>\n" +
                "<li class='bullets'><a href='/public/water.png'>water.png</a></li>\n" +
                "</ul>\n" +
                "</div></body>\n" +
                "</html>";

        assertEquals(directoryBody, directoryCreator.generateHTML());
    }
}
