package http_server;

import java.util.List;

public class DirectoryBuilder {
    private List<String> directoryList;

    public DirectoryBuilder(List<String> directoryList) {
        this.directoryList = directoryList;
    }

    public String generateHTML() {
        HTMLBuilder builder = new HTMLBuilder();

        builder.append("<ul>\n");
        builder.append(renderAsList());
        builder.append("</ul>\n");
        return builder.generate();
    }

    private String renderAsList() {
        String htmlList = "";
        for (int i = 0; i < directoryList.size(); i++) {
            htmlList += "<li>" + directoryList.get(i) + "</li>\n";
        }
        return htmlList;
    }
}
