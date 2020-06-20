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
import be.sgerard.i18n.service.security.auth.AuthenticationManager;
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

    private final AuthenticationManager authenticationManager;
    private final AppProperties appProperties;

    public GitHubRepositoryHandler(GitRepositoryApiProvider apiProvider,
                                   AuthenticationManager authenticationManager,
                                   AppProperties appProperties) {
        super(apiProvider);

        this.authenticationManager = authenticationManager;
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
                        .setAccessKey(creationDto.getAccessKey().orElse(null))
        );
    }

    @Override
    public Mono<GitHubRepositoryEntity> updateRepository(GitHubRepositoryEntity repository, GitHubRepositoryPatchDto patchDto) throws RepositoryException {
        updateFromPatch(patchDto, repository);

        repository.setAccessKey(patchDto.getAccessKey().or(repository::getAccessKey).orElse(null));
        repository.setWebHookSecret(patchDto.getWebHookSecret().or(repository::getWebHookSecret).orElse(null));

        return Mono.just(repository);
    }

    @Override
    protected DefaultGitRepositoryApi.Configuration createConfiguration(GitHubRepositoryEntity repository) {
        return new DefaultGitRepositoryApi.Configuration(appProperties.getRepository().getDirectoryBaseDir(repository.getId()), URI.create(repository.getLocation()))
                .setUsername(
                        authenticationManager
                                .getCurrentUserOrDie()
                                .getCredentials(repository.getId(), RepositoryTokenCredentials.class)
                                .map(RepositoryTokenCredentials::getToken)
                                .orElse(null)
                )
                .setPassword(null)
                .setDefaultBranch(repository.getDefaultBranch());
    }

}
