package http_server;

public class HTMLBuilder {
    private String body;
    private String headerHTML;

    public HTMLBuilder() {
        this.body = "";
        this.headerHTML = "";
    }

    public void append(String input) {
        body += input;
    }

    public void addHeader(String input) {
        headerHTML += input;
    }

    public String generate() {
        return  starterHTML() + headHTML() + starterBody() + body + endHtml();
    }

    private String starterHTML() {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n";
    }

    private String headHTML() {
        return "<head>\n" +
                "<meta charset=\"UTF-8\">\n" +
                "<title>Home Page</title>\n" +
                headerHTML +
                "</head>\n";
    }

    private String starterBody() {
        return "<body>\n";
    }

    private String endHtml() {
        return "</body>\n" +
                "</html>";
    }

}
