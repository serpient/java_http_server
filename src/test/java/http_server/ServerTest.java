package http_server;

import Mocks.MockClientSocket;
import Mocks.MockRouter;
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

    String crlf = "\r\n";
    String dateHeader = "Date: " + currentDateTime() + crlf;
    String serverHeader = "Server: JavaServer/0.1" + crlf;
    Router router = new MockRouter().getApp();

    @Test
    public void GET_Request_Is_Responded_With_Headers_And_No_Body() {
        String request = "GET /simple_get HTTP/1.1";
        String responseLine = "HTTP/1.1 200 OK" + crlf;
        String response = responseLine + dateHeader + serverHeader;

        MockClientSocket mockClientSocket = new MockClientSocket(request);
        Session session = new Session(mockClientSocket, router);

        session.run();

        assertEquals(response, mockClientSocket.getSentData());
    }

    @Test
    public void HEAD_Request_Is_Responded_With_Headers_Only() {
        String request = "HEAD /simple_get HTTP/1.1";
        String responseLine = "HTTP/1.1 200 OK" + crlf;
        String response = responseLine + dateHeader + serverHeader;

        MockClientSocket mockClientSocket = new MockClientSocket(request);
        Session session = new Session(mockClientSocket, router);

        session.run();

        assertEquals(response, mockClientSocket.getSentData());
    }


    @Test
    public void GET_Request_Is_Responded_With_Headers_And_Body() {
        String request = "GET /harry_potter HTTP/1.1";
        String responseLine = "HTTP/1.1 200 OK" + crlf;
        String contentTypeHeader = "Content-Type: text/plain"  + crlf;
        String contentLengthHeader = "Content-Length: 48" + crlf;
        String body = crlf + "Here are all my favorite movies:\n" + "- Harry Potter\n";
        String response = responseLine + dateHeader + serverHeader + contentTypeHeader + contentLengthHeader + body;

        MockClientSocket mockClientSocket = new MockClientSocket(request);
        Session session = new Session(mockClientSocket, router);

        session.run();

        assertEquals(response, mockClientSocket.getSentData());
    }

    @Test
    public void HEAD_Request_Is_Responded_With_Headers_Only_Even_If_Body_Exists() {
        String request = "HEAD /get_with_body HTTP/1.1";
        String responseLine = "HTTP/1.1 200 OK" + crlf;
        String contentTypeHeader = "Content-Type: text/plain"  + crlf;
        String contentLengthHeader = "Content-Length: 48" + crlf;
        String response = responseLine + dateHeader + serverHeader + contentTypeHeader + contentLengthHeader;

        MockClientSocket mockClientSocket = new MockClientSocket(request);
        Session session = new Session(mockClientSocket, router);

        session.run();

        assertEquals(response, mockClientSocket.getSentData());
    }


    @Test
    public void GET_Request_Is_Responded_with_404_When_Resource_Invalid() {
        String request = "GET /resource_not_found HTTP/1.1";
        String responseLine = "HTTP/1.1 404 Not Found" + crlf;
        String response = responseLine + dateHeader + serverHeader;

        MockClientSocket mockClientSocket = new MockClientSocket(request);
        Session session = new Session(mockClientSocket, router);

        session.run();

        assertEquals(response, mockClientSocket.getSentData());
    }

    @Test
    public void POST_Request_Is_Responded_with_Headers_And_Echoed_Post_Value() {
        String request_line = "POST /echo_body HTTP/1.1" + crlf;
        String user_agent = "User-Agent: HTTPTool/1.0" + crlf;
        String content_type = "Content-Type: text/plain" + crlf;
        String content_length = "Content-Length: 47" + crlf;
        String body = crlf + "Here are all my favorite movies:\n" + "- Harry Potter";
        String request = request_line + user_agent + content_type + content_length + body;
        String responseLine = "HTTP/1.1 200 OK" + crlf;
        String response = responseLine + dateHeader + serverHeader + content_type + content_length + body;

        MockClientSocket mockClientSocket = new MockClientSocket(request);
        Session session = new Session(mockClientSocket, router);

        session.run();

        assertEquals(response, mockClientSocket.getSentData());
    }

    @Test
    public void OPTIONS_Request_Is_Responded_With_Current_Methods_Available_On_Route() {
        String request = "OPTIONS /method_options HTTP/1.1";
        String responseLine = "HTTP/1.1 200 OK" + crlf;
        String allowed_options = "Allow: OPTIONS, GET, HEAD" + crlf;
        String response = responseLine + allowed_options + dateHeader + serverHeader;

        MockClientSocket mockClientSocket = new MockClientSocket(request);
        Session session = new Session(mockClientSocket, router);

        session.run();

        assertEquals(response, mockClientSocket.getSentData());
    }

    @Test
    public void Valid_Route_But_Invalid_Method_Is_Responded_With_405_Not_Allowed() {
        String request = "GET /get_with_body HTTP/1.1";
        String responseLine = "HTTP/1.1 405 Method Not Allowed" + crlf;
        String allowed_options = "Allow: OPTIONS, HEAD" + crlf;
        String response = responseLine + allowed_options + dateHeader + serverHeader ;

        MockClientSocket mockClientSocket = new MockClientSocket(request);
        Session session = new Session(mockClientSocket, router);

        session.run();

        assertEquals(response, mockClientSocket.getSentData());
    }

    @Test
    public void Redirected_route_is_responded_with_301_And_New_Route() {
        String request = "GET /redirect HTTP/1.1";
        String responseLine = "HTTP/1.1 301 Moved Permanently" + crlf;
        String location = "Location: http://127.0.0.1:5000/simple_get" + crlf;
        String response = responseLine + location + dateHeader + serverHeader;

        MockClientSocket mockClientSocket = new MockClientSocket(request);
        Session session = new Session(mockClientSocket, router);

        session.run();

        assertEquals(response, mockClientSocket.getSentData());
    }
}
