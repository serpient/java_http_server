package http_server;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;

public class ResponseTest {
    String crlf = "\r\n";
    String request_line = "GET /simple_get HTTP/1.1" + crlf;
    String user_agent = "User-Agent: HTTPTool/1.0" + crlf + crlf;
    String request = request_line + user_agent;

    RequestParser parser = new RequestParser(request);
    Request requestParser = new Request(parser.method(), parser.route(), parser.body(), parser.headers());
    Router mockRouter = new MockRouter().getRouter();

    private String currentDateTime() {
        ZonedDateTime date = LocalDateTime.now().atZone(ZoneId.of("GMT+00"));
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("E, MMM dd yyyy HH:mm:ss z");
        String formattedDate = date.format(dateFormatter);

        return formattedDate;
    }

    @Test
    public void response_can_set_status() {
        Response response = new Response(requestParser, mockRouter);
        response.status("404 Not Found");
        response.header("Date", currentDateTime());
        response.header("Server", "JavaServer/0.1");

        String returnedHeader = "HTTP/1.1 404 Not Found" + crlf +
                "Date: " + currentDateTime() + crlf +
                "Server: " + "JavaServer/0.1" + crlf;

        assertEquals(returnedHeader, response.getHeader());
    }

    @Test
    public void response_can_add_custom_headers() {
        Response response = new Response(requestParser, mockRouter);
        response.header("Date", currentDateTime());
        response.header("Server", "JavaServer/0.1");
        response.header("CustomHeader", "Hiya!");

        String returnedHeader = "HTTP/1.1 200 OK" + crlf +
                "Date: " + currentDateTime() + crlf +
                "Server: " + "JavaServer/0.1" + crlf +
                "CustomHeader: Hiya!" + crlf;

        assertEquals(returnedHeader, response.getHeader());
    }

    @Test
    public void response_can_change_body() {
        Response response = new Response(requestParser, mockRouter);
        String customBody = "This is my custom responseBody!";
        response.body(customBody);

        String returnedResponse = "HTTP/1.1 200 OK" + crlf +
                "Date: " + currentDateTime() + crlf +
                "Server: " + "JavaServer/0.1" + crlf +
                "Content-Type: " + "text/plain" + crlf +
                "Content-Length: " + "31" + crlf + crlf +
                customBody;

        assertEquals(returnedResponse, response.generateResponse());
    }
}
