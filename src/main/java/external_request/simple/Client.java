package external_request.simple;

import external_request.simple.requests.SimpleHttpPostRequester;
import external_request.simple.requests.SimpleRequestBody;

public class Client {
    public static void main(String[] args) {
        SimpleRequestBody requestBody = SimpleRequestBody.builder()
                .userId("Hello")
                .userName("World")
                .userNumber("5001")
                .build();

        SimpleHttpPostRequester requester = new SimpleHttpPostRequester(requestBody);

        SimpleRequestWorker worker = SimpleRequestWorker.getWorker();
        worker.requestAndReceive(requester);
    }
}
