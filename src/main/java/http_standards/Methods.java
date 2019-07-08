package http_standards;

import java.util.Arrays;
import java.util.LinkedHashSet;
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

    public final static LinkedHashSet<String> allMethods() {
        LinkedHashSet methods = new LinkedHashSet<>();
        methods.add(Methods.get);
        methods.add(Methods.head);
        methods.add(Methods.post);
        methods.add(Methods.put);
        methods.add(Methods.options);
        methods.add(Methods.delete);

        return methods;
    }
}
