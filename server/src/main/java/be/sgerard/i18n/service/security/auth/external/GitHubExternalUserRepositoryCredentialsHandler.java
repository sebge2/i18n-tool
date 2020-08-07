package be.sgerard.i18n.service.security.auth.external;

import be.sgerard.i18n.model.repository.RepositoryType;
import be.sgerard.i18n.model.repository.persistence.GitHubRepositoryEntity;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.security.auth.RepositoryCredentials;
import be.sgerard.i18n.model.security.auth.RepositoryTokenCredentials;
import be.sgerard.i18n.model.security.user.ExternalAuthSystem;
import com.jcabi.github.Coordinates;
import com.jcabi.github.RtGithub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * {@link ExternalUserRepositoryCredentialsHandler Handler} that takes the GitHub OAuth token for accessing the repository if the token
 * allows the access to it.
 *
 * @author Sebastien Gerard
 */
@Component
@Order(0)
public class GitHubExternalUserRepositoryCredentialsHandler implements ExternalUserRepositoryCredentialsHandler {

    private static final Logger logger = LoggerFactory.getLogger(GitHubExternalUserRepositoryCredentialsHandler.class);

    public GitHubExternalUserRepositoryCredentialsHandler() {
    }

    @Override
    public boolean support(ExternalAuthSystem authSystem, RepositoryEntity repository) {
        return (authSystem == ExternalAuthSystem.OAUTH_GITHUB) && (repository.getType() == RepositoryType.GITHUB);
    }

    @Override
    public Mono<RepositoryCredentials> loadCredentials(ExternalAuthSystem authSystem, String token, RepositoryEntity repository) {
        final GitHubRepositoryEntity gitHubRepository = (GitHubRepositoryEntity) repository;

        return isRepoMember(token, gitHubRepository)
                ? Mono.just(new RepositoryTokenCredentials(repository.getId(), token))
                : Mono.justOrEmpty(gitHubRepository.getAccessKey().map(accessKey -> new RepositoryTokenCredentials(repository.getId(), accessKey)));
    }

    /**
     * Checks whether the specified token can access the specified repository.
     */
    private boolean isRepoMember(String tokenValue, GitHubRepositoryEntity repository) {
        final RtGithub github = new RtGithub(tokenValue);

        try {
            return github.repos().get(new Coordinates.Simple(repository.getUsername(), repository.getRepository())).branches().iterate().iterator().hasNext();
        } catch (AssertionError e) {
            logger.debug("The user cannot access the GitHub repository [" + repository.getName() + "].", e);
            return false;
        }
    }
}
