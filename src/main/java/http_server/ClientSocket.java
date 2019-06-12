package http_server;

import java.io.BufferedReader;
import java.net.Socket;
import java.io.IOException;
import java.util.Arrays;

public class ClientSocket implements SocketWrapper {
    private final Socket clientSocket;
    private BufferedReader inputStream;
    private WriterWrapper outputStream;
    char[] characterBuffer = new char[100000];

    public ClientSocket(
            Socket clientSocket,
            BufferedReader inputStream,
            WriterWrapper outputStream
    ) {
        this.clientSocket = clientSocket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public String readData() {
        try {
            int bytes_read = inputStream.read(characterBuffer);
            return new String(characterBuffer, 0, bytes_read);
        } catch (IOException e) {
            System.err.println(e.toString());
            return e.toString();
        }
    }

    public void sendData(String data) {
        outputStream.send(data);
    }

    public void close() {
        try {
            inputStream.close();
            outputStream.close();
            clientSocket.close();
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }

    public boolean ready() {
        try {
            return inputStream.ready();
        } catch (IOException e) {
            System.err.println(e.toString());
            return false;
        }
    }
}
