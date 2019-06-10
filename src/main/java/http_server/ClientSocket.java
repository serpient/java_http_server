package http_server;

import java.io.BufferedReader;
import java.net.Socket;
import java.io.IOException;

public class ClientSocket implements SocketWrapper {
    private final Socket clientSocket;
    private BufferedReader inputStream;
    private WriterWrapper outputStream;

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
            return inputStream.readLine();
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
}
