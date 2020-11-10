package be.sgerard.i18n.service.security.repository;

import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.security.auth.AuthenticatedUser;
import be.sgerard.i18n.model.security.repository.RepositoryCredentials;
import reactor.core.publisher.Mono;

/**
 * Provides {@link RepositoryCredentials credentials} for users accessing repositories.
 *
 * @author Sebastien Gerard
 */
public interface RepositoryCredentialsManager {

    /**
     * Loads {@link RepositoryCredentials credentials} when there is no token for accessing the specified {@link RepositoryEntity repository}.
     */
    Mono<RepositoryCredentials> loadStaticCredentials(RepositoryEntity repository);

    /**
     * Loads {@link RepositoryCredentials credentials} of the current authenticated user for accessing the specified {@link RepositoryEntity repository}.
     * If there is no current user, the mono fails. This method helps to ensure that there is an authenticated user.
     */
    Mono<RepositoryCredentials> loadUserCredentialsOrDie(RepositoryEntity repository);

    /**
     * Loads {@link RepositoryCredentials credentials} of the current authenticated user for accessing the specified {@link RepositoryEntity repository}.
     * If there is no current user, {@link #loadStaticCredentials(RepositoryEntity) static credentials are returned}.
     */
    Mono<RepositoryCredentials> loadUserCredentials(RepositoryEntity repository);

    /**
     * Loads {@link RepositoryCredentials credentials} of the specified authenticated user for accessing the specified {@link RepositoryEntity repository}.
     */
    Mono<RepositoryCredentials> loadUserCredentials(RepositoryEntity repository, AuthenticatedUser authenticatedUser);

}
