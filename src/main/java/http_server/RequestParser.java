package http_server;

import java.util.HashMap;

public class RequestParser {
    String[] splitRequest;
    String[] request_line;

    public RequestParser(String request) {
        splitRequest = request.trim().split("\r\n");
        request_line = splitRequest[0].trim().split("\\s");
    }

    public HashMap<String, String> headers() {
        HashMap<String, String> headersCollection = new HashMap<>();
        int startOfBodyIdx = getStartOfBodyIdx(splitRequest);
        int skipRequestLineIdx = 1;
        int endOfHeadersIdx = startOfBodyIdx == -1 ? splitRequest.length : startOfBodyIdx;

        for (int i = skipRequestLineIdx; i < endOfHeadersIdx; i++) {
            String[] splitHeader = splitRequest[i].split(":");
            String headerName = splitHeader[0].trim();
            String headerValue = splitHeader[1].trim();
            headersCollection.put(headerName, headerValue);
        }
        return headersCollection;
    }

    public String method() {
        return request_line[0];
    }

    public String route() {
        return request_line[1];
    }

    public String body() {
        int startOfBodyIdx = getStartOfBodyIdx(splitRequest);

        String body = "";
        if (startOfBodyIdx != -1) {
            for (int i = startOfBodyIdx + 1; i < splitRequest.length; i++) {
                body += atEndOfBody(i, splitRequest) ? splitRequest[i] : splitRequest[i] + "\n";
            }
        }

        return body;
    }

    private boolean atEndOfBody(int currentIdx, String[] text) {
        return currentIdx == text.length - 1;
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
