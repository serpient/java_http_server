package http_server;

import http_protocol.RequestCreator;
import http_protocol.StatusCode;
import http_protocol.Stringer;
import mocks.MockClientSocket;
import mocks.MockRouter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResponseTest {
    String request_line = "GET /simple_get HTTP/1.1" + Stringer.crlf;
    String user_agent = "User-Agent: HTTPTool/1.0" + Stringer.crlf + Stringer.crlf;
    String request = request_line + user_agent;

    Request requestData = RequestCreator.from(request);
    Router mockRouter = new MockRouter().getApp();
    MockClientSocket mockClient = new MockClientSocket("");
    Response response = new Response();

    private String currentDateTime() {
        ZonedDateTime date = LocalDateTime.now().atZone(ZoneId.of("GMT+00"));
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("E, MMM dd yyyy HH:mm:ss z");
        String formattedDate = date.format(dateFormatter);

        return formattedDate;
    }

    @Test
    public void response_can_set_status() {
        ResponseSender builder = new ResponseSender(mockClient, response, requestData, mockRouter);

        response.setStatus(StatusCode.notFound);
        response.setHeader("Date", currentDateTime());
        response.setHeader("Server", "JavaServer/0.1");

        String returnedHeader = "HTTP/1.1 404 Not Found" + Stringer.crlf;

        assertEquals(returnedHeader, builder.buildStatus());
    }

    @Test
    public void response_can_add_custom_headers() {
        ResponseSender builder = new ResponseSender(mockClient, response, requestData, mockRouter);
        response.setHeader("Date", currentDateTime());
        response.setHeader("Server", "JavaServer/0.1");
        response.setHeader("CustomHeader", "Hiya!");

        String returnedHeader = "Date: " + currentDateTime() + Stringer.crlf +
                "Server: " + "JavaServer/0.1" + Stringer.crlf +
                "CustomHeader: Hiya!" + Stringer.crlf;

        assertEquals(returnedHeader, builder.buildHeader());
    }

    @Test
    public void response_can_change_body() {
        ResponseSender builder = new ResponseSender(mockClient, response, requestData, mockRouter);
        String customBody = "This is my custom responseBody!";
        response.setBody(customBody);

        assertEquals(Stringer.crlf + customBody, builder.buildBody());
    }

    @Test
    public void response_can_build_a_full_body_response() {
        ResponseSender builder = new ResponseSender(mockClient, response, requestData, mockRouter);
        String customBody = "This is my custom responseBody!";
        response.setBody(customBody);

        String returnedResponse = "HTTP/1.1 200 OK" + Stringer.crlf +
                "Date: " + currentDateTime() + Stringer.crlf +
                "Server: " + "JavaServer/0.1" + Stringer.crlf +
                "Content-Type: " + "text/plain" + Stringer.crlf +
                "Content-Length: " + "31" + Stringer.crlf + Stringer.crlf +
                customBody;

        String builderResponse = builder.buildStatus() + builder.buildHeader() + builder.buildBody();

        assertEquals(returnedResponse, builderResponse);
    }
}
