package json_handler;

import org.json.JSONArray;
import org.json.JSONObject;

public class JSONHandler {
    public static JSONObject parse(String jsonString) {
        return new JSONObject(jsonString);
    }

    public static JSONArray parseArray(String jsonString) {
        return new JSONArray(jsonString);
    }

    public static JSONObject run(String operation, String queryPath, JSONObject jsonObject) {
        return run(operation, queryPath, "", jsonObject);
    }

    public static JSONObject run(String operation, String queryPath, String value, JSONObject jsonObject) {
        JSONObject newObject = jsonObject;
        switch (operation) {
            case "replace":
                newObject = replace(jsonObject, queryPath, value);
                break;
            case "remove":
                newObject = remove(jsonObject, queryPath);
                break;
            case "add":
                newObject = add(jsonObject, queryPath, value);
                break;
        }
        return newObject;
    }

    public static JSONObject replace(JSONObject jsonObject, String key, String value) {
        if (key.startsWith("/")) {
            Integer lastKeyIdx = key.lastIndexOf("/");
            String newKey = key.substring(lastKeyIdx + 1);
            String queryPath = key.substring(0, lastKeyIdx);

            if (jsonObject.query(queryPath) instanceof  JSONArray) {
                JSONArray array = (JSONArray) jsonObject.query(queryPath);
                updateSingleValue(array, Integer.parseInt(newKey), value);
            } else {
                JSONObject object = (JSONObject) jsonObject.query(queryPath);
                updateSingleValue(object, newKey, value);
            }

            return jsonObject;
        } else {
            return updateSingleValue(jsonObject, key, value);
        }
    }

    private static JSONObject updateSingleValue(JSONObject jsonObject, String key, String value) {
        if (value.startsWith("[")) {
            jsonObject.put(key, parseArray(value));
        } else if (value.startsWith("{")) {
            jsonObject.put(key, parse(value));
        } else {
            jsonObject.put(key, value);
        }

        return jsonObject;
    }

    private static JSONArray updateSingleValue(JSONArray jsonArray, int key, String value) {
        if (value.startsWith("[")) {
            jsonArray.put(key, parseArray(value));
        } else if (value.startsWith("{")) {
            jsonArray.put(key, parse(value));
        } else {
            jsonArray.put(key,value);
        }

        return jsonArray;
    }

    public static JSONObject add(JSONObject jsonObject, String key, String value) {
        return replace(jsonObject, key, value);
    }

    public static JSONObject remove(JSONObject jsonObject, String key) {
        if (key.startsWith("/")) {
            Integer lastKeyIdx = key.lastIndexOf("/");
            String newKey = key.substring(lastKeyIdx + 1);
            String queryPath = key.substring(0, lastKeyIdx);
            if (jsonObject.query(queryPath) instanceof  JSONArray) {
                JSONArray array = (JSONArray) jsonObject.query(queryPath);
                array.remove(Integer.parseInt(newKey));
            } else {
                JSONObject object = (JSONObject) jsonObject.query(queryPath);
                object.remove(newKey);
            }

            return jsonObject;
        } else {
            jsonObject.remove(key);
        }
        return jsonObject;
    }
}
