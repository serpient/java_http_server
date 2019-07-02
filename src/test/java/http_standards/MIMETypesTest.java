package http_standards;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MIMETypesTest {
    @Test
    public void given_a_plain_MIMEType_return_file_type() {
        assertEquals("txt", MIMETypes.getFileType(MIMETypes.plain));
    }

    @Test
    public void given_a_image_MIMEType_return_file_type() {
        assertEquals("png", MIMETypes.getFileType(MIMETypes.png));
    }

    @Test
    public void given_a_image_type_return_MIMEType() {
        assertEquals(MIMETypes.png, MIMETypes.getMIMEType("png"));
    }

    @Test
    public void given_a_text_type_return_MIMEType() {
        assertEquals(MIMETypes.json, MIMETypes.getMIMEType("json"));
    }

    @Test
    public void verify_whether_a_value_is_a_mime_type() {
        assertEquals(true, MIMETypes.isMIMEType(MIMETypes.plain));
    }

    @Test
    public void verify_whether_a_value_is_not_a_mime_type() {
        assertEquals(false, MIMETypes.isMIMEType("png"));
    }
}
