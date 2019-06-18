package http_server;

public enum StatusCode {
    OK(200, "OK"),
    MOVED(301, "Moved Permanently"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed")
    ;

    public String Description;
    public int Code;

    StatusCode(int code, String description) {
        Description = description;
        Code = code;
    }

    String get() {
        return Code + " " + Description;
    }
}
