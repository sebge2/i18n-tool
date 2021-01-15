package be.sgerard.i18n.service.repository.listener;

import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
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
     * Performs an action before that the specified repository has been persisted.
     */
    default Mono<Void> beforePersist(R repository) {
        return Mono.empty();
    }

    /**
     * Performs an action after that the specified repository has been persisted.
     */
    default Mono<Void> afterPersist(R repository) {
        return Mono.empty();
    }

    /**
     * Performs an action after the initialization of the specified repository. The repository has been persisted.
     * {@link #afterUpdate(RepositoryEntity) afterUpdate} is also called (before this callback).
     */
    default Mono<Void> afterInitialize(R repository) {
        return Mono.empty();
    }

    /**
     * Performs an action after that the specified repository has been updated, but changes are not persisted.
     */
    default Mono<Void> beforeUpdate(R repository) {
        return Mono.empty();
    }

    /**
     * Performs an action after the update of the specified repository.
     */
    default Mono<Void> afterUpdate(R repository) {
        return Mono.empty();
    }

    /**
     * Performs an action before the deletion of the specified repository.
     */
    default Mono<Void> beforeDelete(R repository) {
        return Mono.empty();
    }

    /**
     * Performs an action after the deletion of the specified repository.
     */
    default Mono<Void> afterDelete(R repository) {
        return Mono.empty();
    }

}
