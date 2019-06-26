package http_server;

import java.io.BufferedReader;
import java.io.OutputStream;
import java.net.Socket;
import java.io.IOException;

public class ClientSocket implements SocketWrapper {
    private final Socket clientSocket;
    private BufferedReader inputStream;
    private WriterWrapper writer;
    private OutputStream outputStream;

    char[] characterBuffer = new char[100000];

    public ClientSocket(
            Socket clientSocket,
            BufferedReader inputStream,
            WriterWrapper outputStream
    ) {
        this.clientSocket = clientSocket;
        this.inputStream = inputStream;
        this.writer = outputStream;
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
        writer.send(data);
    }

    public void close() {
        try {
            inputStream.close();
            writer.close();
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

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void sendBinary(byte[] binary) {
        try {
            outputStream.write(binary);
            outputStream.flush();
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
