package http_server;

import java.util.HashMap;

public class RequestParser {
    HashMap<String, String> requestCollection = new HashMap<>();
    HashMap<String, String> headersCollection = new HashMap<>();

    public RequestParser(String request) {
        String[] splitRequest = request.trim().split("\r\n");

        String[] request_line = splitRequest[0].trim().split("\\s");
        requestCollection.put("method", request_line[0]);
        requestCollection.put("route", request_line[1]);
        requestCollection.put("body", bodyParser(splitRequest));
        headerParser(splitRequest);
    }

    public String method() {
        return requestCollection.get("method");
    }

    public String body() {
        return requestCollection.get("body");
    }

    public String route() {
        return requestCollection.get("route");
    }

    public HashMap<String, String> headers() {
        return headersCollection;
    }

    private String bodyParser(String[] splitRequest) {
        int startOfBodyIdx = getStartOfBodyIdx(splitRequest);

        if (startOfBodyIdx == -1) {
            return "";
        } else {
            String requestBody = "";
            for (int i = startOfBodyIdx + 1; i < splitRequest.length; i++) {
                if (i == splitRequest.length - 1) {
                    requestBody += splitRequest[i];
                } else {
                    requestBody += splitRequest[i] + "\n";
                }
            }
            return requestBody;
        }
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
