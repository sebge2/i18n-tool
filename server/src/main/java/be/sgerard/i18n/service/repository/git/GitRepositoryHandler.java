package be.sgerard.i18n.service.repository.git;

import be.sgerard.i18n.configuration.AppProperties;
import be.sgerard.i18n.model.repository.RepositoryType;
import be.sgerard.i18n.model.repository.dto.GitRepositoryCreationDto;
import be.sgerard.i18n.model.repository.dto.GitRepositoryPatchDto;
import be.sgerard.i18n.model.repository.persistence.GitRepositoryEntity;
import be.sgerard.i18n.service.repository.RepositoryException;
import be.sgerard.i18n.service.security.auth.AuthenticationManager;
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

    private final AuthenticationManager authenticationManager;
    private final AppProperties appProperties;

    public GitRepositoryHandler(AuthenticationManager authenticationManager,
                                AppProperties appProperties) {
        this.authenticationManager = authenticationManager;
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

        return Mono.just(repository);
    }

    @Override
    protected DefaultGitRepositoryApi.Configuration createConfiguration(GitRepositoryEntity repository) {
        return new DefaultGitRepositoryApi.Configuration(URI.create(repository.getLocation()), appProperties.getRepository().getDirectoryBaseDir(repository.getId()))
                .setUsername(
                        authenticationManager
                                .getCurrentUserOrFail()
                                .getGitHubToken()
                                .orElse(null)
                )
                .setPassword(null)
                .setDefaultBranch(repository.getDefaultBranch());
    }
}
