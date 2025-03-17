package external_request.simple;

import external_request.base.abstracts.SiteWorker;
import external_request.base.requests.DefaultRequest;
import external_request.base.responses.DefaultResponse;
import external_request.simple.requests.SimpleHttpPostRequester;
import external_request.simple.responses.SimpleRequestResult;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SimpleRequestWorker implements SiteWorker {
    private ExecutorService workerPool;

    private SimpleRequestWorker() {
        this.workerPool = Executors.newFixedThreadPool(3);
    }

    private static class SingletonHolder {
        private static final SimpleRequestWorker INSTANCE = new SimpleRequestWorker();
    }

    public static SimpleRequestWorker getWorker() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public DefaultResponse requestAndReceive(DefaultRequest defaultRequest) {
        SimpleHttpPostRequester requester = (SimpleHttpPostRequester) defaultRequest;
        Future<SimpleRequestResult> futureResponse = workerPool.submit(requester::request);

        try {
            return futureResponse.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
