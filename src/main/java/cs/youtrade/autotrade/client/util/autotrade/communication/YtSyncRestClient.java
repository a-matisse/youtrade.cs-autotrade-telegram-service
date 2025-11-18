package cs.youtrade.autotrade.client.util.autotrade.communication;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import cs.youtrade.autotrade.client.util.autotrade.communication.util.YtRestClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@Log
@RequiredArgsConstructor
public class YtSyncRestClient {
    private static final Gson gson = new Gson();

    private final String baseUrl;
    private final CloseableHttpClient httpClient;

    public YtSyncRestClient(String baseUrl) {
        this(baseUrl, HttpClients.createDefault());
    }

    public void fetchFromApi(
            HttpMethod method, String endpoint) {
        fetchFromApi(
                method, endpoint, Collections.emptyMap(), Collections.emptyMap(), null, new TypeToken<>(Void.class) {
                });
    }

    public <T> RestAnswer<T> fetchFromApi(
            HttpMethod method, String endpoint, TypeToken<T> type) {
        return fetchFromApi(
                method, endpoint, Collections.emptyMap(), Collections.emptyMap(), null, type);
    }

    public <T> RestAnswer<T> fetchFromApi(
            HttpMethod method, String endpoint, Object body, TypeToken<T> type) {
        return fetchFromApi(
                method, endpoint, Collections.emptyMap(), Collections.emptyMap(), body, type);
    }

    public <T> RestAnswer<T> fetchFromApi(
            HttpMethod method, String endpoint, Map<String, String> headers, Object body, TypeToken<T> type) {
        return fetchFromApi(
                method, endpoint, headers, Collections.emptyMap(), body, type);
    }

    public <T> RestAnswer<T> fetchFromApi(HttpMethod method, String endpoint, Map<String, String> headers, Map<String, String> params, TypeToken<T> type) {
        return fetchFromApi(
                method, endpoint, headers, params, null, type);
    }

    public <T> RestAnswer<T> fetchFromApi(HttpMethod method, String endpoint, Map<String, String> headers, Map<String, String> params, Object body, TypeToken<T> type) {
        try {
            ClassicHttpRequest request = new YtHttpRequestBuilder()
                    .setMethod(method)
                    .setBaseUrl(baseUrl)
                    .setEndpoint(endpoint)
                    .setHeaders(headers)
                    .setParams(params)
                    .setBody(body)
                    .build();
            return execute(request, type);
        } catch (IOException e) {
            throw new YtRestClientException(String.format("Couldn't connect to: [%s%s]", baseUrl, endpoint), e);
        }
    }

    private <T> RestAnswer<T> execute(ClassicHttpRequest request, TypeToken<T> type) throws IOException {
        return httpClient.execute(request, response -> {
            HttpEntity entity = response.getEntity();
            int statusCode = response.getCode();
            if (!(statusCode >= 200 && statusCode < 300))
                return new RestAnswer<>(statusCode);

            String responseBody = EntityUtils.toString(entity);
            T ans = gson.fromJson(responseBody, type);
            return new RestAnswer<>(statusCode, ans);
        });
    }
}
