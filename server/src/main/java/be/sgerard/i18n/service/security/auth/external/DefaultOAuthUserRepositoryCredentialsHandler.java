package be.sgerard.i18n.service.security.auth.external;

import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.security.auth.RepositoryCredentials;
import be.sgerard.i18n.model.security.auth.external.OAuthExternalUser;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * {@link OAuthUserRepositoryCredentialsHandler Handler} that simply takes the default repository credentials if present.
 *
 * @author Sebastien Gerard
 */
@Component
@Order()
public class DefaultOAuthUserRepositoryCredentialsHandler implements OAuthUserRepositoryCredentialsHandler {

    public DefaultOAuthUserRepositoryCredentialsHandler() {
    }

    @Override
    public boolean support(OAuthExternalUser externalUser, RepositoryEntity repository) {
        return true;
    }

    @Override
    public Mono<RepositoryCredentials> loadCredentials(OAuthExternalUser externalUser,
                                                       RepositoryEntity repository,
                                                       Mono<RepositoryCredentials> defaultCredentials) {
        return defaultCredentials;
    }
}
