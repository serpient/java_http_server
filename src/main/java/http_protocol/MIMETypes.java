package http_protocol;

import java.util.HashMap;

public class MIMETypes {
    public static String plain = "text/plain";
    public static String html = "text/html";
    public static String css = "text/css";
    public static String jpeg = "image/jpeg";
    public static String png = "image/png";
    private static HashMap<String, String> types = new HashMap<String, String>() {{
        put(plain, "txt");
        put(html, "html");
        put(css, "css");
        put(jpeg, "jpg");
        put(png, "png");
    }};

    public static String getFileType(String mimeType) {
        return types.get(mimeType);
    }
}
