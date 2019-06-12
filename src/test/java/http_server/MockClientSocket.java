package http_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class MockClientSocket implements SocketWrapper {
    private MockWriter mockWriter;
    private BufferedReader inputStream;
    char[] characterBuffer = new char[100000];

    public MockClientSocket(String input) {
        this.inputStream = new BufferedReader(new StringReader(input));
        this.mockWriter = new MockWriter();
    }

    public String readData() {
        try {
            int bytes_read = inputStream.read(characterBuffer);
            return new String(characterBuffer, 0, bytes_read);
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

    public boolean ready() {
        return true;
    }
}
