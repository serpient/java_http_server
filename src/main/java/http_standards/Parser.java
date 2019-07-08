package http_standards;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;

public class Parser {
    public static HashMap<String, String> getHeaders(String request) {
        String[] splitRequest = splitMessage(request);

        HashMap<String, String> headersCollection = new HashMap<>();
        int startOfBodyIdx = getStartOfBodyIdx(splitRequest);
        int skipRequestLineIdx = 1;
        int endOfHeadersIdx = startOfBodyIdx == -1 ? splitRequest.length : startOfBodyIdx;

        for (int i = skipRequestLineIdx; i < endOfHeadersIdx; i++) {
            String[] splitHeader = splitRequest[i].split(":", 2);
            String headerName = splitHeader[0].trim();
            String headerValue = splitHeader[1].trim();
            headersCollection.put(headerName, headerValue);
        }
        return headersCollection;
    }

    public static String getBody(String request) {
        String[] splitRequest = splitMessage(request);

        int startOfBodyIdx = getStartOfBodyIdx(splitRequest);

        String body = "";
        if (startOfBodyIdx != -1) {
            for (int i = startOfBodyIdx + 1; i < splitRequest.length; i++) {
                body += atEndOfBody(i, splitRequest) ? splitRequest[i] : splitRequest[i] + "\n";
            }
        }

        return body.length() > 0 ? body : null;
    }

    public static String getMethod(String request) {
        return getFirstLine(request)[0];
    }

    public static String getRoute(String request) {
        String routeLine =  getFirstLine(request)[1];
        if (routeLine.contains("?")) {
            int paramIndex = routeLine.indexOf("?");
            return routeLine.substring(0, paramIndex);
        }
        return routeLine;
    }

    public static String getStatusCode(String request) {
        return Parser.getFirstLine(request)[1];
    }

    public static HashMap<String, String> getParameters(String request) {
        String routeLine = getFirstLine(request)[1];
        if (routeLine.contains("?")) {
            return decodeData(routeLine);
        } else {
            return new HashMap<>();
        }
    }

    public static HashMap<String, String> decodeData(String data) {
        HashMap<String, String> decodedCollection = new HashMap<>();

        int startOfParametersIndex = data.indexOf("?");
        String encodedParameters = data.substring(startOfParametersIndex + 1);
        try {
            String[] parameterSets = encodedParameters.split("[&]");
            for (int i = 0; i < parameterSets.length; i++) {
                String[] keyValuePair = parameterSets[i].split("=");
                String key = URLDecoder.decode(keyValuePair[0], "UTF-8");
                String value = keyValuePair.length > 1 ? URLDecoder.decode(keyValuePair[1], "UTF-8") : "";
                decodedCollection.put(key, value);
            }
        } catch (UnsupportedEncodingException e) {
            System.err.println(e);
        }

        return decodedCollection;
    }

    private static String[] splitMessage(String request) {
        return request.trim().split("\r\n");
    }

    private static String[] getFirstLine(String request) {
        return splitMessage(request)[0].trim().split("\\s");
    }

    private static boolean atEndOfBody(int currentIdx, String[] text) {
        return currentIdx == text.length - 1;
    }

    private static int getStartOfBodyIdx(String[] splitRequest) {
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
