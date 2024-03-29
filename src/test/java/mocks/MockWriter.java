package mocks;

import http_server.WriterWrapper;

public class MockWriter implements WriterWrapper {
    private String sentData = "";

    public void send(byte[] data) {
        if (data != null) {
            sentData += new String(data);
        }
    }

    public String getSentData() {
        return sentData;
    }

    public void close() { return; }
}
