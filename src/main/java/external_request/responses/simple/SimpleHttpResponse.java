package external_request.responses.simple;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import external_request.responses.DefaultResponse;

public class SimpleHttpResponse extends DefaultResponse {
    private String code;
    private String message;
    private String vcData;

    public SimpleHttpResponse(Object rawData) {
        super(rawData);

        JsonObject jsonObject = JsonParser.parseString(rawData.toString()).getAsJsonObject();

        code = jsonObject.get("RESULT").getAsString();
        message = jsonObject.get("MSG").getAsString();
        vcData = jsonObject.get("VCDATA").getAsString();
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getVcData() {
        return vcData;
    }

    @Override
    public String toString() {
        return "SimpleHttpResponse { " +
                "RESULT = '" + code + '\'' +
                ", MSG = '" + message + '\'' +
                ", VCDATA = '" + vcData + '\'' +
                " }";
    }
}
