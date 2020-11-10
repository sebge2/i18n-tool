package be.sgerard.i18n.service.repository.github;

import be.sgerard.i18n.configuration.AppProperties;
import be.sgerard.i18n.model.repository.RepositoryType;
import be.sgerard.i18n.model.repository.dto.GitHubRepositoryCreationDto;
import be.sgerard.i18n.model.repository.dto.GitHubRepositoryPatchDto;
import be.sgerard.i18n.model.repository.persistence.GitHubRepositoryEntity;
import be.sgerard.i18n.model.security.repository.GitHubRepositoryTokenCredentials;
import be.sgerard.i18n.service.repository.RepositoryApi;
import be.sgerard.i18n.service.repository.RepositoryException;
import be.sgerard.i18n.service.repository.git.BaseGitRepositoryHandler;
import be.sgerard.i18n.service.repository.git.DefaultGitRepositoryApi;
import be.sgerard.i18n.service.repository.git.GitRepositoryApi;
import be.sgerard.i18n.service.repository.git.GitRepositoryApiProvider;
import be.sgerard.i18n.support.StringUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.File;

/**
 * {@link BaseGitRepositoryHandler Repository handler} for GitHub.
 *
 * @author Sebastien Gerard
 */
@Component
public class GitHubRepositoryHandler extends
        BaseGitRepositoryHandler<GitHubRepositoryEntity, GitHubRepositoryCreationDto, GitHubRepositoryPatchDto, GitHubRepositoryTokenCredentials> {

    private final AppProperties appProperties;

    public GitHubRepositoryHandler(GitRepositoryApiProvider apiProvider, AppProperties appProperties) {
        super(apiProvider);

        this.appProperties = appProperties;
    }

    @Override
    public boolean support(RepositoryType type) {
        return type == RepositoryType.GITHUB;
    }

    @Override
    public Mono<GitHubRepositoryEntity> createRepository(GitHubRepositoryCreationDto creationDto) {
        return Mono.just(
                new GitHubRepositoryEntity(creationDto.getUsername(), creationDto.getRepository())
                        .setAccessKey(creationDto.getAccessKey().filter(StringUtils::isNotEmptyString).orElse(null))
        );
    }

    @Override
    public Mono<GitHubRepositoryEntity> updateRepository(GitHubRepositoryEntity repository,
                                                         GitHubRepositoryPatchDto patchDto,
                                                         GitHubRepositoryTokenCredentials credentials) throws RepositoryException {
        updateFromPatch(patchDto, repository);

        repository.setAccessKey(patchDto.getUpdateAccessKey(repository).orElse(null));
        repository.setWebHookSecret(patchDto.getUpdateWebHookSecret(repository).orElse(null));

        return Mono.just(repository);
    }

    @Override
    public Mono<RepositoryApi> initApi(GitHubRepositoryEntity repository,
                                       GitHubRepositoryTokenCredentials credentials) throws RepositoryException {
        return createConfiguration(repository, credentials)
                .flatMap(this::initApi)
                .map(a -> a);
    }

    /**
     * Initializes the {@link DefaultGitRepositoryApi.Configuration configuration} to use to access Git.
     */
    private Mono<GitRepositoryApi.Configuration> createConfiguration(GitHubRepositoryEntity repository,
                                                                     GitHubRepositoryTokenCredentials credentials) {
        return Mono
                .just(credentials)
                .map(cred ->
                        new DefaultGitRepositoryApi.Configuration(getLocalFile(repository), repository.getLocation())
                                .setDefaultBranch(repository.getDefaultBranch())
                                .setUsername(cred.getToken().orElse(null))
                                .setPassword("")
                                .setDisplayName(cred.getUserDisplayName().orElse(null))
                                .setEmail(cred.getUserEmail().orElse(null))
                );
    }

    /**
     * Returns the local file storing the repository.
     */
    private File getLocalFile(GitHubRepositoryEntity repository) {
        return appProperties.getRepository().getDirectoryBaseDir(repository.getId());
    }
}
