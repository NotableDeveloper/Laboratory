package external_request.abstracts;

import external_request.requests.DefaultRequest;
import external_request.responses.DefaultResponse;

public interface SiteWorker {
    default void requestWithoutResponse(DefaultRequest defaultRequest) { }
    public DefaultResponse requestAndReceive(DefaultRequest defaultRequest);
}