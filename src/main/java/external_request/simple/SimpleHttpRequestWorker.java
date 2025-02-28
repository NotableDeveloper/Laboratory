package external_request.simple;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import external_request.abstracts.SiteWorker;
import external_request.requests.DefaultRequest;
import external_request.responses.DefaultResponse;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleHttpRequestWorker implements SiteWorker {
    private ExecutorService workerPool;

    public SimpleHttpRequestWorker(){
        this.workerPool = Executors.newFixedThreadPool(3);
    }

    @Override
    public DefaultResponse requestAndReceive(DefaultRequest defaultRequest) {
        SimpleHttpRequest request = (SimpleHttpRequest) defaultRequest;

        try {
            return workerPool.submit(() -> requestTask(request)).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private DefaultResponse requestTask(SimpleHttpRequest request) {
        HttpURLConnection connection = getConnection(request.getHttpMethod(), request.getUrl());

        try {
            String requestBody = request.getRawData().toString();
            request(connection, requestBody);
            String response = receive(connection);
            return new SimpleHttpResponse(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private HttpURLConnection getConnection(String requestMethod, String requestUrl) {
        HttpURLConnection connection = null;

        try {
            /*
                To do :
                Connection Timeout, Retry 관련 설정도 추가하기
             */
            URL url = new URL(requestUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(requestMethod);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
        } catch (Exception e) {
            new RuntimeException(e);
        }

        return connection;
    }

    private void request(HttpURLConnection connection, String requestBody) {
        try {
            OutputStream outputStream = connection.getOutputStream();
            byte[] outputBytes = requestBody.getBytes("UTF-8");
            outputStream.write(outputBytes);
            outputStream.flush();
        } catch (Exception e) {
            new RuntimeException(e);
        }
    }

    private String receive(HttpURLConnection connection) {
        try {
            InputStream inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder responseBody = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                responseBody.append(line);
            }

            bufferedReader.close();

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonElement jsonElement = JsonParser.parseString(responseBody.toString());
            return gson.toJson(jsonElement);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
