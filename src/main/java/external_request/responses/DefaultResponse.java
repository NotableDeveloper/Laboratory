package external_request.responses;

public class DefaultResponse {
    protected Object content;

    public DefaultResponse(Object rawData) {
        this.content = rawData;
    }
}
