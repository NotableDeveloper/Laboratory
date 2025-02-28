package external_request.clients.simple;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import external_request.abstracts.SiteWorker;
import external_request.requests.DefaultRequest;
import external_request.requests.simple.SimpleHttpRequest;
import external_request.responses.DefaultResponse;
import external_request.responses.simple.SimpleHttpResponse;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SimpleHttpRequestWorker implements SiteWorker {
    private ExecutorService workerPool;

    public SimpleHttpRequestWorker(){
        this.workerPool = Executors.newFixedThreadPool(3);
    }

    @Override
    public DefaultResponse requestAndReceive(DefaultRequest defaultRequest) {
        SimpleHttpRequest request = (SimpleHttpRequest) defaultRequest;

        Future<DefaultResponse> future = workerPool.submit(() -> {
            HttpURLConnection connection = getConnection(request.getHttpMethod(), request.getUrl());

            try {
                String requestBody = request.getRawData().toString();
                request(connection, requestBody);
                String response = receive(connection);
                return new SimpleHttpResponse(response);
            } finally {
                connection.disconnect();
            }
        });

        try {
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private HttpURLConnection getConnection(String requestMethod, String requestUrl) {
        HttpURLConnection connection = null;

        try {
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
