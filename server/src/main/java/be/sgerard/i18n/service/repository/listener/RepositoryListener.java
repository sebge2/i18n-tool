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
     * Performs an action after the creation of the specified repository.
     */
    default Mono<Void> onCreate(R repository) {
        return Mono.empty();
    }

    /**
     * Performs an action after the initialization of the specified repository.
     */
    default Mono<Void> onInitialize(R repository) {
        return Mono.empty();
    }

    /**
     * Performs an action after the initialization of the specified repository.
     */
    default Mono<Void> onInitializationError(R repository, Throwable error) {
        return Mono.empty();
    }

    /**
     * Performs an action after the patch of the specified repository.
     */
    default Mono<Void> onUpdate(RepositoryPatchDto patch, R repository) {
        return Mono.empty();
    }

    /**
     * Performs an action after the deletion of the specified repository.
     */
    default Mono<Void> onDelete(R repository) {
        return Mono.empty();
    }

}
