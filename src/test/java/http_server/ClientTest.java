package http_server;

import mocks.MockWriter;
import java.io.BufferedReader;
import java.io.StringReader;
import java.net.Socket;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClientTest {

    private final String testInput = "Hello";
    Socket mockClient = new Socket();
    BufferedReader input = new BufferedReader(new StringReader(testInput));
    MockWriter output = new MockWriter();
    ClientSocket clientSocket = new ClientSocket(mockClient, input, output);
    @Test
    public void clientReadsFromStream() {
        assertEquals(testInput, clientSocket.readData());
    }

    @Test
    public void clientWritesToStream() {
        clientSocket.sendData(testInput);

        assertEquals(testInput, output.getSentData());
    }
}