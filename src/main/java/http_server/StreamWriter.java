package http_server;

import java.io.IOException;
import java.io.OutputStream;

public class StreamWriter implements WriterWrapper {
    private OutputStream outputStream;

    public StreamWriter(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void send(byte[] binary) {
        try {
            outputStream.write(binary);
            outputStream.flush();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public void close() {
        try {
            outputStream.close();
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
