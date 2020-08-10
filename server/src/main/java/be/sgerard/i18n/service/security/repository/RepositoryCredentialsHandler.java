package be.sgerard.i18n.service.security.repository;

import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.security.auth.RepositoryCredentials;
import be.sgerard.i18n.model.security.auth.external.ExternalUserDetails;
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
     * Loads the {@link RepositoryCredentials credentails} to use for accessing the specified {@link RepositoryEntity repository}.
     */
    Mono<RepositoryCredentials> loadCredentials(RepositoryEntity repository);

    /**
     * Loads the {@link RepositoryCredentials credentails} to use for the specified {@link ExternalUserDetails user} when
     * accessing the specified {@link RepositoryEntity repository}.
     */
    Mono<RepositoryCredentials> loadCredentials(ExternalUserDetails userDetails, RepositoryEntity repository);

}
