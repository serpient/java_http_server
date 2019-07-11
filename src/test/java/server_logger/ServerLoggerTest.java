package server_logger;

import mocks.MockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.Repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServerLoggerTest {
    Repository repository;
    ServerLogger log;

    @BeforeEach
    public void initializeLogger() {
        repository = new MockRepository("/test");
        log = new ServerLogger(repository, "./logs");
    }

    @Test
    public void logger_can_write_to_a_file() {
        String request = "GET /simple_get HTTP/1.1";
        String response = "HTTP/1.1 200 OK";
        log.writeRequest(1, request);
        String filePath = log.writeResponse(1, response);
        String fileContent = new String(repository.readFile(filePath));

        assertEquals(true,  fileContent.contains(request));
        assertEquals(true,  fileContent.contains(response));
    }

    @Test
    public void logger_can_append_to_a_file_with_same_session() {
        String request = "GET /simple_get HTTP/1.1";
        String response = "HTTP/1.1 200 OK";
        String request_2 = "GET /simple_get_2 HTTP/1.1";
        String response_2 = "HTTP/1.1 404";

        log.writeRequest(1, request);
        log.writeResponse(1, response);
        log.writeRequest(1, request_2);
        String filePath = log.writeResponse(1, response_2);
        String fileContent = new String(repository.readFile(filePath));

        assertEquals(true,  fileContent.contains(request));
        assertEquals(true,  fileContent.contains(response));
        assertEquals(true,  fileContent.contains(request_2));
        assertEquals(true,  fileContent.contains(response_2));
    }

    @Test
    public void logger_can_append_to_different_files_given_a_different_session_number() {
        String request = "GET /simple_get HTTP/1.1";
        String response = "HTTP/1.1 200 OK";
        log.writeRequest(1, request);
        String session1 = log.writeResponse(1, response);

        String request_2 = "GET /simple_get_2 HTTP/1.1";
        String response_2 = "HTTP/1.1 404";
        log.writeRequest(2, request_2);
        String session2 = log.writeResponse(2, response_2);

        String fileContent = new String(repository.readFile(session1));
        assertEquals(true,  fileContent.contains(request));
        assertEquals(true,  fileContent.contains(response));
        assertEquals(false,  fileContent.contains(request_2));
        assertEquals(false,  fileContent.contains(response_2));

        String fileContent2 = new String(repository.readFile(session2));
        assertEquals(false,  fileContent2.contains(request));
        assertEquals(false,  fileContent2.contains(response));
        assertEquals(true,  fileContent2.contains(request_2));
        assertEquals(true,  fileContent2.contains(response_2));
    }
}
