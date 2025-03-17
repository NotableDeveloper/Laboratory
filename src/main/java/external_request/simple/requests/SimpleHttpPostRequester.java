package external_request.simple.requests;

import external_request.base.requests.DefaultRequest;
import external_request.base.requests.ProtocolType;
import external_request.base.requests.RequestType;
import external_request.simple.responses.SimpleRequestResult;
import lombok.ToString;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import java.net.URL;

public class SimpleHttpPostRequester extends DefaultRequest {
    private CloseableHttpClient httpClient;
    private HttpPost httpPost;
    private SimpleRequestBody requestBody;

    public SimpleHttpPostRequester(SimpleRequestBody requestBody) {
        super(RequestType.SIMPLE,
                ProtocolType.HTTP,
                System.getenv("ADDRESS"),
                Integer.parseInt(System.getenv("PORT")));

        httpClient = HttpClients.createDefault();

        try {
            URL requestUrl = new URL(
                    this.getProtocolType().name(),
                    this.getTargetAddress(),
                    this.getTargetPort(),
                    System.getenv("RESOURCE")
            );

            this.requestBody = requestBody;
            httpPost = new HttpPost(requestUrl.toURI());
            setHttpHeaders();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setHttpHeaders(){
        this.httpPost.setHeader("Connction-Timeout", "3000");
        this.httpPost.setHeader("Read-Timeout", "3000");
        this.httpPost.setHeader("Content-Type", "application/json");
    }

    public SimpleRequestResult request(){
        CloseableHttpResponse httpResponse;
        StringEntity jsonEntity = new StringEntity(this.requestBody.toString(), "UTF-8");
        jsonEntity.setContentType("application/json");
        this.httpPost.setEntity(jsonEntity);

        try {
            httpResponse = this.httpClient.execute(this.httpPost);
            this.httpClient.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return SimpleRequestResult.createFromHttpResponse(
                this.getRequestId(),
                this.getTargetAddress(),
                this.getTargetPort(),
                httpResponse);
    }

}
