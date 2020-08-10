package be.sgerard.i18n.service.security.repository;

import be.sgerard.i18n.model.security.auth.RepositoryCredentials;
import be.sgerard.i18n.model.security.auth.external.ExternalUserDetails;
import be.sgerard.i18n.model.security.auth.internal.InternalUserDetails;
import reactor.core.publisher.Flux;

/**
 * Provides {@link RepositoryCredentials credentials} for users accessing repositories.
 *
 * @author Sebastien Gerard
 */
public interface RepositoryCredentialsManager {

    /**
     * Loads {@link RepositoryCredentials credentials} for the specified internal user.
     */
    Flux<RepositoryCredentials> loadCredentials(InternalUserDetails internalUserDetails);

    /**
     * Loads {@link RepositoryCredentials credentials} for the specified external user.
     */
    Flux<RepositoryCredentials> loadCredentials(ExternalUserDetails externalUserDetails);
}
