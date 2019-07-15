package http_server;

public class OperationResult {
    private boolean valid;
    private String statusCode;
    private byte[] data;

    public OperationResult(boolean valid, String statusCode) {
        this.valid = valid;
        this.statusCode = statusCode;
        this.data = null;
    }

    public OperationResult(boolean valid, String statusCode, String data) {
        this(valid, statusCode, data.getBytes());
    }

    public OperationResult(boolean valid, String statusCode, byte[] data) {
        this(valid, statusCode);
        this.data = data;
    }

    public boolean valid() {
        return valid;
    }

    public String statusCode() {
        return statusCode;
    }

    public byte[] data() {
        return data;
    }
}
