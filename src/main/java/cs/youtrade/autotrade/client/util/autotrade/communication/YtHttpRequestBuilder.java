package cs.youtrade.autotrade.client.util.autotrade.communication;

import com.google.gson.Gson;
import cs.youtrade.autotrade.client.util.autotrade.communication.util.YtRestClientException;
import cs.youtrade.autotrade.client.util.gson.GsonConfig;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.net.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class YtHttpRequestBuilder {
    private static final Gson gson = GsonConfig.createGson();

    private String baseUrl;
    private HttpMethod method;
    private String endpoint;
    private Map<String, String> headers;
    private Map<String, String> params;
    private Object body;

    public ClassicHttpRequest build() {
        if (baseUrl == null)
            throw new YtRestClientException("baseUrl not set");

        if (method == null)
            throw new YtRestClientException("method not set");

        if (endpoint == null)
            throw new YtRestClientException("endpoint not set");

        try {
            return buildRequest();
        } catch (URISyntaxException e) {
            throw new YtRestClientException("uri is broken", e);
        }
    }

    private ClassicHttpRequest buildRequest() throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(baseUrl)
                .appendPath(endpoint);
        if (params != null)
            params.forEach(uriBuilder::addParameter);

        return buildBody(method, uriBuilder.build(), body);
    }

    private ClassicHttpRequest buildBody(HttpMethod method, URI uri, Object body) {
        switch (method) {
            case POST:
                return buildBody(method, new HttpPost(uri), body);
            case PUT:
                return buildBody(method, new HttpPut(uri), body);
            case PATCH:
                return buildBody(method, new HttpPatch(uri), body);
            case GET:
                return buildBody(method, new HttpGet(uri), body);
            case HEAD:
                return buildBody(method, new HttpHead(uri), body);
            case DELETE:
                return buildBody(method, new HttpDelete(uri), body);
            case OPTIONS:
                return buildBody(method, new HttpOptions(uri), body);
            case TRACE:
                return buildBody(method, new HttpTrace(uri), body);
            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }
    }

    private ClassicHttpRequest buildBody(HttpMethod method, ClassicHttpRequest request, Object body) {
        if (method.allowsBody())
            addJsonBody(request, body);
        else
            validateNoBody(method, body);

        return buildHeader(request);
    }

    private ClassicHttpRequest buildHeader(ClassicHttpRequest request) {
        if (headers != null)
            headers.forEach(request::addHeader);

        return request;
    }

    private void addJsonBody(ClassicHttpRequest request, Object body) {
        if (body != null) {
            String jsonBody = gson.toJson(body);
            request.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));
        }
    }

    private void validateNoBody(HttpMethod method, Object body) {
        if (body != null) {
            throw new YtRestClientException(
                    String.format("HTTP method %s does not support request body. Provided body: %s", method, body));
        }
    }

    public YtHttpRequestBuilder setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public YtHttpRequestBuilder setMethod(HttpMethod method) {
        this.method = method;
        return this;
    }

    public YtHttpRequestBuilder setEndpoint(String endpoint) {
        this.endpoint = endpoint;
        return this;
    }

    public YtHttpRequestBuilder setHeaders(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public YtHttpRequestBuilder setParams(Map<String, String> params) {
        this.params = params;
        return this;
    }

    public YtHttpRequestBuilder setBody(Object body) {
        this.body = body;
        return this;
    }
}
