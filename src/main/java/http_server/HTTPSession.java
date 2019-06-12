package http_server;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class HTTPSession implements Runnable {
    private final SocketWrapper client;
    private Boolean isRunning;
    private String crlf = "\r\n";

    public HTTPSession(SocketWrapper client) {
        this.client = client;
        this.isRunning = true;
    }

    public void run() {
        while (!Thread.interrupted() && isRunning) {
            if (!client.ready()) {
                try {
                    Thread.sleep(1000);
                    continue;
                } catch (InterruptedException e) {
                    close();
                    return;
                }
            }

            String newLine = client.readData();

            System.err.println(newLine);

            String[] request = newLine.trim().split(crlf);

            String[] request_line = request[0].trim().split("\\s");
            String requestMethod = request_line[0];
            String requestRoute = request_line[1];

            String body = "";
            int blankLineIdx = -1;

            for (int i = 0; i < request.length; i++) {
                if (request[i].length() == 0) {
                    blankLineIdx = i;
                    break;
                }
            }

            for (int i = blankLineIdx + 1; i < request.length; i++) {
                if (i == request.length - 1) {
                    body += request[i];
                } else {
                    body += request[i] + "\n";
                }
            }

            RequestParser parser = new RequestParser(requestMethod, requestRoute, body);

            String response = responseGenerator(parser.getResponseStatus(), parser.getResponseBody(), requestMethod);

            serverPrint(response);

            client.sendData(response);

            close();
        }
    }

    public void close() {
        client.close();
        isRunning = false;
        System.out.println("Disconnecting client");
    }

    private String currentDateTime() {
        ZonedDateTime date = LocalDateTime.now().atZone(ZoneId.of("GMT+00"));
        DateTimeFormatter byPattern = DateTimeFormatter.ofPattern("E, MMM dd yyyy HH:mm:ss z");
        return date.format(byPattern);
    }

    private void serverPrint(String message) {
        System.out.println(
                crlf +
                "=============" + crlf +
                "ServerResponse: " + crlf +
                message +
                "=============" + crlf
        );
    }

    private String responseGenerator(String status, String body, String method) {
        String responseLine = "HTTP/1.1 " + status + crlf;

        String dateHeader = "Date: " + currentDateTime() + crlf;

        String serverHeader = "Server: JavaServer/0.1" + crlf;

        String outputLine = responseLine + dateHeader + serverHeader;

        if (hasBody(body)) {
            String contentTypeHeader = "Content-Type: text/plain" + crlf;
            String contentLengthHeader = "Content-Length: " + getContentLength(body) + crlf;

            outputLine += contentTypeHeader + contentLengthHeader + crlf;

            if (!method.equals("HEAD")) {
                outputLine += body;
            }

        }

        return outputLine;
    }

    private int getContentLength(String body) {
        return body.getBytes().length;
    }

    private boolean hasBody(String body) {
        return body.length() > 0;
    }
}
