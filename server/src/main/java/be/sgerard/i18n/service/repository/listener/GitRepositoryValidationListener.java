package be.sgerard.i18n.service.repository.listener;

import be.sgerard.i18n.model.repository.dto.BaseGitRepositoryPatchDto;
import be.sgerard.i18n.model.repository.dto.RepositoryPatchDto;
import be.sgerard.i18n.model.repository.persistence.BaseGitRepositoryEntity;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.validation.ValidationMessage;
import be.sgerard.i18n.model.validation.ValidationResult;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * {@link RepositoryListener Listener} checking that Git repositories are valid.
 *
 * @author Sebastien Gerard
 */
@Component
public class GitRepositoryValidationListener implements RepositoryListener<RepositoryEntity> {

    /**
     * Validation message key specifying that the pattern of allowed branches is invalid.
     */
    public static final String ALLOWED_BRANCHES_PATTERN_INVALID = "validation.repository.allowed-branches-pattern-invalid";

    /**
     * Validation message key specifying that the default branch is not allowed by the specified pattern.
     */
    public static final String DEFAULT_BRANCH_NOT_ALLOWED = "validation.repository.default-branch-not-allowed";

    public GitRepositoryValidationListener() {
    }

    @Override
    public boolean support(RepositoryEntity repositoryEntity) {
        return repositoryEntity instanceof BaseGitRepositoryEntity;
    }

    @Override
    public Mono<ValidationResult> beforeUpdate(RepositoryEntity repository, RepositoryPatchDto patch) {
        final BaseGitRepositoryEntity gitRepository = (BaseGitRepositoryEntity) repository;
        final BaseGitRepositoryPatchDto gitPatch = (BaseGitRepositoryPatchDto) patch;

        final ValidationResult.Builder builder = ValidationResult.builder();
        if (gitPatch.getDefaultBranch().isPresent() || gitPatch.getAllowedBranches().isPresent()) {
            final String defaultBranch = gitPatch.getDefaultBranch().orElseGet(gitRepository::getDefaultBranch);
            final String allowedBranches = gitPatch.getAllowedBranches().orElseGet(() -> gitRepository.getAllowedBranches().toString());

            try {
                final Pattern compile = Pattern.compile(allowedBranches);

                if (!compile.matcher(defaultBranch).matches()) {
                    builder.messages(new ValidationMessage(DEFAULT_BRANCH_NOT_ALLOWED, defaultBranch, allowedBranches));
                }
            } catch (PatternSyntaxException e) {
                builder.messages(new ValidationMessage(ALLOWED_BRANCHES_PATTERN_INVALID, allowedBranches));
            }
        }

        return Mono.just(builder.build());
    }
}
