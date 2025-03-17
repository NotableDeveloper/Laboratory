package external_request.simple.responses;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import external_request.base.responses.DefaultResponse;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class SimpleRequestResult extends DefaultResponse {
    private String code;
    private String message;

    protected SimpleRequestResult(String requestId, String sourceAddress, int sourcePort) {
        super(requestId, sourceAddress, sourcePort);
    }

    public static SimpleRequestResult createFromHttpResponse(String requestId,
                                                             String sourceAddress,
                                                             int sourcePort,
                                                             HttpResponse httpResponse) {
        SimpleRequestResult result = new SimpleRequestResult(requestId, sourceAddress, sourcePort);

        try {
            String responseBody = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
            JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();


            Map<String, Field> fieldMap = new HashMap<>();
            for (Field field : SimpleRequestResult.class.getDeclaredFields()) {
                fieldMap.put(field.getName().toLowerCase(), field);
            }

            for (String jsonKey : jsonObject.keySet()) {
                String lowerCaseKey = jsonKey.toLowerCase();

                if (fieldMap.containsKey(lowerCaseKey)) {
                    try {
                        Field field = fieldMap.get(lowerCaseKey);
                        field.setAccessible(true);
                        field.set(result, jsonObject.get(jsonKey).getAsString());
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Failed to set field value: " + jsonKey, e);
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Error parsing HTTP response", e);
        }

        return result;
    }
}
