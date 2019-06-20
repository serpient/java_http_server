package http_server;

import java.util.List;

public class DirectoryBuilder {
    private List<String> directoryList;
    private String newStaticDirectoryPath;

    public DirectoryBuilder(List<String> directoryList, String newStaticDirectoryPath) {
        this.directoryList = directoryList;
        this.newStaticDirectoryPath = newStaticDirectoryPath;
    }

    public String generateHTML() {
        String directoryCSSPath = "/Users/fsadikin/Documents/java_http_server/src/main/java/http_server" +
                "/DirectoryStyles";
        HTMLBuilder builder = new HTMLBuilder();

        builder.addHeader("<style>");
        builder.addHeader(FileHandler.readFile(directoryCSSPath));
        builder.addHeader("</style>");
        builder.append("<div class='directory-page'>");
        builder.append("<h1>Directory for " + newStaticDirectoryPath + "</h1>");
        builder.append("<hr />");
        builder.append("<ul>\n");
        builder.append(renderAsList());
        builder.append("</ul>\n");
        builder.append("</div>");
        return builder.generate();
    }

    private String renderAsList() {
        String htmlList = "";
        for (int i = 0; i < directoryList.size(); i++) {
            htmlList += "<li class='bullets'><a href='" + newStaticDirectoryPath + "/" +
                    directoryList.get(i) + "'>" + directoryList.get(i) + "</a></li>\n";
        }
        return htmlList;
    }
}
