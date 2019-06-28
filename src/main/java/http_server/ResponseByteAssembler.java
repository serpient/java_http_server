package http_server;

import http_standards.Stringer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseByteAssembler {
    public static byte[] generateBytes(String status, LinkedHashMap<String, String> headers, byte[] body) {
        String fullStatus = "HTTP/1.1 " + status + Stringer.crlf;
        String fullHeader = fullStatus + buildHeader(headers) + Stringer.crlf;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        try {
            outputStream.write(fullHeader.getBytes());
            if (body != null) {
                outputStream.write(body);
            }
        } catch (IOException e) {
            System.err.println(e);
        }
        return outputStream.toByteArray();
    }

    private static String buildHeader(LinkedHashMap<String, String> headers) {
        String header = "";

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            header += key + ": " + value + Stringer.crlf;
        }

        return header;
    }
}
