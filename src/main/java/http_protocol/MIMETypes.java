package http_protocol;

import java.util.HashMap;

public class MIMETypes {
    public static String plain = "text/plain";
    public static String html = "text/html";
    public static String css = "text/css";
    public static String jpeg = "image/jpeg";
    public static String png = "image/png";

    private static HashMap<String, String> types = new HashMap<>();

    public static String getFileType(String mimeType) {
        types.put(plain, "txt");
        types.put(html, "html");
        types.put(css, "css");
        types.put(jpeg, "jpg");
        types.put(png, "png");
        return types.get(mimeType);
    }
}
