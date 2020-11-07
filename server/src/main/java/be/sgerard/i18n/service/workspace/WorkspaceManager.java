package be.sgerard.i18n.service.workspace;

import be.sgerard.i18n.model.workspace.dto.WorkspaceDto;
import be.sgerard.i18n.model.workspace.dto.WorkspacesPublishRequestDto;
import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;
import be.sgerard.i18n.service.ResourceNotFoundException;
import be.sgerard.i18n.service.repository.RepositoryException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Manager of {@link WorkspaceEntity workspaces}.
 *
 * @author Sebastien Gerard
 */
public interface WorkspaceManager {

    /**
     * Finds all {@link WorkspaceDto workspaces}.
     */
    Flux<WorkspaceEntity> findAll();

    /**
     * Finds all {@link WorkspaceDto workspaces} associated to the specified repository.
     */
    Flux<WorkspaceEntity> findAll(String repositoryId);

    /**
     * Returns the {@link WorkspaceEntity workspace} having the specified id.
     */
    Mono<WorkspaceEntity> findById(String id);

    /**
     * Returns the {@link WorkspaceEntity workspace} having the specified id.
     */
    default Mono<WorkspaceEntity> findByIdOrDie(String id) throws ResourceNotFoundException {
        return findById(id)
                .switchIfEmpty(Mono.error(ResourceNotFoundException.workspaceNotFoundException(id)));
    }

    /**
     * Synchronizes the current workspaces with the specified repository: missing ones are created, workspaces
     * that are no more relevant (branch does not exist anymore) are deleted.
     */
    Flux<WorkspaceEntity> synchronizeAll(String repositoryId) throws ResourceNotFoundException, RepositoryException;

    /**
     * Initializes the {@link WorkspaceEntity workspace} having the specified id and returns it.
     */
    Mono<WorkspaceEntity> initialize(String workspaceId) throws ResourceNotFoundException, RepositoryException;

    /**
     * Synchronize the {@link WorkspaceEntity workspace} having the specified id and returns it. All translations
     * will be synchronized with the repository.
     */
    Mono<WorkspaceEntity> synchronize(String workspaceId) throws ResourceNotFoundException, RepositoryException;

    /**
     * Publishes all the modifications made on the specified workspace. Based on the type of repository, a review may start afterwards.
     * If it's not the case, a new fresh workspace will be created and returned.
     */
    Mono<WorkspaceEntity> publish(String workspaceId, String message) throws ResourceNotFoundException, RepositoryException;

    /**
     * Publishes all the modifications made on the specified workspaces. Based on the type of repository, a review may start afterwards.
     * If it's not the case, a new fresh workspace will be created and returned.
     */
    Flux<WorkspaceEntity> publish(WorkspacesPublishRequestDto request);

    /**
     * Terminates the review of the specified workspace. The workspace will be removed and a new fresh workspace will be created and returned.
     */
    Mono<WorkspaceEntity> finishReview(String workspaceId) throws ResourceNotFoundException, RepositoryException;

    /**
     * Updates the specified workspace. The status cannot be updated.
     */
    Mono<WorkspaceEntity> update(WorkspaceEntity workspaceEntity) throws ResourceNotFoundException, RepositoryException;

    /**
     * Removes the {@link WorkspaceEntity workspace} having the specified id.
     */
    Mono<WorkspaceEntity> delete(String id);
}
