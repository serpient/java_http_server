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
}
