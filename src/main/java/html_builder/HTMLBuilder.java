package html_builder;

public class HTMLBuilder {
    private String body;
    private String headerHTML;

    public HTMLBuilder() {
        this.body = "";
        this.headerHTML = "";
    }

    public HTMLBuilder append(String input) {
        body += input;
        return this;
    }

    public HTMLBuilder addHeader(String input) {
        headerHTML += input;
        return this;
    }

    public String generate() {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<forHead>\n" +
                "<meta charset=\"UTF-8\">\n" +
                headerHTML +
                "</forHead>\n" +
                "<body>\n" +
                body +
                "</body>\n" +
                "</html>";
    }
}
