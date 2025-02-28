package external_request.requests;

public class DefaultRequest {
    RequestType requestType;
    Object rawData;

    protected DefaultRequest(RequestType requestType, Object rawData) {
        this.requestType = requestType;
        this.rawData = rawData;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public Object getRawData() {
        return rawData;
    }
}
