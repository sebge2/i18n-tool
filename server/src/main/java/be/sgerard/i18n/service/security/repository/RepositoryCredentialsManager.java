package be.sgerard.i18n.service.security.repository;

import be.sgerard.i18n.model.security.auth.RepositoryCredentials;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Provides {@link RepositoryCredentials credentials} for users accessing repositories.
 *
 * @author Sebastien Gerard
 */
public interface RepositoryCredentialsManager {

    /**
     * Loads {@link RepositoryCredentials credentials} when there is no token.
     */
    Flux<RepositoryCredentials> loadAllCredentials();

    /**
     * Loads {@link RepositoryCredentials credentials} when there is no token for accessing the specified repository.
     */
    Mono<RepositoryCredentials> loadCredentials(String repositoryId);

    /**
     * Loads {@link RepositoryCredentials credentials} with the specified token.
     */
    Flux<RepositoryCredentials> loadAllCredentials(String token);

    /**
     * Loads {@link RepositoryCredentials credentials} with the specified token for accessing the specified repository.
     */
    Mono<RepositoryCredentials> loadCredentials(String repositoryId, String token);
}
