package be.sgerard.i18n.service.security.repository;

import be.sgerard.i18n.model.security.auth.RepositoryCredentials;
import reactor.core.publisher.Flux;

/**
 * Provides {@link RepositoryCredentials credentials} for users accessing repositories.
 *
 * @author Sebastien Gerard
 */
public interface RepositoryCredentialsManager {

    /**
     * Loads {@link RepositoryCredentials credentials} when there is no token.
     */
    Flux<RepositoryCredentials> loadCredentials();

    /**
     * Loads {@link RepositoryCredentials credentials} with the specified token.
     */
    Flux<RepositoryCredentials> loadCredentials(String token);
}
