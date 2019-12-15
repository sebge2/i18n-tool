package be.sgerard.i18n.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

/**
 * @author Sebastien Gerard
 */
public interface LockService {

    /**
     * @throws LockTimeoutException if the lock cannot be obtained
     */
    <T> Mono<T> executeAndGetMono(String repositoryId, Supplier<Mono<T>> supplier) throws LockTimeoutException;

    /**
     * @throws LockTimeoutException if the lock cannot be obtained
     */
    <T> Flux<T> executeAndGetFlux(String repositoryId, Supplier<Flux<T>> supplier) throws LockTimeoutException;
}
