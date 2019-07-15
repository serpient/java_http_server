package json_handler;

import http_server.OperationResult;
import http_standards.StatusCode;
import org.json.JSONArray;
import org.json.JSONObject;

public class JSONPatcher {
    public static OperationResult runPatchDocument(String patchDocument, String jsonFile) {
        JSONObject updatedJSONObject = JSONHandler.parse(jsonFile);

        OperationResult patchCheck = validPatchRequest(patchDocument, updatedJSONObject);
        if (patchCheck.valid()) {
            JSONArray patchInstructions = JSONHandler.parseArray(patchDocument);

            for (int i = 0; i < patchInstructions.length(); i++) {
                JSONObject instructions = patchInstructions.getJSONObject(i);

                String operation = instructions.getString("op");
                String path = instructions.getString("path");
                String value = instructions.has("value") ? instructions.getString("value") : "";
                updatedJSONObject = JSONHandler.run(operation, path, value, updatedJSONObject);
            }

            return new OperationResult(true, StatusCode.noContent, updatedJSONObject.toString());
        }
        return patchCheck;
    }

    public static OperationResult validPatchRequest(String patchInstructions, JSONObject jsonDocument) {
        if (!patchInstructions.startsWith("[")) {
            System.err.println("Invalid JSON patch document. Terminating update request.");
            return new OperationResult(false, StatusCode.badRequest);
        }

        JSONArray patchDocument = JSONHandler.parseArray(patchInstructions);

        for (int i = 0; i < patchDocument.length(); i++) {
            JSONObject instructions = patchDocument.getJSONObject(i);
            if (!instructions.keySet().contains("op") && !instructions.keySet().contains("path")) {
                System.err.println("Invalid JSON patch document. Terminating update request.");
                return new OperationResult(false, StatusCode.badRequest);
            }

            String operation = instructions.getString("op");
            String path = instructions.getString("path");

            if (!operation.equals("replace") && !operation.equals("add") && !operation.equals("remove")) {
                System.err.println("JSON Patch Operation is not supported. Terminating update request.");
                return new OperationResult(false, StatusCode.badRequest);
            }

            if (jsonDocument.query(path) == null && !operation.equals("add")) {
                System.err.println("JSON Patch Document path does not exist. Terminating update request.");
                return new OperationResult(false, StatusCode.conflict);
            }
        }
        return new OperationResult(true, StatusCode.noContent);
    }
}
