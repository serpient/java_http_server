package http_server;

import http_protocol.Stringer;
import mocks.MockClientSocket;
import mocks.MockRouter;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;

public class ServerTest {
    private String currentDateTime() {
        ZonedDateTime date = LocalDateTime.now().atZone(ZoneId.of("GMT+00"));
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("E, MMM dd yyyy HH:mm:ss z");
        String formattedDate = date.format(dateFormatter);

        return formattedDate;
    }

    String dateHeader = "Date: " + currentDateTime() + Stringer.crlf;
    String serverHeader = "Server: JavaServer/0.1" + Stringer.crlf;
    Router router = new MockRouter().getApp();

    @Test
    public void GET_Request_Is_Responded_With_Headers_And_No_Body() {
        String request = "GET /simple_get HTTP/1.1";
        String responseLine = "HTTP/1.1 200 OK" + Stringer.crlf;
        String response = responseLine + dateHeader + serverHeader;

        MockClientSocket mockClientSocket = new MockClientSocket(request);
        Session session = new Session(mockClientSocket, router);

        session.run();

        assertEquals(response, mockClientSocket.getSentData());
    }

    @Test
    public void HEAD_Request_Is_Responded_With_Headers_Only() {
        String request = "HEAD /simple_get HTTP/1.1";
        String responseLine = "HTTP/1.1 200 OK" + Stringer.crlf;
        String response = responseLine + dateHeader + serverHeader;

        MockClientSocket mockClientSocket = new MockClientSocket(request);
        Session session = new Session(mockClientSocket, router);

        session.run();

        assertEquals(response, mockClientSocket.getSentData());
    }


    @Test
    public void GET_Request_Is_Responded_With_Headers_And_Body() {
        String request = "GET /harry_potter HTTP/1.1";
        String responseLine = "HTTP/1.1 200 OK" + Stringer.crlf;
        String contentTypeHeader = "Content-Type: text/plain"  + Stringer.crlf;
        String contentLengthHeader = "Content-Length: 48" + Stringer.crlf;
        String body = Stringer.crlf + "Here are all my favorite movies:\n" + "- Harry Potter\n";
        String response = responseLine + dateHeader + serverHeader + contentTypeHeader + contentLengthHeader + body;

        MockClientSocket mockClientSocket = new MockClientSocket(request);
        Session session = new Session(mockClientSocket, router);

        session.run();

        assertEquals(response, mockClientSocket.getSentData());
    }

    @Test
    public void HEAD_Request_Is_Responded_With_Headers_Only_Even_If_Body_Exists() {
        String request = "HEAD /get_with_body HTTP/1.1";
        String responseLine = "HTTP/1.1 200 OK" + Stringer.crlf;
        String contentTypeHeader = "Content-Type: text/plain"  + Stringer.crlf;
        String contentLengthHeader = "Content-Length: 48" + Stringer.crlf;
        String response = responseLine + dateHeader + serverHeader + contentTypeHeader + contentLengthHeader;

        MockClientSocket mockClientSocket = new MockClientSocket(request);
        Session session = new Session(mockClientSocket, router);

        session.run();

        assertEquals(response, mockClientSocket.getSentData());
    }


    @Test
    public void GET_Request_Is_Responded_with_404_When_Resource_Invalid() {
        String request = "GET /resource_not_found HTTP/1.1";
        String responseLine = "HTTP/1.1 404 Not Found" + Stringer.crlf;
        String response = responseLine + dateHeader + serverHeader;

        MockClientSocket mockClientSocket = new MockClientSocket(request);
        Session session = new Session(mockClientSocket, router);

        session.run();

        assertEquals(response, mockClientSocket.getSentData());
    }

    @Test
    public void POST_Request_Is_Responded_with_Headers_And_Echoed_Post_Value() {
        String request_line = "POST /echo_body HTTP/1.1" + Stringer.crlf;
        String user_agent = "User-Agent: HTTPTool/1.0" + Stringer.crlf;
        String content_type = "Content-Type: text/plain" + Stringer.crlf;
        String content_length = "Content-Length: 47" + Stringer.crlf;
        String body = Stringer.crlf + "Here are all my favorite movies:\n" + "- Harry Potter";
        String request = request_line + user_agent + content_type + content_length + body;
        String responseLine = "HTTP/1.1 200 OK" + Stringer.crlf;
        String response = responseLine + dateHeader + serverHeader + content_type + content_length + body;

        MockClientSocket mockClientSocket = new MockClientSocket(request);
        Session session = new Session(mockClientSocket, router);

        session.run();

        assertEquals(response, mockClientSocket.getSentData());
    }

    @Test
    public void OPTIONS_Request_Is_Responded_With_Current_Methods_Available_On_Route() {
        String request = "OPTIONS /method_options HTTP/1.1";
        String responseLine = "HTTP/1.1 200 OK" + Stringer.crlf;
        String allowed_options = "Allow: OPTIONS, GET, HEAD" + Stringer.crlf;
        String response = responseLine + allowed_options + dateHeader + serverHeader;

        MockClientSocket mockClientSocket = new MockClientSocket(request);
        Session session = new Session(mockClientSocket, router);

        session.run();

        assertEquals(response, mockClientSocket.getSentData());
    }

    @Test
    public void Valid_Route_But_Invalid_Method_Is_Responded_With_405_Not_Allowed() {
        String request = "GET /get_with_body HTTP/1.1";
        String responseLine = "HTTP/1.1 405 Method Not Allowed" + Stringer.crlf;
        String allowed_options = "Allow: OPTIONS, HEAD" + Stringer.crlf;
        String response = responseLine + allowed_options + dateHeader + serverHeader ;

        MockClientSocket mockClientSocket = new MockClientSocket(request);
        Session session = new Session(mockClientSocket, router);

        session.run();

        assertEquals(response, mockClientSocket.getSentData());
    }

    @Test
    public void Redirected_route_is_responded_with_301_And_New_Route() {
        String request = "GET /redirect HTTP/1.1";
        String responseLine = "HTTP/1.1 301 Moved Permanently" + Stringer.crlf;
        String location = "Location: http://127.0.0.1:5000/simple_get" + Stringer.crlf;
        String response = responseLine + location + dateHeader + serverHeader;

        MockClientSocket mockClientSocket = new MockClientSocket(request);
        Session session = new Session(mockClientSocket, router);

        session.run();

        assertEquals(response, mockClientSocket.getSentData());
    }

    @Test
    public void Navigating_to_static_directory_generates_HTML_Directory() {
        String request = "GET /public HTTP/1.1";
        String responseLine = "HTTP/1.1 200 OK" + Stringer.crlf;
        String content_type = "Content-Type: text/html" + Stringer.crlf;
        String content_length = "Content-Length: 209" + Stringer.crlf;
        String body = Stringer.crlf + "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "<meta charset=\"UTF-8\">\n" +
                "<title>Home Page</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<ul>\n" +
                "<li>Home.html</li>\n" +
                "<li>TurtleTab.txt</li>\n" +
                "<li>japan.png</li>\n" +
                "<li>water.png</li>\n" +
                "</ul>\n" +
                "</body>\n" +
                "</html>";
        String response = responseLine + content_type + dateHeader + serverHeader + content_length + body;

        MockClientSocket mockClientSocket = new MockClientSocket(request);
        Session session = new Session(mockClientSocket, router);

        session.run();

        assertEquals(response, mockClientSocket.getSentData());
    }

    @Test
    public void Navigating_to_Base_path_generates_HTML_Directory() {
        String request = "GET / HTTP/1.1";
        String responseLine = "HTTP/1.1 200 OK" + Stringer.crlf;
        String content_type = "Content-Type: text/html" + Stringer.crlf;
        String content_length = "Content-Length: 209" + Stringer.crlf;
        String body = Stringer.crlf + "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "<meta charset=\"UTF-8\">\n" +
                "<title>Home Page</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<ul>\n" +
                "<li>Home.html</li>\n" +
                "<li>TurtleTab.txt</li>\n" +
                "<li>japan.png</li>\n" +
                "<li>water.png</li>\n" +
                "</ul>\n" +
                "</body>\n" +
                "</html>";
        String response = responseLine + content_type + dateHeader + serverHeader + content_length + body;

        MockClientSocket mockClientSocket = new MockClientSocket(request);
        Session session = new Session(mockClientSocket, router);

        session.run();

        assertEquals(response, mockClientSocket.getSentData());
    }
}
