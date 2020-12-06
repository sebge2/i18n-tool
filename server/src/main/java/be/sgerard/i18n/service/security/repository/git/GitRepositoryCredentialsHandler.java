package be.sgerard.i18n.service.security.repository.git;

import be.sgerard.i18n.model.repository.RepositoryType;
import be.sgerard.i18n.model.repository.dto.GitRepositoryPatchDto;
import be.sgerard.i18n.model.repository.persistence.GitRepositoryEntity;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.security.repository.CurrentUser;
import be.sgerard.i18n.model.security.repository.GitRepositoryUserPasswordCredentials;
import be.sgerard.i18n.model.security.repository.RepositoryCredentials;
import be.sgerard.i18n.service.security.repository.RepositoryCredentialsHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * {@link RepositoryCredentialsHandler Handler} that takes care of credentials for simple Git repositories. Credentials are configured
 * in the {@link GitRepositoryEntity repository entity}.
 *
 * @author Sebastien Gerard
 */
@Component
public class GitRepositoryCredentialsHandler implements RepositoryCredentialsHandler<GitRepositoryUserPasswordCredentials, GitRepositoryEntity> {

    public GitRepositoryCredentialsHandler() {
    }

    @Override
    public boolean support(RepositoryEntity repository) {
        return repository.getType() == RepositoryType.GIT;
    }

    @Override
    public Mono<GitRepositoryUserPasswordCredentials> loadStaticCredentials(GitRepositoryEntity repository) {
        return loadUserCredentials(repository, null);
    }

    @Override
    public Mono<GitRepositoryUserPasswordCredentials> loadUserCredentials(GitRepositoryEntity repository, CurrentUser currentUser) {
        return Mono
                .just(repository)
                .map(rep ->
                        new GitRepositoryUserPasswordCredentials(
                                rep.getId(),
                                rep.getUsername().orElse(null),
                                rep.getPassword().orElse(null),
                                Optional.ofNullable(currentUser).map(CurrentUser::getDisplayName).orElse(null),
                                Optional.ofNullable(currentUser).map(CurrentUser::getEmail).orElse(null)
                        )
                );
    }

    /**
     * Loads the {@link RepositoryCredentials credentails} to access the specified repository (after patching it)
     * when there is no current {@link CurrentUser user}.
     */
    public Mono<GitRepositoryUserPasswordCredentials> loadStaticCredentials(GitRepositoryEntity original, GitRepositoryPatchDto patch) {
        return Mono.just(
                new GitRepositoryUserPasswordCredentials(
                        original.getId(),
                        patch.getUpdatedUserName(original).orElse(null),
                        patch.getUpdatedPassword(original).orElse(null)
                )
        );
    }
}
