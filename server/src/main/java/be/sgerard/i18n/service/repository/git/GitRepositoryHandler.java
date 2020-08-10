package be.sgerard.i18n.service.repository.git;

import be.sgerard.i18n.configuration.AppProperties;
import be.sgerard.i18n.model.repository.RepositoryType;
import be.sgerard.i18n.model.repository.dto.GitRepositoryCreationDto;
import be.sgerard.i18n.model.repository.dto.GitRepositoryPatchDto;
import be.sgerard.i18n.model.repository.persistence.GitRepositoryEntity;
import be.sgerard.i18n.model.security.auth.RepositoryTokenCredentials;
import be.sgerard.i18n.service.repository.RepositoryException;
import be.sgerard.i18n.service.security.auth.AuthenticationUserManager;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * {@link BaseGitRepositoryHandler Repository handler} for Git.
 *
 * @author Sebastien Gerard
 */
@Component
public class GitRepositoryHandler extends BaseGitRepositoryHandler<GitRepositoryEntity, GitRepositoryCreationDto, GitRepositoryPatchDto> {

    private final AuthenticationUserManager authenticationUserManager;
    private final AppProperties appProperties;

    public GitRepositoryHandler(GitRepositoryApiProvider apiProvider,
                                AuthenticationUserManager authenticationUserManager,
                                AppProperties appProperties) {
        super(apiProvider);

        this.authenticationUserManager = authenticationUserManager;
        this.appProperties = appProperties;
    }

    @Override
    public boolean support(RepositoryType type) {
        return type == RepositoryType.GIT;
    }

    @Override
    public Mono<GitRepositoryEntity> createRepository(GitRepositoryCreationDto creationDto) {
        return validateRepository(new GitRepositoryEntity(creationDto.getName(), creationDto.getLocation()));
    }

    @Override
    public Mono<GitRepositoryEntity> updateRepository(GitRepositoryEntity repository, GitRepositoryPatchDto patchDto) throws RepositoryException {
        updateFromPatch(patchDto, repository);

        patchDto.getName().ifPresent(repository::setName);

        return Mono.just(repository);
    }

    @Override
    protected Mono<DefaultGitRepositoryApi.Configuration> createConfiguration(GitRepositoryEntity repository) {
        return authenticationUserManager
                .getCurrentUserOrDie()
                .map(currentUser ->
                        new DefaultGitRepositoryApi.Configuration(appProperties.getRepository().getDirectoryBaseDir(repository.getId()), URI.create(repository.getLocation()))
                                .setUsername(
                                        currentUser
                                                .getCredentials(repository.getId(), RepositoryTokenCredentials.class)
                                                .map(RepositoryTokenCredentials::getToken)
                                                .orElse(null)
                                )
                                .setPassword(null)
                                .setDefaultBranch(repository.getDefaultBranch())
                );
    }
}
