package http_server;

import html_builder.HTMLBuilder;
import http_standards.Headers;
import http_standards.MIMETypes;
import http_standards.Parser;
import http_standards.Stringer;
import mocks.MockClientSocket;
import mocks.MockRepository;
import mocks.MockRouter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.Repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServerTest {
    Router router;

    private String runSessionAndRetrieveResponse(String request) {
        MockClientSocket mockClientSocket = new MockClientSocket(request);
        Session session = new Session(mockClientSocket, router);
        session.run();
        return mockClientSocket.getSentData();
    }

    @BeforeEach
    public void cleanRouter() {
        Repository mockRepository = new MockRepository("/public");
        mockRepository.writeFile("./public/Home.html", MIMETypes.html, "<!DOCTYPE html>\n".getBytes());
        mockRepository.writeFile("./public/TurtleTab.txt", MIMETypes.plain, "TurtleTabs a Google".getBytes());
        mockRepository.writeFile("./public/water.png", MIMETypes.png, "water image".getBytes());
        mockRepository.writeFile("./public/japan.png", MIMETypes.png, "japan image".getBytes());
        router = new MockRouter(mockRepository).getApp();
    }

    @Test
    public void GET_Request_Is_Responded_With_Headers_And_No_Body() {
        String request = "GET /simple_get HTTP/1.1";
        String response = runSessionAndRetrieveResponse(request);

        assertEquals("200", Parser.getStatusCode(response));
        assertEquals(true, Parser.getHeaders(response).containsKey("Date"));
        assertEquals(true, Parser.getHeaders(response).containsKey("Server"));
        assertEquals(null, Parser.getBody(response));
    }

    @Test
    public void HEAD_Request_Is_Responded_With_Headers_Only() {
        String request = "HEAD /simple_get HTTP/1.1";
        String response = runSessionAndRetrieveResponse(request);

        assertEquals("200", Parser.getStatusCode(response));
        assertEquals(true, Parser.getHeaders(response).containsKey("Date"));
        assertEquals(true, Parser.getHeaders(response).containsKey("Server"));
        assertEquals(null, Parser.getBody(response));
    }


    @Test
    public void GET_Request_Is_Responded_With_Headers_And_Body() {
        String request = "GET /harry_potter HTTP/1.1";
        String response = runSessionAndRetrieveResponse(request);

        assertEquals("200", Parser.getStatusCode(response));
        assertEquals("text/plain", Parser.getHeaders(response).get("Content-Type"));
        assertEquals("48", Parser.getHeaders(response).get("Content-Length"));
        assertEquals("Here are all my favorite movies:\n" + "- Harry Potter", Parser.getBody(response));
    }

    @Test
    public void HEAD_request_is_responded_with_headers_only_even_if_body_exists() {
        String request = "HEAD /get_with_body HTTP/1.1";
        String response = runSessionAndRetrieveResponse(request);

        assertEquals("200", Parser.getStatusCode(response));
        assertEquals("text/plain", Parser.getHeaders(response).get("Content-Type"));
        assertEquals("48", Parser.getHeaders(response).get("Content-Length"));
        assertEquals(null, Parser.getBody(response));
    }


    @Test
    public void GET_request_is_responded_with_404_when_resource_invalid() {
        String request = "GET /resource_not_found HTTP/1.1";
        String response = runSessionAndRetrieveResponse(request);

        assertEquals("404", Parser.getStatusCode(response));
    }

    @Test
    public void POST_request_is_responded_with_headers_and_echoed_post_value() {
        String request_line = "POST /echo_body HTTP/1.1" + Stringer.crlf;
        String content_type = "Content-Type: text/plain" + Stringer.crlf;
        String content_length = "Content-Length: 47" + Stringer.crlf;
        String body = Stringer.crlf + "Here are all my favorite movies:\n" + "- Harry Potter";
        String request = request_line + content_type + content_length + body;
        String response = runSessionAndRetrieveResponse(request);

        assertEquals("201", Parser.getStatusCode(response));
        assertEquals("/echo_body", Parser.getHeaders(response).get("Location"));
    }

    @Test
    public void OPTIONS_request_is_responded_with_current_methods_available_On_route() {
        String request = "OPTIONS /method_options HTTP/1.1";
        String response = runSessionAndRetrieveResponse(request);

        assertEquals("200", Parser.getStatusCode(response));
        assertEquals("OPTIONS, GET, HEAD", Parser.getHeaders(response).get("Allow"));
        assertEquals(null, Parser.getBody(response));
    }

    @Test
    public void valid_route_but_invalid_method_is_responded_with_405_not_allowed() {
        String request = "GET /get_with_body HTTP/1.1";
        String response = runSessionAndRetrieveResponse(request);

        assertEquals("405", Parser.getStatusCode(response));
        assertEquals("OPTIONS, HEAD", Parser.getHeaders(response).get("Allow"));
    }

    @Test
    public void redirected_route_is_responded_with_301_and_new_route() {
        router.setPort(1234);
        String request = "GET /redirect HTTP/1.1";
        String response = runSessionAndRetrieveResponse(request);

        assertEquals("301", Parser.getStatusCode(response));
        assertEquals("http://127.0.0.1:1234/simple_get", Parser.getHeaders(response).get("Location"));
    }

    String directoryBody = "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "<meta charset=\"UTF-8\">\n" +
            "<title>Home Page</title>\n" +
            "<style>.directory-page {    padding: 20px;    font-family: -apple-system, BlinkMacSystemFont, \"Segoe UI\", \"Roboto\", \"Oxygen\", \"Ubuntu\", \"Cantarell\", \"Fira Sans\",    \"Droid Sans\", \"Helvetica Neue\", sans-serif;    font-size: 20px;}.bullets {    color: grey;    margin: 20px 0px;}h1 {    text-align: center;    color: dark-grey;    font-weight: 600;    font-size: 42px;}hr {    color: grey;    border-weight: 2px;}</style></head>\n" +
            "<body>\n" +
            "<div class='directory-page'><h1>Directory for /public</h1><hr /><ul>\n" +
            "<li class='bullets'><a href='/public/Home.html'>Home.html</a></li>\n" +
            "<li class='bullets'><a href='/public/water.png'>water.png</a></li>\n" +
            "<li class='bullets'><a href='/public/TurtleTab.txt'>TurtleTab.txt</a></li>\n" +
            "<li class='bullets'><a href='/public/japan.png'>japan.png</a></li>\n" +
            "</ul>\n" +
            "</div></body>\n" +
            "</html>";


    @Test
    public void navigating_to_static_directory_generates_HTML_directory() {
        String request = "GET /public HTTP/1.1";
        String response = runSessionAndRetrieveResponse(request);

        assertEquals("200", Parser.getStatusCode(response));
        assertEquals("text/html", Parser.getHeaders(response).get("Content-Type"));
        assertEquals(true, Parser.getBody(response).startsWith("<!DOCTYPE html>"));
    }

    @Test
    public void navigating_to_base_route_redirects_to_static_directory() {
        router.setPort(1234);
        String request = "GET / HTTP/1.1";
        String response = runSessionAndRetrieveResponse(request);

        assertEquals("301", Parser.getStatusCode(response));
        assertEquals("http://127.0.0.1:1234/public", Parser.getHeaders(response).get("Location"));
    }

    @Test
    public void navigating_to_base_path_redirects_to_static_directory() {
        router.setPort(5000);
        String request = "GET / HTTP/1.1";
        String response = runSessionAndRetrieveResponse(request);

        assertEquals("301", Parser.getStatusCode(response));
        assertEquals("http://127.0.0.1:5000/public", Parser.getHeaders(response).get("Location"));
    }

    @Test
    public void navigating_to_directory_file_sends_back_file() {
        String request = "GET /public/Home.html HTTP/1.1";
        String body = "<!DOCTYPE html>";
        String response = runSessionAndRetrieveResponse(request);

        assertEquals("200", Parser.getStatusCode(response));
        assertEquals("text/html", Parser.getHeaders(response).get("Content-Type"));
        assertEquals(body, Parser.getBody(response));
    }

    @Test
    public void navigating_to_directory_image_sends_back_image() {
        String request = "GET /public/water.png HTTP/1.1";
        String response = runSessionAndRetrieveResponse(request);

        assertEquals("200", Parser.getStatusCode(response));
        assertEquals("image/png", Parser.getHeaders(response).get("Content-Type"));
    }

    @Test
    public void post_request_can_save_resource_under_new_route() {
        String content_type = "Content-Type: text/plain" + Stringer.crlf;
        String content_length = "Content-Length: 16" + Stringer.crlf;
        String body = Stringer.crlf + "Dog Breed: Corgi";

        assertAll("post request",
            () -> {
                String request_line = "POST /dog HTTP/1.1" + Stringer.crlf;
                String request = request_line + content_type + content_length + body;
                String response = runSessionAndRetrieveResponse(request);

                assertEquals("201", Parser.getStatusCode(response));
                assertEquals("/dog/1", Parser.getHeaders(response).get("Location"));
                assertEquals(null, Parser.getBody(response));
            },
            () -> {
                String request = "GET /dog/1 HTTP/1.1";
                String response = runSessionAndRetrieveResponse(request);

                assertEquals("200", Parser.getStatusCode(response));
                assertEquals(body.trim(), Parser.getBody(response));
            }
        );
    }

    @Test
    public void post_request_can_save_html_files_types() {
        String content_type = "Content-Type: text/html" + Stringer.crlf;
        String content_length = "Content-Length: 16" + Stringer.crlf;
        HTMLBuilder html = new HTMLBuilder();
        html.append("<div>Dog Breed: Maine Coon>/div>");
        String body = Stringer.crlf + html.generate();

        assertAll("post request",
                () -> {
                    String request_line = "POST /dog HTTP/1.1" + Stringer.crlf;
                    String request = request_line + content_type + content_length + body;
                    String response = runSessionAndRetrieveResponse(request);

                    assertEquals("201", Parser.getStatusCode(response));
                    assertEquals("/dog/1", Parser.getHeaders(response).get("Location"));
                    assertEquals(null, Parser.getBody(response));
                },
                () -> {
                    String request = "GET /dog/1 HTTP/1.1";
                    String response = runSessionAndRetrieveResponse(request);

                    assertEquals("200", Parser.getStatusCode(response));
                    assertEquals("text/html", Parser.getHeaders(response).get("Content-Type"));
                    assertEquals(body.trim(), Parser.getBody(response));
                },
                () -> {
                    String request = "GET /dog/1.html HTTP/1.1";
                    String response = runSessionAndRetrieveResponse(request);
                    System.err.println(response);

                    assertEquals("200", Parser.getStatusCode(response));
                    assertEquals("text/html", Parser.getHeaders(response).get("Content-Type"));
                    assertEquals(body.trim(), Parser.getBody(response));
                }
        );
    }

    @Test
    public void post_request_with_no_body_does_not_create_a_resource() {
        assertAll("post request",
                () -> {
                    String request = "POST /dog HTTP/1.1" + Stringer.crlf;
                    String response = runSessionAndRetrieveResponse(request);

                    assertEquals("204", Parser.getStatusCode(response));
                    assertEquals(null, Parser.getBody(response));
                    assertEquals(null, Parser.getHeaders(response).get(Headers.location));
                },
                () -> {
                    String request = "GET /dog/1 HTTP/1.1";
                    String response = runSessionAndRetrieveResponse(request);

                    assertEquals("404", Parser.getStatusCode(response));
                }
        );
    }

    @Test
    public void put_request_can_save_to_resource_route() {
        String content_type = "Content-Type: text/html" + Stringer.crlf;
        String content_length = "Content-Length: 16" + Stringer.crlf;
        HTMLBuilder html = new HTMLBuilder();
        html.append("<div>Cat Breed: Maine Coon>/div>");
        String body = Stringer.crlf + html.generate();

        assertAll("put request",
                () -> {
                    String request_line = "PUT /cat/1 HTTP/1.1" + Stringer.crlf;
                    String request = request_line + content_type + content_length + body;
                    String response = runSessionAndRetrieveResponse(request);

                    assertEquals("201", Parser.getStatusCode(response));
                    assertEquals(null, Parser.getBody(response));
                },
                () -> {
                    String request = "GET /cat/1 HTTP/1.1";
                    String response = runSessionAndRetrieveResponse(request);

                    assertEquals("200", Parser.getStatusCode(response));
                    assertEquals("text/html", Parser.getHeaders(response).get("Content-Type"));
                    assertEquals(body.trim(), Parser.getBody(response));
                }
        );
    }

    @Test
    public void put_request_can_overwrite_existing_resource() {
        HTMLBuilder replaced_html = new HTMLBuilder();
        replaced_html.append("Hello kitty!");
        String replaced_body = Stringer.crlf + replaced_html.generate();

        assertAll("put request",
                () -> {
                    String content_type = "Content-Type: text/html" + Stringer.crlf;
                    String content_length = "Content-Length: 16" + Stringer.crlf;
                    HTMLBuilder html = new HTMLBuilder();
                    html.append("<div>Cat Breed: Maine Coon>/div>");
                    String body = Stringer.crlf + html.generate();
                    String request_line = "PUT /cat/1 HTTP/1.1" + Stringer.crlf;
                    String user_agent = "User-Agent: HTTPTool/1.0" + Stringer.crlf;
                    String request = request_line + user_agent + content_type + content_length + body;
                    String response = runSessionAndRetrieveResponse(request);

                    assertEquals("201", Parser.getStatusCode(response));
                },
                () -> {
                    String request_line = "PUT /cat/1 HTTP/1.1" + Stringer.crlf;
                    String replaced_content_type = "Content-Type: text/plain" + Stringer.crlf;
                    String replaced_content_length = "Content-Length: 16" + Stringer.crlf;
                    String request = request_line + replaced_content_type + replaced_content_length + replaced_body;
                    String response = runSessionAndRetrieveResponse(request);

                    assertEquals("201", Parser.getStatusCode(response));
                },
                () -> {
                    String request = "GET /cat/1 HTTP/1.1";
                    String response = runSessionAndRetrieveResponse(request);

                    assertEquals("200", Parser.getStatusCode(response));
                    assertEquals("text/plain", Parser.getHeaders(response).get("Content-Type"));
                    assertEquals(replaced_body.trim(), Parser.getBody(response));
                }
        );
    }

    @Test
    public void delete_request_deletes_the_resource() {
        router.saveResource("/delete_me", "txt", "DELETE ME".getBytes());

        assertAll("delete request",
                () -> {
                    String request = "GET /delete_me HTTP/1.1";
                    String response = runSessionAndRetrieveResponse(request);

                    assertEquals("200", Parser.getStatusCode(response));
                    assertEquals("DELETE ME", Parser.getBody(response));
                },
                () -> {
                    String request = "DELETE /delete_me HTTP/1.1";
                    String response = runSessionAndRetrieveResponse(request);

                    assertEquals("204", Parser.getStatusCode(response));
                },
                () -> {
                    String request = "GET /delete_me.txt HTTP/1.1";
                    String response = runSessionAndRetrieveResponse(request);

                    assertEquals("404", Parser.getStatusCode(response));
                }
        );
    }
}
