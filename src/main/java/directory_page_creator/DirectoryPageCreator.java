package directory_page_creator;

import html_builder.HTMLBuilder;
import repository.FileRepository;
import java.util.List;

public class DirectoryPageCreator {
    public static String generateHTML(List<String> directoryList, String directoryPath) {
        String directoryCSSPath = "./src/main/java/directory_page_creator/DirectoryStyles";
        HTMLBuilder builder = new HTMLBuilder();

        return builder
            .addHeader("<style>")
            .addHeader(new FileRepository().getFileContents(directoryCSSPath))
            .addHeader("</style>")
            .append("<div class='directory-page'>")
            .append("<h1>Directory for " + directoryPath + "</h1>")
            .append("<hr />")
            .append("<ul>\n")
            .append(renderAsList(directoryList, directoryPath))
            .append("</ul>\n")
            .append("</div>")
            .generate();
    }

    private static String renderAsList(List<String> directoryList, String directoryPath) {
        String htmlList = "";
        for (int i = 0; i < directoryList.size(); i++) {
            htmlList += "<li class='bullets'><a href='" + directoryPath + "/" +
                    directoryList.get(i) + "'>" + directoryList.get(i) + "</a></li>\n";
        }
        return htmlList;
    }
}
