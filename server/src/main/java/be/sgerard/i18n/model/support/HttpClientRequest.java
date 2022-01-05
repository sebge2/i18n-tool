package be.sgerard.i18n.model.support;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * HTTP request.
 *
 * @author Sebastien Gerard
 */
@Getter
public class HttpClientRequest<T> {

    public static <T> HttpClientRequest.Builder<T> newBuilder(HttpMethod method, String url, Class<T> responseType) {
        return new Builder<>(method, url, responseType);
    }

    private HttpClientRequest(Builder<T> builder) {
        this.url = builder.url;
        this.method = builder.method;
        this.responseType = builder.responseType;
        this.parameters = builder.parameters;
        this.headers = builder.headers;
        this.body = builder.body;
    }

    /**
     * The HTTP URL.
     */
    private final String url;

    /**
     * The HTTP method.
     */
    private final HttpMethod method;

    /**
     * The response type.
     */
    private final Class<T> responseType;

    /**
     * The query parameters.
     */
    private final Map<String, String> parameters;

    /**
     * The query headers.
     */
    private final Map<String, String> headers;

    /**
     * The HTTP body.
     */
    private final Object body;

    /**
     * @see #body
     */
    public Optional<Object> getBody() {
        return Optional.ofNullable(body);
    }

    /**
     * Builder of {@link HttpClientRequest request}.
     */
    @Setter
    @Accessors(prefix = "", chain = true)
    public static final class Builder<T> {

        private final HttpMethod method;
        private final String url;
        private final Class<T> responseType;

        private final Map<String, String> parameters = new HashMap<>();
        private final Map<String, String> headers = new HashMap<>();
        private Object body;

        private Builder(HttpMethod method, String url, Class<T> responseType) {
            this.method = method;
            this.url = url;
            this.responseType = responseType;
        }

        public Builder<T> parameters(Map<String, String> parameters) {
            this.parameters.putAll(parameters);
            return this;
        }

        public Builder<T> headers(Map<String, String> headers) {
            this.headers.putAll(headers);
            return this;
        }

        public Builder<T> body(Object body) {
            this.body = body;
            return this;
        }

        public HttpClientRequest<T> build() {
            return new HttpClientRequest<>(this);
        }
    }
}
