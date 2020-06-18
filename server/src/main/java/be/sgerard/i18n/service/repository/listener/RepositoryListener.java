package be.sgerard.i18n.service.repository.listener;

import be.sgerard.i18n.model.repository.dto.RepositoryPatchDto;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.validation.ValidationResult;
import reactor.core.publisher.Mono;

/**
 * Listener of the lifecycle of {@link RepositoryEntity repositories}.
 *
 * @author Sebastien Gerard
 */
public interface RepositoryListener<R extends RepositoryEntity> {

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
     * Performs an action after the creation of the specified repository.
     */
    default Mono<Void> onCreate(R repository) {
        return Mono.empty();
    }

    /**
     * Validates before updating the specified repository.
     */
    default Mono<ValidationResult> beforeUpdate(R original, RepositoryPatchDto patch) {
        return Mono.just(ValidationResult.EMPTY);
    }

    /**
     * Performs an action after the update of the specified repository.
     */
    default Mono<Void> onUpdate(R repository) {
        return Mono.empty();
    }

    /**
     * Validates before deleting the specified repository.
     */
    default Mono<ValidationResult> beforeDelete(R repository) {
        return Mono.just(ValidationResult.EMPTY);
    }

    /**
     * Performs an action after the deletion of the specified repository.
     */
    default Mono<Void> onDelete(R repository) {
        return Mono.empty();
    }

}
