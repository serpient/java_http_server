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
                if (portValid(args[i], args[i + 1])) {
                    settings.put("port", Integer.parseInt(args[i + 1]) + "");
                    i += 1;
                    continue;
                }

                if (directoryValid(args[i], args[i + 1])) {
                    settings.put("directory", args[i + 1]);
                    i += 1;
                    continue;
                }

                return false;
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
        settings.put("directory", "/public");
    }

    private static boolean portValid(String setting, String input) {
        if (!setting.equals("-p")) {
            return false;
        }
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            System.err.println(input + " is not a valid port input. Please use a numeric port input.");
            System.err.println(e);
            return false;
        }
    }

    private static boolean directoryValid(String setting, String input) {
        if (!setting.equals("-d")) {
            return false;
        }
        if (input.startsWith("/")) {
            return true;
        } else {
            System.err.println("'" + input + "' is not a valid directory path.");
            return false;
        }
    }
}

