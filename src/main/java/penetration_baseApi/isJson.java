package penetration_baseApi;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class isJson {
    public static boolean validate(String jsonStr) {
        JsonElement jsonElement;
        try {
            jsonElement = new JsonParser().parse(jsonStr);
        } catch (Exception e) {
            return false;
        }
        if (jsonElement == null) {
            return false;
        }
        if (!jsonElement.isJsonObject()) {
            return false;
        }
        return true;
    }
}
