package be.sgerard.i18n.service.repository.git.validation;

import be.sgerard.i18n.model.repository.RepositoryType;
import be.sgerard.i18n.model.repository.dto.GitRepositoryPatchDto;
import be.sgerard.i18n.model.repository.dto.RepositoryPatchDto;
import be.sgerard.i18n.model.repository.persistence.GitRepositoryEntity;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.security.repository.GitRepositoryUserPasswordCredentials;
import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.service.repository.git.GitRepositoryApi;
import be.sgerard.i18n.service.repository.git.GitRepositoryHandler;
import be.sgerard.i18n.service.repository.validation.RepositoryValidator;
import be.sgerard.i18n.service.security.repository.RepositoryCredentialsManager;
import be.sgerard.i18n.service.security.repository.git.GitRepositoryCredentialsHandler;
import be.sgerard.i18n.support.StringUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * {@link RepositoryValidator Validator} checking that the Git API works properly with the specified URL and credentials.
 *
 * @author Sebastien Gerard
 */
@Component
public class GitApiAccessValidator implements RepositoryValidator<GitRepositoryEntity> {

    private final GitRepositoryHandler handler;
    private final GitRepositoryCredentialsHandler credentialsHandler;
    private final RepositoryCredentialsManager credentialsManager;

    public GitApiAccessValidator(GitRepositoryHandler handler,
                                 GitRepositoryCredentialsHandler credentialsHandler,
                                 RepositoryCredentialsManager credentialsManager) {
        this.handler = handler;
        this.credentialsHandler = credentialsHandler;
        this.credentialsManager = credentialsManager;
    }

    @Override
    public boolean support(RepositoryEntity repositoryEntity) {
        return repositoryEntity.getType() == RepositoryType.GIT;
    }

    @Override
    public Mono<ValidationResult> beforePersist(GitRepositoryEntity repository) {
        return validateApiAccessCurrentUser(repository)
                .flatMap(validationResult -> { // this avoid twice the same message if the URL is not correct
                    if (validationResult.isSuccessful()) {
                        return validateApiAccessStatic(repository);
                    } else {
                        return Mono.just(validationResult);
                    }
                });
    }

    @Override
    public Mono<ValidationResult> beforeUpdate(GitRepositoryEntity original, RepositoryPatchDto patch) {
        final GitRepositoryPatchDto gitPatch = (GitRepositoryPatchDto) patch;

        if (gitPatch.getUsername().filter(StringUtils::isNotEmptyString).isPresent() || gitPatch.getPassword().filter(StringUtils::isNotEmptyString).isPresent()) {
            return credentialsHandler.loadStaticCredentials(original, gitPatch)
                    .flatMap(credentials -> validateApiAccess(original, credentials));
        }

        return Mono.just(ValidationResult.EMPTY);
    }

    /**
     * Validates credentials that are specified at the repository entity level.
     */
    private Mono<ValidationResult> validateApiAccessStatic(GitRepositoryEntity repository) {
        if (repository.getUsername().isEmpty() && repository.getPassword().isEmpty()) {
            return Mono.just(ValidationResult.EMPTY);
        }

        return credentialsHandler
                .loadStaticCredentials(repository)
                .flatMap(credentials -> validateApiAccess(repository, credentials));
    }

    /**
     * Validates that the current user with the specified repository configuration can initialize the local Git repository.
     */
    private Mono<ValidationResult> validateApiAccessCurrentUser(GitRepositoryEntity repository) {
        return credentialsManager
                .loadUserCredentialsOrDie(repository)
                .map(GitRepositoryUserPasswordCredentials.class::cast)
                .flatMap(credentials -> validateApiAccess(repository, credentials));
    }

    /**
     * Validates that the repository can be accessed with the specified credentials.
     */
    private Mono<ValidationResult> validateApiAccess(GitRepositoryEntity repository, GitRepositoryUserPasswordCredentials credentials) {
        return handler
                .initApi(repository, credentials)
                .flatMap(api ->
                        Mono
                                .just(api)
                                .map(GitRepositoryApi.class::cast)
                                .map(GitRepositoryApi::validateInfo)
                                .doFinally(signalType -> api.close())
                );
    }
}
