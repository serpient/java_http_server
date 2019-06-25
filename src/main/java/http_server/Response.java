package http_server;

import file_handler.FileHandler;
import http_protocol.Headers;
import http_protocol.StatusCode;
import java.util.LinkedHashMap;

public class Response {
    private LinkedHashMap<String, String> headerCollection = new LinkedHashMap<>();
    private String status;
    private String responseBody = "";
    private byte[] binaryFile;

    public Response() {
        this.status = StatusCode.ok;
    }

    public void setHeader(String headerName, String headerValue) {
        headerCollection.put(headerName, headerValue);
    }

    public LinkedHashMap<String, String> getHeaders() {
        return headerCollection;
    }

    public void setStatus(String newStatus) {
        this.status = newStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setBody(String newBody) {
        this.responseBody = newBody;
    }

    public String getBody() {
        return responseBody;
    }

    public void redirect(String redirectedRoute) {
        setStatus(StatusCode.moved);
        setHeader(Headers.location, "http://127.0.0.1:5000" + redirectedRoute);
    }

    public void saveBinary(byte[] bytes)
    {
        setHeader(Headers.contentLength, bytes.length + "");
        this.binaryFile = bytes;
    }

    public byte[] getBinaryFile() {
        return binaryFile;
    }

    public void sendFile(String path) {
        String filePath = Router.getStaticDirectoryPath() + path;
        byte[] file = FileHandler.readFile(filePath);
        saveBinary(file);
        setHeader(Headers.contentType, FileHandler.getFileType(filePath));
        setHeader(Headers.contentLength, file.length + "");
    }
}
