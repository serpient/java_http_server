package http_server;

import java.nio.file.Files;
import java.nio.file.Paths;
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
                throw new Error("Missing settings input for ( " + args[i] + " ).\nSystem exiting.");
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
            throw new Error(input + " is not a valid port input. Please use a numeric port input.");
        }
    }

    private static boolean directoryValid(String setting, String input) {
        if (!setting.equals("-d")) {
            return false;
        }

        boolean directoryExists = Files.exists(Paths.get(System.getProperty("user.dir"), input));

        if (directoryExists) {
            return true;
        } else {
            throw new Error("'" + input + "' is not a valid directory path.");
        }
    }
}

