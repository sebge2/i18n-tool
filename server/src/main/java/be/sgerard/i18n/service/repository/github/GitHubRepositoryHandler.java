package be.sgerard.i18n.service.repository.github;

import be.sgerard.i18n.configuration.AppProperties;
import be.sgerard.i18n.model.repository.RepositoryType;
import be.sgerard.i18n.model.repository.dto.GitHubRepositoryCreationDto;
import be.sgerard.i18n.model.repository.dto.GitHubRepositoryPatchDto;
import be.sgerard.i18n.model.repository.persistence.GitHubRepositoryEntity;
import be.sgerard.i18n.model.security.auth.RepositoryTokenCredentials;
import be.sgerard.i18n.service.repository.RepositoryException;
import be.sgerard.i18n.service.repository.git.BaseGitRepositoryHandler;
import be.sgerard.i18n.service.repository.git.DefaultGitRepositoryApi;
import be.sgerard.i18n.service.repository.git.GitRepositoryApiProvider;
import be.sgerard.i18n.service.security.auth.AuthenticationUserManager;
import be.sgerard.i18n.support.StringUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * {@link BaseGitRepositoryHandler Repository handler} for GitHub.
 *
 * @author Sebastien Gerard
 */
@Component
public class GitHubRepositoryHandler extends BaseGitRepositoryHandler<GitHubRepositoryEntity, GitHubRepositoryCreationDto, GitHubRepositoryPatchDto> {

    private final AuthenticationUserManager authenticationUserManager;
    private final AppProperties appProperties;

    public GitHubRepositoryHandler(GitRepositoryApiProvider apiProvider,
                                   AuthenticationUserManager authenticationUserManager,
                                   AppProperties appProperties) {
        super(apiProvider);

        this.authenticationUserManager = authenticationUserManager;
        this.appProperties = appProperties;
    }

    @Override
    public boolean support(RepositoryType type) {
        return type == RepositoryType.GITHUB;
    }

    @Override
    public Mono<GitHubRepositoryEntity> createRepository(GitHubRepositoryCreationDto creationDto) {
        return validateRepository(
                new GitHubRepositoryEntity(creationDto.getUsername(), creationDto.getRepository())
                        .setAccessKey(creationDto.getAccessKey().filter(StringUtils::isEmptyString).orElse(null))
        );
    }

    @Override
    public Mono<GitHubRepositoryEntity> updateRepository(GitHubRepositoryEntity repository, GitHubRepositoryPatchDto patchDto) throws RepositoryException {
        updateFromPatch(patchDto, repository);

        repository.setAccessKey(patchDto.getAccessKey().or(repository::getAccessKey).filter(StringUtils::isEmptyString).orElse(null));
        repository.setWebHookSecret(patchDto.getWebHookSecret().or(repository::getWebHookSecret).filter(StringUtils::isEmptyString).orElse(null));

        return Mono.just(repository);
    }

    @Override
    protected Mono<DefaultGitRepositoryApi.Configuration> createConfiguration(GitHubRepositoryEntity repository) {
        return authenticationUserManager
                .getCurrentUserOrDie()
                .map(currentUser ->
                        new DefaultGitRepositoryApi.Configuration(appProperties.getRepository().getDirectoryBaseDir(repository.getId()), URI.create(repository.getLocation()))
                                .setUsername(
                                        currentUser
                                                .getCredentials(repository.getId(), RepositoryTokenCredentials.class)
                                                .map(RepositoryTokenCredentials::getToken)
                                                .or(repository::getAccessKey)
                                                .orElse(null)
                                )
                                .setPassword(null)
                                .setDefaultBranch(repository.getDefaultBranch()));
    }

}
