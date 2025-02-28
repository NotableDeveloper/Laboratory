package external_request.requests.simple;

import external_request.requests.DefaultRequest;
import external_request.requests.RequestType;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SimpleHttpRequest extends DefaultRequest {
    private String url;
    private String httpMethod;
    private Map<String, String> requestBody;

    private SimpleHttpRequest(Builder builder) {
        /*
            Builder로 만들어진 Reqeust Body를 JSON 형태로 만들어서
            DefaultRequest에 저장.
         */
        super(RequestType.SIMPLE_HTTP,
                builder.requestBody.entrySet().stream()
                        .map(entry -> "\"" + entry.getKey() + "\":\"" + entry.getValue() + "\"")
                        .collect(Collectors.joining(",", "{", "}"))
                );

        this.requestBody = builder.requestBody;
        this.url = builder.url;
        this.httpMethod = builder.httpMethod;

        builder.requestBody = new HashMap<>();
    }

    public String getUrl() {
        return url;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public Map<String, String> getRequestBody() {
        return requestBody;
    }

    public static class Builder {
        private String url;
        private String httpMethod;
        private Map<String, String> requestBody = new HashMap<>();

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder httpMethod(String httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        public Builder requestBody(String key, String value) {
            this.requestBody.put(key, value);
            return this;
        }

        public SimpleHttpRequest build() {
            return new SimpleHttpRequest(this);
        }
    }
}
