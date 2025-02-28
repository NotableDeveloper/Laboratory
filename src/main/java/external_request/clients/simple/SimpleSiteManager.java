package external_request.clients.simple;

import external_request.abstracts.SiteManager;
import external_request.abstracts.SiteWorker;
import external_request.requests.DefaultRequest;
import external_request.requests.RequestType;
import external_request.responses.DefaultResponse;

public class SimpleSiteManager extends SiteManager {
    public SimpleSiteManager() {
        addWorker(RequestType.SIMPLE_HTTP, new SimpleHttpRequestWorker());
    }

    @Override
    public DefaultResponse request(DefaultRequest request) {
        SiteWorker worker = getWorker(request.getRequestType());
        return worker.requestAndReceive(request);
    }
}
