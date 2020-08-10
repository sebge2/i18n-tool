package be.sgerard.i18n.service.security.repository;

import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.security.auth.RepositoryCredentials;
import be.sgerard.i18n.model.security.auth.external.ExternalUserDetails;
import be.sgerard.i18n.model.security.auth.internal.InternalUserDetails;
import reactor.core.publisher.Mono;

/**
 * Provides {@link RepositoryCredentials credentials} for a particular user and repository.
 *
 * @author Sebastien Gerard
 */
public interface RepositoryCredentialsHandler {

    /**
     * Returns whether the handler supports the specified {@link InternalUserDetails user}
     * and the specified {@link RepositoryEntity repository}.
     */
    boolean support(InternalUserDetails userDetails, RepositoryEntity repository);

    /**
     * Returns whether the handler supports the specified {@link ExternalUserDetails user}
     * and the specified {@link RepositoryEntity repository}.
     */
    boolean support(ExternalUserDetails userDetails, RepositoryEntity repository);

    /**
     * Loads the {@link RepositoryCredentials credentails} to use for the specified {@link InternalUserDetails user} when
     * accessing the specified {@link RepositoryEntity repository}.
     */
    Mono<RepositoryCredentials> loadCredentials(InternalUserDetails userDetails, RepositoryEntity repository);

    /**
     * Loads the {@link RepositoryCredentials credentails} to use for the specified {@link ExternalUserDetails user} when
     * accessing the specified {@link RepositoryEntity repository}.
     */
    Mono<RepositoryCredentials> loadCredentials(ExternalUserDetails userDetails, RepositoryEntity repository);

}
