package be.sgerard.i18n.service.security.repository;

import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.security.auth.RepositoryCredentials;
import be.sgerard.i18n.model.security.auth.external.ExternalUserToken;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * {@link RepositoryCredentialsHandler Handler} that does not return any credentials.
 *
 * @author Sebastien Gerard
 */
@Component
@Order()
public class DefaultRepositoryCredentialsHandler implements RepositoryCredentialsHandler {

    public DefaultRepositoryCredentialsHandler() {
    }

    @Override
    public boolean support(RepositoryEntity repository) {
        return true;
    }

    @Override
    public Mono<RepositoryCredentials> loadCredentials(RepositoryEntity repository) {
        return Mono.empty();
    }

    @Override
    public Mono<RepositoryCredentials> loadCredentials(ExternalUserToken externalToken, RepositoryEntity repository) {
        return Mono.empty();
    }
}
