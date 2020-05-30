package be.sgerard.i18n.service.workspace.strategy;

import be.sgerard.i18n.model.i18n.persistence.WorkspaceEntity;
import be.sgerard.i18n.service.repository.RepositoryException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * A strategy defines the behaviour used by the {@link be.sgerard.i18n.service.workspace.WorkspaceManager workspace manager}
 * to manipulate a {@link WorkspaceEntity workspace} and the way it's used by the
 * {@link be.sgerard.i18n.service.i18n.TranslationManager translation manager}.
 *
 * @author Sebastien Gerard
 */
public interface WorkspaceTranslationsStrategy {

    /**
     * Returns whether the specified workspace is supported.
     */
    boolean support(WorkspaceEntity workspace);

    /**
     * Returns all the available branches.
     */
    Flux<String> listBranches() throws RepositoryException;

    /**
     * Returns whether the review of the specified {@link WorkspaceEntity workspace} is finished.
     */
    Mono<Boolean> isReviewFinished(WorkspaceEntity workspace);

    /**
     * Initializes the specified {@link WorkspaceEntity workspace} after that translations can be read/write.
     */
    Mono<WorkspaceEntity> onInitialize(WorkspaceEntity workspace);

    /**
     * Publishes all modified translations of the specified {@link WorkspaceEntity workspace}.
     *
     * @return <tt>true</tt> if a review started, otherwise <tt>false</tt> the workspace in that case must be dropped
     * @see be.sgerard.i18n.model.i18n.WorkspaceStatus#IN_REVIEW
     */
    Mono<Boolean> onPublish(WorkspaceEntity workspace);

    /**
     * Performs some actions before the specified {@link WorkspaceEntity workspace} is deleted. It may include
     */
    Mono<WorkspaceEntity> onDelete(WorkspaceEntity workspace);
}
