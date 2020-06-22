package be.sgerard.i18n.service.security.auth.external;

import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.security.auth.RepositoryCredentials;
import be.sgerard.i18n.model.security.user.ExternalAuthSystem;
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
    public boolean support(ExternalAuthSystem authSystem, RepositoryEntity repository) {
        return handlers.stream()
                .anyMatch(handler -> handler.support(authSystem, repository));
    }

    @Override
    public Mono<RepositoryCredentials> loadCredentials(ExternalAuthSystem authSystem, String token, RepositoryEntity repository) {
        return handlers.stream()
                .filter(handler -> handler.support(authSystem, repository))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("Unsupported client [" + authSystem + "]."))
                .loadCredentials(authSystem, token, repository);
    }
}
