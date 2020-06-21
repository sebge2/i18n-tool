package be.sgerard.i18n.service.security.auth.external;

import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.security.auth.RepositoryCredentials;
import be.sgerard.i18n.model.security.auth.RepositoryTokenCredentials;
import be.sgerard.i18n.model.security.auth.external.OAuthExternalUser;
import be.sgerard.i18n.model.security.user.persistence.ExternalAuthClient;
import be.sgerard.i18n.service.security.UserRole;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static java.util.Collections.singleton;

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
    public boolean support(OAuthExternalUser externalUser, RepositoryEntity repository) {
        return externalUser.getOauthClient() == ExternalAuthClient.GITHUB;
    }

    @Override
    public Mono<RepositoryCredentials> loadCredentials(OAuthExternalUser externalUser,
                                                       RepositoryEntity repository,
                                                       Mono<RepositoryCredentials> defaultCredentials) {
        // TODO check has access

        return Mono.just(new RepositoryTokenCredentials(repository.getId(), singleton(UserRole.MEMBER_OF_REPOSITORY), externalUser.getToken()));
    }
}
