package http_server;

import http_standards.RequestCreator;
import http_standards.Stringer;
import java.util.HashMap;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RequestTest {
    String post_request_line = "POST /echo_body HTTP/1.1" + Stringer.crlf;
    String post_user_agent = "User-Agent: HTTPTool/1.0" + Stringer.crlf;
    String post_content_type = "Content-Type: text/plain" + Stringer.crlf;
    String post_content_length = "Content-Length: 47" + Stringer.crlf;
    String post_body = Stringer.crlf + "Here are all my favorite movies:\n" + "- Harry Potter";
    String post_request = post_request_line + post_user_agent + post_content_type + post_content_length + post_body;

    Request request = RequestCreator.from(post_request);
    @Test
    public void request_with_body_parse_method() {
        assertEquals("POST", request.getMethod());
    }

    @Test
    public void request_with_body_parse_route() {
        assertEquals("/echo_body", request.getRoute());
    }

    @Test
    public void request_with_body_parse_header() {
        HashMap<String, String> testHeaders = new HashMap<>();
        testHeaders.put("User-Agent", "HTTPTool/1.0");
        testHeaders.put("Content-Type", "text/plain");
        testHeaders.put("Content-Length", "47");

        assertEquals(testHeaders, request.getHeaders());
    }

    @Test
    public void request_with_body_parse_body() {
        assertEquals(post_body.trim(), request.getBody());
    }

    String head_request_line = "HEAD /head_request HTTP/1.1" + Stringer.crlf;
    String head_user_agent = "User-Agent: HTTPTool/1.0" + Stringer.crlf;
    String head_content_type = "Content-Type: text/plain" + Stringer.crlf;
    String head_content_length = "Content-Length: 47" + Stringer.crlf + Stringer.crlf;
    String head_request = head_request_line + head_user_agent + head_content_type + head_content_length;

    Request h_request = RequestCreator.from(head_request);
    @Test
    public void request_with_NO_body_parse_method() {
        assertEquals("HEAD", h_request.getMethod());
    }

    @Test
    public void request_with_NO_body_parse_route() {
        assertEquals("/head_request", h_request.getRoute());
    }

    @Test
    public void request_with_NO_body_parse_header() {
        HashMap<String, String> testHeaders = new HashMap<>();
        testHeaders.put("User-Agent", "HTTPTool/1.0");
        testHeaders.put("Content-Type", "text/plain");
        testHeaders.put("Content-Length", "47");

        assertEquals(testHeaders, h_request.getHeaders());
    }

    @Test
    public void request_with_NO_body_parse_body() {
        assertEquals(null, h_request.getBody());
    }

    @Test
    public void request_can_parse_url_parameters() {
        Request request = RequestCreator.from("GET /dog/1?name=Buddy&breed=Corgi");
        HashMap<String, String> parameters = request.getParameters();

        assertEquals("/dog/1", request.getRoute());
        assertEquals("Buddy", parameters.get("name"));
        assertEquals("Corgi", parameters.get("breed"));
    }

    @Test
    public void request_can_parse_empty_parameters() {
        Request request = RequestCreator.from("GET /dog/1");

        assertEquals("/dog/1", request.getRoute());
        assertEquals(new HashMap<>(), request.getParameters());
    }

    @Test
    public void request_can_parse_url_encoded_parameters() {
        Request request = RequestCreator.from("GET /dog/1?message=Hello%20G%C3%BCnter&author=%40Mrs%20JK%20Rowling");
        HashMap<String, String> parameters = request.getParameters();

        assertEquals("/dog/1", request.getRoute());
        assertEquals("Hello Günter", parameters.get("message"));
        assertEquals("@Mrs JK Rowling", parameters.get("author"));
    }

    @Test
    public void request_can_parse_long_url_encoded_parameters() {
        Request request = RequestCreator.from("GET /dog/1?variable_1=Operators%20%3C%2C%20%3E%2C%20%3D%2C%20!%3D%3B%20%2B%2C%20-%2C%20*%2C%20%26%2C%20%40%2C%20%23%2C%20%24%2C%20%5B%2C%20%5D%3A%20%22is%20that%20all%22%3F&variable_2=stuff");
        HashMap<String, String> parameters = request.getParameters();

        assertEquals("/dog/1", request.getRoute());
        assertEquals("Operators <, >, =, !=; +, -, *, &, @, #, $, [, ]: \"is that all\"?", parameters.get("variable_1"));
        assertEquals("stuff", parameters.get("variable_2"));
    }

    @Test
    public void request_can_handle_incorrect_url_encoded_parameters() {
        Request request = RequestCreator.from("GET /dog/1?message=Hello%20G%C3%BCnter&author=");
        HashMap<String, String> parameters = request.getParameters();

        assertEquals("/dog/1", request.getRoute());
        assertEquals("Hello Günter", parameters.get("message"));
        assertEquals("", parameters.get("author"));
    }

    @Test
    public void request_can_handle_incorrect_url_encoded_parameters_2() {
        Request request = RequestCreator.from("GET /dog/1?message=Hello%20G%C3%BCnter&");
        HashMap<String, String> parameters = request.getParameters();

        assertEquals("/dog/1", request.getRoute());
        assertEquals("Hello Günter", parameters.get("message"));
    }
}

