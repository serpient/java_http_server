package json_handler;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JSONPatcherTest {
    String jsonString = "{\n" +
            "    \"firstName\": \"John\",\n" +
            "    \"lastName\": \"Smith\",\n" +
            "    \"age\": 25,\n" +
            "    \"address\": {\n" +
            "        \"streetAddress\": \"21 2nd Street\",\n" +
            "        \"city\": \"New York\",\n" +
            "        \"state\": \"NY\",\n" +
            "        \"postalCode\": 10021\n" +
            "    },\n" +
            "    \"phoneNumbers\": [\n" +
            "        {\n" +
            "            \"type\": \"home\",\n" +
            "            \"number\": \"212 555-1234\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"type\": \"fax\",\n" +
            "            \"number\": \"646 555-4567\" \n" +
            "        }\n" +
            "    ] \n" +
            "}";


    @Test
    public void handler_can_check_for_invalid_paths() {
        JSONObject object = JSONHandler.parse(jsonString);
        String invalidPatchDocument = "[{ \"op\": \"replace\", \"path\": \"/non_existing_key\", " +
                "\"value\":" +
                " " +
                "\"testValue\" }]";

        assertEquals(false, JSONPatcher.validPatchRequest(invalidPatchDocument, object).valid());
    }

    @Test
    public void handler_can_check_for_unsupported_patch_methods() {
        JSONObject object = JSONHandler.parse(jsonString);
        String invalidPatchDocument = "[{ \"op\": \"unsupported\", \"path\": \"/firstName\", " +
                "\"value\":" +
                " " +
                "\"testValue\" }]";

        assertEquals(false, JSONPatcher.validPatchRequest(invalidPatchDocument, object).valid());
    }

    @Test
    public void handler_can_check_for_supported_patch_methods() {
        JSONObject object = JSONHandler.parse(jsonString);
        String invalidPatchDocument = "[{ \"op\": \"add\", \"path\": \"/firstName\", " +
                "\"value\":" +
                " " +
                "\"testValue\" }]";

        assertEquals(true, JSONPatcher.validPatchRequest(invalidPatchDocument, object).valid());
    }

    @Test
    public void handler_allows_unknown_keys_if_add_operation() {
        JSONObject object = JSONHandler.parse(jsonString);
        String invalidPatchDocument = "[{ \"op\": \"add\", \"path\": \"/unknown\", " +
                "\"value\":" +
                " " +
                "\"testValue\" }]";

        assertEquals(true, JSONPatcher.validPatchRequest(invalidPatchDocument, object).valid());
    }
}
