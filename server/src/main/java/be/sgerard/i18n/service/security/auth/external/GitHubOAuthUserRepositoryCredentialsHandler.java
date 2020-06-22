package be.sgerard.i18n.service.security.auth.external;

import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.security.auth.RepositoryCredentials;
import be.sgerard.i18n.model.security.auth.RepositoryTokenCredentials;
import be.sgerard.i18n.model.security.user.persistence.ExternalAuthClient;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * {@link OAuthUserRepositoryCredentialsHandler Handler} that takes the GitHub OAuth token for accessing the repository if the token
 * allows the access to it.
 *
 * @author Sebastien Gerard
 */
@Component
@Order(0)
public class GitHubOAuthUserRepositoryCredentialsHandler implements OAuthUserRepositoryCredentialsHandler {

    public GitHubOAuthUserRepositoryCredentialsHandler() {
    }

    @Override
    public boolean support(ExternalAuthClient client, RepositoryEntity repository) {
        return client == ExternalAuthClient.GITHUB;
    }

    @Override
    public Mono<RepositoryCredentials> loadCredentials(ExternalAuthClient client, String token, RepositoryEntity repository) {
        // TODO check has access

        return Mono.just(new RepositoryTokenCredentials(repository.getId(), token));
    }
}
