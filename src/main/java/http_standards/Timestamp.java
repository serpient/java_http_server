package http_standards;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Timestamp {
    public static String GMT() {
        ZonedDateTime date = LocalDateTime.now().atZone(ZoneId.of("GMT+00"));
        return date.format(datePattern());
    }

    public static String local() {
        return LocalDateTime.now().format(datePattern());
    }

    public static String localSmall() {
        return LocalDateTime.now().format(shortDatePattern());
    }

    private static DateTimeFormatter datePattern() {
        return DateTimeFormatter.ofPattern("E, MMM dd yyyy HH:mm:ss.SSS");
    }

    private static DateTimeFormatter shortDatePattern() {
        return DateTimeFormatter.ofPattern("yyyyMMdd-hh:mm:ss");
    }
}
