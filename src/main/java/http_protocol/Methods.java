package http_protocol;

import java.util.Arrays;
import java.util.List;

public class Methods {
    public final static String get = "GET";
    public final static String post = "POST";
    public final static String head = "HEAD";
    public final static String options = "OPTIONS";
    public final static String put = "PUT";
    public final static String delete = "DELETE";
    private final static List<String> creationMethods = Arrays.asList(new String[]{post, put});


    public final static List<String> creationMethods() {
        return creationMethods;
    }
}
