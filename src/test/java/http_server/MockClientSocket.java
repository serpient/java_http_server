package http_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class MockClientSocket {
    private MockWriter mockWriter;
    private BufferedReader inputStream;

    public MockClientSocket(String input) {
        this.inputStream = new BufferedReader(new StringReader(input));
        this.mockWriter = new MockWriter();
    }

    public String readData() {
        try {
            return inputStream.readLine();
        } catch (IOException e) {
            return System.err.toString();
        }
    }

    public void sendData(String data) {
        mockWriter.send(data);
    }

    public String getSentData() {
        return mockWriter.getSentData();
    }

    public void close() {
        return;
    }
}
