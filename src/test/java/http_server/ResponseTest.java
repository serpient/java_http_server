package http_server;

import Mocks.MockRouter;
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
    Router mockRouter = new MockRouter().getApp();

    private String currentDateTime() {
        ZonedDateTime date = LocalDateTime.now().atZone(ZoneId.of("GMT+00"));
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("E, MMM dd yyyy HH:mm:ss z");
        String formattedDate = date.format(dateFormatter);

        return formattedDate;
    }

    @Test
    public void response_can_set_status() {
        Response response = new Response(requestParser, mockRouter);
        ResponseBuilder builder = new ResponseBuilder(response, requestParser, mockRouter);

        response.setStatus(StatusCode.NOT_FOUND.get());
        response.setHeader("Date", currentDateTime());
        response.setHeader("Server", "JavaServer/0.1");

        String returnedHeader = "HTTP/1.1 404 Not Found" + crlf;

        assertEquals(returnedHeader, builder.buildStatus());
    }

    @Test
    public void response_can_add_custom_headers() {
        Response response = new Response(requestParser, mockRouter);
        ResponseBuilder builder = new ResponseBuilder(response, requestParser, mockRouter);
        response.setHeader("Date", currentDateTime());
        response.setHeader("Server", "JavaServer/0.1");
        response.setHeader("CustomHeader", "Hiya!");

        String returnedHeader = "Date: " + currentDateTime() + crlf +
                "Server: " + "JavaServer/0.1" + crlf +
                "CustomHeader: Hiya!" + crlf;

        assertEquals(returnedHeader, builder.buildHeader());
    }

    @Test
    public void response_can_change_body() {
        Response response = new Response(requestParser, mockRouter);
        ResponseBuilder builder = new ResponseBuilder(response, requestParser, mockRouter);
        String customBody = "This is my custom responseBody!";
        response.setBody(customBody);

        assertEquals(crlf + customBody, builder.buildBody());
    }

    @Test
    public void response_can_build_a_full_body_response() {
        Response response = new Response(requestParser, mockRouter);
        String customBody = "This is my custom responseBody!";
        response.setBody(customBody);

        String returnedResponse = "HTTP/1.1 200 OK" + crlf +
                "Date: " + currentDateTime() + crlf +
                "Server: " + "JavaServer/0.1" + crlf +
                "Content-Type: " + "text/plain" + crlf +
                "Content-Length: " + "31" + crlf + crlf +
                customBody;

        assertEquals(returnedResponse, response.generateResponse());
    }
}
