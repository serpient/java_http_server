package http_server;

import java.io.BufferedReader;
import java.net.Socket;
import java.io.IOException;

public class ClientSocket implements SocketWrapper {
    private final Socket clientSocket;
    private BufferedReader input;
    private WriterWrapper output;

    char[] characterBuffer = new char[1000];

    public ClientSocket(
            Socket clientSocket,
            BufferedReader input,
            WriterWrapper output
    ) {
        this.clientSocket = clientSocket;
        this.input = input;
        this.output = output;
    }

    public String readData() {
        try {
            String result = "";
            int bytes_read = input.read(characterBuffer);
            while (bytes_read != -1 && bytes_read != 0) {
                result += new String(characterBuffer, 0, bytes_read);
                bytes_read = input.ready() ? input.read(characterBuffer) : -1;
            }
            return result;
        } catch (IOException e) {
            System.err.println(e.toString());
            return e.toString();
        }
    }

    public void close() {
        try {
            input.close();
            output.close();
            clientSocket.close();
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }

    public boolean ready() {
        try {
            return input.ready();
        } catch (IOException e) {
            System.err.println(e.toString());
            return false;
        }
    }

    public void sendBinary(byte[] binary) {
        output.send(binary);
    }
}
