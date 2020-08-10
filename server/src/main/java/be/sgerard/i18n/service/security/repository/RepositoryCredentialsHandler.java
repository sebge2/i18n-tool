package be.sgerard.i18n.service.security.repository;

import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.security.auth.RepositoryCredentials;
import reactor.core.publisher.Mono;

/**
 * Provides {@link RepositoryCredentials credentials} for a particular user and repository.
 *
 * @author Sebastien Gerard
 */
public interface RepositoryCredentialsHandler {

    /**
     * Returns whether the handler supports the specified {@link RepositoryEntity repository}.
     */
    boolean support(RepositoryEntity repository);

    /**
     * Loads the {@link RepositoryCredentials credentails} to access the specified repository when there is no token.
     */
    Mono<RepositoryCredentials> loadCredentials(RepositoryEntity repository);

    /**
     * Loads the {@link RepositoryCredentials credentails} to access the specified repository with the specified token.
     */
    Mono<RepositoryCredentials> loadCredentials(String token, RepositoryEntity repository);

}
