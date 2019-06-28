package directory_page_creator;

import mocks.MockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.Repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;

public class DirectoryPageCreatorTest {
    Repository repository = new MockRepository("/public");
    @BeforeEach
    public void prepFiles() {
        repository.deleteDirectory("./public/dog");
        repository.deleteDirectory("./public/cat");
        repository.deleteDirectory("./public/delete_me.txt");
    }

    @AfterEach
    public void cleanUpFiles() {
        repository.deleteDirectory("./public/dog");
        repository.deleteDirectory("./public/cat");
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
