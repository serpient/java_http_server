package http_server;

public class HTMLBuilder {
    private String html;

    public HTMLBuilder() {
        this.html = starterHTML();
    }

    public void append(String input) {
        html += input;
    }

    public String generate() {
        return html += endHtml();
    }

    private String starterHTML() {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "<meta charset=\"UTF-8\">\n" +
                "<title>Home Page</title>\n" +
                "</head>\n" +
                "<body>\n";
    }

    private String endHtml() {
        return "</body>\n" +
                "</html>";
    }

}
