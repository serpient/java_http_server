package server_logger;

import http_standards.Timestamp;
import repository.Repository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ServerLogger {
    private Repository repository;
    private String logDirectory;

    public ServerLogger(Repository repository, String logDirectory) {
        this.repository = repository;
        this.logDirectory = logDirectory;
    }

    public String writeRequest(int session, String request) {
        String formattedString = logHeader(session) + "[REQUEST]\n" + request + "\n";
        return write(session, formattedString.getBytes());
    }

    public String writeResponse(int session, String response) {
        return writeResponse(session, response.getBytes());
    }

    public String writeResponse(int session, byte[] response) {
        String formattedString = logHeader(session) + "[RESPONSE]\n";
        byte[] formattedResponse = {};
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
            outputStream.write(formattedString.getBytes());
            outputStream.write(response);
            formattedResponse = outputStream.toByteArray( );
        } catch (IOException e) {
            System.err.println(e);
        }
        return write(session, formattedResponse);
    }

    private String write(int session, byte[] content) {
        String fileName = Timestamp.localSmall() + "_" + session;
        repository.writeAndAppendFile(logDirectory + "/" + fileName, "txt", content);
        return "./logs/" + fileName + ".txt";
    }

    private String logHeader(int session) {
        return  "[" + Timestamp.local() + "] [Session " + session + "]\n";
    }
}
