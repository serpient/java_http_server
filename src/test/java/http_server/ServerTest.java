package http_server;

import file_handler.FileHandler;
import http_protocol.Stringer;
import mocks.MockClientSocket;
import mocks.MockRouter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServerTest {
    Router router = new MockRouter().getApp();

    @BeforeEach
    public void prepFiles() {
        FileHandler.deleteDirectory("./public/dog");
        FileHandler.deleteDirectory("./public/echo_body.txt");
    }


    @AfterEach
    public void cleanUpFiles() {
        FileHandler.deleteDirectory("./public/dog");
        FileHandler.deleteDirectory("./public/echo_body.txt");
    }

    @Test
    public void GET_Request_Is_Responded_With_Headers_And_No_Body() {
        String request = "GET /simple_get HTTP/1.1";
        MockClientSocket mockClientSocket = new MockClientSocket(request);
        Session session = new Session(mockClientSocket, router);
        session.run();
        String response = mockClientSocket.getSentData();

        assertEquals("200", Parser.getStatusCode(response));
        assertEquals(true, Parser.getHeaders(response).containsKey("Date"));
        assertEquals(true, Parser.getHeaders(response).containsKey("Server"));
    }

    @Test
    public void HEAD_Request_Is_Responded_With_Headers_Only() {
        String request = "HEAD /simple_get HTTP/1.1";
        MockClientSocket mockClientSocket = new MockClientSocket(request);
        Session session = new Session(mockClientSocket, router);
        session.run();
        String response = mockClientSocket.getSentData();

        assertEquals("200", Parser.getStatusCode(response));
        assertEquals(true, Parser.getHeaders(response).containsKey("Date"));
        assertEquals(true, Parser.getHeaders(response).containsKey("Server"));
    }


    @Test
    public void GET_Request_Is_Responded_With_Headers_And_Body() {
        String request = "GET /harry_potter HTTP/1.1";
        MockClientSocket mockClientSocket = new MockClientSocket(request);
        Session session = new Session(mockClientSocket, router);
        session.run();
        String response = mockClientSocket.getSentData();

        assertEquals("200", Parser.getStatusCode(response));
        assertEquals("text/plain", Parser.getHeaders(response).get("Content-Type"));
        assertEquals("48", Parser.getHeaders(response).get("Content-Length"));
        assertEquals("Here are all my favorite movies:\n" + "- Harry Potter", Parser.getBody(response));
    }

    @Test
    public void HEAD_Request_Is_Responded_With_Headers_Only_Even_If_Body_Exists() {
        String request = "HEAD /get_with_body HTTP/1.1";
        MockClientSocket mockClientSocket = new MockClientSocket(request);
        Session session = new Session(mockClientSocket, router);
        session.run();
        String response = mockClientSocket.getSentData();

        assertEquals("200", Parser.getStatusCode(response));
        assertEquals("text/plain", Parser.getHeaders(response).get("Content-Type"));
        assertEquals("48", Parser.getHeaders(response).get("Content-Length"));
        assertEquals(null, Parser.getBody(response));
    }


    @Test
    public void GET_Request_Is_Responded_with_404_When_Resource_Invalid() {
        String request = "GET /resource_not_found HTTP/1.1";
        MockClientSocket mockClientSocket = new MockClientSocket(request);
        Session session = new Session(mockClientSocket, router);
        session.run();
        String response = mockClientSocket.getSentData();

        assertEquals("404", Parser.getStatusCode(response));
    }

    @Test
    public void POST_Request_Is_Responded_with_Headers_And_Echoed_Post_Value() {
        String request_line = "POST /echo_body HTTP/1.1" + Stringer.crlf;
        String user_agent = "User-Agent: HTTPTool/1.0" + Stringer.crlf;
        String content_type = "Content-Type: text/plain" + Stringer.crlf;
        String content_length = "Content-Length: 47" + Stringer.crlf;
        String body = Stringer.crlf + "Here are all my favorite movies:\n" + "- Harry Potter";
        String request = request_line + user_agent + content_type + content_length + body;

        MockClientSocket mockClientSocket = new MockClientSocket(request);
        Session session = new Session(mockClientSocket, router);
        session.run();
        String response = mockClientSocket.getSentData();

        assertEquals("201", Parser.getStatusCode(response));
        assertEquals("/echo_body", Parser.getHeaders(response).get("Location"));
    }

    @Test
    public void OPTIONS_Request_Is_Responded_With_Current_Methods_Available_On_Route() {
        String request = "OPTIONS /method_options HTTP/1.1";
        MockClientSocket mockClientSocket = new MockClientSocket(request);
        Session session = new Session(mockClientSocket, router);
        session.run();
        String response = mockClientSocket.getSentData();

        assertEquals("200", Parser.getStatusCode(response));
        assertEquals("OPTIONS, GET, HEAD", Parser.getHeaders(response).get("Allow"));
    }

    @Test
    public void Valid_Route_But_Invalid_Method_Is_Responded_With_405_Not_Allowed() {
        String request = "GET /get_with_body HTTP/1.1";
        MockClientSocket mockClientSocket = new MockClientSocket(request);
        Session session = new Session(mockClientSocket, router);
        session.run();
        String response = mockClientSocket.getSentData();

        assertEquals("405", Parser.getStatusCode(response));
        assertEquals("OPTIONS, HEAD", Parser.getHeaders(response).get("Allow"));
    }

    @Test
    public void Redirected_route_is_responded_with_301_And_New_Route() {
        String request = "GET /redirect HTTP/1.1";
        MockClientSocket mockClientSocket = new MockClientSocket(request);
        Session session = new Session(mockClientSocket, router);
        session.run();
        String response = mockClientSocket.getSentData();

        assertEquals("301", Parser.getStatusCode(response));
        assertEquals("http://127.0.0.1:5000/simple_get", Parser.getHeaders(response).get("Location"));
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
            "<li class='bullets'><a href='/public/TurtleTab.txt'>TurtleTab.txt</a></li>\n" +
            "<li class='bullets'><a href='/public/japan.png'>japan.png</a></li>\n" +
            "<li class='bullets'><a href='/public/water.png'>water.png</a></li>\n" +
            "</ul>\n" +
            "</div></body>\n" +
            "</html>";


    @Test
    public void Navigating_to_static_directory_generates_HTML_Directory() {
        String request = "GET /public HTTP/1.1";
        MockClientSocket mockClientSocket = new MockClientSocket(request);
        Session session = new Session(mockClientSocket, router);
        session.run();
        String response = mockClientSocket.getSentData();

        assertEquals("200", Parser.getStatusCode(response));
        assertEquals("text/html", Parser.getHeaders(response).get("Content-Type"));
        assertEquals("894", Parser.getHeaders(response).get("Content-Length"));
        assertEquals(directoryBody, Parser.getBody(response));
    }

    @Test
    public void Navigating_to_Base_path_redirects_to_Static_directory() {
        String request = "GET / HTTP/1.1";
        MockClientSocket mockClientSocket = new MockClientSocket(request);
        Session session = new Session(mockClientSocket, router);
        session.run();
        String response = mockClientSocket.getSentData();

        assertEquals("301", Parser.getStatusCode(response));
        assertEquals("http://127.0.0.1:5000/public", Parser.getHeaders(response).get("Location"));
    }

    @Test
    public void navigating_to_directory_file_sends_back_file() {
        String request = "GET /public/Home.html HTTP/1.1";
        String body = "<!DOCTYPE html>" +
                "<html lang=\"en\">" +
                "<head>" +
                "    <meta charset=\"UTF-8\">" +
                "    <title>Home Page</title>" +
                "</head>" +
                "<body BGCOLOR=\"FFFFFF\">" +
                "<h1>HELLO!!</h1>" +
                "<p>This is a very simple HTML document</p>" +
                "<p>It only has two paragraphs</p>" +
                "</body>" +
                "</html>";
        MockClientSocket mockClientSocket = new MockClientSocket(request);
        Session session = new Session(mockClientSocket, router);
        session.run();
        String response = mockClientSocket.getSentData();

        assertEquals("200", Parser.getStatusCode(response));
        assertEquals("text/html", Parser.getHeaders(response).get("Content-Type"));
        assertEquals("226", Parser.getHeaders(response).get("Content-Length"));
        assertEquals(body, Parser.getBody(response));
    }

    @Test
    public void navigating_to_directory_image_sends_back_image() {
        String request = "GET /public/water.png HTTP/1.1";

        MockClientSocket mockClientSocket = new MockClientSocket(request);
        Session session = new Session(mockClientSocket, router);

        session.run();

        String response = mockClientSocket.getSentData();

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
                String request_line = "POST /dog/1 HTTP/1.1" + Stringer.crlf;
                String user_agent = "User-Agent: HTTPTool/1.0" + Stringer.crlf;
                String request = request_line + user_agent + content_type + content_length + body;
                MockClientSocket mockClientSocket = new MockClientSocket(request);
                Session session = new Session(mockClientSocket, router);
                session.run();
                String response = mockClientSocket.getSentData();

                assertEquals("201", Parser.getStatusCode(response));
                assertEquals("/dog/1", Parser.getHeaders(response).get("Location"));
                assertEquals(null, Parser.getBody(response));
            },
            () -> {
                String request = "GET /dog/1 HTTP/1.1";
                MockClientSocket mockClientSocket = new MockClientSocket(request);
                Session session = new Session(mockClientSocket, router);
                session.run();
                String response = mockClientSocket.getSentData();

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
                    String request_line = "POST /dog/3 HTTP/1.1" + Stringer.crlf;
                    String user_agent = "User-Agent: HTTPTool/1.0" + Stringer.crlf;
                    String request = request_line + user_agent + content_type + content_length + body;
                    MockClientSocket mockClientSocket = new MockClientSocket(request);
                    Session session = new Session(mockClientSocket, router);
                    session.run();
                    String response = mockClientSocket.getSentData();

                    assertEquals("201", Parser.getStatusCode(response));
                    assertEquals("/dog/3", Parser.getHeaders(response).get("Location"));
                    assertEquals(null, Parser.getBody(response));
                },
                () -> {
                    String request = "GET /dog/3 HTTP/1.1";
                    MockClientSocket mockClientSocket = new MockClientSocket(request);
                    Session session = new Session(mockClientSocket, router);

                    session.run();

                    String response = mockClientSocket.getSentData();

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
                    String request_line = "POST /dog/5 HTTP/1.1" + Stringer.crlf;
                    String user_agent = "User-Agent: HTTPTool/1.0" + Stringer.crlf;
                    String request = request_line + user_agent;
                    MockClientSocket mockClientSocket = new MockClientSocket(request);
                    Session session = new Session(mockClientSocket, router);
                    session.run();
                    String response = mockClientSocket.getSentData();

                    assertEquals("204", Parser.getStatusCode(response));
                    assertEquals(null, Parser.getBody(response));
                },
                () -> {
                    String request = "GET /dog/5 HTTP/1.1";
                    MockClientSocket mockClientSocket = new MockClientSocket(request);
                    Session session = new Session(mockClientSocket, router);

                    session.run();

                    String response = mockClientSocket.getSentData();

                    assertEquals("405", Parser.getStatusCode(response));
                }
        );
    }
}
