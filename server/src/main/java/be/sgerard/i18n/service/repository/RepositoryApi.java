package be.sgerard.i18n.service.repository;

import reactor.core.publisher.Mono;

/**
 * API for accessing a repository.
 *
 * @author Sebastien Gerard
 */
public interface RepositoryApi extends AutoCloseable {

    /**
     * Returns whether this API access has been closed. In that case, further operation are not allowed.
     */
    boolean isClosed();

    /**
     * {@link FunctionalInterface Functional interface} for consuming the API.
     */
    @FunctionalInterface
    interface ApiConsumer<A extends RepositoryApi> {

        /**
         * Consumes the specified {@link RepositoryApi API}.
         */
        Mono<Void> consume(A api) throws RepositoryException;

    }

    /**
     * {@link FunctionalInterface Functional interface} for applying a function using the API.
     */
    @FunctionalInterface
    interface ApiFunction<A extends RepositoryApi, T> {

        /**
         * Consumes the specified {@link RepositoryApi API} and returns a value.
         */
        T apply(A api) throws RepositoryException;

    }
}
