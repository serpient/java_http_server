package directory_page_creator;

import http_standards.MIMETypes;
import mocks.MockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.Repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;

public class DirectoryPageCreatorTest {
    Repository repository;

    @BeforeEach
    public void createRepository() {
        repository = new MockRepository("/public");
        repository.writeFile("./public/Home.html", MIMETypes.html, "<!DOCTYPE html>\n".getBytes());
        repository.writeFile("./public/TurtleTab.txt", MIMETypes.plain, "TurtleTabs a Google".getBytes());
        repository.writeFile("./public/water.png", MIMETypes.png, "water image".getBytes());
        repository.writeFile("./public/japan.png", MIMETypes.png, "japan image".getBytes());
    }

    @Test
    public void static_files_in_public_directory_are_rendered_as_a_HTML_page_with_contents_listed() {
        String path = "./public";
        List<String> directoryContents = repository.readDirectoryContents(path);
        System.err.println(directoryContents);
        DirectoryPageCreator directoryPageCreator = new DirectoryPageCreator(directoryContents, "/public");
        String page = directoryPageCreator.generateHTML();
        String homeList = "<li class='bullets'><a href='/public/Home.html'>Home.html</a></li>\n";
        String japanList = "<li class='bullets'><a href='/public/japan.png'>japan.png</a></li>\n";
        String waterList =  "<li class='bullets'><a href='/public/water.png'>water.png</a></li>\n";
        String turtleList = "<li class='bullets'><a href='/public/TurtleTab.txt'>TurtleTab.txt</a></li>\n";

        assertEquals(true, page.contains(homeList));
        assertEquals(true, page.contains(japanList));
        assertEquals(true, page.contains(waterList));
        assertEquals(true, page.contains(turtleList));
    }
}
