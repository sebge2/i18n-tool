package be.sgerard.i18n.service.workspace.validation;

import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;
import reactor.core.publisher.Mono;

/**
 * Validator of the lifecycle of {@link WorkspaceEntity workspaces}.
 *
 * @author Sebastien Gerard
 */
public interface WorkspaceValidator {

    /**
     * Checks that the specified workspace is supported.
     */
    boolean support(WorkspaceEntity workspace);

    /**
     * Validates before the workspace is persisted.
     */
    default Mono<ValidationResult> beforePersist(WorkspaceEntity workspace) {
        return Mono.just(ValidationResult.EMPTY);
    }

    /**
     * Validates before the workspace is initialized.
     */
    default Mono<ValidationResult> beforeInitialize(WorkspaceEntity workspace) {
        return Mono.just(ValidationResult.EMPTY);
    }

    /**
     * Validates that the specified workspace can be published and eventually be in review.
     */
    default Mono<ValidationResult> beforePublish(WorkspaceEntity workspace) {
        return Mono.just(ValidationResult.EMPTY);
    }

    /**
     * Validates that the review on the specified workspace can finish.
     */
    default Mono<ValidationResult> beforeFinishReview(WorkspaceEntity workspace) {
        return Mono.just(ValidationResult.EMPTY);
    }

    /**
     * Validates that the specified workspace can be updated.
     */
    default Mono<ValidationResult> beforeUpdate(WorkspaceEntity workspace) {
        return Mono.just(ValidationResult.EMPTY);
    }
}
