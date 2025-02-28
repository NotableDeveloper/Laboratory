package external_request.abstracts;

import external_request.requests.DefaultRequest;
import external_request.requests.RequestType;
import external_request.responses.DefaultResponse;

import java.util.HashMap;
import java.util.Map;

public abstract class SiteManager {
    private Map<RequestType, SiteWorker> workers = new HashMap<>();

    protected SiteWorker getWorker(RequestType requestType) {
        return workers.get(requestType);
    }

    protected void addWorker(RequestType type, SiteWorker worker) {
        workers.put(type, worker);
    }

    public abstract DefaultResponse request(DefaultRequest request);
}
