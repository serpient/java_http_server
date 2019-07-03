package http_server;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
    public void settings_can_set_default_port_if_missing_from_settings() {
        String[] args = {
                "-p",
        };

        assertEquals(false, Settings.validateSettings(args));
        assertEquals(5000, Settings.getPort());
    }

    @Test
    public void settings_can_set_default_port_if_args_is_not_a_number() {
        String[] args = {
                "-p",
                "/public"
        };

        assertEquals(false, Settings.validateSettings(args));
        assertEquals(5000, Settings.getPort());
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

        assertEquals(false, Settings.validateSettings(args));
        assertEquals("/public", Settings.getDirectory());
    }

    @Test
    public void settings_can_throw_error_if_directory_is_not_real_from_terminal() {
        String[] args = {
                "-d",
                "public"
        };

        assertEquals(false, Settings.validateSettings(args));
        assertEquals("/public", Settings.getDirectory());
    }


    @Test
    public void settings_can_throw_error_if_port_and_directory_is_missing_input() {
        String[] args = {
                "-d",
                "-p"
        };

        assertEquals(false, Settings.validateSettings(args));
        assertEquals("/public", Settings.getDirectory());
        assertEquals(5000, Settings.getPort());
    }

    @Test
    public void settings_can_throw_error_if_port_and_directory_is_missing_input_reversed() {
        String[] args = {
                "-p",
                "-d"
        };

        assertEquals(false, Settings.validateSettings(args));
        assertEquals("/public", Settings.getDirectory());
        assertEquals(5000, Settings.getPort());
    }

    @Test
    public void settings_can_throw_error_if_directory_is_missing_input() {
        String[] args = {
                "-p",
                "1234",
                "-d"
        };

        assertEquals(false, Settings.validateSettings(args));
        assertEquals("/public", Settings.getDirectory());
        assertEquals(1234, Settings.getPort());
    }
}
