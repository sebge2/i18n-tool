package be.sgerard.i18n.service.security.repository;

import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.security.auth.RepositoryCredentials;
import be.sgerard.i18n.model.security.auth.external.ExternalUserDetails;
import be.sgerard.i18n.model.security.auth.internal.InternalUserDetails;
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
    public boolean support(InternalUserDetails userDetails, RepositoryEntity repository) {
        return true;
    }

    @Override
    public boolean support(ExternalUserDetails userDetails, RepositoryEntity repository) {
        return true;
    }

    @Override
    public Mono<RepositoryCredentials> loadCredentials(InternalUserDetails userDetails, RepositoryEntity repository) {
        return Mono.empty();
    }

    @Override
    public Mono<RepositoryCredentials> loadCredentials(ExternalUserDetails userDetails, RepositoryEntity repository) {
        return Mono.empty();
    }
}
