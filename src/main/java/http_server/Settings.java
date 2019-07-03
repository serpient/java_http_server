package http_server;

import java.util.HashMap;

public class Settings {
    private static HashMap<String, String> settings = new HashMap<>();

    public static int getPort() {
        return Integer.parseInt(settings.get("port"));
    }

    public static String getDirectory() {
        return settings.get("directory");
    }

    public static boolean validateSettings(String[] args) {
        initializeSettings();

        for (int i = 0; i < args.length; i++) {
            if (i + 1 < args.length) {
                if (args[i].equals("-p")) {
                    try {
                        settings.put("port", Integer.parseInt(args[i + 1]) + "");
                    } catch (NumberFormatException e) {
                        System.err.println(args[i + 1] + " is not a valid port input. Please use a numeric port input" +
                                ".");
                        System.err.println(e);
                        return false;
                    }
                }

                if (args[i].equals("-d")) {
                    if (args[i + 1].startsWith("/")) {
                        settings.put("directory", args[i + 1]);
                    } else {
                        System.err.println("'" + args[i + 1] + "' is not a valid directory path.");
                        return false;
                    }
                }
            } else if ((args[i].equals("-p") || args[i].equals("-d")) && i + 1 >= args.length) {
                System.err.println("Missing settings input for ( " + args[i] + " ).\nServer will start with default " +
                        "settings.");
                return false;
            }
        }
        return true;
    }

    private static void initializeSettings() {
        settings.put("port", "5000");
        settings.put("directory", "");
    }
}

