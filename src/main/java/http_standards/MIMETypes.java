package http_standards;

import java.util.HashMap;
import java.util.Map;

public class MIMETypes {
    public static String plain = "text/plain";
    public static String html = "text/html";
    public static String css = "text/css";
    public static String jpeg = "image/jpeg";
    public static String png = "image/png";
    public static String json = "application/json";
    public static String xml = "text/xml";
    public static String javascript = "application/javascript";

    private static HashMap<String, String> types = new HashMap<String, String>() {{
        put(plain, "txt");
        put(html, "html");
        put(css, "css");
        put(jpeg, "jpg");
        put(png, "png");
        put(json, "json");
        put(xml, "xml");
        put(javascript, "js");
    }};

    public static boolean isMIMEType(String mimeType) {
        return types.containsKey(mimeType);
    }

    public static String getFileType(String mimeType) {
        return types.get(mimeType);
    }

    public static String getMIMEType(String fileType) {
        String fileMIMEType = plain;
        for(Map.Entry<String, String> entry : types.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value.contains(fileType)) {
                fileMIMEType = key;
            }
        }
        return fileMIMEType;
    }
}
