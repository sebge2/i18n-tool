package be.sgerard.i18n.service.repository.git;

import be.sgerard.i18n.configuration.AppProperties;
import be.sgerard.i18n.model.repository.RepositoryType;
import be.sgerard.i18n.model.repository.dto.GitRepositoryCreationDto;
import be.sgerard.i18n.model.repository.dto.GitRepositoryPatchDto;
import be.sgerard.i18n.model.repository.persistence.GitRepositoryEntity;
import be.sgerard.i18n.model.security.auth.AuthenticatedUser;
import be.sgerard.i18n.model.security.auth.RepositoryTokenCredentials;
import be.sgerard.i18n.service.repository.RepositoryException;
import be.sgerard.i18n.service.security.auth.AuthenticationUserManager;
import be.sgerard.i18n.service.user.UserManager;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.File;
import java.net.URI;

/**
 * {@link BaseGitRepositoryHandler Repository handler} for Git.
 *
 * @author Sebastien Gerard
 */
@Component
public class GitRepositoryHandler extends BaseGitRepositoryHandler<GitRepositoryEntity, GitRepositoryCreationDto, GitRepositoryPatchDto> {

    private final AuthenticationUserManager authenticationUserManager;
    private final UserManager userManager;
    private final AppProperties appProperties;

    public GitRepositoryHandler(GitRepositoryApiProvider apiProvider,
                                AuthenticationUserManager authenticationUserManager,
                                UserManager userManager,
                                AppProperties appProperties) {
        super(apiProvider);

        this.authenticationUserManager = authenticationUserManager;
        this.userManager = userManager;
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
                .getCurrentUser()
                .flatMap(currentAuthUser ->
                        userManager
                                .findByIdOrDie(currentAuthUser.getUserId())
                                .map(currentUser ->
                                        new DefaultGitRepositoryApi.Configuration(getLocalFile(repository), getRemoteUri(repository))
                                                .setUsername(getUsername(repository, currentAuthUser))
                                                .setPassword(null)
                                                .setDisplayName(currentUser.getDisplayName())
                                                .setEmail(currentUser.getEmail())
                                                .setDefaultBranch(repository.getDefaultBranch())
                                )
                )
                .switchIfEmpty(Mono.just(
                        new DefaultGitRepositoryApi.Configuration(getLocalFile(repository), getRemoteUri(repository))
                                .setDefaultBranch(repository.getDefaultBranch())
                ));
    }

    /**
     * Returns the local file storing the repository.
     */
    private File getLocalFile(GitRepositoryEntity repository) {
        return appProperties.getRepository().getDirectoryBaseDir(repository.getId());
    }

    /**
     * Returns the remote repository URI.
     */
    private URI getRemoteUri(GitRepositoryEntity repository) {
        return URI.create(repository.getLocation());
    }

    /**
     * Returns the username used to access the repository.
     */
    private String getUsername(GitRepositoryEntity repository, AuthenticatedUser currentAuthUser) {
        return currentAuthUser
                .getCredentials(repository.getId(), RepositoryTokenCredentials.class)
                .map(RepositoryTokenCredentials::getToken)
                .orElse(null);
    }
}
