package json_handler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JSONHandlerTest {
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
    public void parser_can_handle_simple_key_value_pairs() {
        JSONObject object = JSONHandler.parse(jsonString);

        assertEquals(false, object.get("firstName") instanceof JSONArray);
        assertEquals(false, object.get("firstName") instanceof JSONObject);
        assertEquals(true, object.get("firstName") instanceof String);

        assertEquals("John", object.get("firstName"));
        assertEquals("Smith", object.get("lastName"));
        assertEquals(25, object.get("age"));
    }

    @Test
    public void parser_can_handle_json_array() {
        JSONObject object = JSONHandler.parse(jsonString);
        JSONArray phoneArray = object.getJSONArray("phoneNumbers");

        assertEquals(true, object.get("phoneNumbers") instanceof JSONArray);
        assertEquals(false, object.get("phoneNumbers") instanceof JSONObject);
        assertEquals(false, object.get("phoneNumbers") instanceof String);
        assertEquals(false, phoneArray.isEmpty());

        assertEquals("home", phoneArray.getJSONObject(0).get("type"));
        assertEquals("212 555-1234", phoneArray.getJSONObject(0).get("number"));
        assertEquals("fax", phoneArray.getJSONObject(1).get("type"));
        assertEquals("646 555-4567", phoneArray.getJSONObject(1).get("number"));
    }

    @Test
    public void parser_can_handle_json_object() {
        JSONObject object = JSONHandler.parse(jsonString);
        JSONObject addressObject = object.getJSONObject("address");

        assertEquals(false, object.get("address") instanceof JSONArray);
        assertEquals(true, object.get("address") instanceof JSONObject);
        assertEquals(false, object.get("address") instanceof String);
        assertEquals(false, addressObject.isEmpty());

        assertEquals("21 2nd Street", addressObject.get("streetAddress"));
        assertEquals("New York", addressObject.get("city"));
        assertEquals("NY", addressObject.get("state"));
        assertEquals(10021, addressObject.get("postalCode"));
    }

    @Test
    public void parser_can_query_for_nested_values() {
        JSONObject object = JSONHandler.parse(jsonString);

        assertEquals("21 2nd Street", object.query("/address/streetAddress"));
        assertEquals("fax", object.query("/phoneNumbers/1/type"));
    }

    @Test
    public void update_can_replace_with_a_string_value() {
        JSONObject object = JSONHandler.parse(jsonString);
        JSONObject newJsonObject = JSONHandler.replace(object, "firstName", "Mary");

        assertEquals("Mary", newJsonObject.get("firstName"));
    }

    @Test
    public void update_can_replace_with_a_object() {
        JSONObject object = JSONHandler.parse(jsonString);
        JSONObject newJsonObject = JSONHandler.replace(object, "firstName", "{ \"name\": \"Digestive\" }");

        assertEquals(true, newJsonObject.get("firstName") instanceof JSONObject);
        assertEquals("Digestive", newJsonObject.getJSONObject("firstName").get("name"));
    }

    @Test
    public void update_can_replace_a_string_with_a_array() {
        JSONObject object = JSONHandler.parse(jsonString);
        JSONObject newJsonObject = JSONHandler.replace(object, "firstName", "[\n" +
                "    { \"name\": \"Digestive\" },\n" +
                "    { \"name\": \"Choco Leibniz\" }\n" +
                "  ]");

        assertEquals(true, newJsonObject.get("firstName") instanceof JSONArray);
        assertEquals("Choco Leibniz", newJsonObject.getJSONArray("firstName").getJSONObject(1).get("name"));
    }

    @Test
    public void update_can_replace_a_string_with_a_nested_array() {
        JSONObject object = JSONHandler.parse(jsonString);
        JSONObject newJsonObject = JSONHandler.replace(object, "/address/streetAddress", "[\n" +
                "    { \"name\": \"Digestive\" },\n" +
                "    { \"name\": \"Choco Leibniz\" }\n" +
                "  ]");

        assertEquals(true, newJsonObject.query("/address/streetAddress") instanceof JSONArray);
        assertEquals("Choco Leibniz", newJsonObject.query("/address/streetAddress/1/name"));
        assertEquals("Choco Leibniz",
                newJsonObject.getJSONObject("address").getJSONArray("streetAddress").getJSONObject(1).get("name"));
    }

    @Test
    public void update_can_replace_a_nested_object_with_a_string() {
        JSONObject object = JSONHandler.parse(jsonString);
        JSONObject newJsonObject = JSONHandler.replace(object, "/address", "hello");

        System.err.println(newJsonObject);
        assertEquals("hello", newJsonObject.query("/address"));
    }

    @Test
    public void update_can_replace_a_nested_array_object_with_a_string() {
        JSONObject object = JSONHandler.parse(jsonString);
        JSONObject newJsonObject = JSONHandler.replace(object, "/phoneNumbers/0", "hello");

        assertEquals("hello", newJsonObject.query("/phoneNumbers/0"));
    }

    @Test
    public void update_can_replace_a_value_in_a_nested_array() {
        JSONObject object = JSONHandler.parse(jsonString);
        JSONObject newJsonObject = JSONHandler.replace(object, "/phoneNumbers/1/type", "mobile");

        assertEquals(true, newJsonObject.query("/phoneNumbers/1/type") instanceof String);
        assertEquals("mobile", newJsonObject.query("/phoneNumbers/1/type"));
        assertEquals("mobile",
                newJsonObject.getJSONArray("phoneNumbers").getJSONObject(1).get("type"));
    }

    @Test
    public void add_can_add_new_a_string_value() {
        JSONObject object = JSONHandler.parse(jsonString);
        JSONObject newJsonObject = JSONHandler.add(object, "school", "UCLA");

        assertEquals("UCLA", newJsonObject.get("school"));
    }

    @Test
    public void add_can_add_new_object() {
        JSONObject object = JSONHandler.parse(jsonString);
        JSONObject newJsonObject = JSONHandler.add(object, "school", "{ \"name\": \"Elementary\" }");

        assertEquals(true, newJsonObject.get("school") instanceof JSONObject);
        assertEquals("Elementary", newJsonObject.getJSONObject("school").get("name"));
    }

    @Test
    public void add_can_add_new_array() {
        JSONObject object = JSONHandler.parse(jsonString);
        JSONObject newJsonObject = JSONHandler.add(object, "school", "[\n" +
                "    { \"name\": \"UC Berkeley\" },\n" +
                "    { \"name\": \"MIT\" }\n" +
                "  ]");

        assertEquals(true, newJsonObject.get("school") instanceof JSONArray);
        assertEquals("MIT", newJsonObject.getJSONArray("school").getJSONObject(1).get("name"));
    }

    @Test
    public void add_can_add_string_to_nested_object() {
        JSONObject object = JSONHandler.parse(jsonString);
        JSONObject newJsonObject = JSONHandler.add(object, "/address/nickname", "HOME");

        assertEquals("HOME", newJsonObject.query("/address/nickname"));
    }

    @Test
    public void add_can_add_string_to_nested_array() {
        JSONObject object = JSONHandler.parse(jsonString);
        JSONObject newJsonObject = JSONHandler.add(object, "/phoneNumbers/0/nickname", "HOME");

        assertEquals("HOME", newJsonObject.query("/phoneNumbers/0/nickname"));
    }

    @Test
    public void add_can_add_array_to_nested_array() {
        JSONObject object = JSONHandler.parse(jsonString);
        JSONObject newJsonObject = JSONHandler.add(object, "/phoneNumbers/0/nickname", "[\n" +
                "    { \"name\": \"UC Berkeley\" },\n" +
                "    { \"name\": \"MIT\" }\n" +
                "  ]");

        assertEquals("UC Berkeley", newJsonObject.query("/phoneNumbers/0/nickname/0/name"));
    }

    @Test
    public void removes_can_delete_a_string_value() {
        JSONObject object = JSONHandler.parse(jsonString);
        JSONObject newJsonObject = JSONHandler.add(object, "school", "MIT");
        JSONObject afterDeletion = JSONHandler.remove(newJsonObject, "school");

        assertThrows(JSONException.class, () -> afterDeletion.get("school"));
    }

    @Test
    public void removes_can_delete_a_string_array() {
        JSONObject object = JSONHandler.parse(jsonString);
        JSONObject newJsonObject = JSONHandler.add(object, "school", "[\n" +
                "    { \"name\": \"UC Berkeley\" },\n" +
                "    { \"name\": \"MIT\" }\n" +
                "  ]");
        JSONObject afterDeletion = JSONHandler.remove(newJsonObject, "school");

        assertThrows(JSONException.class, () -> afterDeletion.get("school"));
    }

    @Test
    public void removes_can_delete_from_array() {
        String jsonString = "{\n" +
                "  \"biscuits\": [\n" +
                "    { \"name\": \"Digestive\" },\n" +
                "    { \"name\": \"Choco Leibniz\" }\n" +
                "  ]\n" +
                "}";
        JSONObject jsonObject = JSONHandler.parse(jsonString);
        JSONObject newJsonObject = JSONHandler.run("remove", "/biscuits/1", jsonObject);

        assertThrows(JSONException.class, () -> newJsonObject.query("/biscuits/1"));
    }

    @Test
    public void removes_can_delete_from_nested_object() {
        JSONObject jsonObject = JSONHandler.parse(jsonString);
        JSONObject newJsonObject = JSONHandler.run("remove", "/address/streetAddress", jsonObject);

        assertEquals(null, newJsonObject.query("/address/streetAddress"));
    }

    @Test
    public void handler_can_determine_correct_operation__Replace() {
        String jsonString = "{\n" +
                "  \"biscuits\": [\n" +
                "    { \"name\": \"Digestive\" },\n" +
                "    { \"name\": \"Choco Leibniz\" }\n" +
                "  ]\n" +
                "}";
        JSONObject jsonObject = JSONHandler.parse(jsonString);
        JSONObject newJsonObject = JSONHandler.run("replace", "/biscuits/1/name", "Tea Biscuits", jsonObject);

        assertEquals("Tea Biscuits", newJsonObject.getJSONArray("biscuits").getJSONObject(1).get("name"));
        assertEquals("Tea Biscuits", newJsonObject.query("/biscuits/1/name"));
    }

    @Test
    public void handler_can_determine_correct_operation__Remove_from_array() {
        String jsonString = "{\n" +
                "  \"biscuits\": [\n" +
                "    { \"name\": \"Digestive\" },\n" +
                "    { \"name\": \"Choco Leibniz\" }\n" +
                "  ]\n" +
                "}";
        JSONObject jsonObject = JSONHandler.parse(jsonString);
        JSONObject newJsonObject = JSONHandler.run("remove", "/biscuits/1", jsonObject);

        assertThrows(JSONException.class, () -> newJsonObject.query("/biscuits/1"));
    }

    @Test
    public void handler_can_determine_correct_operation__Add() {
        String jsonString = "{\n" +
                "  \"biscuits\": [\n" +
                "    { \"name\": \"Digestive\" },\n" +
                "    { \"name\": \"Choco Leibniz\" }\n" +
                "  ]\n" +
                "}";
        JSONObject jsonObject = JSONHandler.parse(jsonString);
        JSONObject newJsonObject = JSONHandler.run("add", "/biscuits/1/type", "dry", jsonObject);

        assertEquals("dry", newJsonObject.query("/biscuits/1/type"));
    }
}
