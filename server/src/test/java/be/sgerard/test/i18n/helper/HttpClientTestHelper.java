package be.sgerard.test.i18n.helper;

import be.sgerard.i18n.model.support.HttpClientRequest;
import be.sgerard.i18n.service.support.HttpClient;
import org.mockito.ArgumentMatcher;
import org.mockito.internal.util.MockUtil;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.when;

@Component
public class HttpClientTestHelper {

    private final HttpClient httpClient;

    public HttpClientTestHelper(HttpClient httpClient) {
        this.httpClient = httpClient;

        clear();
    }

    @SuppressWarnings("UnusedReturnValue")
    public HttpClientTestHelper clear() {
        MockUtil.resetMock(httpClient);

        return this;
    }

    public MockRequestStep mockRequest() {
        return new MockRequestStep();
    }

    public class MockRequestStep {

        private final RequestArgumentMatcher matcher = new RequestArgumentMatcher();

        public MockRequestStep methodEqualsTo(HttpMethod method) {
            return addPredicate(request -> Objects.equals(method, request.getMethod()));
        }

        public MockRequestStep urlMatchingExactly(String url) {
            return addPredicate(request -> Objects.equals(url, request.getUrl()));
        }

        public MockRequestStep parametersContainingExactly(Map<String, String> parameters) {
            return addPredicate(request -> Objects.equals(parameters, request.getParameters()));
        }

        public MockRequestStep headersContainingExactly(Map<String, String> headers) {
            return addPredicate(request -> Objects.equals(headers, request.getHeaders()));
        }

        public MockRequestStep bodyMatchingExactly(String body) {
            return addPredicate(request -> Objects.equals(body, request.getBody().orElse(null)));
        }

        @SuppressWarnings("UnusedReturnValue")
        public HttpClientTestHelper answerValue(Object value) {
            return answerWith(response -> value);
        }

        @SuppressWarnings("unchecked")
        public HttpClientTestHelper answerWith(Function<HttpClientRequest<Object>, Object> provider) {
            when(httpClient.execute(argThat(matcher)))
                    .thenAnswer(invocationOnMock ->
                            Mono.just(provider.apply(invocationOnMock.getArgument(0, HttpClientRequest.class)))
                    );

            return HttpClientTestHelper.this;
        }

        private MockRequestStep addPredicate(Predicate<HttpClientRequest<Object>> predicate) {
            matcher.withPredicate(predicate);
            return this;
        }
    }

    private static final class RequestArgumentMatcher implements ArgumentMatcher<HttpClientRequest<Object>> {

        private final List<Predicate<HttpClientRequest<Object>>> predicates = new ArrayList<>();

        @Override
        public boolean matches(HttpClientRequest<Object> httpClientRequest) {
            if (httpClientRequest == null) {
                return false;
            }

            return predicates.stream().allMatch(predicate -> predicate.test(httpClientRequest));
        }

        @SuppressWarnings("UnusedReturnValue")
        public RequestArgumentMatcher withPredicate(Predicate<HttpClientRequest<Object>> predicate) {
            this.predicates.add(predicate);
            return this;
        }
    }
}
