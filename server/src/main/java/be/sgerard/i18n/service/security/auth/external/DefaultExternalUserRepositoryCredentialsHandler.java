package be.sgerard.i18n.service.security.auth.external;

import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.security.auth.RepositoryCredentials;
import be.sgerard.i18n.model.security.user.ExternalAuthSystem;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * {@link ExternalUserRepositoryCredentialsHandler Handler} that simply takes the default repository credentials if present.
 *
 * @author Sebastien Gerard
 */
@Component
@Order()
public class DefaultExternalUserRepositoryCredentialsHandler implements ExternalUserRepositoryCredentialsHandler {

    public DefaultExternalUserRepositoryCredentialsHandler() {
    }

    @Override
    public boolean support(ExternalAuthSystem authSystem, RepositoryEntity repository) {
        return true;
    }

    @Override
    public Mono<RepositoryCredentials> loadCredentials(ExternalAuthSystem authSystem, String token, RepositoryEntity repository) {
        return Mono.empty();
    }
}
