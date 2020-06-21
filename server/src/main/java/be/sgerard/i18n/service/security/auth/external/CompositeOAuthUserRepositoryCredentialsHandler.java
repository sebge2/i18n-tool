package be.sgerard.i18n.service.security.auth.external;

import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.security.auth.RepositoryCredentials;
import be.sgerard.i18n.model.security.auth.external.OAuthExternalUser;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Composite {@link OAuthUserRepositoryCredentialsHandler handler}.
 *
 * @author Sebastien Gerard
 */
@Primary
@Component
public class CompositeOAuthUserRepositoryCredentialsHandler implements OAuthUserRepositoryCredentialsHandler {

    private final List<OAuthUserRepositoryCredentialsHandler> handlers;

    public CompositeOAuthUserRepositoryCredentialsHandler(List<OAuthUserRepositoryCredentialsHandler> handlers) {
        this.handlers = handlers;
    }

    @Override
    public boolean support(OAuthExternalUser externalUser, RepositoryEntity repository) {
        return handlers.stream()
                .anyMatch(handler -> handler.support(externalUser, repository));
    }

    @Override
    public Mono<RepositoryCredentials> loadCredentials(OAuthExternalUser externalUser, RepositoryEntity repository, Mono<RepositoryCredentials> defaultCredentials) {
        return handlers.stream()
                .filter(handler -> handler.support(externalUser, repository))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("Unsupported user [" + externalUser + "]."))
                .loadCredentials(externalUser, repository, defaultCredentials);
    }
}
