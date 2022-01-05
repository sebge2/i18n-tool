package be.sgerard.i18n.service.support;

import be.sgerard.i18n.model.support.HttpClientRequest;
import reactor.core.publisher.Mono;

/**
 * Reactive HTTP client.
 */
public interface HttpClient {

    /**
     * Executes the specified request and returns the result.
     */
    <V> Mono<V> execute(HttpClientRequest<V> request);

}
