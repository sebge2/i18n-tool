package be.sgerard.i18n.service.security.repository;

import be.sgerard.i18n.model.repository.RepositoryType;
import be.sgerard.i18n.model.repository.persistence.GitHubRepositoryEntity;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.security.auth.RepositoryCredentials;
import be.sgerard.i18n.model.security.auth.RepositoryTokenCredentials;
import be.sgerard.i18n.model.security.auth.external.ExternalUserDetails;
import com.jcabi.github.Coordinates;
import com.jcabi.github.RtGithub;
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

    public GitHubRepositoryCredentialsHandler() {
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
                .map(accessKey -> new RepositoryTokenCredentials(repository.getId(), accessKey));
    }

    @Override
    public Mono<RepositoryCredentials> loadCredentials(ExternalUserDetails userDetails, RepositoryEntity repository) {
        final GitHubRepositoryEntity gitHubRepository = (GitHubRepositoryEntity) repository;

        return isRepoMember(userDetails.getToken(), gitHubRepository)
                ? Mono.just(new RepositoryTokenCredentials(repository.getId(), userDetails.getToken()))
                : Mono.justOrEmpty(gitHubRepository.getAccessKey().map(accessKey -> new RepositoryTokenCredentials(repository.getId(), accessKey)));
    }

    /**
     * Checks whether the specified token can access the specified repository.
     */
    private boolean isRepoMember(String tokenValue, GitHubRepositoryEntity repository) {
        final RtGithub github = new RtGithub(tokenValue);

        // TODO check that it's a collaborator
        try {
            return github.repos().get(new Coordinates.Simple(repository.getUsername(), repository.getRepository())).branches().iterate().iterator().hasNext();
        } catch (AssertionError e) {
            logger.debug("The user cannot access the GitHub repository [" + repository.getName() + "].", e);
            return false;
        }
    }
}
