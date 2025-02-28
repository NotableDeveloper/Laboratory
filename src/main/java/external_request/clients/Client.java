package external_request.clients;

import external_request.simple.SimpleSiteManager;
import external_request.simple.SimpleHttpRequest;

public class Client {
    public static void main(String[] args) {
        SimpleSiteManager manager = new SimpleSiteManager();

        SimpleHttpRequest simpleHttpRequest = new SimpleHttpRequest.Builder()
                .url(System.getenv("HTTP_URL"))
                .httpMethod("POST")
                .requestBody("Number", "123456")
                .requestBody("ID", "ABCD")
                .requestBody("TYPE", "I")
                .build();

        System.out.println(manager.request(simpleHttpRequest));
    }
}
