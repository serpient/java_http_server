package http_protocol;

import java.util.HashMap;

public class MIMETypes {
    private static HashMap<String, String> types = new HashMap<>();

    public static String getFileType(String mimeType) {
        types.put("text/plain", "txt");
        types.put("text/html", "html");
        types.put("text/css", "css");
        types.put("image/jpeg", "jpg");
        types.put("image/png", "png");

        return types.get(mimeType);

    }
}
