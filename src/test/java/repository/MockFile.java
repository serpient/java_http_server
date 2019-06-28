package repository;

public class MockFile {
    private String name;
    private String type;
    private byte[] content;

    public MockFile(String name, String type, byte[] content) {
        this.name = name;
        this.type = type;
        this.content = content;
    }

    public String name() {
        return name;
    }

    public String type() {
        return type;
    }

    public byte[] content() {
        return content;
    }
}