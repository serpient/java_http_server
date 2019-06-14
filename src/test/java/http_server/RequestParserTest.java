package http_server;

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class RequestParserTest {
    String crlf = "\r\n";
    String post_request_line = "POST /echo_body HTTP/1.1" + crlf;
    String post_user_agent = "User-Agent: HTTPTool/1.0" + crlf;
    String post_content_type = "Content-Type: text/plain" + crlf;
    String post_content_length = "Content-Length: 47" + crlf;
    String post_body = crlf + "Here are all my favorite movies:\n" + "- Harry Potter";
    String post_request = post_request_line + post_user_agent + post_content_type + post_content_length + post_body;

    @Test
    public void request_with_body_parse_method() {
        RequestParser parser = new RequestParser(post_request);

        assertEquals("POST", parser.method());
    }

    @Test
    public void request_with_body_parse_route() {
        RequestParser parser = new RequestParser(post_request);

        assertEquals("/echo_body", parser.route());
    }

    @Test
    public void request_with_body_parse_header() {
        RequestParser parser = new RequestParser(post_request);

        HashMap<String, String> testHeaders = new HashMap<>();
        testHeaders.put("User-Agent", "HTTPTool/1.0");
        testHeaders.put("Content-Type", "text/plain");
        testHeaders.put("Content-Length", "47");

        assertEquals(testHeaders, parser.headers());
    }

    @Test
    public void request_with_body_parse_body() {
        RequestParser parser = new RequestParser(post_request);

        assertEquals(post_body.trim(), parser.body());
    }

    String head_request_line = "HEAD /head_request HTTP/1.1" + crlf;
    String head_user_agent = "User-Agent: HTTPTool/1.0" + crlf;
    String head_content_type = "Content-Type: text/plain" + crlf;
    String head_content_length = "Content-Length: 47" + crlf + crlf;
    String head_request = head_request_line + head_user_agent + head_content_type + head_content_length;

    @Test
    public void request_with_NO_body_parse_method() {
        RequestParser parser = new RequestParser(head_request);

        assertEquals("HEAD", parser.method());
    }

    @Test
    public void request_with_NO_body_parse_route() {
        RequestParser parser = new RequestParser(head_request);

        assertEquals("/head_request", parser.route());
    }


    @Test
    public void request_with_NO_body_parse_header() {
        RequestParser parser = new RequestParser(head_request);

        HashMap<String, String> testHeaders = new HashMap<>();
        testHeaders.put("User-Agent", "HTTPTool/1.0");
        testHeaders.put("Content-Type", "text/plain");
        testHeaders.put("Content-Length", "47");

        assertEquals(testHeaders, parser.headers());
    }

    @Test
    public void request_with_NO_body_parse_body() {
        RequestParser parser = new RequestParser(head_request);

        assertEquals("", parser.body());
    }
}
