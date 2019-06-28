package repository;

import http_protocol.MIMETypes;
import mocks.MockRepository;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MockRepositoryTest {
    MockRepository repository = new MockRepository("/public");

    @Test
    public void repository_can_read_from_a_directory() {
        List<String> directoryContents = repository.readDirectoryContents("/public");

        assertEquals(true, directoryContents.contains("Home.html"));
        assertEquals(true, directoryContents.contains("TurtleTab.txt"));
        assertEquals(true, directoryContents.contains("water.png"));
        assertEquals(true, directoryContents.contains("japan.png"));
    }

    @Test
    public void repository_can_read_from_a_prepended_directory() {
        List<String> directoryContents = repository.readDirectoryContents("./public");

        assertEquals(true, directoryContents.contains("Home.html"));
        assertEquals(true, directoryContents.contains("TurtleTab.txt"));
        assertEquals(true, directoryContents.contains("water.png"));
        assertEquals(true, directoryContents.contains("japan.png"));
    }

    @Test
    public void repository_can_read_a_file_given_a_path() {
        String turtleText = "TurtleTabs a Google";

        assertEquals(turtleText, new String(repository.readFile("./public/TurtleTab.txt")));
    }

    @Test
    public void repository_can_write_to_directory() {
        byte[] newContent = "test content".getBytes();
        repository.writeFile("./public/test_file.txt", MIMETypes.plain, newContent);
        assertEquals(newContent, repository.readFile("./public/test_file.txt"));
    }

    @Test
    public void repository_can_get_file_type() {
        assertEquals(MIMETypes.png, repository.getFileType("./public/water.png"));
    }

    @Test
    public void repository_can_delete_file() {
        byte[] newContent = "test content".getBytes();
        repository.writeFile("./public/test_file.txt", MIMETypes.plain, newContent);
        repository.deleteFile("./public/test_file.txt");
        assertThrows(NullPointerException.class, () -> repository.readFile("./public/test_file.txt"));
    }

    @Test
    public void repository_can_format_path() {
        String invalidPath = "/Users/fsadikin/Documents/java_http_server/public/dog/1";
        assertEquals("/public/dog/1", repository.validatedPath(invalidPath));
    }
}
