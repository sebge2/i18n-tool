package be.sgerard.i18n.service.repository.listener;

import be.sgerard.i18n.model.repository.dto.RepositoryPatchDto;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.validation.ValidationResult;

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
    default ValidationResult beforePersist(R repository) {
        return ValidationResult.EMPTY;
    }

    /**
     * Performs an action after the creation of the specified repository.
     */
    default void onCreate(R repository) {
    }

    /**
     * Validates before updating the specified repository.
     */
    default ValidationResult beforeUpdate(R original, RepositoryPatchDto patch) {
        return ValidationResult.EMPTY;
    }

    /**
     * Performs an action after the update of the specified repository.
     */
    default void onUpdate(R repository) {
    }

    /**
     * Validates before deleting the specified repository.
     */
    default ValidationResult beforeDelete(R repository) {
        return ValidationResult.EMPTY;
    }

    /**
     * Performs an action after the deletion of the specified repository.
     */
    default void onDelete(R repository) {
    }

}
