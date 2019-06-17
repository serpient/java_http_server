package http_server;

import java.util.HashMap;

public class RequestParser {
    HashMap<String, String> requestCollection = new HashMap<>();
    HashMap<String, String> headersCollection = new HashMap<>();

    public RequestParser(String request) {
        String[] splitRequest = request.trim().split("\r\n");

        responseLineParser(splitRequest);
        bodyParser(splitRequest);
        headerParser(splitRequest);
    }

    public String method() {
        return requestCollection.get("method");
    }

    public String body() {
        return requestCollection.get("responseBody");
    }

    public String route() {
        return requestCollection.get("route");
    }

    public HashMap<String, String> headers() {
        return headersCollection;
    }

    private void responseLineParser(String[] splitRequest) {
        String[] request_line = splitRequest[0].trim().split("\\s");
        requestCollection.put("method", request_line[0]);
        requestCollection.put("route", request_line[1]);
    }

    private void bodyParser(String[] splitRequest) {
        int startOfBodyIdx = getStartOfBodyIdx(splitRequest);

        String body = "";
        if (startOfBodyIdx != -1) {
            for (int i = startOfBodyIdx + 1; i < splitRequest.length; i++) {
                body += atEndOfBody(i, splitRequest) ? splitRequest[i] : splitRequest[i] + "\n";
            }
        }

        requestCollection.put("responseBody", body);
    }

    private boolean atEndOfBody(int currentIdx, String[] text) {
        return currentIdx == text.length - 1;
    }

    private void headerParser(String[] splitRequest) {
        int startOfBodyIdx = getStartOfBodyIdx(splitRequest);
        int skipRequestLineIdx = 1;
        int endOfHeadersIdx = startOfBodyIdx == -1 ? splitRequest.length : startOfBodyIdx;

        for (int i = skipRequestLineIdx; i < endOfHeadersIdx; i++) {
            String[] splitHeader = splitRequest[i].split(":");
            String headerName = splitHeader[0].trim();
            String headerValue = splitHeader[1].trim();
            headersCollection.put(headerName, headerValue);
        }
    }

    private int getStartOfBodyIdx(String[] splitRequest) {
        int startOfBodyIdx = -1;

        for (int i = 0; i < splitRequest.length; i++) {
            if (splitRequest[i].length() == 0) {
                startOfBodyIdx = i;
                break;
            }
        }

        return startOfBodyIdx;
    }

}
