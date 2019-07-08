package http_server;
import org.junit.jupiter.api.Test;

import javax.lang.model.type.ErrorType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ServerSettingsTest {
    @Test
    public void settings_can_parse_port_from_terminal() {
        String[] args = {
                "-p",
                "1234",
        };
        assertEquals(true, Settings.validateSettings(args));
        assertEquals(1234, Settings.getPort());
    }

    @Test
    public void settings_errors_if_default_port_if_missing_from_settings() {
        String[] args = {
                "-p",
        };

        assertThrows(Error.class, () -> Settings.validateSettings(args));
    }

    @Test
    public void settings_errors_if_default_port_if_args_is_not_a_number() {
        String[] args = {
                "-p",
                "/public"
        };

        assertThrows(Error.class, () -> Settings.validateSettings(args));
    }

    @Test
    public void settings_can_parse_directory_from_terminal() {
        String[] args = {
                "-d",
                "/public"
        };

        assertEquals(true, Settings.validateSettings(args));
        assertEquals("/public", Settings.getDirectory());
    }


    @Test
    public void settings_can_throw_error_if_directory_is_missing_from_terminal() {
        String[] args = {
                "-d"
        };

        assertThrows(Error.class, () -> Settings.validateSettings(args));
    }

    @Test
    public void settings_can_throw_error_if_port_and_directory_is_missing_input() {
        String[] args = {
                "-d",
                "-p"
        };

        assertThrows(Error.class, () -> Settings.validateSettings(args));
    }

    @Test
    public void settings_can_throw_error_if_port_and_directory_is_missing_input_reversed() {
        String[] args = {
                "-p",
                "-d"
        };

        assertThrows(Error.class, () -> Settings.validateSettings(args));
    }

    @Test
    public void settings_can_throw_error_if_directory_is_missing_input() {
        String[] args = {
                "-p",
                "1234",
                "-d"
        };

        assertThrows(Error.class, () -> Settings.validateSettings(args));
    }

    @Test
    public void settings_can_set_valid_port_and_directory() {
        String[] args = {
                "-p",
                "1234",
                "-d",
                "/public"
        };

        assertEquals(true, Settings.validateSettings(args));
        assertEquals("/public", Settings.getDirectory());
        assertEquals(1234, Settings.getPort());
    }

    @Test
    public void settings_can_set_valid_port_and_directory_reversed() {
        String[] args = {
                "-d",
                "/public",
                "-p",
                "1111"
        };

        assertEquals(true, Settings.validateSettings(args));
        assertEquals("/public", Settings.getDirectory());
        assertEquals(1111, Settings.getPort());
    }
}
