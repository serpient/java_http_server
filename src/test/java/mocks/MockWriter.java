package mocks;

import http_server.WriterWrapper;

public class MockWriter implements WriterWrapper {
    private String sentData;

    public void send(String data) {
        sentData = data;
    }

    public String getSentData() {
        return sentData;
    }

    public void close() { return; }
}
