package http_server;

import http_standards.Headers;
import http_standards.MIMETypes;
import http_standards.Parser;
import http_standards.RequestCreator;
import http_standards.StatusCode;
import http_standards.Stringer;
import mocks.MockRepository;
import mocks.MockRouter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.Repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResponseTest {
    Repository mockRepository;
    Router mockRouter;

    @BeforeEach
    public void cleanRepository() {
        mockRepository = new MockRepository("/public");
        mockRepository.writeFile("./public/Home.html", MIMETypes.html, "<!DOCTYPE html>\n".getBytes());
        mockRepository.writeFile("./public/TurtleTab.txt", MIMETypes.plain, "TurtleTabs a Google".getBytes());
        mockRepository.writeFile("./public/water.png", MIMETypes.png, "water image".getBytes());
        mockRepository.writeFile("./public/japan.png", MIMETypes.png, "japan image".getBytes());
        mockRouter = new MockRouter(mockRepository).getApp();
    }

    private Response createResponseObject(String request) {
        Request requestData = RequestCreator.from(request);
        return new Response(mockRouter, requestData);
    }

    @Test
    public void response_can_check_if_request_has_invalid_route() {
        String request = "GET /undefined_route HTTP/1.1" + Stringer.crlf;
        String response = new String(createResponseObject(request).create());

        assertEquals("404", Parser.getStatusCode(response));
        assertEquals(null, Parser.getBody(response));
    }

    @Test
    public void response_can_check_if_request_has_invalid_method() {
        String request = "PUT /simple_get HTTP/1.1" + Stringer.crlf;
        String response = new String(createResponseObject(request).create());

        assertEquals("405", Parser.getStatusCode(response));
        assertEquals("OPTIONS, GET, HEAD", Parser.getHeaders(response).get(Headers.allowedHeaders));
        assertEquals(null, Parser.getBody(response));
    }

    @Test
    public void response_can_check_if_post_has_no_body_response() {
        String request = "POST /dog HTTP/1.1" + Stringer.crlf;
        String response = new String(createResponseObject(request).create());

        assertEquals("204", Parser.getStatusCode(response));
        assertEquals(null, Parser.getHeaders(response).get(Headers.contentLength));
        assertEquals(null, Parser.getHeaders(response).get(Headers.contentType));
        assertEquals(null, Parser.getBody(response));
    }

    @Test
    public void response_can_check_if_put_has_no_body_response() {
        String request = "PUT /cat/1 HTTP/1.1" + Stringer.crlf;
        String response = new String(createResponseObject(request).create());

        assertEquals("204", Parser.getStatusCode(response));
        assertEquals(null, Parser.getHeaders(response).get(Headers.contentLength));
        assertEquals(null, Parser.getHeaders(response).get(Headers.contentType));
        assertEquals(null, Parser.getBody(response));
    }


    @Test
    public void response_can_check_if_request_has_valid_request() {
        String request = "GET /simple_get HTTP/1.1" + Stringer.crlf;
        Response response = createResponseObject(request);

        assertEquals(StatusCode.ok, response.getStatus());
    }

    @Test
    public void response_can_format_full_response_from_file() {
        String request = "GET /simple_get HTTP/1.1" + Stringer.crlf;
        Response response = createResponseObject(request);
        response.setFile("/water.png");
        byte[] readImage = mockRepository.readFile("./public/water.png");

        assertEquals(StatusCode.ok, response.getStatus());
        assertEquals(MIMETypes.png, response.getHeaders().get(Headers.contentType));
        assertEquals(readImage.length + "", response.getHeaders().get(Headers.contentLength));
    }

    @Test
    public void response_can_format_full_redirect_response() {
        String request = "GET /simple_get HTTP/1.1" + Stringer.crlf;
        Response response = createResponseObject(request);
        response.redirect("/harry_potter");

        assertEquals(StatusCode.moved, response.getStatus());
        assertEquals("http://127.0.0.1:5000/harry_potter", response.getHeaders().get(Headers.location));
        assertEquals(null, response.getBody());
    }

    @Test
    public void response_can_format_full_response_from_body() {
        String request_line = "GET /dog HTTP/1.1" + Stringer.crlf;
        String content_type = "Content-Type: text/plain" + Stringer.crlf;
        String content_length = "Content-Length: 16" + Stringer.crlf;
        String body = "Dog Breed: Corgi";
        String request = request_line + content_type + content_length + Stringer.crlf + body;
        byte[] bodyBytes = body.getBytes();
        Response response = createResponseObject(request);
        response.setBody(bodyBytes, MIMETypes.plain);

        assertEquals(StatusCode.ok, response.getStatus());
        assertEquals("16", response.getHeaders().get(Headers.contentLength));
        assertEquals(MIMETypes.plain, response.getHeaders().get(Headers.contentType));
        assertEquals(bodyBytes, response.getBody());
    }

    @Test
    public void response_can_format_full_put_response() {
        String request_line = "PUT /dog HTTP/1.1" + Stringer.crlf;
        String content_type = "Content-Type: text/plain" + Stringer.crlf;
        String content_length = "Content-Length: 16" + Stringer.crlf;
        String body = "Dog Breed: Corgi";
        String request = request_line + content_type + content_length + Stringer.crlf + body;
        byte[] bodyBytes = body.getBytes();
        Response response = createResponseObject(request);
        response.forPut();

        assertEquals(StatusCode.created, response.getStatus());
        assertEquals(null, response.getHeaders().get(Headers.contentLength));
        assertEquals(null, response.getHeaders().get(Headers.contentType));
        assertEquals(null, response.getBody());
    }

    @Test
    public void response_can_format_full_post_response() {
        String request_line = "POST /dog HTTP/1.1" + Stringer.crlf;
        String content_type = "Content-Type: text/plain" + Stringer.crlf;
        String content_length = "Content-Length: 16" + Stringer.crlf;
        String body = "Dog Breed: Corgi";
        String request = request_line + content_type + content_length + Stringer.crlf + body;
        byte[] bodyBytes = body.getBytes();
        Response response = createResponseObject(request);
        response.forPost("/dog/1");

        assertEquals(StatusCode.created, response.getStatus());
        assertEquals("/dog/1", response.getHeaders().get(Headers.location));
        assertEquals(null, response.getBody());
    }

    @Test
    public void response_can_format_head_response() {
        String request_line = "HEAD /dog HTTP/1.1" + Stringer.crlf;
        String request = request_line + Stringer.crlf;
        byte[] bodyBytes = "HELLO".getBytes();
        Response response = createResponseObject(request);
        response.forHead(bodyBytes, MIMETypes.plain);

        assertEquals(StatusCode.ok, response.getStatus());
        assertEquals("5", response.getHeaders().get(Headers.contentLength));
        assertEquals(MIMETypes.plain, response.getHeaders().get(Headers.contentType));
        assertEquals(null, response.getBody());
    }

    @Test
    public void response_can_format_options_response() {
        String request_line = "OPTIONS /get_with_body HTTP/1.1" + Stringer.crlf;
        String request = request_line + Stringer.crlf;
        Response response = createResponseObject(request);
        response.forOptions(mockRouter.createOptionsHeader("/get_with_body"));

        assertEquals(StatusCode.ok, response.getStatus());
        assertEquals("OPTIONS, HEAD", response.getHeaders().get(Headers.allowedHeaders));
        assertEquals(null, response.getBody());
    }
}
