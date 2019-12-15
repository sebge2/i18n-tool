package be.sgerard.i18n.service.repository.validation;

import be.sgerard.i18n.model.repository.dto.RepositoryPatchDto;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.validation.ValidationResult;
import reactor.core.publisher.Mono;

/**
 * Validator of the lifecycle of {@link RepositoryEntity repositories}.
 *
 * @author Sebastien Gerard
 */
public interface RepositoryValidator<R extends RepositoryEntity> {

    /**
     * Checks that the specified repository is supported.
     */
    boolean support(RepositoryEntity repositoryEntity);

    /**
     * Validates before persisting the specified repository.
     */
    default Mono<ValidationResult> beforePersist(R repository) {
        return Mono.just(ValidationResult.EMPTY);
    }

    /**
     * Validates before updating the specified repository.
     */
    default Mono<ValidationResult> beforeUpdate(R original, RepositoryPatchDto patch) {
        return Mono.just(ValidationResult.EMPTY);
    }

    /**
     * Validates before deleting the specified repository.
     */
    default Mono<ValidationResult> beforeDelete(R repository) {
        return Mono.just(ValidationResult.EMPTY);
    }

}
