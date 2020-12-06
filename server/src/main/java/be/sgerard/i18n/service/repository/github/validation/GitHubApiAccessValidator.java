package be.sgerard.i18n.service.repository.github.validation;

import be.sgerard.i18n.model.repository.RepositoryType;
import be.sgerard.i18n.model.repository.dto.GitHubRepositoryPatchDto;
import be.sgerard.i18n.model.repository.dto.RepositoryPatchDto;
import be.sgerard.i18n.model.repository.persistence.GitHubRepositoryEntity;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.security.repository.GitHubRepositoryTokenCredentials;
import be.sgerard.i18n.model.validation.ValidationMessage;
import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.service.repository.git.GitRepositoryApi;
import be.sgerard.i18n.service.repository.github.GitHubRepositoryHandler;
import be.sgerard.i18n.service.repository.validation.RepositoryValidator;
import be.sgerard.i18n.service.security.repository.RepositoryCredentialsManager;
import be.sgerard.i18n.service.security.repository.github.GitHubRepositoryCredentialsHandler;
import be.sgerard.i18n.support.StringUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * {@link RepositoryValidator Validator} checking that the GitHub API works properly based on the specified configuration.
 *
 * @author Sebastien Gerard
 */
@Component
public class GitHubApiAccessValidator implements RepositoryValidator<GitHubRepositoryEntity> {

    /**
     * Validation message key specifying that Git credentials are invalid.
     */
    public static final String INVALID_CREDENTIALS = "validation.git.invalid-credentials";

    private final GitHubRepositoryHandler handler;
    private final GitHubRepositoryCredentialsHandler credentialsHandler;
    private final RepositoryCredentialsManager credentialsManager;

    public GitHubApiAccessValidator(GitHubRepositoryHandler handler,
                                    GitHubRepositoryCredentialsHandler credentialsHandler,
                                    RepositoryCredentialsManager credentialsManager) {
        this.handler = handler;
        this.credentialsHandler = credentialsHandler;
        this.credentialsManager = credentialsManager;
    }

    @Override
    public boolean support(RepositoryEntity repositoryEntity) {
        return repositoryEntity.getType() == RepositoryType.GITHUB;
    }

    @Override
    public Mono<ValidationResult> beforePersist(GitHubRepositoryEntity repository) {
        return validateApiAccessCurrentUser(repository)
                .flatMap(validationResult -> {
                    if (validationResult.isSuccessful()) {
                        return validateApiAccessStatic(repository);
                    } else {
                        return Mono.just(validationResult);
                    }
                });
    }

    @Override
    public Mono<ValidationResult> beforeUpdate(GitHubRepositoryEntity original, RepositoryPatchDto patch) {
        final GitHubRepositoryPatchDto gitPatch = (GitHubRepositoryPatchDto) patch;

        if (gitPatch.getAccessKey().filter(StringUtils::isNotEmptyString).isPresent()) {
            return credentialsHandler
                    .loadStaticCredentials(original, gitPatch)
                    .flatMap(credentials -> {
                        if (!credentials.hasCredentials()) {
                            return Mono.just(createInvalidCredentialsResult(original));
                        }

                        return validateApiAccess(original, credentials);
                    });
        }

        return Mono.just(ValidationResult.EMPTY);
    }

    /**
     * Validates credentials that are specified at the repository entity level.
     */
    private Mono<ValidationResult> validateApiAccessStatic(GitHubRepositoryEntity repository) {
        if (repository.getAccessKey().isEmpty()) {
            return Mono.just(ValidationResult.EMPTY);
        }

        return credentialsHandler
                .loadStaticCredentials(repository)
                .flatMap(credentials -> {
                    if (!credentials.hasCredentials()) {
                        return Mono.just(createInvalidCredentialsResult(repository));
                    }

                    return validateApiAccess(repository, credentials);
                });
    }

    /**
     * Validates that the current user with the specified repository configuration can initialize the local Git repository.
     */
    private Mono<ValidationResult> validateApiAccessCurrentUser(GitHubRepositoryEntity repository) {
        return credentialsManager
                .loadUserCredentialsOrDie(repository)
                .map(GitHubRepositoryTokenCredentials.class::cast)
                .flatMap(credentials -> validateApiAccess(repository, credentials));
    }

    /**
     * Validates that the repository can be accessed with the specified credentials.
     */
    private Mono<ValidationResult> validateApiAccess(GitHubRepositoryEntity repository, GitHubRepositoryTokenCredentials credentials) {
        return handler
                .initApi(repository, credentials)
                .flatMap(api ->
                        Mono
                                .just(api)
                                .map(GitRepositoryApi.class::cast)
                                .map(GitRepositoryApi::validateInfo)
                                .doFinally(signalType -> api.close())
                )
                .switchIfEmpty(Mono.just(ValidationResult.EMPTY));
    }

    /**
     * Creates a {@link ValidationResult validation result} saying  that credentials are invalid for the specified repository.
     */
    private ValidationResult createInvalidCredentialsResult(GitHubRepositoryEntity repository) {
        return ValidationResult.singleMessage(new ValidationMessage(INVALID_CREDENTIALS, repository.getLocation()));
    }
}
