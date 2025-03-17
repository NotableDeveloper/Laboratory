package external_request.base.abstracts;

import external_request.base.requests.DefaultRequest;
import external_request.base.responses.DefaultResponse;

public interface SiteWorker {
    default void requestWithoutResponse(DefaultRequest defaultRequest) { }
    public DefaultResponse requestAndReceive(DefaultRequest defaultRequest);
}