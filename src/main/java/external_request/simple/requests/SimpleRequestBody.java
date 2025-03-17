package external_request.simple.requests;

import com.google.gson.Gson;
import lombok.Builder;

@Builder
public class SimpleRequestBody {
    private String userId;
    private String userName;
    private String userNumber;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
