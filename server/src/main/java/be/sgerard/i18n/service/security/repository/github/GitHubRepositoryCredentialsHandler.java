package be.sgerard.i18n.service.security.repository.github;

import be.sgerard.i18n.client.repository.github.GitHubClient;
import be.sgerard.i18n.model.repository.RepositoryType;
import be.sgerard.i18n.model.repository.dto.GitHubRepositoryPatchDto;
import be.sgerard.i18n.model.repository.persistence.GitHubRepositoryEntity;
import be.sgerard.i18n.model.repository.persistence.GitRepositoryEntity;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.security.auth.external.ExternalAuthSystem;
import be.sgerard.i18n.model.security.auth.external.ExternalUserToken;
import be.sgerard.i18n.model.security.repository.CurrentUser;
import be.sgerard.i18n.model.security.repository.GitHubRepositoryTokenCredentials;
import be.sgerard.i18n.model.security.repository.RepositoryCredentials;
import be.sgerard.i18n.service.security.repository.RepositoryCredentialsHandler;
import be.sgerard.i18n.support.StringUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static be.sgerard.i18n.support.StringUtils.isNotEmptyString;

/**
 * {@link RepositoryCredentialsHandler Handler} that takes care of credentials for GitHub repositories. Credentials are configured
 * in the {@link GitRepositoryEntity repository entity} and also for user authenticated with the GitHub OAuth service. In this last case,
 * users have a GitHub Oauth token that will be used to access GitHub repositories.
 *
 * @author Sebastien Gerard
 */
@Component
public class GitHubRepositoryCredentialsHandler implements RepositoryCredentialsHandler<GitHubRepositoryTokenCredentials, GitHubRepositoryEntity> {

    private final GitHubClient gitHubClient;

    public GitHubRepositoryCredentialsHandler(GitHubClient gitHubClient) {
        this.gitHubClient = gitHubClient;
    }

    @Override
    public boolean support(RepositoryEntity repository) {
        return repository.getType() == RepositoryType.GITHUB;
    }

    @Override
    public Mono<GitHubRepositoryTokenCredentials> loadStaticCredentials(GitHubRepositoryEntity repository) {
        return loadUserCredentials(repository, null);
    }

    @Override
    public Mono<GitHubRepositoryTokenCredentials> loadUserCredentials(GitHubRepositoryEntity repository, CurrentUser currentUser) {
        return Mono
                .zip(
                        getGitHubToken(currentUser)
                                .filterWhen(externalUserToken -> isRepoMember(repository, externalUserToken))
                                .switchIfEmpty(Mono.just("")),
                        getAccessToken(repository)
                                .switchIfEmpty(Mono.just(""))
                )
                .map(credentials -> {
                    final String userToken = Optional.of(credentials.getT1()).filter(StringUtils::isNotEmptyString).orElse(null);
                    final String repositoryAccessToken = Optional.of(credentials.getT2()).filter(StringUtils::isNotEmptyString).orElse(null);

                    return new GitHubRepositoryTokenCredentials(
                            repository.getId(),
                            userToken,
                            repositoryAccessToken,
                            Optional.ofNullable(currentUser).map(CurrentUser::getDisplayName).orElse(null),
                            Optional.ofNullable(currentUser).map(CurrentUser::getEmail).orElse(null)
                    );
                });
    }

    /**
     * Loads the {@link RepositoryCredentials credentails} to access the specified repository (after patching it)
     * when there is no current {@link CurrentUser user}.
     */
    public Mono<GitHubRepositoryTokenCredentials> loadStaticCredentials(GitHubRepositoryEntity original, GitHubRepositoryPatchDto patch) {
        return Mono
                .justOrEmpty(patch.getUpdateAccessKey(original))
                .filterWhen(accessKey -> this.isRepoMember(original, accessKey))
                .map(accessKey -> new GitHubRepositoryTokenCredentials(
                        original.getId(),
                        null,
                        accessKey,
                        null,
                        null
                ))
                .switchIfEmpty(Mono.just(new GitHubRepositoryTokenCredentials(original.getId(), null, null, null, null)));
    }

    /**
     * Returns the token associated to the current user (available if this user is authenticated externally with GitHub Oauth).
     */
    private Mono<String> getGitHubToken(CurrentUser currentUser) {
        return Mono
                .justOrEmpty(currentUser)
                .flatMap(user -> Mono.justOrEmpty(user.getExternalToken()))
                .filter(token -> token.getExternalSystem() == ExternalAuthSystem.OAUTH_GITHUB)
                .map(ExternalUserToken::getToken);
    }

    /**
     * Checks whether the specified token can access the specified repository.
     */
    private Mono<Boolean> isRepoMember(GitHubRepositoryEntity repository, String token) {
        if (!isNotEmptyString(token)) {
            return Mono.just(false);
        }

        return gitHubClient.isRepositoryMember(repository.getGlobalId(), token);
    }

    /**
     * Returns the access token contained in the specified entity.
     */
    private Mono<String> getAccessToken(GitHubRepositoryEntity repository) {
        return Mono
                .just(repository)
                .map(GitHubRepositoryEntity::getAccessKey)
                .filter(Optional::isPresent)
                .map(Optional::get);
    }
}
