package http_server;

import java.util.List;

public class DirectoryCreator {
    private List<String> directoryList;
    private String newStaticDirectoryPath;

    public DirectoryCreator(List<String> directoryList, String newStaticDirectoryPath) {
        this.directoryList = directoryList;
        this.newStaticDirectoryPath = newStaticDirectoryPath;
    }

    public String generateHTML() {
        String directoryCSSPath = "./src/main/java/http_server/DirectoryStyles";
        HTMLBuilder builder = new HTMLBuilder();

        return builder
            .addHeader("<style>")
            .addHeader(FileHandler.readFile(directoryCSSPath))
            .addHeader("</style>")
            .append("<div class='directory-page'>")
            .append("<h1>Directory for " + newStaticDirectoryPath + "</h1>")
            .append("<hr />")
            .append("<ul>\n")
            .append(renderAsList())
            .append("</ul>\n")
            .append("</div>")
            .generate();
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
