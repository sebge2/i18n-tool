package be.sgerard.i18n.service.repository.git;

import be.sgerard.i18n.configuration.AppProperties;
import be.sgerard.i18n.model.repository.RepositoryType;
import be.sgerard.i18n.model.repository.dto.GitRepositoryCreationDto;
import be.sgerard.i18n.model.repository.dto.GitRepositoryPatchDto;
import be.sgerard.i18n.model.repository.persistence.GitRepositoryEntity;
import be.sgerard.i18n.model.security.repository.GitRepositoryUserPasswordCredentials;
import be.sgerard.i18n.service.repository.RepositoryApi;
import be.sgerard.i18n.service.repository.RepositoryException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.File;

/**
 * {@link BaseGitRepositoryHandler Repository handler} for Git.
 *
 * @author Sebastien Gerard
 */
@Component
public class GitRepositoryHandler extends BaseGitRepositoryHandler<GitRepositoryEntity, GitRepositoryCreationDto, GitRepositoryPatchDto, GitRepositoryUserPasswordCredentials> {

    private final AppProperties appProperties;

    public GitRepositoryHandler(GitRepositoryApiProvider apiProvider, AppProperties appProperties) {
        super(apiProvider, RepositoryType.GIT);

        this.appProperties = appProperties;
    }

    @Override
    public Mono<GitRepositoryEntity> createRepository(GitRepositoryCreationDto creationDto) {
        return Mono.just(
                new GitRepositoryEntity(creationDto.getName(), creationDto.getLocation())
                        .setUsername(creationDto.getUsername().orElse(null))
                        .setPassword(creationDto.getPassword().orElse(null))
        );
    }

    @Override
    public Mono<RepositoryApi> initApi(GitRepositoryEntity repository, GitRepositoryUserPasswordCredentials credentials) throws RepositoryException {
        return createConfiguration(repository, credentials)
                .flatMap(this::initApi)
                .map(a -> a);
    }

    @Override
    protected Mono<GitRepositoryEntity> updateGitRepoFromPatch(GitRepositoryPatchDto patchDto, GitRepositoryEntity repository) {
        patchDto.getName().ifPresent(repository::setName);
        repository.setUsername(patchDto.getUpdatedUserName(repository).orElse(null));
        repository.setPassword(patchDto.getUpdatedPassword(repository).orElse(null));

        return Mono.just(repository);
    }

    /**
     * Initializes the {@link DefaultGitRepositoryApi.Configuration configuration} to use to access Git.
     */
    private Mono<GitRepositoryApi.Configuration> createConfiguration(GitRepositoryEntity repository,
                                                                     GitRepositoryUserPasswordCredentials credentials) {
        return Mono
                .just(credentials)
                .map(cred ->
                        new DefaultGitRepositoryApi.Configuration(getLocalFile(repository), repository.getLocation())
                                .setDefaultBranch(repository.getDefaultBranch())
                                .setUsername(cred.getUsername().orElse(null))
                                .setPassword(cred.getPassword().orElse(null))
                                .setDisplayName(cred.getUserDisplayName().orElse(null))
                                .setEmail(cred.getUserEmail().orElse(null))
                );
    }

    /**
     * Returns the local file storing the repository.
     */
    private File getLocalFile(GitRepositoryEntity repository) {
        return appProperties.getRepository().getDirectoryBaseDir(repository.getId());
    }
}
