package be.sgerard.i18n.service.security.repository;

import be.sgerard.i18n.client.repository.github.GitHubClient;
import be.sgerard.i18n.model.repository.RepositoryType;
import be.sgerard.i18n.model.repository.persistence.GitHubRepositoryEntity;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.security.auth.GitHubRepositoryTokenCredentials;
import be.sgerard.i18n.model.security.auth.RepositoryCredentials;
import be.sgerard.i18n.model.security.auth.external.ExternalAuthSystem;
import be.sgerard.i18n.model.security.auth.external.ExternalUserToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * {@link RepositoryCredentialsHandler Handler} that takes the GitHub OAuth token for accessing the repository if the token
 * allows the access to it, otherwise fallback to the access-key.
 *
 * @author Sebastien Gerard
 */
@Component
@Order(0)
public class GitHubRepositoryCredentialsHandler implements RepositoryCredentialsHandler {

    private static final Logger logger = LoggerFactory.getLogger(GitHubRepositoryCredentialsHandler.class);

    private final GitHubClient gitHubClient;

    public GitHubRepositoryCredentialsHandler(GitHubClient gitHubClient) {
        this.gitHubClient = gitHubClient;
    }

    @Override
    public boolean support(RepositoryEntity repository) {
        return repository.getType() == RepositoryType.GITHUB;
    }

    @Override
    public Mono<RepositoryCredentials> loadCredentials(RepositoryEntity repository) {
        return Mono
                .just(repository)
                .map(GitHubRepositoryEntity.class::cast)
                .map(GitHubRepositoryEntity::getAccessKey)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(accessKey -> new GitHubRepositoryTokenCredentials(repository.getId(), null, accessKey));
    }

    @Override
    public Mono<RepositoryCredentials> loadCredentials(ExternalUserToken token, RepositoryEntity repository) {
        final GitHubRepositoryEntity gitHubRepository = (GitHubRepositoryEntity) repository;

        return this
                .isRepoMember(token, gitHubRepository)
                .flatMap(repoMember -> {
                    final String repositoryAccessKey = gitHubRepository.getAccessKey().orElse(null);
                    final String userToken = repoMember ? token.getToken() : null;

                    if ((repositoryAccessKey != null) || (userToken != null)) {
                        return Mono.just(new GitHubRepositoryTokenCredentials(repository.getId(), userToken, repositoryAccessKey));
                    } else {
                        logger.debug("The user is not a member GitHub repository [" + repository.getName() + "] and there is no repository access key.");

                        return Mono.empty();
                    }
                });
    }

    /**
     * Checks whether the specified token can access the specified repository.
     */
    private Mono<Boolean> isRepoMember(ExternalUserToken tokenValue, GitHubRepositoryEntity repository) {
        if (tokenValue.getExternalSystem() != ExternalAuthSystem.OAUTH_GITHUB) {
            return Mono.just(false);
        }

        return gitHubClient.isRepoMember(repository.getCompositeId(), tokenValue.getToken());
    }
}
