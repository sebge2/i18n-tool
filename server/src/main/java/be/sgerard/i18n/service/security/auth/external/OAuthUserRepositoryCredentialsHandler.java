package be.sgerard.i18n.service.security.auth.external;

import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.security.auth.RepositoryCredentials;
import be.sgerard.i18n.model.security.auth.external.OAuthExternalUser;
import reactor.core.publisher.Mono;

/**
 * Handler that makes the link between an external authentication system and a particular repository type.
 *
 * @author Sebastien Gerard
 */
public interface OAuthUserRepositoryCredentialsHandler {

    /**
     * Returns whether the handler supports the specified {@link OAuthExternalUser external user}
     * and the specified {@link RepositoryEntity repository}.
     */
    boolean support(OAuthExternalUser externalUser, RepositoryEntity repository);

    /**
     * Loads the {@link RepositoryCredentials credentails} to use for the specified {@link OAuthExternalUser external user}
     * and the specified {@link RepositoryEntity repository}.
     */
    Mono<RepositoryCredentials> loadCredentials(OAuthExternalUser externalUser,
                                                RepositoryEntity repository,
                                                Mono<RepositoryCredentials> defaultCredentials);

}
