package be.sgerard.i18n.service.support;

import be.sgerard.i18n.model.translator.persistence.ExternalTranslatorGenericRestConfigEntity;
import be.sgerard.i18n.model.support.HttpClientRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * {@link HttpClient} based on the {@link WebClient web client}.
 */
@Service
public class DefaultHttpClient implements HttpClient {

    @Override
    public <V> Mono<V> execute(HttpClientRequest<V> request) {
        return WebClient
                .builder()
                .baseUrl(request.getUrl())
                .build()
                .method(request.getMethod())
                .uri(uriBuilder -> setupQueryParams(request, uriBuilder))
                .headers(httpHeaders -> setupQueryHeaders(request, httpHeaders))
                .bodyValue(request.getBody().map(Object.class::cast).orElse(""))
                .retrieve()
                .bodyToMono(request.getResponseType());
    }

    /**
     * Sets up the query parameters as specified by the {@link ExternalTranslatorGenericRestConfigEntity configuration}.
     */
    private URI setupQueryParams(HttpClientRequest<?> request, UriBuilder uriBuilder) {
        request.getParameters().forEach(uriBuilder::queryParam);

        return uriBuilder.build();
    }

    /**
     * Sets up the query headers as specified by the {@link ExternalTranslatorGenericRestConfigEntity configuration}.
     */
    private void setupQueryHeaders(HttpClientRequest<?> request, HttpHeaders httpHeaders) {
        request.getHeaders().forEach(httpHeaders::add);
    }
}
