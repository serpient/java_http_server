package directory_page_creator;

import file_handler.FileHandler;
import html_builder.HTMLBuilder;

import java.util.List;

public class DirectoryPageCreator {
    private List<String> directoryList;
    private String staticDirectoryPath;

    public DirectoryPageCreator(List<String> directoryList, String staticDirectoryPath) {
        this.directoryList = directoryList;
        this.staticDirectoryPath = staticDirectoryPath;
    }

    public String generateHTML() {
        String directoryCSSPath = "./src/main/java/directory_page_creator/DirectoryStyles";
        HTMLBuilder builder = new HTMLBuilder();

        return builder
            .addHeader("<style>")
            .addHeader(FileHandler.getFileContents(directoryCSSPath))
            .addHeader("</style>")
            .append("<div class='directory-page'>")
            .append("<h1>Directory for " + staticDirectoryPath + "</h1>")
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
            htmlList += "<li class='bullets'><a href='" + staticDirectoryPath + "/" +
                    directoryList.get(i) + "'>" + directoryList.get(i) + "</a></li>\n";
        }
        return htmlList;
    }
}
