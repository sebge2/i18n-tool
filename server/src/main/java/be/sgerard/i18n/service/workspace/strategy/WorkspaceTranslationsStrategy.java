package be.sgerard.i18n.service.workspace.strategy;

import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.workspace.WorkspaceStatus;
import be.sgerard.i18n.service.repository.RepositoryException;
import be.sgerard.i18n.service.workspace.WorkspaceException;
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
     * Returns whether the specified repository is supported.
     */
    boolean support(RepositoryEntity repository);

    /**
     * Returns all the available branches.
     */
    Flux<String> listBranches(RepositoryEntity repository)  throws WorkspaceException, RepositoryException;

    /**
     * Returns whether the review of the specified {@link WorkspaceEntity workspace} is finished.
     */
    Mono<Boolean> isReviewFinished(WorkspaceEntity workspace) throws WorkspaceException, RepositoryException;

    /**
     * Initializes the specified {@link WorkspaceEntity workspace} after that translations can be read/write.
     */
    Mono<WorkspaceEntity> onInitialize(WorkspaceEntity workspace) throws WorkspaceException, RepositoryException;

    /**
     * Publishes all modified translations of the specified {@link WorkspaceEntity workspace}. The workspace
     * {@link WorkspaceEntity#getStatus() status} will be {@link WorkspaceStatus#IN_REVIEW in review}
     * if a review started. Otherwise, the status won't change. The strategy has also the responsibility to fill
     * {@link WorkspaceEntity#getReview() review information} if needed.
     *
     * @param message the message provided to explain modifications
     */
    Mono<WorkspaceEntity> onPublish(WorkspaceEntity workspace, String message) throws WorkspaceException, RepositoryException;

    /**
     * Performs some actions before the specified {@link WorkspaceEntity workspace} is deleted. It may include cleanup
     * of branches used during the review.
     */
    Mono<WorkspaceEntity> onDelete(WorkspaceEntity workspace) throws WorkspaceException, RepositoryException;
}
