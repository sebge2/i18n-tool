package be.sgerard.i18n.service.repository.git;

import be.sgerard.i18n.configuration.AppProperties;
import be.sgerard.i18n.model.repository.RepositoryType;
import be.sgerard.i18n.model.repository.dto.GitHubRepositoryCreationDto;
import be.sgerard.i18n.model.repository.persistence.GitHubRepositoryEntity;
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
public class GitHubRepositoryHandler extends BaseGitRepositoryHandler<GitHubRepositoryEntity, GitHubRepositoryCreationDto> {

    private final AuthenticationManager authenticationManager;
    private final AppProperties appProperties;

    public GitHubRepositoryHandler(AuthenticationManager authenticationManager,
                                   AppProperties appProperties) {
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
    protected DefaultGitAPI.Configuration createConfiguration(GitHubRepositoryEntity repository) {
        return new DefaultGitAPI.Configuration(URI.create(repository.getLocation()), appProperties.getRepository().getDirectoryBaseDir(repository.getId()))
                .setUsername(
                        authenticationManager
                                .getCurrentUserOrFail()
                                .getGitHubToken()
                                .or(repository::getAccessKey)
                                .orElse(null)
                )
                .setPassword(null);
    }

}
